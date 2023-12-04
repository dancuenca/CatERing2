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
            ArrayList<String> notes = new ArrayList<>();
            String note1 = "nota1";
            notes.add(note1);
            String note2 = "note2";
            notes.add(note2);
            String note3 = "note3";
            notes.add(note3);
            Event ev = CatERing.getInstance().getEventManager().createEvent("Ex titolo", "Ex location", "11-09-2001", "10-11-2023", 69, "Daniele Rossi", notes);
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

            System.out.println("\nTEST UPDATE EVENT TITLE");
            CatERing.getInstance().getEventManager().changeEventTitle("modifica titolo evento");
            System.out.println("ev id: " + ev.getId());
            System.out.println(ev);

            System.out.println("\nTEST UPDATE EVENT START DATE");
            CatERing.getInstance().getEventManager().changeEventStartDate("07-07-1997");
            System.out.println("ev id: " + ev.getId());

            System.out.println("\nTEST UPDATE EVENT END DATE");
            CatERing.getInstance().getEventManager().changeEventEndDate("07-07-1997");
            System.out.println("ev id: " + ev.getId());

            System.out.println("\nTEST UPDATE EVENT LOCATION");
            CatERing.getInstance().getEventManager().changeEventLocation("modifica location");
            System.out.println("ev id: " + ev.getId());

            System.out.println("\nTEST UPDATE EVENT NUM PARTICIPANTS");
            CatERing.getInstance().getEventManager().changeEventNumParticipants(420);
            System.out.println("ev id: " + ev.getId());

            System.out.println("\nTEST UPDATE EVENT ADD NOTES");
            CatERing.getInstance().getEventManager().addNoteToEvent("aggiunta nota");

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
