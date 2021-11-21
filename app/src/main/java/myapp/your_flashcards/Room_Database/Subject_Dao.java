package myapp.your_flashcards.Room_Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;
import myapp.your_flashcards.Subject.Subject;

@Dao
public interface Subject_Dao {
    @Query("SELECT * FROM subject")
    List<Subject> getAllSubjects();

    @Insert
    void insertAllSubjects(Subject... subjects);

    @Update
    void updateCurrentSubject(Subject subject);
	
    @Query("DELETE FROM flashcard WHERE subjectID IS :subjectID")
    void deleteFlashcards(int subjectID);

    @Delete
    void deleteSubject(Subject subject);
}
