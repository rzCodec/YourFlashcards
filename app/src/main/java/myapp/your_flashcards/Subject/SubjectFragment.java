package myapp.your_flashcards.Subject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;

import butterknife.BindView;
import myapp.your_flashcards.CustomViewManager;
import myapp.your_flashcards.Flashcard.Flashcard;
import myapp.your_flashcards.Flashcard.Flashcard_Activity;
import myapp.your_flashcards.R;
import myapp.your_flashcards.Room_Database.AppDatabase;
import myapp.your_flashcards.Room_Database.DatabaseManager;
import myapp.your_flashcards.Utilities.UtilitiesMessage;
import myapp.your_flashcards.iOnClickListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SubjectFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SubjectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubjectFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public SubjectFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SubjectFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SubjectFragment newInstance(String param1, String param2) {
        SubjectFragment fragment = new SubjectFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @BindView(R.id.FabAddSubject)
    private FloatingActionButton fabAddSubject;
    private ArrayList<String> subjectNameList = new ArrayList<>();
    private AppDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and find the components
        final View fragmentView = inflater.inflate(R.layout.content_main_subject, container, false);
        db = AppDatabase.getAppDatabase(fragmentView.getContext());
        //Create a singleton instance for this fragment
        //Create a new adapter with an empty list
        final SubjectAdapter adapter = new SubjectAdapter(new ArrayList<Subject>(),
                fragmentView.getContext(),
                getActivity(),
                db,
                //Custom Interface to start a new activity from the subject adapter which is linked to the recycler view
                new iOnClickListener() {
                    @Override
                    public void onItemClick(Object elem) {
                        Intent i = new Intent(fragmentView.getContext(), Flashcard_Activity.class);
                        i.putExtra("subject_data", (Subject) elem); //Cast the object the user has selected to the desired one we need
                        startActivity(i);
                        AppDatabase.destroyInstance(); //When the user starts a new activity, destroy the database
                    }
                });

        RecyclerView recyclerView = fragmentView.findViewById(R.id.subject_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(fragmentView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        final DatabaseManager databaseManager = new DatabaseManager(fragmentView.getContext(),
                db,
                new DatabaseManager.iCallback() {
            @Override
            public void returnSubjects(ArrayList<Subject> subjectArrayList) {
                //Work with the subjects that have been returned from the database
                adapter.refreshData(subjectArrayList);
            }

            @Override
            public void returnFlashcards(ArrayList<Flashcard> flashcardArrayList) {
                //Work with the flashcards that have been returned from the database
            }
        });
        databaseManager.subjectDatabaseOperation("READ", null);

        fabAddSubject = fragmentView.findViewById(R.id.FabAddSubject);
        fabAddSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                CustomViewManager manager = new CustomViewManager(getActivity(), R.layout.custom_subject_dialog);
                final EditText editTextSubjectName = manager.customView.findViewById(R.id.editTextCustomSubject);

                AlertDialog.Builder builder = new AlertDialog.Builder(fragmentView.getContext());
                builder.setTitle("Create Subject");
                builder.setView(manager.customView);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String subjectName = editTextSubjectName.getText().toString();
                        //Check if the user has entered a subject name that already exists.
                        //We can not store duplicate subject names because it violates the primary key constraint.
                        if(subjectNameList.contains(subjectName)){
                            UtilitiesMessage.showToastMessage(fragmentView.getContext()
                                    , "Subject " + subjectName + " already exists.");
                        }
                        else{
                            databaseManager.subjectDatabaseOperation("INSERT", new Subject(subjectName));
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
        return fragmentView;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        AppDatabase.destroyInstance(); //Destroy the database
    }
}
