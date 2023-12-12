import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.event.Event;
import businesslogic.event.Recurrence;

import java.util.ArrayList;

public class TestEvent1a {
    public static void main(String args[]){
         /*   try{
            System.out.println("TEST CANCEL EVENT (spread = false)");
            CatERing.getInstance().getUserManager().fakeLogin("Lidia");
            System.out.println(CatERing.getInstance().getUserManager().getCurrentUser());

            ArrayList<String> notes = new ArrayList<>();
            String note1 = "Portare droga";
            notes.add(note1);
            String note2 = "Portare alcol";
            notes.add(note2);
            String note3 = "Portare bitches";
            notes.add(note3);

            Event ev = CatERing.getInstance().getEventManager().createEvent("Test cancel", "Casa di mox", "12-12-2023", "01-01-2024", 69, "Dan Cuenca", notes);
            System.out.println(ev);

            CatERing.getInstance().getEventManager().cancelEvent(ev, false);
        } catch(UseCaseLogicException ex){
            System.out.println("Errore di logica nello use case (cancelEvent)");
        }
        */
        try{
            System.out.println("TEST CANCEL EVENT (spread = true)");
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

            System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
            System.out.println("rec ev id: " + rec.getRecurrentEvents().get(1).getId());
            System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");

            CatERing.getInstance().getEventManager().cancelEvent(rec.getRecurrentEvents().get(1), false);

            System.out.println("rec ev state: " + rec.getRecurrentEvents().get(1).getState());
        } catch (UseCaseLogicException ex){
            System.out.println("Errore di logica nello use case (cancelEvent)");
        }
    }
}
