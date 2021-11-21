package myapp.your_flashcards.Room_Database;

import android.content.Context;
import android.os.AsyncTask;
import java.util.ArrayList;
import myapp.your_flashcards.Subject.Subject;
import myapp.your_flashcards.Utilities.UtilitiesMessage;

public class DatabaseTask extends AsyncTask<Void, Void, ArrayList<Subject>> {
    private Subject subject;
    private Context context;
    private boolean isRetrieving;
    private iReturnSubjects iReturnSubjects;

    public DatabaseTask(Subject subject,
                        Context context,
                        boolean isRetrieving,
                        iReturnSubjects iReturnSubjects
                        ){
        this.subject = subject;
        this.context = context;
        this.isRetrieving = isRetrieving;
        this.iReturnSubjects = iReturnSubjects;
    }

    @Override
    protected ArrayList<Subject> doInBackground(Void... voids) {
        AppDatabase appDatabase = AppDatabase.getAppDatabase(this.context);
        if(this.isRetrieving == false){
            appDatabase.subjectDao().insertAllSubjects(this.subject);
            return null;
        }
        else {
            return (ArrayList<Subject>) appDatabase.subjectDao().getAllSubjects();
        }
    }

    @Override
    protected void onPostExecute(ArrayList<Subject> subjectList){
        if(this.isRetrieving == false){
            UtilitiesMessage.showToastMessage(this.context, "Added new subject!");
        }
        else {
            iReturnSubjects.getSubjects(subjectList);
        }
    }
}
