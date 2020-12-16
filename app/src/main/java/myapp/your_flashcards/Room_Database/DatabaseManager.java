package myapp.your_flashcards.Room_Database;

import android.content.Context;

import java.util.ArrayList;

import myapp.your_flashcards.Flashcard.Flashcard;
import myapp.your_flashcards.Subject.Subject;
import myapp.your_flashcards.Utilities.UtilitiesMessage;
import needle.Needle;
import needle.UiRelatedTask;

/**
 * Created by User on 2018/06/22.
 */

public class DatabaseManager {
    public interface iCallback {
        void returnSubjects(ArrayList<Subject> subjectArrayList);
        void returnFlashcards(ArrayList<Flashcard> flashcardArrayList);
    }

    private Context context;
    private iCallback callback;
    private AppDatabase db;

    public DatabaseManager(Context context, AppDatabase db, iCallback callback) {
        this.context = context;
        this.callback = callback;
        this.db = db;
    }

    public void subjectDatabaseOperation(final String operationType, final Subject subject){
        Needle.onBackgroundThread().execute(new UiRelatedTask<ArrayList<Subject>>() {
            private boolean doesExist = false;
            @Override
            protected ArrayList doWork() {
                if(operationType.equals("READ")){
                    return (ArrayList<Subject>) db.subjectDao().getAllSubjects();
                }
                else if(operationType.equals("INSERT")){
                    ArrayList<Subject> subjectList = (ArrayList<Subject>) db.subjectDao().getAllSubjects();
                    ArrayList<String> stringArrayList = new ArrayList<>();

                    //Add each subject name to an array list of strings
                    for(Subject s : subjectList){
                        stringArrayList.add(s.getSubjectName());
                    }

                    //If the array list of subject names does not contain the subject name to be inserted...
                    if(!stringArrayList.contains(subject.getSubjectName())){
                        db.subjectDao().insertAllSubjects(subject); //Then insert the new subject into the database
                        return (ArrayList<Subject>) db.subjectDao().getAllSubjects();
                    }
                    else {
                        doesExist = true; //Otherwise the subject already exists and a different name must be chosen
                    }
                }
				else if(operationType.equals("UPDATE")){
					db.subjectDao().updateCurrentSubject(subject);
					return (ArrayList<Subject>) db.subjectDao().getAllSubjects();
				}
                else if(operationType.equals("DELETE")){
                    db.subjectDao().deleteFlashcards(subject.getSubjectID());
                    db.subjectDao().deleteSubject(subject);
                    return (ArrayList<Subject>) db.subjectDao().getAllSubjects();
                }
                return new ArrayList<>();
            }

            @Override
            protected void thenDoUiRelatedWork(ArrayList<Subject> subjects) {
                if(doesExist == true){//If the current subject trying to be added already exists, deny the creation
                    UtilitiesMessage.showToastMessage(context, "Please enter a different subject name.");
                }
                else {//Otherwise send all the subjects that have been read back to the main activity line 85
                    callback.returnSubjects(subjects);
                }
            }
        });
    }

    public void flashcardDatabaseOperation(final String operationType, final Subject subject, final Flashcard flashcard) {
        Needle.onBackgroundThread().execute(new UiRelatedTask<ArrayList<Flashcard>>() {
            @Override
            protected ArrayList<Flashcard> doWork() {
                if(operationType.equals("READ")){
                    return (ArrayList<Flashcard>) db.flashcard_Dao().getFlashcardsForSubjectByID(subject.getSubjectID());
                }
                else if(operationType.equals("INSERT")){
                    db.flashcard_Dao().insertAllFlashcards(flashcard);
                    return (ArrayList<Flashcard>) db.flashcard_Dao().getFlashcardsForSubjectByID(subject.getSubjectID());
                }
                else if(operationType.equals("UPDATE")){
                    db.flashcard_Dao().updateCurrentFlashcard(flashcard);
                    return (ArrayList<Flashcard>) db.flashcard_Dao().getFlashcardsForSubjectByID(subject.getSubjectID());

                }
                else if(operationType.equals("DELETE")){
                    db.flashcard_Dao().deleteFlashcard(flashcard);
                    return (ArrayList<Flashcard>) db.flashcard_Dao().getFlashcardsForSubjectByID(subject.getSubjectID());
                }
                return null;
            }

            @Override
            protected void thenDoUiRelatedWork(ArrayList<Flashcard> flashcards) {
                callback.returnFlashcards(flashcards);
            }
        });
    }
}
