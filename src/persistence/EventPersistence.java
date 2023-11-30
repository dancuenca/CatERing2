package persistence;

import businesslogic.event.*;

public class EventPersistence implements EventEventReceiver {
    @Override
    public void updateEventCreated(Event ev) {
        Event.saveNewEvent(ev);
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

}
