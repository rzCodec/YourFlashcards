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
        final Subject subject = getIntent().getParcelableExtra("subject_data");
        setTitle(subject.getSubjectName());

        final AppDatabase db = AppDatabase.getAppDatabase(this); 
        final ArrayList<Flashcard> flashcardList = new ArrayList<>();
        flashcardAdapter = new FlashcardAdapter(flashcardList, this, this, subject, db); 
	    
        RecyclerView recyclerView = findViewById(R.id.flashcard_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(flashcardAdapter);

        Needle.onBackgroundThread().execute(new UiRelatedTask<ArrayList<Flashcard>>() {
            @Override
            protected ArrayList<Flashcard> doWork() {       
                return (ArrayList<Flashcard>)  db.flashcard_Dao().getFlashcardsForSubjectByID(subject.getSubjectID());
            }

            @Override
            protected void thenDoUiRelatedWork(ArrayList<Flashcard> flashcards) {
                flashcardAdapter.refreshData(flashcards);
            }
        });

        fabAddFlashcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
		AppDatabase.destroyInstance(); 
    	}
}
