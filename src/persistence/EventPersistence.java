package persistence;

import businesslogic.event.*;

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

}
