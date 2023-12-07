package businesslogic.event;

import businesslogic.menu.Chef;
import businesslogic.shift.Shift;
import businesslogic.shift.StaffMember;
import businesslogic.user.User;
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
    private ArrayList<String> notes;
    private String state;
    private Recurrence recurrence;
    private Chef chef;

    private ArrayList<Service> services;

    private User organizer;


    public Event(User user, String title, String location, String startDate, String endDate, int numParticipants, String client, ArrayList<String> notes){
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

        this.services = new ArrayList<>();

    }

    public Event(User user, String title, String location, Date startDate, Date endDate, int numParticipants, String client, ArrayList<String> notes){
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

    public Event(){}

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
        result += "id: " + id + "\n";
/*
        result += "\nnote: \n";
        for(int i = 0; i < notes.length; i++){
            result += (i+1) + ") " + notes[i] + "\n";
        }
*/
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

    public ArrayList<String> getNotes() {
        return notes;
    }

    public String getState() {
        return state;
    }

    public Recurrence getRecurrence() {
        return recurrence;
    }

    public int getIdRecurrence () {
        if(recurrence == null) {
            return 0;
        }
        else {
            return recurrence.getId();
        }
    }

    public Chef getChef(){
        return chef;
    }

    public void setRecurrence(Recurrence recurrence) {
        this.recurrence = recurrence;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setNumParticipants(int numParticipants) {
        this.numParticipants = numParticipants;
    }

    public void setNotes(ArrayList<String> notes) {
        this.notes = notes;
    }

    public void addNote(String note){
        this.notes.add(note);
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

    public void setChef(Chef chef){
        this.chef = chef;
    }

    public Service addService(Event ev, Date date, String serviceType, String startTime, String endTime) {
        Service service = new Service(ev, date, serviceType, startTime, endTime);
        this.services.add(service);
        return service;
    }

    // STATIC METHODS FOR PERSISTENCE
    public static void saveNewEvent(Event ev){
        String eventInsert = "INSERT INTO Catering.EventsCatering (title, start_date, end_date, location, num_participants, recurrence_id, client, organizer_id, state) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
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
                ps.setInt(8, ev.organizer.getId());
                ps.setString(9, PersistenceManager.escapeString(ev.state));
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // should be only one
                if (count == 0) {
                    ev.id = rs.getInt(1);
                }
            }
        });

        if (result[0] > 0) {
            // salva le note
             notesToDB(ev);
        }
    }

    public static void deleteEvent(Event ev, boolean spread){
        if(spread == true && ev.getRecurrence() != null){
            Recurrence rec = ev.getRecurrence();
            for(Event recEv: rec.getRecurrentEvents()){
                deleteSingleEvent(recEv);
            }

            String delRec = "DELETE FROM catering.recurrencecatering WHERE id = " + ev.getRecurrence().getId();
            PersistenceManager.executeUpdate(delRec);
        }

        deleteSingleEvent(ev);
    }

    private static void deleteSingleEvent(Event ev){
        for(Service serv: ev.getServices()){
            for(Shift shift: serv.getShifts()){
                for(StaffMember sm: shift.getAvailableStaffMems()){
                    //del staff members
                    String delSm = "DELETE FROM catering.staffmembercatering WHERE shift_id = " + shift.getId();
                    PersistenceManager.executeUpdate(delSm);
                }

                //del tasks
                String delAssignment = "DELETE FROM catering.tasksassignment WHERE shift_id = " + shift.getId();
                PersistenceManager.executeUpdate(delAssignment);
            }

            //del shifts
            String delShift = "DELETE FROM catering.shiftscatering WHERE service_id = " + serv.getId();
            PersistenceManager.executeUpdate(delShift);
        }

        //del services
        String delServ = "DELETE FROM catering.servicescatering WHERE event_id = " + ev.id;
        PersistenceManager.executeUpdate(delServ);

        //del notes
        String delNote = "DELETE FROM catering.notescatering WHERE event_id = " + ev.id;
        PersistenceManager.executeUpdate(delNote);

        String delEv = "DELETE FROM catering.eventscatering WHERE id = " + ev.id;
        PersistenceManager.executeUpdate(delEv);
    }

    public static void notesToDB(Event ev) {
        String featureInsert = "INSERT INTO catering.NotesCatering (event_id, note) VALUES (?, ?)";
        PersistenceManager.executeBatchUpdate(featureInsert, ev.getNotes().size(), new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, ev.id);
                ps.setString(2, PersistenceManager.escapeString(ev.getNotes().get(batchCount)));
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {}
        });
    }

    public static void saveAllNewRecurrentEvents(Recurrence rec, ArrayList<Event> recurrentEvents) {
        String recEventsInsert = "INSERT INTO catering.EventsCatering (title, start_date, end_date, location, num_participants, recurrence_id, client, organizer_id, state) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
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
                ps.setInt(8, rec.getMainEvent().getOrganizer().getId());
                ps.setString(9, PersistenceManager.escapeString(rec.getMainEvent().getState()));
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                recurrentEvents.get(count).id = rs.getInt(1);
            }
        });

        String updateMainEvent = "UPDATE catering.eventscatering SET recurrence_id = ' " + rec.getId() + "' " + "WHERE id = " + rec.getMainEvent().getId();
        PersistenceManager.executeUpdate(updateMainEvent);
    }

    public static ArrayList<Event> loadAllEventInfo(User organizer){
        ArrayList<Event> all = new ArrayList<>();
        String query = "SELECT * FROM catering.eventscatering WHERE organizer_id = " + organizer.getId();
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                Event ev = new Event();
                ev.organizer = organizer;
                ev.title = rs.getString("title");
                ev.startDate = rs.getDate("start_date");
                ev.endDate = rs.getDate("end_date");
                ev.location = rs.getString("location");
                ev.numParticipants = rs.getInt("num_participants");
                ev.client = rs.getString("client");
                ev.id = rs.getInt("id");
                all.add(ev);
            }
        });

        return all;
    }

    public static void saveEventTitle(Event ev) {
        String titleUpdate = "UPDATE catering.eventscatering SET title = '" + PersistenceManager.escapeString(ev.title) + "' " + "WHERE id = " + ev.id;
        PersistenceManager.executeUpdate(titleUpdate);
    }

    public static void saveEventStartDate(Event ev){
        String startDateUpdate = "UPDATE catering.eventscatering SET start_date = '" + new java.sql.Date(ev.startDate.getTime()) + "' " + "WHERE id = " + ev.id;
        PersistenceManager.executeUpdate(startDateUpdate);
    }

    public static void saveEventEndDate(Event ev){
        String endDateUpdate = "UPDATE catering.eventscatering SET end_date = '" + new java.sql.Date(ev.endDate.getTime()) + "' " + "WHERE id = " + ev.id;
        PersistenceManager.executeUpdate(endDateUpdate);
    }

    public static void saveEventLocation(Event ev){
        String locationUpdate = "UPDATE catering.eventscatering SET location = '" + ev.location + "' " + "WHERE id = " + ev.id;
        PersistenceManager.executeUpdate(locationUpdate);
    }

    public static void saveEventNumParticipants(Event ev){
        String numParticipantsUpdate = "UPDATE catering.eventscatering SET num_participants = '" + ev.numParticipants + "' " + "WHERE id = " + ev.id;
        PersistenceManager.executeUpdate(numParticipantsUpdate);
    }

    public static void saveChefAssigned(Event ev){
        String chefAssignedUpdate = "UPDATE catering.eventscatering SET chef_id = '" + ev.chef.getId() + "' " + "WHERE id = " + ev.id;
        PersistenceManager.executeUpdate(chefAssignedUpdate);
    }

    public static void saveNewEventState(String newState){
        String stateUpdate = "UPDATE catering.eventscatering SET ";
    }

}
