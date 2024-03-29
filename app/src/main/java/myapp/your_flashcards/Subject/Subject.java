package myapp.your_flashcards.Subject;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

@Entity(tableName = "subject")
public class Subject implements Parcelable{

    @PrimaryKey(autoGenerate = true)
    private int subjectID;

    @ColumnInfo(name = "subjectName")
    private String subjectName;

    @ColumnInfo(name = "num_flashcards")
    private int numFlashcards;
	
   @ColumnInfo(name = "subject_date")
   private String subjectDate;
	
    @ColumnInfo(name = "subject_time")
    private String subjectTime;

    private Subject(Parcel parcel){
        this.subjectName = parcel.readString();
        this.numFlashcards = parcel.readInt();
        this.subjectID = parcel.readInt();
		this.subjectDate = parcel.readString();
		this.subjectTime = parcel.readString();
    }

    public Subject(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setSubjectID(int subjectID) {
        this.subjectID = subjectID;
    }
	
    public void setSubjectDate(String date) {
	this.subjectDate = date;
    }
	
    public void setSubjectTime(String time) {		
	this.subjectTime = time;
    }

    public int getNumFlashcards() {
        return numFlashcards;
    }

    public void setNumFlashcards(int numFlashcards) {
        this.numFlashcards = numFlashcards;
    }

    public int getSubjectID() {
        return subjectID;
    }
	
	public String getSubjectDate() {
		return subjectDate;
	}
	
	public String getSubjectTime() {
		return subjectTime;
	}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.subjectName);
        parcel.writeInt(this.numFlashcards);
        parcel.writeInt(this.subjectID);
		parcel.writeString(this.subjectDate);
		parcel.writeString(this.subjectTime);
    }

    public static final Parcelable.Creator<Subject> CREATOR = new Parcelable.Creator<Subject>() {
        public Subject createFromParcel(Parcel in) {
            return new Subject(in); 
        }

        public Subject[] newArray(int size) {
            return new Subject[size];
        }
    };
}
