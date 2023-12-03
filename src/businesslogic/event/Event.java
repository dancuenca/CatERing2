package businesslogic.event;

import businesslogic.CatERing;
import businesslogic.menu.Menu;
import businesslogic.menu.MenuItem;
import businesslogic.menu.Section;
import businesslogic.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.BatchUpdateHandler;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Event {
    private int id;
    private String title;
    private String client;
    private Date startDate;
    private Date endDate;
    private String location;
    private int numParticipants;
    private String[] notes;
    private String state;
    private Recurrence recurrence;

    private ArrayList<Service> services;

    private User organizer;


    public Event(User user, String title, String location, String startDate, String endDate, int numParticipants, String client, String[] notes){
        if(notes != null){
            this.notes = notes;
        }

        this.organizer = user;
        this.title = title;
        this.location = location;
        this.startDate = convertStringToDate(startDate);
        this.endDate = convertStringToDate(endDate);
        this.numParticipants = numParticipants;
        this.state = "waiting for menu";
        this.recurrence = null;
        this.client = client;

        services = new ArrayList<>();
    }

    public Event(User user, String title, String location, Date startDate, Date endDate, int numParticipants, String client, String[] notes){
        if(notes != null){
            this.notes = notes;
        }

        this.organizer = user;
        this.title = title;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numParticipants = numParticipants;
        this.state = "waiting for menu";
        this.recurrence = null;
        this.client = client;

        services = new ArrayList<>();
    }

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

    public String toString(){
        String result = "---> EVENT: \n";
        result += "title: " + title + "\n";
        result += "client: " + client + "\n";
        result += "dates: " + startDate + " - " + endDate + "\n";
        result += "location: " + location + "\n";
        result += "number of participants: " + numParticipants + "\n";
        result += "state: " + state + "\n";

        result += "\nnote: \n";
        for(int i= 0; i < notes.length; i++){
            result += (i+1) + ") " + notes[i] + "\n";
        }

        return result;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getClient() {
        return client;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getLocation() {
        return location;
    }

    public int getNumParticipants() {
        return numParticipants;
    }

    public String[] getNotes() {
        return notes;
    }

    public String getState() {
        return state;
    }

    public Recurrence getRecurrence() {
        return recurrence;
    }

    private int getIdRecurrence () {
        if(recurrence == null) {
            return 0;
        }
        else {
            return recurrence.getId();
        }
    }

    public void setRecurrence(Recurrence recurrence) {
        this.recurrence = recurrence;
    }

    public ArrayList<Service> getServices() {
        return services;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Service addService(Event ev, Date date, String serviceType, String startTime, String endTime) {
        Service service = new Service(ev, date, serviceType, startTime, endTime);
        this.services.add(service);
        return service;
    }

    // STATIC METHODS FOR PERSISTENCE
    public static void saveNewEvent(Event ev){
        String eventInsert = "INSERT INTO Catering.EventsCatering (title, start_date, end_date, location, num_participants, recurrence_id, client) VALUES (?, ?, ?, ?, ?, ?, ?);";
        int[] result = PersistenceManager.executeBatchUpdate(eventInsert, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setString(1, PersistenceManager.escapeString(ev.title));
                ps.setDate(2, new java.sql.Date(ev.startDate.getTime()));
                ps.setDate(3, new java.sql.Date(ev.endDate.getTime()));
                ps.setString(4, PersistenceManager.escapeString(ev.location));
                ps.setInt(5, ev.numParticipants);
                ps.setInt(6, ev.getIdRecurrence());
                ps.setString(7, PersistenceManager.escapeString(ev.client));
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // should be only one
                if (count == 0) {
                    ev.id = rs.getInt(1);
                }
            }
        });

        if (result[0] > 0) { // menu effettivamente inserito
            // salva le note
             notesToDB(ev);
        }
    }

    public static void deleteEvent(Event ev){
        //del services
        String delServ = "DELETE FROM catering.servicescatering WHERE event_id = " + ev.id;
        PersistenceManager.executeUpdate(delServ);

        //del shifts
        for(Service serv: ev.services){
            String delShift = "DELETE FROM catering.shiftscatering WHERE service_id = " + serv.getId();
            PersistenceManager.executeUpdate(delShift);
        }

        //del notes
        String delNote = "DELETE FROM catering.notescatering WHERE event_id = " + ev.id;
        PersistenceManager.executeUpdate(delNote);

        //TODO: eliminare staff member e assignments

        String delEv = "DELETE FROM catering.eventscatering WHERE id = " + ev.id;
        PersistenceManager.executeUpdate(delEv);
    }

    private static void notesToDB(Event ev) {
        String featureInsert = "INSERT INTO catering.NotesCatering (event_id, note) VALUES (?, ?)";
        PersistenceManager.executeBatchUpdate(featureInsert, ev.getNotes().length, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, ev.id);
                ps.setString(2, PersistenceManager.escapeString(ev.getNotes()[batchCount]));
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // non ci sono id autogenerati in MenuFeatures
            }
        });
    }

    public static void saveAllNewRecurrentEvents(Recurrence rec, ArrayList<Event> recurrentEvents) {
        String recEventsInsert = "INSERT INTO catering.EventsCatering (title, start_date, end_date, location, num_participants, recurrence_id, client) VALUES (?, ?, ?, ?, ?, ?, ?);";
        PersistenceManager.executeBatchUpdate(recEventsInsert, recurrentEvents.size(), new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setString(1, PersistenceManager.escapeString(rec.getMainEvent().getTitle()));
                ps.setDate(2, new java.sql.Date(recurrentEvents.get(batchCount).startDate.getTime()));
                ps.setDate(3, new java.sql.Date(recurrentEvents.get(batchCount).endDate.getTime()));
                ps.setString(4, PersistenceManager.escapeString(rec.getMainEvent().getLocation()));
                ps.setInt(5, rec.getMainEvent().getNumParticipants());
                ps.setInt(6, rec.getId());
                ps.setString(7, PersistenceManager.escapeString(rec.getMainEvent().getClient()));
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                recurrentEvents.get(count).id = rs.getInt(1);
            }
        });
    }

    public static ObservableList<Event> loadAllEventInfo(){
        ObservableList<Event> all = FXCollections.observableArrayList();
        String query = "SELECT * FROM catering.eventscatering WHERE true";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                //TODO: selezionare solo gli eventi che appartengono a current user
                User user = CatERing.getInstance().getUserManager().getCurrentUser();
                /*Event ev = new Event(user,
                        rs.getString("title"),
                        formatDatetoString(rs.getDate("start_date")),
                        formatDatetoString(rs.getDate("end_date")),
                        rs.getString("location"),
                        rs.getInt("num_participants"),
                        rs.getInt("recurrence_id"),
                        rs.getString("client")
                );*/
            }
        });

        return all;
    }

    private static String formatDatetoString(Date data) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String dataFormattata = sdf.format(data);
        return dataFormattata;
    }
}
