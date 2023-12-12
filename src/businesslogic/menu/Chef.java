package businesslogic.menu;

import businesslogic.event.Event;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Chef {
    private int id;
    private String name;
    private int available;
    private int eventId;

    public Chef(){

    }

    public Chef(int id, String name){
        this.id = id;
        this.name = name;
        this.available = 1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String toString(){
        String result = "---> CHEF: \n";
        result += "id: " + id + "\n";
        result += "name: " + name + "\n";
        result += "available: " + available + "\n";
        result += "event id: " + eventId + "\n";

        return result;
    }

    // STATIC METHODS FOR PERSISTENCE
    public static ArrayList<Chef> loadAvailableChefs(){
        ArrayList<Chef> chefs = new ArrayList<>();
        String query = "SELECT * FROM catering.chefscatering WHERE availability = 1";
        PersistenceManager.executeQuery(query, new ResultHandler(){
            @Override
            public void handle(ResultSet rs) throws SQLException{
                Chef chef = new Chef();
                chef.id = rs.getInt("id");
                chef.name = rs.getString("name");
                chef.available = rs.getInt("availability");
                chef.eventId = rs.getInt("event_id");
                chefs.add(chef);
            }
        });

        return chefs;
    }

    public static void saveChefEvent(Event ev, Chef chef){
        chef.setEventId(ev.getId());
        String chefEventUpdate = "UPDATE catering.chefscatering SET event_id = '" + ev.getId() + "', " +
                "availability = 0 " +
                "WHERE id = " + chef.id;
        PersistenceManager.executeUpdate(chefEventUpdate);
    }

    public static Chef loadChefById(int cid){
        Chef load = new Chef();
        String chefQuery = "SELECT * FROM catering.chefscatering WHERE id = " + cid;
        PersistenceManager.executeQuery(chefQuery, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                load.id = rs.getInt("id");
                load.name = rs.getString("name");
                load.available = rs.getInt("availability");
                load.eventId = rs.getInt("event_id");
            }
        });

        return load;
    }
}
