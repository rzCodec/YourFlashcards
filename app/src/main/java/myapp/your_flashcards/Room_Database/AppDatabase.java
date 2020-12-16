package myapp.your_flashcards.Room_Database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import myapp.your_flashcards.Flashcard.Flashcard;
import myapp.your_flashcards.Subject.Subject;

/**
 * Created by User on 2018/06/12.
 */

@Database(entities = {Subject.class, Flashcard.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract Subject_Dao subjectDao();
    public abstract Flashcard_Dao flashcard_Dao();

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "subject-database")
                            // allow queries on the main thread.
                            // Don't do this on a real app! See PersistenceBasicSample for an example.
                            .fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}