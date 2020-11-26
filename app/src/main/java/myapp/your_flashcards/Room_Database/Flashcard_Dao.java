package myapp.your_flashcards.Room_Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import myapp.your_flashcards.Flashcard.Flashcard;


/**
 * Created by User on 2018/06/17.
 */

@Dao
public interface Flashcard_Dao {
    @Query("SELECT * FROM flashcard")
    List<Flashcard> getAllFlashcards();

    @Query("SELECT * FROM flashcard where Title LIKE :flashcardTitle")
    Flashcard findByFlashcardTitle(String flashcardTitle);

    @Query("SELECT * FROM flashcard WHERE subjectName IS :subjectName")
    List<Flashcard> getFlashcardsForSubject(String subjectName);

    @Query("SELECT * FROM flashcard WHERE subjectID IS :subjectID")
    List<Flashcard> getFlashcardsForSubjectByID(int subjectID);

    @Query("SELECT COUNT(*) from flashcard")
    int countFlashcards();

    @Insert
    void insertAllFlashcards(Flashcard... flashcards);

    @Update
    void updateCurrentFlashcard(Flashcard flashcard);

    @Delete
    void deleteFlashcard(Flashcard flashcard);
}

