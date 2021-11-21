package myapp.your_flashcards;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import java.util.ArrayList;
import myapp.your_flashcards.Flashcard.Flashcard;
import myapp.your_flashcards.Flashcard.Flashcard_Activity;
import myapp.your_flashcards.Room_Database.AppDatabase;
import myapp.your_flashcards.Utilities.UtilitiesMessage;
import needle.Needle;
import needle.UiRelatedTask;

public class CustomViewManager {
    private int layout;
    private Activity activity;
	public View customView;

    public CustomViewManager(Activity activity, int layout){
        this.activity = activity;
        this.layout = layout;
		customView = createCustomView();
    }

    private View createCustomView(){
        LayoutInflater layoutInflater = LayoutInflater.from(this.activity);
        View customView = layoutInflater.inflate(layout, null);
        return customView;
    }
}
