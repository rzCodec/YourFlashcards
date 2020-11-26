package myapp.your_flashcards.Flashcard;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import myapp.your_flashcards.MainActivity;
import myapp.your_flashcards.R;
import myapp.your_flashcards.Room_Database.AppDatabase;
import myapp.your_flashcards.Subject.Subject;
import myapp.your_flashcards.Utilities.UtilitiesMessage;
import needle.Needle;
import needle.UiRelatedTask;

public class Flashcard_Activity extends AppCompatActivity {

    private FlashcardAdapter flashcardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });

        final FloatingActionButton fabAddFlashcard = findViewById(R.id.FabAddFlashcard);
        //This activity is started when the user clicks a button on the subject card view called view flashcards

        //Get the subject object which is passed to this activity from the MainActivity class on line #67
        final Subject subject = getIntent().getParcelableExtra("subject_data");
        setTitle(subject.getSubjectName());

        final AppDatabase db = AppDatabase.getAppDatabase(this); //Create a singleton instance of the db for this activity
        final ArrayList<Flashcard> flashcardList = new ArrayList<>();
        flashcardAdapter = new FlashcardAdapter(flashcardList, this, this, subject, db); //Pass the singleton

        //Whenever a user creates a new flashcard,
        //it must be associated with the current subject.

        RecyclerView recyclerView = findViewById(R.id.flashcard_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(flashcardAdapter);

        //Read the flashcards and then display them to the user.
        Needle.onBackgroundThread().execute(new UiRelatedTask<ArrayList<Flashcard>>() {
            @Override
            protected ArrayList<Flashcard> doWork() {
                //Find all the flashcards related to the current subject.
                return (ArrayList<Flashcard>)  db.flashcard_Dao().getFlashcardsForSubjectByID(subject.getSubjectID());
            }

            @Override
            protected void thenDoUiRelatedWork(ArrayList<Flashcard> flashcards) {
                //Receive the flashcard list from above (line 75) and pass it to the adapter
                flashcardAdapter.refreshData(flashcards);
            }
        });

        //Create a new flashcard when this button is clicked
        //The flashcard is created related to the current subject.
        fabAddFlashcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Custom Dialog to get flashcard details
                LayoutInflater inflater = LayoutInflater.from(Flashcard_Activity.this);
                View customView = inflater.inflate(R.layout.custom_flashcard_dialog, null);
                final EditText editTextFlashcardTitle = customView.findViewById(R.id.editTextFlashcardTitle);
                final EditText editTextFlashcardQuestion = customView.findViewById(R.id.editTextFlashcardQuestion);
                final EditText editTextFlashcardAnswer = customView.findViewById(R.id.editTextFlashcardAnswer);

                final String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                final String time = new SimpleDateFormat("HH:mm").format(new Date());

                AlertDialog.Builder builder = new AlertDialog.Builder(Flashcard_Activity.this);
                builder.setTitle("Create Flashcard");
                builder.setView(customView);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Flashcard flashcard = new Flashcard(editTextFlashcardTitle.getText().toString(),
                                editTextFlashcardQuestion.getText().toString(),
                                editTextFlashcardAnswer.getText().toString(),
                                subject.getSubjectName(),
                                subject.getSubjectID());

                        flashcard.setFlashcard_Date(date);
                        flashcard.setFlashcard_Time(time);

                        Needle.onBackgroundThread().execute(new UiRelatedTask<ArrayList<Flashcard>>() {
                            @Override
                            protected ArrayList<Flashcard> doWork() {
                                db.flashcard_Dao().insertAllFlashcards(flashcard);
                                return (ArrayList<Flashcard>) db.flashcard_Dao().getFlashcardsForSubjectByID(subject.getSubjectID());
                            }

                            @Override
                            protected void thenDoUiRelatedWork(ArrayList<Flashcard> flashcards) {
                                flashcardAdapter.refreshData(flashcards);
                                UtilitiesMessage.showToastMessage(Flashcard_Activity.this.getApplicationContext(),
                                        "New Flashcard Created");
                               
                            }
                        });
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UtilitiesMessage.showToastMessage(Flashcard_Activity.this.getApplicationContext(),
                                "Action cancelled");
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                flashcardAdapter.notifyDataSetChanged();
            }
        });
    }
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		AppDatabase.destroyInstance(); //Destroy the database when the user exits this activity
	}
}//end of Flashcard Activity
