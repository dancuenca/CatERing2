package businesslogic.event;

import businesslogic.menu.Chef;
import businesslogic.menu.Menu;
import businesslogic.shift.Shift;
import persistence.BatchUpdateHandler;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;

public class Service {
    private int id;
    private Event ev;
    private Date date;
    private String serviceType;
    private LocalTime startTime;
    private LocalTime endTime;
    private Menu menu;
    private boolean approvedMenu;

    private ArrayList<Shift> shifts;

    public Service(Event ev, Date date, String serviceType, String startTime, String endTime){
        this.ev = ev;
        this.date = date;
        this.serviceType = serviceType;
        this.startTime = convertStringInLocalTime(startTime);
        this.endTime = convertStringInLocalTime(endTime);
        this.menu = null;
        this.shifts = new ArrayList<>();

        //shifts = generateShifts(this, startTime, endTime, numShifts);
    }

    public Service(){}

    private static LocalTime convertStringInLocalTime(String timeString){
        return LocalTime.parse(timeString);
    }

    public int getId(){
        return id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getServiceType(){
        return serviceType;
    }

    public LocalTime getStartTime() { return startTime; }

    public LocalTime getEndTime() { return endTime; }

    public ArrayList<Shift> getShifts() {
        return shifts;
    }

    public Menu getMenu(){
        return menu;
    }

    public void setMenu(Menu menu){
        this.menu = menu;
    }

    public void setApproveMenu(){
        approvedMenu = true;
    }

    public boolean isApprovedMenu(){
        return approvedMenu;
    }

    public Event getBelongingEvent() {
        return ev;
    }

    public String toString(){
        String result = "---> SERVICE: \n";
        result += "event title: " + ev.getTitle() + "\n";
        result += "date: " + date + "\n";
        result += "service type: " + serviceType + "\n";
        result += "start time: " + startTime + "\n";
        result += "end time: " + endTime + "\n";
/*
        result += "\nshifts: \n";
        for(int i = 0; i < shifts.size(); i++){
            result += (i+1) + ") " + shifts.get(i) + "\n";
        }
*/
        result += "menu: " + menu + "\n";

        return result;
    }

    // STATIC METHODS FOR PERSISTENCE
    public static void saveNewService(Event ev, Service serv){
        String servInsert = "INSERT INTO Catering.ServicesCatering (event_id, service_type, start_time, end_time, menu_id, approved, date) VALUES (?, ?, ?, ?, ?, ?, ?);";
        int[] result = PersistenceManager.executeBatchUpdate(servInsert, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, ev.getId());
                ps.setString(2, PersistenceManager.escapeString(serv.serviceType));
                ps.setTime(3, Time.valueOf(serv.startTime));
                ps.setTime(4, Time.valueOf(serv.endTime));
                if(serv.menu != null) {
                    ps.setInt(5, serv.getMenu().getId());
                } else {
                     ps.setInt(5, 0);
                }
                ps.setBoolean(6, serv.approvedMenu);
                ps.setDate(7, new java.sql.Date(serv.date.getTime()));
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // should be only one
                if (count == 0) {
                    serv.id = rs.getInt(1);
                }
            }
        });
    }

    public static void saveServiceDeleted(Event ev, Service serv){
        String serviceDeleted = "DELETE FROM catering.servicescatering WHERE event_id = " + ev.getId()
                + " AND id = " + serv.id;
        PersistenceManager.executeUpdate((serviceDeleted));
    }

    public static void saveMenuSet(Service serv, Menu m){
        String serviceMenuUpdate = "UPDATE catering.servicescatering SET menu_id = '" + m.getId() + "' WHERE id = " + serv.getId();
        PersistenceManager.executeUpdate(serviceMenuUpdate);
    }

    public static void saveMenuApproved(Service serv){
        String menuApprovedUpdate = "UPDATE catering.servicescatering SET approved = 1 WHERE id = " + serv.getId();
        PersistenceManager.executeUpdate(menuApprovedUpdate);
    }

    public static Service loadServiceById(int sid){
        Service load = new Service();

        String serviceQuery = "SELECT * FROM catering.servicescatering WHERE id = " + sid;
        PersistenceManager.executeQuery(serviceQuery, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                load.id = rs.getInt("id");
                load.ev = Event.loadEventById(rs.getInt("event_id"));
                load.serviceType = rs.getString("service_type");
                Time startTimeTmp = rs.getTime("start_time");
                load.startTime = startTimeTmp.toLocalTime();
                Time endTimeTmp = rs.getTime("end_time");
                load.endTime = endTimeTmp.toLocalTime();
                load.menu = Menu.loadMenuById(rs.getInt("menu_id"));
                load.approvedMenu = rs.getBoolean("approved");
                load.date = rs.getDate("date");
            }
        });

        return load;
    }

}
