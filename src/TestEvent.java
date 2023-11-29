import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.event.Event;
import businesslogic.event.Recurrence;
import businesslogic.event.Service;
import businesslogic.menu.Menu;
import businesslogic.shift.Shift;
import persistence.PersistenceManager;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.jar.JarOutputStream;

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

            System.out.println("%%%%%%%%%%%%%%%%%%%%%");

            System.out.println("\nTEST CREATE RECURRENCE");
            Recurrence rec = CatERing.getInstance().getEventManager().defineRecurrence(3, 3, "11-09-2001", ev);
            System.out.println(rec.getRecurrentEvents());

            System.out.println("%%%%%%%%%%%%%%%%%%%%%");

            System.out.println("\nTEST INSERT SERVICE");
            Menu m = CatERing.getInstance().getMenuManager().createMenu("Menu Mia Dan");
            Service serv = CatERing.getInstance().getEventManager().insertService(ev, ev.getStartDate(), "Cena", "05:00", "10:00", false);
            serv.setMenu(m);

            System.out.println(ev.getServices());

            System.out.println("\nTEST CREATE SHIFTS");
            ArrayList<Shift> shifts = CatERing.getInstance().getShiftManager().createAllShifts(serv, serv.getStartTime(), serv.getEndTime(), 3);

            System.out.println(serv.getShifts());

        } catch (UseCaseLogicException ex) {
            System.out.println("Errore di logica nello use case");
        }


    }
}
