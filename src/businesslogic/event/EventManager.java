package businesslogic.event;

import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.shift.Shift;
import businesslogic.shift.StaffMember;
import businesslogic.user.User;
import javafx.collections.ObservableList;
import persistence.EventPersistence;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EventManager {
    private Event currentEvent;
    private ArrayList<EventEventReceiver> eventReceivers;

    public EventManager(){
        eventReceivers = new ArrayList<>();
    }

    public Event createEvent(String title, String location, String startDate, String endDate, int numParticipants, String client) throws UseCaseLogicException{
        return this.createEvent(title, location, startDate, endDate, numParticipants, client, null);
    }

    public Event createEvent(String title, String location, String startDate, String endDate, int numParticipants, String client, String[] notes) throws UseCaseLogicException {
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

    public ArrayList<StaffMember> getStaffMembers() {
        return StaffMember.loadAvailableStaffMembers();
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

    public Event deleteEvent(Event ev, boolean spread) throws UseCaseLogicException{
        User u = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!u.isOrganizer() || ev.getState().equals("ongoing")){
            throw new UseCaseLogicException();
        }

        this.notifyEventDeleted(ev, spread);

        return ev;
    }

    public void changeEventTitle(String title) throws UseCaseLogicException{
        if(currentEvent == null){
            throw new UseCaseLogicException();
        }

        currentEvent.setTitle(title);
        this.notifyEventTitleChanged();
    }

    public void changeEventStartDate(String date) throws UseCaseLogicException{
        if(currentEvent == null){
            throw new UseCaseLogicException();
        }

        currentEvent.setStartDate(convertStringToDate(date));
        this.notifyEventStartDateChanged();
    }

    public void changeEventEndDate(String date) throws UseCaseLogicException{
        if(currentEvent == null){
            throw new UseCaseLogicException();
        }

        currentEvent.setEndDate(convertStringToDate(date));
        this.notifyEventEndDateChanged();
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

/*
    public Event cancelEvent(Event event, boolean spread) throws UseCaseLogicException{
        User user = CatERing.getInstance().getUserManager().getCurrentUser();

        if(!user.isOrganizer()){
            throw new UseCaseLogicException();
        }

        if(spread == true){
            for(int i = 0; i < event.getRecurrence().getRecurrentEvents().size(); i++){
                event.getRecurrence().getRecurrentEvents().get(i).setState("cancelled");
            }
        }
        else{
            event.setState("cancelled");
            event.getRecurrence().getRecurrentEvents().remove(event);
        }

        this.notifyEventCancelled(event);

        return event;
    }

    public void selectStaff(Shift shift, int id, String task) throws UseCaseLogicException{
        User user = CatERing.getInstance().getUserManager().getCurrentUser();

        if(!user.isOrganizer()){
            throw new UseCaseLogicException();
        }

        StaffMember selectedStaffMember = shift.getById(id);
        shift.getById(id).setAvailable(false);
        int indexOfSelectedStaffMember = shift.getAvailableStaffMems().indexOf(selectedStaffMember);
        shift.setTasksInIndex(indexOfSelectedStaffMember, task);
    }

    public void approveMenu(Service serv) throws UseCaseLogicException{
        User user = CatERing.getInstance().getUserManager().getCurrentUser();

        if(!user.isOrganizer()){
            throw new UseCaseLogicException();
        }

        serv.approveMenu();

        boolean flag = true;
        for(Service service : serv.getBelongingEvent().getServices()){
            if(!service.isApprovedMenu()){
                flag = false;
            }
        }

        if(flag == true){
            serv.getBelongingEvent().setState("ongoing");
        }
    }
*/

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

    public void addEventEventReceiver(EventPersistence eventPersistence) {
        this.eventReceivers.add(eventPersistence);
    }

    private void notifyServiceAdded(Event ev, Service serv) {
        for(EventEventReceiver er: this.eventReceivers){
            er.updateServiceAdded(ev, serv);
        }
    }

    private void notifyAssignmentAdded(Assignment assignment) {
        for (EventEventReceiver er: this.eventReceivers) {
            er.updateAssignmentAdded(assignment);
        }
    }

    private void notifyEventTitleChanged(){
        for(EventEventReceiver er: this.eventReceivers){
            er.updateEventTitleChanged(this.currentEvent);
        }
    }

    private void notifyEventStartDateChanged(){
        for(EventEventReceiver er: this.eventReceivers){
            er.updateEventStartDateChanged(this.currentEvent);
        }
    }

    private void notifyEventEndDateChanged(){
        for(EventEventReceiver er: this.eventReceivers){
            er.updateEventEndDateChanged(this.currentEvent);
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





