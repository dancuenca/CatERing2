package businesslogic.event;

import persistence.BatchUpdateHandler;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public Recurrence(int frequence, int numInstances, String endDate, Event mainEvent){
        this.frequence = frequence;
        this.numInstances = numInstances;
        this.startDate = mainEvent.getStartDate();
        this.endDate = convertStringToDate(endDate);
        this.mainEvent = mainEvent;

        this.recurrentEvents = new ArrayList<>();

        populateRecEventsList(mainEvent);
    }

    public Recurrence(){}

    private static Date convertStringToDate(String dateString){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        try{
            Date result = sdf.parse(dateString);
            return result;
        } catch (ParseException e){
            e.printStackTrace();
            return null;
        }
    }

    //TODO: crea metodo per trovare numIstances da endDate

    private void populateRecEventsList(Event mainEvent){
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

    public static Date changeDateByAddDays(Date initialDate, int numDays){
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

    public Event getMainEvent(){
        return mainEvent;
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

    // STATIC METHODS FOR PERSISTENCE
    public static void saveNewRecurrence(Recurrence rec) {
        String recurrenceInsert = "INSERT INTO Catering.RecurrenceCatering (num_instances, frequence, start_date, end_date, main_event_id) VALUES (?, ?, ?, ?, ?);";
        int[] result = PersistenceManager.executeBatchUpdate(recurrenceInsert, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, rec.numInstances);
                ps.setInt(2, rec.frequence);
                ps.setDate(3, new java.sql.Date(rec.startDate.getTime()));
                ps.setDate(4, new java.sql.Date(rec.endDate.getTime()));
                ps.setInt(5, rec.mainEvent.getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                if(count == 0){
                    rec.id = rs.getInt(1);
                }
            }
        });

        if(result[0] > 0){
            //salva gli eventi figli che appartengono alla ricorrenza
            if(rec.getRecurrentEvents().size() > 0){
                Event.saveAllNewRecurrentEvents(rec, rec.recurrentEvents);
            }
        }
    }

    public static Recurrence loadRecurrenceById(int rid){
        Recurrence load = new Recurrence();
        String recurrenceQuery = "SELECT * FROM catering.recurrencecatering WHERE id = " + rid;
        PersistenceManager.executeQuery(recurrenceQuery, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                load.id = rs.getInt("id");
                load.numInstances = rs.getInt("num_instances");
                load.frequence = rs.getInt("frequence");
                load.startDate = rs.getDate("start_date");
                load.endDate = rs.getDate("end_date");
                load.mainEvent = Event.loadEventById(rs.getInt("main_event_id"));
            }
        });

        return load;
    }
}
