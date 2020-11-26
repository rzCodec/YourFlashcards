package myapp.your_flashcards;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;

/**
 * Created by User on 2018/07/02.
 */

public class CustomMenuMaker {
    private AppCompatActivity activityInstance;
    private Context context;
    private View view;
    private int menuID;
    private String menuTitle;

    public CustomMenuMaker(AppCompatActivity activityInstance,
                             Context context,
                             View view,
                             int menuID,
                             String menuTitle){
        this.activityInstance = activityInstance;
        this.context = context;
        this.view = view;
        this.menuID = menuID;
        this.menuTitle = menuTitle;
    }

    private void setup(){
        activityInstance.registerForContextMenu(view);
        activityInstance.openContextMenu(view);
        activityInstance.unregisterForContextMenu(view);
    }

    private void createContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        activityInstance.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(menuTitle);
        MenuInflater inflater = activityInstance.getMenuInflater();
        inflater.inflate(menuID, menu);
    }


}
