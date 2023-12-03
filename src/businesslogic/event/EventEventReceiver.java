package businesslogic.event;

public interface EventEventReceiver {
    public void updateEventCreated(Event ev);
    public void updateServiceAdded(Event ev, Service serv);
    public void updateRecurrenceCreated(Recurrence rec);
    public void updateAssignmentAdded(Assignment assignment);
    public void updateEventDeleted(Event ev);

    /*
    public void updateEventCancelled(Event ev);
    public void updateRecurrenceCreated(Recurrence rec);
    public void updateServiceCreated(Service serv);
    */

}
