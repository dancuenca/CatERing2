import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.event.Assignment;
import businesslogic.event.Event;
import businesslogic.event.Recurrence;
import businesslogic.event.Service;
import businesslogic.shift.Shift;
import businesslogic.shift.StaffMember;

import java.util.ArrayList;

public class TestEvent1b {
    public static void main(String[] args) {
        try{
            System.out.println("TEST DELETE EVENT (spread = false)");
            CatERing.getInstance().getUserManager().fakeLogin("Lidia");
            System.out.println(CatERing.getInstance().getUserManager().getCurrentUser());

            ArrayList<String> notes = new ArrayList<>();
            String note1 = "Portare droga";
            notes.add(note1);
            String note2 = "Portare alcol";
            notes.add(note2);
            String note3 = "Portare bitches";
            notes.add(note3);

            Event ev = CatERing.getInstance().getEventManager().createEvent("Cena capodanno", "Casa di mox", "31-12-2023", "01-01-2024", 69, "Dan Cuenca", notes);
            System.out.println(ev.toString());

            Service serv = CatERing.getInstance().getEventManager().insertService(ev, ev.getStartDate(), "Colazione Swag", "05:00", "10:00", false);
            ArrayList<Shift> shifts = CatERing.getInstance().getShiftManager().createAllShifts(serv, serv.getStartTime(), serv.getEndTime(), 3);
            ArrayList<StaffMember> availableStaffMembers = CatERing.getInstance().getEventManager().getStaffMembers();
            Assignment assignment = CatERing.getInstance().getEventManager().defineAssignment(serv, availableStaffMembers.get(0), shifts.get(2), "servire vini");

            System.out.println(assignment);

            CatERing.getInstance().getEventManager().deleteEvent(ev, false);
        } catch (UseCaseLogicException ex){
            System.out.println("Errore di logica nello use case (deleteEvent)");
        }

        try{
            System.out.println("TEST DELETE EVENT (spread = true)");
            CatERing.getInstance().getUserManager().fakeLogin("Lidia");
            System.out.println(CatERing.getInstance().getUserManager().getCurrentUser());

            ArrayList<String> notes = new ArrayList<>();
            String note1 = "Portare droga";
            notes.add(note1);
            String note2 = "Portare alcol";
            notes.add(note2);
            String note3 = "Portare bitches";
            notes.add(note3);

            Event ev = CatERing.getInstance().getEventManager().createEvent("Cena capodanno", "Casa di mox", "31-12-2023", "01-01-2024", 69, "Dan Cuenca", notes);
            System.out.println(ev.toString());

            Recurrence rec = CatERing.getInstance().getEventManager().defineRecurrence(3, 3, "11-09-2001", ev);
            System.out.println(rec.getRecurrentEvents());

            Service serv = CatERing.getInstance().getEventManager().insertService(ev, ev.getStartDate(), "Colazione Swag", "05:00", "10:00", false);
            ArrayList<Shift> shifts = CatERing.getInstance().getShiftManager().createAllShifts(serv, serv.getStartTime(), serv.getEndTime(), 3);
            ArrayList<StaffMember> availableStaffMembers = CatERing.getInstance().getEventManager().getStaffMembers();
            Assignment assignment = CatERing.getInstance().getEventManager().defineAssignment(serv, availableStaffMembers.get(0), shifts.get(2), "servire vini");

            System.out.println(assignment);

            CatERing.getInstance().getEventManager().deleteEvent(ev, true);
        } catch (UseCaseLogicException ex){
            System.out.println("Errore di logica nello use case (deleteEvent)");
        }


    }
}
