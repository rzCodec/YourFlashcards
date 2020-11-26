package myapp.your_flashcards.Room_Database;

import android.os.AsyncTask;
import androidx.annotation.NonNull;

import android.view.View;

import java.util.ArrayList;

import myapp.your_flashcards.Subject.Subject;

/**
 * Created by User on 2018/06/12.
 */


public class DatabaseInitializer {

    private View view;
    private Subject subject;
    private iReturnSubjects iReturnSubjects;
    private PopulateDbAsync task;
    private static final String TAG = DatabaseInitializer.class.getName();

    public DatabaseInitializer(@NonNull final AppDatabase db,
                               View view,
                               Subject subject,
                               iReturnSubjects iReturnSubjects){
        this.view = view;
        this.subject = subject;
        this.iReturnSubjects = iReturnSubjects;
        task = new PopulateDbAsync(db);
    }

    public void writeToDatabase(){
        task.execute();
    }

    public void readFromDatabase(){

    }

    /* Delete this when the app is working perfectly
    public static void populateAsync(@NonNull final AppDatabase db,
                                     View view,
                                     Subject subject) {
        PopulateDbAsync task = new PopulateDbAsync(db);
        task.execute();
    }*/

    public void populateSync(@NonNull final AppDatabase db) {
        writeToDatabase(db);
    }

    private Subject addUser(final AppDatabase db, Subject subject) {
        db.subjectDao().insertAllSubjects(subject);
        return subject;
    }

    public void writeToDatabase(AppDatabase db) {
        //Add the subject to the database
        addUser(db, subject);
        readFromDatabase(db);
    }

    public void readFromDatabase(AppDatabase db){
        //Get all the subjects from the database
        ArrayList<Subject> subjectList = (ArrayList<Subject>) db.subjectDao().getAllSubjects();
        iReturnSubjects.getSubjects(subjectList);
    }

    private class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
        private final AppDatabase mDb;

        PopulateDbAsync(AppDatabase db) {
            mDb = db;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            writeToDatabase(mDb);
            return null;
        }

    }
}

