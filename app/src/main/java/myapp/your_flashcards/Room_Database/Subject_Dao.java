package myapp.your_flashcards.Room_Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import myapp.your_flashcards.Subject.Subject;

/**
 * Created by User on 2018/06/12.
 */

@Dao
public interface Subject_Dao {
    @Query("SELECT * FROM subject")
    List<Subject> getAllSubjects(); //Must be of type List and not ArrayList

    @Insert
    void insertAllSubjects(Subject... subjects);

	@Update
	void updateCurrentSubject(Subject subject);
	
    //These last 2 queries, delete flashcards and delete subject must be executed
    //The first deletes all flashcards
    //Then delete the subject containing the flashcards.
    //Deleting the subject first before deleting the flashcards results in a constraint violation
    //Because the existing flashcards do not have a reference.
    //So the correct way to do it is to delete the flashcards within the current subject, and then delete the subject.
    //Check the SubjectAdapter class btnDeleteSubject.
    @Query("DELETE FROM flashcard WHERE subjectID IS :subjectID")
    void deleteFlashcards(int subjectID);

    @Delete
    void deleteSubject(Subject subject);
}
