package businesslogic.shift;

import businesslogic.event.Service;
import persistence.BatchUpdateHandler;
import persistence.PersistenceManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;

public class Shift {
    private int id;
    private Service serv;
    private LocalTime startTime;
    private LocalTime endTime;

    private ArrayList<StaffMember> availableStaffMems;

    public Shift(Service serv, LocalTime startTime, LocalTime endTime){
        this.serv = serv;
        this.startTime = startTime;
        this.endTime = endTime;
        //this.availableStaffMems = populateAvailStaffMembersArray();
    }

    public int getId(){
        return id;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    /*public ArrayList<StaffMember> getAvailableStaffMems() {
        return availableStaffMems;

    public void setTasksInIndex(int index, String task) {
        this.tasks[index] = task;
    }*/

    /*  public ArrayList<StaffMember> populateAvailStaffMembersArray(){
        ArrayList<StaffMember> populatedAvailStaffMembersArray = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            StaffMember staffMem = new StaffMember(i, "Marco");
            populatedAvailStaffMembersArray.add(staffMem);
        }

        return populatedAvailStaffMembersArray;
    }

    public StaffMember getById(int id){
        StaffMember res = null;
        for(StaffMember sm: availableStaffMems){
            if(sm.getId() == id && sm.isAvailable()){
                res = sm;
            }
        }

        return res;
    } */

    public String toString(){
        String result = "---> SHIFT (for service: " + serv.getServiceType() + "):" + "\n";
        result += "time: " + this.startTime + " - " + this.endTime + "\n";
        /*result += "available staff members: " + "\n";
        for(int i = 0; i < availableStaffMems.size(); i++){
            result += "\tid: " + availableStaffMems.get(i).getId() + "\n";
            result += "\tname: " + availableStaffMems.get(i).getName() + "\n";
            result += "\tavailability: " + availableStaffMems.get(i).isAvailable() + "\n";
            result += "\ttask assigned: " + tasks[i] + "\n";
        }*/

        return result;
    }

    //STATIC METHODS FOR PERSISTENCE
    public static void saveNewShift(Service serv, Shift shift){
        String shiftInsert = "INSERT INTO catering.ShiftsCatering (service_id, start_time, end_time) VALUES (?, ?, ?);";

        PersistenceManager.executeBatchUpdate(shiftInsert, 1, new BatchUpdateHandler(){
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException{
                ps.setInt(1, serv.getId());
                ps.setTime(2, Time.valueOf(shift.startTime));
                ps.setTime(3, Time.valueOf(shift.endTime));
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException{
                shift.id = PersistenceManager.getLastId();
            }
        });

        //PersistenceManager.executeUpdate(shiftInsert);
        shift.id = PersistenceManager.getLastId();
    }

    public static void saveAllNewShifts(Service serv, ArrayList<Shift> shifts){
        String shiftInsert = "INSERT INTO catering.ShiftsCatering (service_id, start_time, end_time) VALUES (?, ?, ?);";
        PersistenceManager.executeBatchUpdate(shiftInsert, shifts.size(), new BatchUpdateHandler(){
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException{
                ps.setInt(1, serv.getId());
                ps.setTime(2, Time.valueOf(shifts.get(batchCount).startTime));
                ps.setTime(3, Time.valueOf(shifts.get(batchCount).endTime));
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException{
                shifts.get(count).id = rs.getInt(1);
            }
        });
    }

}
