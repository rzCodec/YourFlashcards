package myapp.your_flashcards.Utilities;

import android.content.Context;
import android.widget.Toast;

public class UtilitiesMessage {
    public static void showToastMessage(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
