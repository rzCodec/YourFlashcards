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
        this.iOnClickListener = iOnClickListener; 
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subject_cardview, parent, false);
        return new ViewHolder(v, this.iOnClickListener);
    }
	
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Subject subject = items.get(position);
        holder.tvSubjectName.setText(subject.getSubjectName());
        holder.tvDateAndTime.setText("Created on " + subject.getSubjectDate() + " @" + subject.getSubjectTime());
    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    public void refreshData(ArrayList<Subject> subjectList){
        try {
            this.items.clear();
            this.items.addAll(subjectList);
            this.notifyDataSetChanged();         
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

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
            btnViewFlashcards = v.findViewById(R.id.btnViewFlashcards);
            btnViewFlashcards.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Subject subject = items.get(getAdapterPosition());              
                    clickListener.onItemClick(subject);
                }
            });
			
			btnSubjectOptions = v.findViewById(R.id.btnSubjectOptions);
			btnSubjectOptions.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(final View view) {			
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
        }
    }
}
