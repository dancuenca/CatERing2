package persistence;

import businesslogic.event.*;
import businesslogic.menu.Chef;
import businesslogic.menu.Menu;

public class EventPersistence implements EventEventReceiver {
    @Override
    public void updateEventCreated(Event ev) {
        Event.saveNewEvent(ev);
    }

    @Override
    public void updateEventDeleted(Event ev, boolean spread){
        Event.deleteEvent(ev, spread);
    }

    @Override
    public void updateServiceAdded(Event ev, Service serv) {
        Service.saveNewService(ev, serv);
    }

    @Override
    public void updateRecurrenceCreated(Recurrence rec){
        Recurrence.saveNewRecurrence(rec);
    }

    @Override
    public void updateAssignmentAdded(Assignment assignment) {Assignment.saveNewAssignment(assignment); }

    @Override
    public void updateEventTitleChanged(Event ev){
        Event.saveEventTitle(ev);
    }

    @Override
    public void updateEventStartDateChanged(Event ev){
        Event.saveEventStartDate(ev);
    }

    @Override
    public void updateEventEndDateChanged(Event ev){
        Event.saveEventEndDate(ev);
    }

    @Override
    public void updateEventLocationChanged(Event ev){
        Event.saveEventLocation(ev);
    }

    @Override
    public void updateEventNumParticipantsChanged(Event ev){
        Event.saveEventNumParticipants(ev);
    }

    @Override
    public void updateEventNoteAdded(Event ev){
        Event.notesToDB(ev);
    }

    @Override
    public void updateChefAssigned(Event ev, Chef chef){
        Event.saveChefAssigned(ev);
        Chef.saveChefEvent(ev, chef);
    }

    @Override
    public void updateMenuForServiceSet(Service serv, Menu m){
        Service.saveMenuSet(serv, m);
    }

    @Override
    public void updateMenuForServiceApproved(Service serv){
        Service.saveMenuApproved(serv);
    }

    @Override
    public void updateEventStateChanged(Event ev){
        Event.saveNewEventState(ev);
    }

    @Override
    public void updateEventCancelled(Event ev, boolean spread){
        Event.cancelEvent(ev, spread);
    }

    @Override
    public void updateRecurrenceFrequenceChanged(Recurrence rec){
        Recurrence.changeFrequence(rec);
    }

    @Override
    public void updateRecurrenceNumInstancesChanged(Recurrence rec){
        Recurrence.changeNumInstances(rec);
    }

    @Override
    public void updateRecurrentEventAdded(Event ev){
        Event.saveNewEvent(ev);
    }

    @Override
    public void updateRecurrenceSetEvent(Recurrence rec, Event ev){
        Event.saveRecurrenceSetEvent(rec, ev);
    }
}
