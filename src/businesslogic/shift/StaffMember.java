package businesslogic.shift;

import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class StaffMember {
    private int id;
    private String name;
    private int available;
    private int shiftId;

    public StaffMember() {
    }

    public StaffMember(int id, String name){
        this.id = id;
        this.name = name;
        this.available = 1;
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public int isAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public String toString(){
        String result = "---> STAFF MEMBER: \n";
        result += "id: " + id + "\n";
        result += "name: " + name + "\n";
        result += "available: " + available + "\n";
        result += "shift id: " + shiftId + "\n";

        return result;
    }

    // STATIC METHODS FOR PERSISTENCE
    public static ArrayList<StaffMember> loadAvailableStaffMembers() {
        ArrayList<StaffMember> staff = new ArrayList<StaffMember>();
        String query = "SELECT * FROM StaffMemberCatering WHERE availability = 1";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                StaffMember staffMember = new StaffMember();
                staffMember.id = rs.getInt("id");
                staffMember.name = rs.getString("name");
                staffMember.available = rs.getInt("availability");
                staffMember.shiftId = rs.getInt("shift_id");
                staff.add(staffMember);
            }
        });

        return staff;
    }

    public static void changeAvailability(StaffMember staffMember) {
        staffMember.available = 0;
        String upd = "UPDATE StaffMemberCatering SET availability = " + staffMember.available + "  WHERE id = " + staffMember.id;
        PersistenceManager.executeUpdate(upd);
        String upd2 = "UPDATE StaffMemberCatering SET shift_id = " + staffMember.shiftId + "  WHERE id = " + staffMember.id;
        PersistenceManager.executeUpdate(upd2);
    }
}
