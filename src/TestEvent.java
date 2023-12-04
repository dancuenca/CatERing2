import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.event.Assignment;
import businesslogic.event.Event;
import businesslogic.event.Recurrence;
import businesslogic.event.Service;
import businesslogic.menu.Menu;
import businesslogic.shift.Shift;
import businesslogic.shift.StaffMember;
import persistence.PersistenceManager;

import java.util.ArrayList;

public class TestEvent {
    public static void main(String[] args) {
        try{
            System.out.println("TEST DATABASE CONNECTION");
            PersistenceManager.testSQLConnection();
            System.out.println("TEST FAKE LOGIN");
            CatERing.getInstance().getUserManager().fakeLogin("Lidia");
            System.out.println(CatERing.getInstance().getUserManager().getCurrentUser());

            System.out.println("\nTEST CREATE EVENT");
            Event ev = CatERing.getInstance().getEventManager().createEvent("Ex titolo", "Ex location", "11-09-2001", "10-11-2023", 69, "Daniele Rossi", new String[]{"nota1", "nota2", "nota3"});
            System.out.println(ev.toString());

            System.out.println("\nTEST CREATE RECURRENCE");
            Recurrence rec = CatERing.getInstance().getEventManager().defineRecurrence(3, 3, "11-09-2001", ev);
            System.out.println(rec.getRecurrentEvents());

            System.out.println("\nTEST GET EVENT INFO");
            ArrayList<Event> events = CatERing.getInstance().getEventManager().getEventList();
            System.out.println(events);

            System.out.println("\nTEST INSERT SERVICE");
            Menu m = CatERing.getInstance().getMenuManager().createMenu("Menu Mia Dan");
            Service serv = CatERing.getInstance().getEventManager().insertService(ev, ev.getStartDate(), "Cena", "05:00", "10:00", false);
            serv.setMenu(m);

            System.out.println(ev.getServices());

            System.out.println("\nTEST CREATE SHIFTS");
            ArrayList<Shift> shifts = CatERing.getInstance().getShiftManager().createAllShifts(serv, serv.getStartTime(), serv.getEndTime(), 3);

            System.out.println("\nTEST UPDATE EVENT");
            CatERing.getInstance().getEventManager().changeEventTitle("modifica titolo evento");
            System.out.println("ev id: " + ev.getId());
            System.out.println(ev);

            System.out.println(serv.getShifts());
            System.out.println("\nTEST ASSEGNAMENTO COMPITO A MEMBRO PERSONALE");
            ArrayList<StaffMember> availableStaffMembers = CatERing.getInstance().getEventManager().getStaffMembers();
            Assignment assignment = CatERing.getInstance().getEventManager().defineAssignment(serv, availableStaffMembers.get(0), shifts.get(2), "servire vini");

            System.out.println(assignment);

        } catch (UseCaseLogicException ex) {
            System.out.println("Errore di logica nello use case");
        }


    }
}
