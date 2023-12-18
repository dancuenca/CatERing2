import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.event.Event;
import businesslogic.event.Service;
import businesslogic.menu.Menu;

import java.util.ArrayList;

public class TestEvent4b {
    public static void main(String[] args) {
        try{
            System.out.println("TEST DELETE SERVICE");
            CatERing.getInstance().getUserManager().fakeLogin("Lidia");

            ArrayList<String> notes = new ArrayList<>();
            String note1 = "nota1 ex";
            notes.add(note1);
            String note2 = "nota2 ex";
            notes.add(note2);
            String note3 = "nota3 ex";
            notes.add(note3);

            Event ev = CatERing.getInstance().getEventManager().createEvent("Test delete service", "Casa di mox", "12-12-2023", "01-01-2024", 69, "Dan Cuenca", notes);
            CatERing.getInstance().getEventManager().insertService(ev, ev.getStartDate(), "Servizio Test Event", "05:00", "10:00", false);
            Service serv2 = CatERing.getInstance().getEventManager().insertService(ev, ev.getStartDate(), "Servizio da cancellare", "05:00", "10:00", false);

            CatERing.getInstance().getEventManager().deleteService(ev, serv2, false);
        } catch (UseCaseLogicException ex){
            System.out.println("Errore di logica nello use case (deleteService)");
        }
    }
}
