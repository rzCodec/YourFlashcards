package myapp.your_flashcards.Flashcard;

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

import myapp.your_flashcards.CustomViewManager;
import myapp.your_flashcards.R;
import myapp.your_flashcards.Room_Database.AppDatabase;
import myapp.your_flashcards.Room_Database.DatabaseManager;
import myapp.your_flashcards.Subject.Subject;

public class FlashcardAdapter extends RecyclerView.Adapter<FlashcardAdapter.MyViewHolder> {
    private final Context context;
    private ArrayList<Flashcard> flashcard_Items;
    private Activity activity;
    private Subject subject;
    private AppDatabase db;

    public FlashcardAdapter(ArrayList<Flashcard> flashcard_Items,
                            Context context,
                            Activity activity,
                            Subject subject,
                            AppDatabase db) {
        this.flashcard_Items = flashcard_Items;
        this.context = context;
        this.activity = activity;
        this.subject = subject;
        this.db = db;
    }

    public void refreshData(ArrayList<Flashcard> flashcardList){
        try {
            this.flashcard_Items.clear();
            this.flashcard_Items.addAll(flashcardList);
            this.notifyDataSetChanged();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addData(Flashcard flashcard){
        try {
            this.flashcard_Items.add(flashcard);
            this.notifyDataSetChanged();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.flashcard_cardview, parent, false);
        return new MyViewHolder(v);
    }
	
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Flashcard item = flashcard_Items.get(position);
        holder.tvFlashcardTitle.setText(item.getTitle());
		BoldTextTransformation btt = new BoldTextTransformation();
        holder.tvFlashcardQuestion.setText(btt.makeBold("Question: ", item.getQuestion()));
        holder.tvFlashcardAnswer.setText(item.getAnswer());
        holder.tvFlashcardDateAndTime.setText("Created on " + item.getFlashcard_Date() + " @" + item.getFlashcard_Time());
    }

    @Override
    public int getItemCount() {
        if (flashcard_Items == null) {
            return 0;
        }
        return flashcard_Items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvFlashcardTitle,
                         tvFlashcardQuestion,
                         tvFlashcardAnswer,
                         tvFlashcardDateAndTime;

        private Button btnRevealAnswer, btnFlashcardOptions;
        private boolean isClicked = false;

        public MyViewHolder(final View v) {
            super(v);
            tvFlashcardTitle = v.findViewById(R.id.txtviewFlashcardTitle);
            tvFlashcardQuestion = v.findViewById(R.id.txtviewFlashcardQuestion);
            tvFlashcardAnswer = v.findViewById(R.id.txtviewFlashcardAnswer);
            tvFlashcardAnswer.setVisibility(View.INVISIBLE);
            tvFlashcardDateAndTime = v.findViewById(R.id.txtviewFlashcardDateTime);
            btnRevealAnswer = v.findViewById(R.id.btnViewAnswer);
            btnFlashcardOptions = v.findViewById(R.id.btnFlashcardOptions);


            btnRevealAnswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isClicked == false){
                        tvFlashcardAnswer.setVisibility(View.VISIBLE);
                        btnRevealAnswer.setText("Hide Answer");
                        isClicked = true;
                    }
                    else {
                        btnRevealAnswer.setText("Show Answer");
                        tvFlashcardAnswer.setVisibility(View.INVISIBLE);
                        isClicked = false;
                    }
                }
            });

            btnFlashcardOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    PopupMenu popupMenu = new PopupMenu(context, btnFlashcardOptions);
                    popupMenu.inflate(R.menu.flashcard_options_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            final DatabaseManager dbManager = new DatabaseManager(context, db, new DatabaseManager.iCallback() {
                                @Override
                                public void returnSubjects(ArrayList<Subject> subjectArrayList) {

                                }

                                @Override
                                public void returnFlashcards(ArrayList<Flashcard> flashcardArrayList) {
                                    refreshData(flashcardArrayList);
                                }
                            });

                            switch(item.getItemId()){
                                case R.id.editFlashcardDetails :
                                    final CustomViewManager manager = new CustomViewManager(activity, R.layout.custom_flashcard_dialog);
                                    final EditText editTextTitle = manager.customView.findViewById(R.id.editTextFlashcardTitle);
                                    final EditText editTextQuestion = manager.customView.findViewById(R.id.editTextFlashcardQuestion);
                                    final EditText editTextAnswer = manager.customView.findViewById(R.id.editTextFlashcardAnswer);

                                    final Flashcard selectedFlashcard = flashcard_Items.get(getAdapterPosition());
                                    editTextTitle.setText(selectedFlashcard.getTitle());
                                    editTextQuestion.setText(selectedFlashcard.getQuestion());
                                    editTextAnswer.setText(selectedFlashcard.getAnswer());

                                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                    builder.setTitle("Edit Flashcard");
                                    builder.setView(manager.customView);

                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            selectedFlashcard.setTitle(editTextTitle.getText().toString());
                                            selectedFlashcard.setQuestion(editTextQuestion.getText().toString());
                                            selectedFlashcard.setAnswer(editTextAnswer.getText().toString());
                                            dbManager.flashcardDatabaseOperation("UPDATE", subject, selectedFlashcard);
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

                                case R.id.deleteCurrentFlashcard :
                                    Flashcard deleteFlashcard = flashcard_Items.get(getAdapterPosition());
                                    dbManager.flashcardDatabaseOperation("DELETE", subject, deleteFlashcard);

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
