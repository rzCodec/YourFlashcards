package myapp.your_flashcards.Subject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import myapp.your_flashcards.CustomViewManager;
import myapp.your_flashcards.Flashcard.Flashcard;
import myapp.your_flashcards.R;
import myapp.your_flashcards.Room_Database.AppDatabase;
import myapp.your_flashcards.Room_Database.DatabaseManager;
import myapp.your_flashcards.iOnClickListener;

/**
 * Created by User on 2018/06/13.
 */
public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {
    private final Context context;
    private ArrayList<Subject> items;
    private iOnClickListener iOnClickListener;
    private Activity activity;
    private AppDatabase db;

    public SubjectAdapter(ArrayList<Subject> items,
                          Context context,
                          Activity activity,
                          AppDatabase db,
                          iOnClickListener iOnClickListener) {
        this.items = items;
        this.context = context;
        this.activity = activity;
        this.db = db;
        this.iOnClickListener = iOnClickListener; //Callback to be executed when the user clicks...
    }

    /*
     *Constructor to create the card
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subject_cardview, parent, false);
        return new ViewHolder(v, this.iOnClickListener);
    }

    /*
     *Method to handle displaying and showing of text views, etc
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Subject subject = items.get(position);
        //TODO Fill in your logic for binding the view.
        holder.tvSubjectName.setText(subject.getSubjectName());
        holder.tvDateAndTime.setText("Created on " + subject.getSubjectDate() + " @" + subject.getSubjectTime());
    }

    /*
     *Method to return the size of the list of items
     */
    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    /**
     * Adds an updated subject list to the adapter and refreshes
     * @param subjectList
     */
    public void refreshData(ArrayList<Subject> subjectList){
        try {
            this.items.clear();
            this.items.addAll(subjectList);
            this.notifyDataSetChanged();
            //NotifyDataSetChanged can only be called on the Main Thread and not on a background thread
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    //Inner class to find the components on the XML file
    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txtviewSubjectName)
        private TextView tvSubjectName;
        private TextView tvDateAndTime;
        private Button btnViewFlashcards;
		private Button btnSubjectOptions;
        private final iOnClickListener clickListener;

        public ViewHolder(final View v, final iOnClickListener iOnClickListener) {
            super(v);
            this.clickListener = iOnClickListener;
            ButterKnife.bind(this, v);
            tvSubjectName = v.findViewById(R.id.txtviewSubjectName);
            tvDateAndTime = v.findViewById(R.id.txtviewDateAndTime);
            //View the flashcards associated with this selected subject.
            btnViewFlashcards = v.findViewById(R.id.btnViewFlashcards);
            btnViewFlashcards.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Subject subject = items.get(getAdapterPosition());
                    //The callback is defined in the main activity in the SubjectAdapter's parameters
                    //Whenever the user clicks view flashcards, the current subject is passed to the callback handler
                    //Go to MainActivity, line 74
                    //This pattern is great to start another activity from a fragment that uses a recycler view & card view.
                    clickListener.onItemClick(subject);

                }
            });
			
			btnSubjectOptions = v.findViewById(R.id.btnSubjectOptions);
			btnSubjectOptions.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(final View view) {
					//Create the dialog and give the user the option
					//To delete the current subject or edit the name

                    PopupMenu popupMenu = new PopupMenu(context, btnSubjectOptions);
                    popupMenu.inflate(R.menu.subject_options_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            final DatabaseManager databaseManager = new DatabaseManager(v.getContext(),
                                    db,
                                    new DatabaseManager.iCallback() {
                                @Override
                                public void returnSubjects(ArrayList<Subject> subjectArrayList) {
                                    refreshData(subjectArrayList);
                                }

                                @Override
                                public void returnFlashcards(ArrayList<Flashcard> flashcardArrayList) {

                                }
                            });

                            switch(item.getItemId()){
                                case R.id.editSubjectDetails:

									 CustomViewManager manager = new CustomViewManager(activity, R.layout.custom_subject_dialog);
                final EditText editTextSubjectName = manager.customView.findViewById(R.id.editTextCustomSubject);

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Edit Subject");
                builder.setView(manager.customView);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Subject subject = items.get(getAdapterPosition());
                        final String subjectName = editTextSubjectName.getText().toString();
						subject.setSubjectName(subjectName);
                        databaseManager.subjectDatabaseOperation("UPDATE", subject);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
									return true;

                                case R.id.deleteCurrentSubject:
                                    Subject subject = items.get(getAdapterPosition());
                                    databaseManager.subjectDatabaseOperation("DELETE", subject);
									return true;
                                default: return false;
                            }
                        }
                    });
                    popupMenu.show();
				}
			});

			/*
            //Deletes a subject and then updates the UI.
            btnDeleteSubject = v.findViewById(R.id.btnDeleteSubject);
            btnDeleteSubject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    final Subject subject = items.get(getAdapterPosition());

                    DatabaseManager databaseManager = new DatabaseManager(v.getContext(), new DatabaseManager.iCallback() {
                        @Override
                        public void returnSubjects(ArrayList<Subject> subjectArrayList) {
                            refreshData(subjectArrayList);
                            UtilitiesMessage.showToastMessage(view.getContext(),
                                    subject.getSubjectName() + " has been deleted.");
                        }

                        @Override
                        public void returnFlashcards(ArrayList<Flashcard> flashcardArrayList) {

                        }
                    });

                    databaseManager.subjectDatabaseOperation("DELETE", subject);

                    /*
                    Needle.onBackgroundThread().execute(new UiRelatedTask<ArrayList<Subject>>() {
                        @Override
                        protected ArrayList<Subject> doWork() {
                            //Delete all the flashcards first, THEN delete the subject.
                            db.subjectDao().deleteFlashcards(subject.getSubjectName());
                            db.subjectDao().deleteSubject(subject);
                            if(items.size() == 0){
                                AppDatabase.destroyInstance(); //Clear the database because there are no more subjects.
                            }
                            return (ArrayList<Subject>) db.subjectDao().getAllSubjects();
                        }

                        @Override
                        protected void thenDoUiRelatedWork(ArrayList<Subject> subjects) {
                            refreshData(subjects);
                            UtilitiesMessage.showToastMessage(view.getContext(),
                                    subject.getSubjectName() + " has been deleted.");
                        }
                    });
                }
            });*/
        }
    }//end of view holder class

}//end of class