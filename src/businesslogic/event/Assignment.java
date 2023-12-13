package businesslogic.event;

import businesslogic.shift.Shift;
import businesslogic.shift.StaffMember;
import persistence.PersistenceManager;

public class Assignment {
    private int id;
    private StaffMember staffMember;
    private Shift shift;
    private String task;

    public Assignment(StaffMember staffMember, Shift shift, String task) {
        this.id = id;
        this.staffMember = staffMember;
        this.shift = shift;
        this.task = task;
    }

    public StaffMember getStaffMember() {
        return staffMember;
    }

    public Shift getShift() {
        return shift;
    }

    public String getTask() {
        return task;
    }

    public void setStaffMember(StaffMember staffMember) {
        this.staffMember = staffMember;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public void setTask(String task) {
        this.task = task;
    }

    @Override
    public String toString() {
        String result ="---> ASSIGNMENT: \n";
        result +=  staffMember + "\n";
        result +=  shift + "\n";
        result += "---> TASK " + task + "\n";

        return result;
    }

    // STATIC METHODS FOR PERSISTENCE
    public static void saveNewAssignment(Assignment assignment) {
        String assignmentInsert = "INSERT INTO Catering.TasksAssignment(staff_id, shift_id, task) VALUES (" +
                assignment.staffMember.getId() + "," + assignment.shift.getId() + ", '" + assignment.getTask() + "' )";
        PersistenceManager.executeUpdate(assignmentInsert);
        assignment.id = PersistenceManager.getLastId();

        StaffMember.changeAvailability(assignment.staffMember, assignment.shift.getId());
    }
}
