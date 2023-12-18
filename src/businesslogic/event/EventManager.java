package businesslogic.event;

import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.menu.Chef;
import businesslogic.menu.Menu;
import businesslogic.shift.Shift;
import businesslogic.shift.StaffMember;
import businesslogic.user.User;
import javafx.collections.ObservableList;
import persistence.EventPersistence;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

import static businesslogic.event.Recurrence.changeDateByAddDays;

public class EventManager {
    private Event currentEvent;
    private ArrayList<EventEventReceiver> eventReceivers;

    public EventManager(){
        eventReceivers = new ArrayList<>();
    }

    public Event createEvent(String title, String location, String startDate, String endDate, int numParticipants, String client) throws UseCaseLogicException{
        return this.createEvent(title, location, startDate, endDate, numParticipants, client, null);
    }

    public Event createEvent(String title, String location, String startDate, String endDate, int numParticipants, String client, ArrayList<String> notes) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();

        if(!user.isOrganizer()){
            throw new UseCaseLogicException();
        }

        Event ev = new Event(user, title, location, startDate, endDate, numParticipants, client, notes);
        this.setCurrentEvent(ev);
        this.notifyEventAdded(ev);

        return ev;
    }

    public Service insertService(Event ev, Date date, String serviceType, String startTime, String endTime, boolean spread) throws UseCaseLogicException{
        if(currentEvent == null){
            throw new UseCaseLogicException();
        }

        Service service =  this.currentEvent.addService(ev, date, serviceType, startTime, endTime);

        if(!spread) {
            this.notifyServiceAdded(this.currentEvent, service);
        }
        else{
            ArrayList<Event> recEv = ev.getRecurrence().getRecurrentEvents();

            for(Event event: recEv){
                event.getServices().add(service);
                this.notifyServiceAdded(event, service);
            }
        }

        return service;
    }

    public Service deleteService(Event ev, Service serv, boolean spread) throws UseCaseLogicException{
        if(currentEvent == null || ev.getState() == "ongoing"){
            throw new UseCaseLogicException();
        }

        ev.getServices().remove(serv);

        this.notifyServiceDeleted(ev, serv);

        //TODO: implementare spread

        return serv;
    }

    public Recurrence defineRecurrence(int frequence, int numIstances, String endDate, Event mainEvent) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();

        if(!user.isOrganizer()){
            throw new UseCaseLogicException();
        }

        Recurrence rec = new Recurrence(frequence, numIstances, endDate, mainEvent);
        mainEvent.setRecurrence(rec);

        this.notifyRecurrenceAdded(rec);

        return rec;
    }

    public void changeRecurrenceFrequence(Recurrence rec, int newFrequence) throws UseCaseLogicException{
        User user = CatERing.getInstance().getUserManager().getCurrentUser();

        if(!user.isOrganizer()){
            throw new UseCaseLogicException();
        }

        rec.setFrequence(newFrequence);

        for(int i = 1; i <= rec.getNumInstances(); i++){
            Event recEv = rec.getRecurrentEvents().get(i-1);
            recEv.setStartDate(changeDateByAddDays(rec.getMainEvent().getStartDate(), rec.getFrequence() * i));
            this.notifyEventStartDateChanged(recEv);
            recEv.setEndDate(changeDateByAddDays(rec.getMainEvent().getEndDate(), rec.getFrequence() * i));
            this.notifyEventEndDateChanged(recEv);
        }

        this.notifyRecurrenceFrequenceChanged(rec);
    }

    public void changeRecurrenceNumInstances(Recurrence rec, int numInstances) throws UseCaseLogicException{
        User user = CatERing.getInstance().getUserManager().getCurrentUser();

        if(!user.isOrganizer()){
            throw new UseCaseLogicException();
        }

        int numNewInstances = Math.abs(numInstances - rec.getNumInstances());
        int indexLastRecEvent = rec.getRecurrentEvents().size()-1;

        if(numInstances > rec.getNumInstances()){
            for(int i = 1; i <= numNewInstances; i++){
                Event recEv = new Event(rec.getMainEvent().getOrganizer(), rec.getMainEvent().getTitle(), rec.getMainEvent().getLocation(),
                        Recurrence.changeDateByAddDays(rec.getRecurrentEvents().get(indexLastRecEvent).getStartDate(), rec.getFrequence() * i),
                        Recurrence.changeDateByAddDays(rec.getRecurrentEvents().get(indexLastRecEvent).getEndDate(), rec.getFrequence() * i),
                        rec.getMainEvent().getNumParticipants(), rec.getMainEvent().getClient(), rec.getMainEvent().getNotes());

                rec.getRecurrentEvents().add(recEv);
                recEv.setRecurrence(rec);
                this.notifyRecurrenceSetEvent(rec, recEv);

                this.notifyRecurrentEventAdded(recEv);
            }
        }
        else if(numInstances < rec.getNumInstances()){
            while(numNewInstances > 0 && rec.getRecurrentEvents().get(numNewInstances).getState() != "ongoing"){
                deleteEvent(rec.getRecurrentEvents().get(numNewInstances), false);
                numNewInstances--;

                this.notifyRecurrentEventDeleted(rec, numNewInstances);
            }
        }

        rec.setNumInstances(numInstances);

        this.notifyRecurrenceNumInstancesChanged(rec);
    }

    public ArrayList<StaffMember> getStaffMembers() {
        return StaffMember.loadAvailableStaffMembers();
    }

    public ArrayList<Chef> getChefs(){
        return Chef.loadAvailableChefs();
    }

    public Assignment defineAssignment(Service service, StaffMember staffMember, Shift shift, String task) throws UseCaseLogicException{
        if(currentEvent == null && !this.currentEvent.getServices().contains(service) && !service.getShifts().contains(shift)) {
            throw new UseCaseLogicException();
        }

        Assignment assignment = new Assignment(staffMember, shift, task);
        shift.getAvailableStaffMems().add(staffMember);
        this.notifyAssignmentAdded(assignment);

        return assignment;
    }

    public void assignChef(Chef chef, boolean spread) throws UseCaseLogicException{
        if(currentEvent == null){
            throw new UseCaseLogicException();
        }

        if(spread){
            for(Event recEvent: currentEvent.getRecurrence().getRecurrentEvents()){
                recEvent.setChef(chef);
                this.notifyChefAssigned(recEvent, chef);
            }
        }

        currentEvent.setChef(chef);
        this.notifyChefAssigned(currentEvent, chef);
    }

    public Event deleteEvent(Event ev, boolean spread) throws UseCaseLogicException{
        User u = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!u.isOrganizer() || ev.getState().equals("ongoing")){
            throw new UseCaseLogicException();
        }

        if(spread){
            for(Event recEvent: currentEvent.getRecurrence().getRecurrentEvents()){
                this.notifyEventDeleted(recEvent, spread);
            }
        }

        this.notifyEventDeleted(ev, spread);

        return ev;
    }

    public Event cancelEvent(Event ev, boolean spread) throws UseCaseLogicException{
        User u = CatERing.getInstance().getUserManager().getCurrentUser();
        if(!u.isOrganizer() || ev.getState().equals("ongoing")){
            throw new UseCaseLogicException();
        }

        if(spread){
            for(Event recEvent: currentEvent.getRecurrence().getRecurrentEvents()){
                recEvent.setState("cancelled");
                this.notifyEventCancelled(recEvent, spread);
            }

            currentEvent.setState("cancelled");
            this.notifyEventCancelled(currentEvent, spread);
        }

        ev.setState("cancelled");
        this.notifyEventCancelled(ev, spread);

        return ev;
    }

    public void changeEventTitle(String title, boolean spread) throws UseCaseLogicException {
        if (currentEvent == null) {
            throw new UseCaseLogicException();
        }
        if(spread) {
            for (Event recEvent : currentEvent.getRecurrence().getRecurrentEvents()) {
                recEvent.setTitle(title);
                this.notifyEventTitleChanged(recEvent);
            }
        }
        currentEvent.setTitle(title);
        this.notifyEventTitleChanged(currentEvent);
    }

    public void changeEventStartDate(String date, boolean spread) throws UseCaseLogicException{
        if(currentEvent == null ){
            throw new UseCaseLogicException();
        }
        if(spread){
            //calcola il numero di giorni di differenza tra data iniziale vecchia e data iniziale nuova
            Date oldDate = this.currentEvent.getStartDate();
            Date newDate = convertStringToDate(date);
            int numDays = dateDifference(oldDate, newDate);

            for (Event recEvent : currentEvent.getRecurrence().getRecurrentEvents()) {
                Date updatedDate = changeDateByAddDays(recEvent.getStartDate(), numDays);
                recEvent.setStartDate(updatedDate);
                this.notifyEventStartDateChanged(recEvent);
            }
        }
        currentEvent.setStartDate(convertStringToDate(date));
        this.notifyEventStartDateChanged(currentEvent);
    }

    public void changeEventEndDate(String date, boolean spread) throws UseCaseLogicException{
        if(currentEvent == null){
            throw new UseCaseLogicException();
        }
        if(spread){
            //calcola il numero di giorni di differenza tra data iniziale vecchia e data iniziale nuova
            Date oldDate = this.currentEvent.getEndDate();
            Date newDate = convertStringToDate(date);
            int numDays = dateDifference(oldDate, newDate);

            for (Event recEvent : currentEvent.getRecurrence().getRecurrentEvents()) {
                Date updatedDate = changeDateByAddDays(recEvent.getEndDate(), numDays);
                recEvent.setEndDate(updatedDate);
                this.notifyEventStartDateChanged(recEvent);
            }
        }
        currentEvent.setEndDate(convertStringToDate(date));
        this.notifyEventEndDateChanged(currentEvent);
    }

    private int dateDifference(Date oldDate, Date newDate) {
        int numDays = 0;

        LocalDate newLocalDate = newDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate oldLocalDate = oldDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        numDays = (int) ChronoUnit.DAYS.between(oldLocalDate, newLocalDate);

        return numDays;
    }

    private static Date convertStringToDate(String dateString){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        try{
            Date result = sdf.parse(dateString);
            return result;
        } catch (ParseException e){
            e.printStackTrace();
            return null;
        }
    }

    public void changeEventLocation(String location, boolean spread) throws UseCaseLogicException{
        if(currentEvent == null){
            throw new UseCaseLogicException();
        }
        if(spread) {
            for (Event recEvent : currentEvent.getRecurrence().getRecurrentEvents()) {
                recEvent.setLocation(location);
                this.notifyEventLocationChanged(recEvent);
            }
        }
        currentEvent.setLocation(location);
        this.notifyEventLocationChanged(currentEvent);
    }

    public void changeEventNumParticipants(int numParticipants, boolean spread) throws UseCaseLogicException{
        if(currentEvent == null){
            throw new UseCaseLogicException();
        }
        if(spread) {
            for (Event recEvent : currentEvent.getRecurrence().getRecurrentEvents()) {
                recEvent.setNumParticipants(numParticipants);
                this.notifyEventNumParticipantsChanged(recEvent);
            }
        }
        currentEvent.setNumParticipants(numParticipants);
        this.notifyEventNumParticipantsChanged(currentEvent);
    }

    public void addNoteToEvent(String note) throws UseCaseLogicException{
        if(currentEvent == null){
            throw new UseCaseLogicException();
        }

        currentEvent.addNote(note);
        this.notifyEventNoteAdded();
    }

    public void setMenuForService(Service serv, Menu m) throws UseCaseLogicException{
        if(currentEvent == null){
            throw new UseCaseLogicException();
        }

        serv.setMenu(m);
        this.notifyMenuForServiceSet(serv, m);
    }

    public void approveMenu(Service serv) throws UseCaseLogicException{
        if(currentEvent == null || serv.getMenu() == null){
            throw new UseCaseLogicException();
        }

        serv.setApproveMenu();
        this.notifyMenuForServiceApproved(serv);

        boolean flag = true;
        for(Service service: serv.getBelongingEvent().getServices()){
            if(!service.isApprovedMenu()){
                flag = false;
            }
        }

        if(flag){
            currentEvent.setState("ongoing");
            System.out.println("current event state: " + currentEvent.getState());

            this.notifyEventStateChanged(currentEvent);
        }
    }

    public ArrayList<Shift> getEventShifts(Event ev){
        return ev.getShifts();
    }

    public void setCurrentEvent(Event ev){
        this.currentEvent = ev;
    }

    private void notifyEventAdded(Event ev){
        for(EventEventReceiver er: this.eventReceivers){
            er.updateEventCreated(ev);
        }
    }

    private void notifyEventDeleted(Event ev, boolean spread){
        for(EventEventReceiver er: this.eventReceivers){
            er.updateEventDeleted(ev, spread);
        }
    }

    private void notifyRecurrenceAdded(Recurrence rec) {
        for(EventEventReceiver er: this.eventReceivers){
            er.updateRecurrenceCreated(rec);
        }
    }

    private void notifyRecurrenceFrequenceChanged(Recurrence rec) {
        for(EventEventReceiver er: this.eventReceivers ){
            er.updateRecurrenceFrequenceChanged(rec);
        }
    }

    private void notifyRecurrentEventAdded(Event recEv){
        for(EventEventReceiver er: this.eventReceivers){
            er.updateRecurrentEventAdded(recEv);
        }
    }

    private void notifyRecurrentEventDeleted(Recurrence rec, int numNewInstances){
        for(EventEventReceiver er: this.eventReceivers){
            er.updateRecurrentEventDeleted(rec, numNewInstances);
        }
    }

    private void notifyRecurrenceSetEvent(Recurrence rec, Event ev){
        for(EventEventReceiver er: this.eventReceivers){
            er.updateRecurrenceSetEvent(rec, ev);
        }
    }

    private void notifyRecurrenceNumInstancesChanged(Recurrence rec){
        for(EventEventReceiver er: this.eventReceivers){
            er.updateRecurrenceNumInstancesChanged(rec);
        }
    }

    public void addEventEventReceiver(EventPersistence eventPersistence) {
        this.eventReceivers.add(eventPersistence);
    }

    private void notifyServiceAdded(Event ev, Service serv) {
        for(EventEventReceiver er: this.eventReceivers){
            er.updateServiceAdded(ev, serv);
        }
    }

    private void notifyServiceDeleted(Event ev, Service serv){
        for(EventEventReceiver er: this.eventReceivers){
            er.updateServiceDeleted(ev, serv);
        }
    }

    private void notifyAssignmentAdded(Assignment assignment) {
        for (EventEventReceiver er: this.eventReceivers) {
            er.updateAssignmentAdded(assignment);
        }
    }

    private void notifyChefAssigned(Event ev, Chef chef){
        for(EventEventReceiver er: this.eventReceivers){
            er.updateChefAssigned(ev, chef);
        }
    }

    private void notifyEventTitleChanged(Event ev){
        for(EventEventReceiver er: this.eventReceivers){
            er.updateEventTitleChanged(ev);
        }
    }

    private void notifyEventStartDateChanged(Event ev){
        for(EventEventReceiver er: this.eventReceivers){
            er.updateEventStartDateChanged(ev);
        }
    }

    private void notifyEventEndDateChanged(Event ev){
        for(EventEventReceiver er: this.eventReceivers){
            er.updateEventEndDateChanged(ev);
        }
    }

    private void notifyEventLocationChanged(Event ev){
        for(EventEventReceiver er: this.eventReceivers){
            er.updateEventLocationChanged(ev);
        }
    }

    private void notifyEventNumParticipantsChanged(Event ev){
        for(EventEventReceiver er: this.eventReceivers){
            er.updateEventNumParticipantsChanged(ev);
        }
    }

    private void notifyEventNoteAdded(){
        for(EventEventReceiver er: this.eventReceivers){
            er.updateEventNoteAdded(this.currentEvent);
        }
    }

    private void notifyMenuForServiceSet(Service serv, Menu m){
        for(EventEventReceiver er: this.eventReceivers){
            er.updateMenuForServiceSet(serv, m);
        }
    }

    private void notifyMenuForServiceApproved(Service serv){
        for(EventEventReceiver er: this.eventReceivers){
            er.updateMenuForServiceApproved(serv);
        }
    }

    private void notifyEventStateChanged(Event ev){
        for(EventEventReceiver er: this.eventReceivers){
            er.updateEventStateChanged(ev);
        }
    }

    private void notifyEventCancelled(Event ev, boolean spread){
        for(EventEventReceiver er: this.eventReceivers){
            er.updateEventCancelled(ev, spread);
        }
    }

    public ObservableList<EventInfo> getEventInfo() {
        return EventInfo.loadAllEventInfo();
    }

    public ArrayList<Event> getEventList() {
        User u = CatERing.getInstance().getUserManager().getCurrentUser();
        return Event.loadAllEventInfo(u);
    }
}





