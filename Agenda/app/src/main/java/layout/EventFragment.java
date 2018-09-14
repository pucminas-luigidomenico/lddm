package layout;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import pucminas.computacao.luigi.agenda.Person;
import pucminas.computacao.luigi.agenda.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventFragment extends android.support.v4.app.Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_NAME_EVENT = "name_event";
    private static final String ARG_START_DATE = "start_date";
    private static final String ARG_END_DATE = "end_date";
    private static final String ARG_START_TIME = "start_time";
    private static final String ARG_END_TIME = "end_time";

    // Organizer person
    private Person organizer;

    // Event data
    private String nameEvent;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;

    // Fields of fragment
    private EditText nameEventEditText;
    private TextView startDateTextView;
    private TextView endDateTextView;
    private TextView startTimeTextView;
    private TextView endTimeTextView;
    private TextView organizerTextView;
    private ImageButton organizerButton;

    // Event fragment listener
    private OnFragmentInteractionListener eventFragmentListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param nameEvent Name event
     * @param startDate Initial date
     * @param endDate Final date
     * @param startTime Initial hour/minute
     * @param endTime Final hour/minute
     * @return A new instance of fragment EventFragment.
     */
    public static EventFragment newInstance(String nameEvent, String startDate, String endDate,
                                            String startTime, String endTime, Person organizer) {
        EventFragment fragment = new EventFragment();
        fragment.setOrganizer(organizer);
        Bundle args = new Bundle();
        args.putString(ARG_NAME_EVENT, nameEvent);
        args.putString(ARG_START_DATE, startDate);
        args.putString(ARG_END_DATE, endDate);
        args.putString(ARG_START_TIME, startTime);
        args.putString(ARG_END_TIME, endTime);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Setter of organizer.
     *
     * @param organizer Person to set as organizer.
     */
    public void setOrganizer(Person organizer) {
        this.organizer = organizer;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.nameEvent   = getArguments().getString(ARG_NAME_EVENT);
            this.startDate   = getArguments().getString(ARG_START_DATE);
            this.endDate     = getArguments().getString(ARG_END_DATE);
            this.startTime = getArguments().getString(ARG_START_TIME);
            this.endTime = getArguments().getString(ARG_END_TIME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get fields
        this.nameEventEditText   = (EditText) getActivity().findViewById(R.id.name_event);
        this.startDateTextView   = (TextView) getActivity().findViewById(R.id.start_date);
        this.endDateTextView     = (TextView) getActivity().findViewById(R.id.end_date);
        this.startTimeTextView   = (TextView) getActivity().findViewById(R.id.start_hour);
        this.endTimeTextView     = (TextView) getActivity().findViewById(R.id.end_hour);
        this.organizerButton     = (ImageButton) getActivity().findViewById(R.id.organizer_button);

        // Set values on date and time text views
        this.nameEventEditText.setText(nameEvent);
        this.startDateTextView.setText(startDate);
        this.endDateTextView.setText(endDate);
        this.startTimeTextView.setText(startTime);
        this.endTimeTextView.setText(endTime);

        // Set listeners
        this.organizerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If organizer doesn't exists yet, show dialog to create a new one.
                if (organizer == null) {
                    DialogFragment newFragment = PersonDialogFragment.newInstance(
                            getString(R.string.title_add_organizer), true);
                    newFragment.show(getActivity().getSupportFragmentManager(), "AddOrganizerDialog");

                    // Else, remove organizer element
                } else {
                    removeOrganizer();
                    organizer = null;
                    eventFragmentListener.removeOrganizer();
                }
            }
        });

        this.startDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = DatePickerDialogFragment.newInstance((TextView) view);
                newFragment.show(getActivity().getSupportFragmentManager(), "DatePickerDialog");
            }
        });

        this.endDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = DatePickerDialogFragment.newInstance((TextView) view);
                newFragment.show(getActivity().getSupportFragmentManager(), "DatePickerDialog");
            }
        });

        this.startTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = TimePickerDialogFragment.newInstance((TextView) view);
                newFragment.show(getActivity().getSupportFragmentManager(), "TimePickerDialog");
            }
        });

        this.endTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = TimePickerDialogFragment.newInstance((TextView) view);
                newFragment.show(getActivity().getSupportFragmentManager(), "TimePickerDialog");
            }
        });

        if (this.organizer != null) {
            this.createOrganizer(this.organizer);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            this.eventFragmentListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        nameEvent = nameEventEditText.getText().toString();
        startDate = startDateTextView.getText().toString();
        endDate = endDateTextView.getText().toString();
        startTime = startTimeTextView.getText().toString();
        endTime = endTimeTextView.getText().toString();

        eventFragmentListener.setEventData(nameEvent, startDate, endDate, startTime, endTime);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        eventFragmentListener = null;
    }

    /**
     * Method to create new organizer, that is called by MainActivity
     *
     * @param person Person that is going to be the new organizer.
     */
    public void createOrganizer(Person person) {
        // Change image button to delete
        this.organizerButton.setImageResource(R.drawable.ic_remove_circle_red_24dp);

        // Get View and ViewGroup
        View view           = getView();
        ViewGroup viewGroup = (ViewGroup) view;

        // Create new TextView and set params
        this.organizerTextView = new TextView(getContext());
        this.organizerTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        this.organizerTextView.setClickable(true);
        this.organizerTextView.setGravity(Gravity.CENTER);
        int dimension = (int) getResources().getDimension(R.dimen.padding_5);
        this.organizerTextView.setPadding(dimension, dimension, dimension, dimension);
        this.organizerTextView.setText(person.getName());
        this.organizerTextView.setAllCaps(true);
        this.organizerTextView.setBackgroundResource(R.drawable.border_text_view);
        this.organizerTextView.setClickable(false);
        TextViewCompat.setTextAppearance(organizerTextView, android.R.style.TextAppearance_Material_Medium);

        // Reorganize view
        assert viewGroup != null;
        viewGroup.addView(organizerTextView);
    }

    /**
     * Method to remove organizer and change button to add function.
     */
    public void removeOrganizer() {
        // Change image button to add
        this.organizerButton.setImageResource(R.drawable.ic_add_circle_green_24dp);

        // Get View and ViewGroup
        View view           = getView();
        ViewGroup viewGroup = (ViewGroup) view;

        // Remove TextView
        assert viewGroup != null;
        viewGroup.removeView(this.organizerTextView);
    }

    /**
     * Method to validate date and time.
     */
    public void checkDateTime() throws ParseException {
        // boolean variable to check date and time.
        boolean dateTimeValid;

        // Create DateFormats
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("EEE dd, MMMM - yyyy HH:mm");

        // Update startDate, endDate, starTime and endTime
        this.startDate  = this.startDateTextView.getText().toString();
        this.endDate    = this.endDateTextView.getText().toString();
        this.startTime  = this.startTimeTextView.getText().toString();
        this.endTime    = this.endTimeTextView.getText().toString();

        // Create date objects
        Date startDateTime = dateTimeFormat.parse(this.startDate + " " + this.startTime);
        Date endDateTime   = dateTimeFormat.parse(this.endDate + " " + this.endTime);

        // Verifying if date is valid
        dateTimeValid = startDateTime.getTime() <= endDateTime.getTime();

        // If dateTime is invalid, make a toast to show a notification message
        if (!dateTimeValid) {
            // Show error message
            Toast toast = Toast.makeText(getContext(),
                    getResources().getString(R.string.error_message),
                    Toast.LENGTH_LONG);
            toast.show();

            // Set end date equals start date
            this.endDate = this.startDate;
            this.endTime = this.startTime;

            this.endDateTextView.setText(this.endDate);
            this.endTimeTextView.setText(this.endTime);
        }
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
        void setEventData(String nameEvent, String startDate, String endDate,
                          String startHour, String endHour);
        boolean removeOrganizer();
    }
}
