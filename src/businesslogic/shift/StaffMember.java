package businesslogic.shift;

public class StaffMember {
    private int id;
    private String name;
    private boolean available;

    public StaffMember(int id, String name){
        this.id = id;
        this.name = name;
        this.available = true;
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String toString(){
        String result = "---> STAFF MEMBER: \n";
        result += "id: " + id + "\n";
        result += "name: " + name + "\n";

        return result;
    }
}
