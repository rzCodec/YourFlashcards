package myapp.your_flashcards.Reminder;

import java.sql.Time;
import java.util.Date;

public class Reminder {
    private String reminderName;
    private String message;
    private Date date;
    private Time time;

    public Reminder(String reminderName, String message, Date date, Time time) {
        this.reminderName = reminderName;
        this.message = message;
        this.date = date;
        this.time = time;
    }

    public String getReminderName() {
        return reminderName;
    }

    public void setReminderName(String reminderName) {
        this.reminderName = reminderName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }
}
