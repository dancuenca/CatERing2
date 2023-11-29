package businesslogic;

import businesslogic.event.EventManager;
import businesslogic.menu.Menu;
import businesslogic.menu.MenuManager;
import businesslogic.recipe.RecipeManager;
import businesslogic.shift.ShiftManager;
import businesslogic.user.UserManager;
import persistence.EventPersistence;
import persistence.MenuPersistence;
import persistence.PersistenceManager;
import persistence.ShiftPersistence;

public class CatERing {
    private static CatERing singleInstance;

    public static CatERing getInstance() {
        if (singleInstance == null) {
            singleInstance = new CatERing();
        }
        return singleInstance;
    }

    private MenuManager menuMgr;
    private RecipeManager recipeMgr;
    private UserManager userMgr;
    private EventManager eventMgr;
    private ShiftManager shiftMgr;

    private MenuPersistence menuPersistence;
    private EventPersistence eventPersistence;
    private ShiftPersistence shiftPersistence;

    private CatERing() {
        menuMgr = new MenuManager();
        recipeMgr = new RecipeManager();
        userMgr = new UserManager();
        eventMgr = new EventManager();
        shiftMgr = new ShiftManager();

        menuPersistence = new MenuPersistence();
        eventPersistence = new EventPersistence();
        shiftPersistence = new ShiftPersistence();

        menuMgr.addEventReceiver(menuPersistence);
        eventMgr.addEventEventReceiver(eventPersistence);
        shiftMgr.addShiftEventReceiver(shiftPersistence);
    }


    public MenuManager getMenuManager() {
        return menuMgr;
    }

    public RecipeManager getRecipeManager() {
        return recipeMgr;
    }

    public UserManager getUserManager() {
        return userMgr;
    }

    public EventManager getEventManager() { return eventMgr; }

    public ShiftManager getShiftManager() { return shiftMgr; }

}
