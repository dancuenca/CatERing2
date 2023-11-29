package persistence;

import businesslogic.event.Event;
import businesslogic.event.EventEventReceiver;
import businesslogic.event.Service;
import businesslogic.menu.Menu;
import businesslogic.shift.Shift;

public class EventPersistence implements EventEventReceiver {
    @Override
    public void updateEventCreated(Event ev) {
        Event.saveNewEvent(ev);
    }

    @Override
    public void updateServiceAdded(Event ev, Service serv) {
        Service.saveNewService(ev, serv);
    }

}
