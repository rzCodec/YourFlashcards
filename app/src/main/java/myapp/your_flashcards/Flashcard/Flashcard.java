package myapp.your_flashcards.Flashcard;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import myapp.your_flashcards.Subject.Subject;

@Entity(foreignKeys = @ForeignKey(
        entity = Subject.class,
        parentColumns = "subjectID",
        childColumns = "subjectID"))

public class Flashcard implements Parcelable{

    @PrimaryKey(autoGenerate = true)
    private int flashcard_id;

    @ColumnInfo(name = "Title")
    private String Title;

    @ColumnInfo(name = "FlashcardDate")
    private String flashcard_Date;

    @ColumnInfo(name = "FlashcardTime")
    private String flashcard_Time;

    @ColumnInfo(name = "Question")
    private String Question;

    @ColumnInfo(name = "Answer")
    private String Answer;

    @ColumnInfo(name = "subjectID")
    private int subjectID;

    @ColumnInfo(name = "subjectName")
    private String subjectName;

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getFlashcard_Date() {
        return flashcard_Date;
    }

    public void setFlashcard_Date(String flashcard_Date) {
        this.flashcard_Date = flashcard_Date;
    }

    public String getFlashcard_Time() {
        return flashcard_Time;
    }

    public void setFlashcard_Time(String flashcard_Time) {
        this.flashcard_Time = flashcard_Time;
    }

    public Flashcard(){

    }

    public Flashcard(String title, String question, String answer, String subjectName, int subjectID) {
        Title = title;
        Question = question;
        Answer = answer;
        this.subjectName = subjectName;
        this.subjectID = subjectID;
    }
        
    private Flashcard(Parcel in) {
        Title = in.readString();
        Question = in.readString();
        Answer = in.readString();
    }

    public int getFlashcard_id() {
        return flashcard_id;
    }

    public void setFlashcard_id(int flashcard_id) {
        this.flashcard_id = flashcard_id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getAnswer() {
        return Answer;
    }

    public void setAnswer(String answer) {
        Answer = answer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(Title);
        parcel.writeString(Question);
        parcel.writeString(Answer);
    }

    public static final Parcelable.Creator<Flashcard> CREATOR = new Parcelable.Creator<Flashcard>() {
        public Flashcard createFromParcel(Parcel in) {
            return new Flashcard(in);
        }

        public Flashcard[] newArray(int size) {
            return new Flashcard[size];
        }
    };

    public int getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(int subjectID) {
        this.subjectID = subjectID;
    }
}
