package persistence;

import businesslogic.event.Service;
import businesslogic.shift.Shift;
import businesslogic.shift.ShiftEventReceiver;

public class ShiftPersistence implements ShiftEventReceiver {
    @Override
    public void updateShiftAdded(Service serv, Shift shift) {
        Shift.saveNewShift(serv, shift);
    }
}
