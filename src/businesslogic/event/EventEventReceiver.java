package businesslogic.event;

import businesslogic.menu.Chef;

public interface EventEventReceiver {
    public void updateEventCreated(Event ev);
    public void updateServiceAdded(Event ev, Service serv);
    public void updateRecurrenceCreated(Recurrence rec);
    public void updateAssignmentAdded(Assignment assignment);
    public void updateEventDeleted(Event ev, boolean spread);
    public void updateEventTitleChanged(Event ev);
    public void updateEventStartDateChanged(Event ev);
    public void updateEventEndDateChanged(Event ev);
    public void updateEventLocationChanged(Event ev);
    public void updateEventNumParticipantsChanged(Event ev);
    public void updateEventNoteAdded(Event ev);
    public void updateChefAssigned(Event ev, Chef chef);

    /*
    public void updateEventCancelled(Event ev);
    public void updateRecurrenceCreated(Recurrence rec);
    public void updateServiceCreated(Service serv);
    */

}
