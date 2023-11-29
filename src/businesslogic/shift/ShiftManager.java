package businesslogic.shift;

import businesslogic.event.Service;
import persistence.ShiftPersistence;

import java.time.LocalTime;
import java.util.ArrayList;

public class ShiftManager {
    private Shift currentShift;
    private ArrayList<ShiftEventReceiver> eventReceivers;

    public ShiftManager(){
        eventReceivers = new ArrayList<>();
    }

    public ArrayList<Shift> createAllShifts(Service serv, LocalTime startTime, LocalTime endTime, int numShifts) {
        ArrayList<Shift> shifts = new ArrayList<>();

        int totalMinutes = (int) java.time.Duration.between(startTime, endTime).toMinutes();

        int shiftDuration = totalMinutes / numShifts;

        LocalTime shiftStartTime = startTime;
        LocalTime shiftEndTime = null;
        for(int i = 0; i < numShifts; i++){
            shiftEndTime = shiftStartTime.plusMinutes(shiftDuration);
            Shift shift = createShift(serv, shiftStartTime, shiftEndTime);
            serv.getShifts().add(shift);
            shifts.add(shift);
            shiftStartTime = shiftEndTime;
        }
        if(shiftEndTime.isBefore(endTime)){
            Shift lastShift = shifts.get(numShifts-1);
            lastShift.setEndTime(endTime);
        }
        return shifts;
    }

     private Shift createShift(Service serv, LocalTime startTime, LocalTime endTime) {
        Shift shift = new Shift(serv, startTime, endTime);
        this.setCurrentShift(shift);
        this.notifyShiftAdded(serv, shift);

        return shift;
    }

    public void setCurrentShift(Shift shift) {
        this.currentShift = shift;
    }

    private void notifyShiftAdded(Service serv, Shift shift){
        for(ShiftEventReceiver er: this.eventReceivers){
            er.updateShiftAdded(serv, shift);
        }
    }

    public void addShiftEventReceiver(ShiftPersistence shiftPersistence) {
        this.eventReceivers.add(shiftPersistence);
    }
}
