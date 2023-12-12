import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.event.Assignment;
import businesslogic.event.Event;
import businesslogic.event.Recurrence;
import businesslogic.event.Service;
import businesslogic.menu.Chef;
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
            Menu m = CatERing.getInstance().getMenuManager().createMenu("Menu Test Event");
            Service serv = CatERing.getInstance().getEventManager().insertService(ev, ev.getStartDate(), "Servizio Test Event", "05:00", "10:00", false);
            //serv.setMenu(m);
            CatERing.getInstance().getEventManager().setMenuForService(serv, m);

            System.out.println(ev.getServices());

            System.out.println("\nTEST CREATE SHIFTS");
            ArrayList<Shift> shifts = CatERing.getInstance().getShiftManager().createAllShifts(serv, serv.getStartTime(), serv.getEndTime(), 5);

            System.out.println("\nTEST UPDATE EVENT TITLE");
            CatERing.getInstance().getEventManager().changeEventTitle("modifica titolo evento", false);
            System.out.println("ev id: " + ev.getId());
            System.out.println(ev);

            System.out.println("\nTEST UPDATE RECURRENT EVENTS TITLES ");
            CatERing.getInstance().getEventManager().changeEventTitle("modifica titolo evento", true);
            for (Event recEv: rec.getRecurrentEvents() ){
                System.out.println(recEv.getTitle());
            }

            /*
            System.out.println("\nTEST UPDATE EVENT START-DATE");
            CatERing.getInstance().getEventManager().changeEventStartDate("13-11-2023", false);
            System.out.println("ev id: " + ev.getStartDate()); */

            System.out.println("\nTEST UPDATE RECURRENT EVENTS START-DATES");
            CatERing.getInstance().getEventManager().changeEventStartDate("13-11-2001", true);
            System.out.println("(MAIN EVENT) TITLE: " + ev.getTitle()  + "   START-DATE: " + ev.getStartDate());
            for (Event recEv: rec.getRecurrentEvents() ){
                System.out.println("TITLE: " + recEv.getTitle()  + "   START-DATE: " + recEv.getStartDate());
            }

            /*
            System.out.println("\nTEST UPDATE EVENT END-DATE");
            CatERing.getInstance().getEventManager().changeEventEndDate("13-11-2023", false);
            System.out.println("ev id: " + ev.getStartDate());
             */

            System.out.println("\nTEST UPDATE RECURRENT EVENTS END-DATE");
            CatERing.getInstance().getEventManager().changeEventEndDate("13-11-2023", true);
            System.out.println("(MAIN EVENT) TITLE: " + ev.getTitle()  + "   END-DATE: " + ev.getEndDate());
            for (Event recEv: rec.getRecurrentEvents() ){
                System.out.println("TITLE: " + recEv.getTitle()  + "   END-DATE: " + recEv.getEndDate());
            }

            /*
            System.out.println("\nTEST UPDATE EVENT LOCATION");
            CatERing.getInstance().getEventManager().changeEventLocation("modifica location", false);
            System.out.println("ev id: " + ev.getId()); */

            System.out.println("\nTEST UPDATE RECURRENT EVENTS LOCATION");

            CatERing.getInstance().getEventManager().changeEventLocation("modifica location", true);
            System.out.println("(MAIN EVENT) TITLE: " + ev.getTitle()  + "   LOCATION: " + ev.getLocation());
            for (Event recEv: rec.getRecurrentEvents() ){
                System.out.println("TITLE: " + recEv.getTitle()  + "   LOCATION: " + recEv.getLocation());
            }

            /*
            System.out.println("\nTEST UPDATE EVENT NUM PARTICIPANTS");
            CatERing.getInstance().getEventManager().changeEventNumParticipants(420);
            System.out.println("ev id: " + ev.getId());
            */

            System.out.println("\nTEST UPDATE EVENT NUM PARTICIPANTS");
            CatERing.getInstance().getEventManager().changeEventNumParticipants(420, true);
            System.out.println("(MAIN EVENT) TITLE: " + ev.getTitle()  + "   PARTICIPANTS: " + ev.getNumParticipants());
            for (Event recEv: rec.getRecurrentEvents() ){
                System.out.println("TITLE: " + recEv.getTitle()  + "   PARTICIPANTS: " + recEv.getNumParticipants());
            }

            System.out.println("\nTEST UPDATE EVENT ADD NOTES");
            CatERing.getInstance().getEventManager().addNoteToEvent("aggiunta nota");

            System.out.println("\nTEST ASSIGN CHEF");
            ArrayList<Chef> availableChef = CatERing.getInstance().getEventManager().getChefs();
            CatERing.getInstance().getEventManager().assignChef(availableChef.get(0), true);

            System.out.println(availableChef);
            System.out.println("assigned chef: " + ev.getChef());


            System.out.println(serv.getShifts());
            System.out.println("\nTEST ASSIGN TASK TO STAFF MEMBER");
            ArrayList<StaffMember> availableStaffMembers = CatERing.getInstance().getEventManager().getStaffMembers();
            Assignment assignment = CatERing.getInstance().getEventManager().defineAssignment(serv, availableStaffMembers.get(0), shifts.get(2), "servire vini");

            System.out.println(assignment);

            System.out.println("\nTEST APPROVE MENU");
            ArrayList<Menu> publishedMenus = CatERing.getInstance().getMenuManager().getAllPublishedMenus();
            System.out.println(publishedMenus);
            CatERing.getInstance().getMenuManager().publish();
            CatERing.getInstance().getEventManager().approveMenu(ev.getServices().get(0));

            System.out.println("\nTEST CHECK SHIFTS");
            System.out.println("ev id: " + ev.getId());
            ArrayList<Shift> shiftsList = CatERing.getInstance().getEventManager().getEventShifts(ev);

            System.out.println(shiftsList);

        } catch (UseCaseLogicException ex) {
            System.out.println("Errore di logica nello use case");
        }


    }
}
