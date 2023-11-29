package businesslogic.event;

import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.shift.Shift;
import businesslogic.shift.StaffMember;
import businesslogic.user.User;
import javafx.collections.ObservableList;
import persistence.EventPersistence;

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

    public Recurrence defineRecurrence(int frequence, int numIstances, Date endDate, Event mainEvent) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();

        if(!user.isOrganizer()){
            throw new UseCaseLogicException();
        }

        Recurrence rec = new Recurrence(frequence, numIstances, endDate, mainEvent);

        this.notifyRecurrenceAdded(rec);

        return rec;
    }



    public Service insertService(Event ev, Date date, String serviceType, int numShifts, String startTime, String endTime, boolean spread) throws UseCaseLogicException{
        User user = CatERing.getInstance().getUserManager().getCurrentUser();

        if(!user.isOrganizer()){
            throw new UseCaseLogicException();
        }

        if(spread == false) {
            Service serv = new Service(ev, date, serviceType, numShifts, startTime, endTime);

            this.notifyServiceAdded(serv);

            return serv;
        }
        else{
            Service serv = new Service(ev, date, serviceType, numShifts, startTime, endTime);

            ArrayList<Event> recEv = ev.getRecurrence().getRecurrentEvents();

            for(Event event: recEv){
                event.getServices().add(serv);
                //TODO: non so come avvertire che anche agli altri eventi che appartengono alla ricorrenza è stato aggiunto un servizio
                //this.notifyServiceAdded(serv)
            }

            return serv;
        }
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
    private void notifyEventAdded(Event ev){
        for(EventEventReceiver er: this.eventReceivers){
            er.updateEventCreated(ev);
        }
    }
/*
    private void notifyEventCancelled(Event ev){
        for(EventEventReceiver er: this.eventReceivers){
            er.updateEventCancelled(ev);
        }
    }

    private void notifyRecurrenceAdded(Recurrence rec) {
        for(EventEventReceiver er: this.eventReceivers){
            er.updateRecurrenceCreated(rec);
        }
    }

    private void notifyServiceAdded(Service serv){
        for(EventEventReceiver er: this.eventReceivers){
            er.updateServiceCreated(serv);
        }
    }
*/
    public void setCurrentEvent(Event ev){
        this.currentEvent = ev;
    }

    public ObservableList<EventInfo> getEventInfo() {
        return EventInfo.loadAllEventInfo();
    }

    public void addEventEventReceiver(EventPersistence eventPersistence) {
        this.eventReceivers.add(eventPersistence);
    }

    private void notifyServiceAdded(Event ev, Service serv) {
        for(EventEventReceiver er: this.eventReceivers){
            er.updateServiceAdded(ev, serv);
        }
    }
}





