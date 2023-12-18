import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.event.Event;
import businesslogic.event.Recurrence;

import java.util.ArrayList;

public class TestEvent2b {
    public static void main(String[] args) {
        try{
            System.out.println("TEST CHANGE RECURRENCE NUMBER OF INSTANCES");
            CatERing.getInstance().getUserManager().fakeLogin("Lidia");

            ArrayList<String> notes = new ArrayList<>();
            String note1 = "nota1 ex";
            notes.add(note1);
            String note2 = "nota2 ex";
            notes.add(note2);
            String note3 = "nota3 ex";
            notes.add(note3);

            Event ev = CatERing.getInstance().getEventManager().createEvent("Test change num instances", "Casa di mox", "12-12-2023", "01-01-2024", 69, "Dan Cuenca", notes);
            Recurrence rec = CatERing.getInstance().getEventManager().defineRecurrence(3, 3, "11-09-2001", ev);

            System.out.println(rec.getRecurrentEvents());

            CatERing.getInstance().getEventManager().changeRecurrenceNumInstances(rec, 5);
        } catch (UseCaseLogicException ex){
            System.out.println("Errore di logica nello use case (changeRecurrenceNumInstances)");
        }
    }
}
