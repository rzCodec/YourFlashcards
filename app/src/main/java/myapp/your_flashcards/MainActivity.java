package myapp.your_flashcards;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import myapp.your_flashcards.Flashcard.Flashcard;
import myapp.your_flashcards.Flashcard.Flashcard_Activity;
import myapp.your_flashcards.Reminder.ReminderFragment;
import myapp.your_flashcards.Room_Database.AppDatabase;
import myapp.your_flashcards.Room_Database.DatabaseManager;
import myapp.your_flashcards.Subject.Subject;
import myapp.your_flashcards.Subject.SubjectAdapter;
import myapp.your_flashcards.Subject.SubjectFragment;
import myapp.your_flashcards.Utilities.UtilitiesMessage;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SubjectFragment.OnFragmentInteractionListener,
        ReminderFragment.OnFragmentInteractionListener {


    private FloatingActionButton fabAddSubject;
    private ArrayList<String> subjectNameList = new ArrayList<>();
    private AppDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Study Flashcards");

        db = AppDatabase.getAppDatabase(getApplicationContext());
        //Create a singleton instance for this activity

        //Create a new adapter with an empty list/
        final SubjectAdapter adapter = new SubjectAdapter(new ArrayList<Subject>(),
                this,
                MainActivity.this,
                db,
                //Custom Interface to start a new activity from the subject adapter which is linked to the recycler view
                //This callback code is only executed when the user clicks the button on the card view to start the new activity
                new iOnClickListener() {
                    @Override
                    public void onItemClick(Object elem) {
                        Intent i = new Intent(getApplicationContext(), Flashcard_Activity.class);
                        i.putExtra("subject_data", (Subject) elem); //Cast the object to the desired one we need
                        startActivity(i);
                        AppDatabase.destroyInstance(); //When the user starts a new activity, destroy the database
                    }
                });

        RecyclerView recyclerView = findViewById(R.id.subject_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        final DatabaseManager databaseManager = new DatabaseManager(this,
                db,

                //Define a new callback that listens for the execution of it in line 96.
                new DatabaseManager.iCallback() {
                    @Override
                    public void returnSubjects(ArrayList<Subject> subjectArrayList) {
                        //Work with the subjects that have been returned from the database
                        //Check DatabaseManager line 77
                        adapter.refreshData(subjectArrayList);
                    }

                    @Override
                    public void returnFlashcards(ArrayList<Flashcard> flashcardArrayList) {
                        //Work with the flashcards that have been returned from the database
                    }
                });
        databaseManager.subjectDatabaseOperation("READ", null);
        //Read all the subjects from the database, execute the callback and handle the callback in line 88

        fabAddSubject = findViewById(R.id.FabAddSubject);
        fabAddSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                CustomViewManager manager = new CustomViewManager(MainActivity.this, R.layout.custom_subject_dialog);
                final EditText editTextSubjectName = manager.customView.findViewById(R.id.editTextCustomSubject);
				final String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                final String time = new SimpleDateFormat("HH:mm").format(new Date());
				
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Create Subject");
                builder.setView(manager.customView);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String subjectName = editTextSubjectName.getText().toString();
                        //Check if the user has entered a subject name that already exists.
                        //We can not store duplicate subject names because it violates the primary key constraint.
                        if(subjectNameList.contains(subjectName)){
                            UtilitiesMessage.showToastMessage(getApplicationContext()
                                    , "Subject " + subjectName + " already exists.");
                        }
                        else{
							Subject subject = new Subject(subjectName);
							subject.setSubjectDate(date);
							subject.setSubjectTime(time);
                            databaseManager.subjectDatabaseOperation("INSERT", subject);
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        AppDatabase.destroyInstance(); //Destroy the database when the user has exited the app.
    }
}
