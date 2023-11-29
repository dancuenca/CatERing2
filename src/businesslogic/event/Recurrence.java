package businesslogic.event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Recurrence {
    private int id;
    private int numInstances;
    private int frequence;
    private Date startDate;
    private Date endDate;
    private Event mainEvent;

    private ArrayList<Event> recurrentEvents;

    public Recurrence(int frequence, int numInstances, Date endDate, Event mainEvent){
        this.frequence = frequence;
        this.numInstances = numInstances;
        this.endDate = endDate;
        this.mainEvent = mainEvent;

        this.recurrentEvents = new ArrayList<>();

        populateRecEventsList(mainEvent);
    }

    //TODO: crea metodo per trovare numIstances da endDate

    private void populateRecEventsList(Event mainEvent){
        mainEvent.setRecurrence(this);
        //TODO: vedere come gestire le date
        for(int i = 1; i <= this.numInstances; i++){

            Event recEv = new Event(mainEvent.getOrganizer(), mainEvent.getTitle(), mainEvent.getLocation(),
                    changeDateByAddDays(mainEvent.getStartDate(), this.frequence * i),
                    changeDateByAddDays(mainEvent.getEndDate(), this.frequence * i),
                    mainEvent.getNumParticipants(), mainEvent.getClient(), mainEvent.getNotes());

            recEv.setRecurrence(this);
            ArrayList<Service> services = mainEvent.getServices();

            for (Service serv: services) {
                serv.setDate(changeDateByAddDays(serv.getDate(), this.frequence * i));
            }
            this.recurrentEvents.add(recEv);
        }
    }

    private static Date changeDateByAddDays(Date initialDate, int numDays){
        Calendar cal = Calendar.getInstance();
        cal.setTime(initialDate);

        cal.add(Calendar.DAY_OF_MONTH, numDays);

        return cal.getTime();
    }

    public ArrayList<Event> getRecurrentEvents() {
        return recurrentEvents;
    }

    public void setRecurrentEvents(ArrayList<Event> recurrentEvents) {
        this.recurrentEvents = recurrentEvents;
    }

    public int getId(){
        return id;
    }

    public String toString(){
        String result = "---> RECURRENCE: \n";
        result += "frequence: " + frequence + "\n";
        result += "number of instances: " + numInstances + "\n";
        result += "end date: " + endDate + "\n";

        result += "\nrecurrent events:\n";
        for(int i = 0; i < recurrentEvents.size(); i++){
            result += (i+1) + ") " + recurrentEvents.get(i) + "\n";
        }

        return result;
    }
}
