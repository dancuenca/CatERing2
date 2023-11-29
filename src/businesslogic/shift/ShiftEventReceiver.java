package businesslogic.shift;

import businesslogic.event.Service;

public interface ShiftEventReceiver {
    public void updateShiftAdded(Service service, Shift shift);
}
