import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.event.Event;
import businesslogic.event.Service;
import businesslogic.menu.Menu;

public class TestEvent1b {
    public static void main(String[] args) {
        try{
            System.out.println("TEST DELETE EVENT");
            CatERing.getInstance().getUserManager().fakeLogin("Lidia");
            System.out.println(CatERing.getInstance().getUserManager().getCurrentUser());

            Event ev = CatERing.getInstance().getEventManager().createEvent("Cena capodanno", "Casa di mox", "31-12-2023", "01-01-2024", 69, "Dan Cuenca", new String[]{"Portare droga", "Portare alcol", "Portare bitches"});
            System.out.println(ev.toString());

            Service serv = CatERing.getInstance().getEventManager().insertService(ev, ev.getStartDate(), "Colazione Swag", "05:00", "10:00", false);
            CatERing.getInstance().getShiftManager().createAllShifts(serv, serv.getStartTime(), serv.getEndTime(), 3);

            System.out.println(ev.getServices());
            System.out.println("--> SHIFTS:");
            System.out.println(ev.getServices().get(0).getShifts());

            CatERing.getInstance().getEventManager().deleteEvent(ev);
        } catch (UseCaseLogicException ex){
            System.out.println("Errore di logica nello use case (deleteEvent)");
        }
    }
}
