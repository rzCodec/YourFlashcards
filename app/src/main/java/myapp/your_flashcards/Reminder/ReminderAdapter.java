package myapp.your_flashcards.Reminder;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import myapp.your_flashcards.R;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.MyViewHolder> {
    private final Context context;
    private ArrayList<Reminder> items;

    public ReminderAdapter(ArrayList<Reminder> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reminder_cardview, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Reminder item = items.get(position);
    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(View v) {
            super(v);
        }
    }
}
