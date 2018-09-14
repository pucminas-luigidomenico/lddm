package layout;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * TimePickerDialog to pick start and end hour/minute of event.
 */
public class TimePickerDialogFragment extends android.support.v4.app.DialogFragment
                                implements TimePickerDialog.OnTimeSetListener {

    private TextView textView;
    private OnFragmentInteractionListener timePickerFragmentListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param textView TextView to set date
     * @return A new instance of fragment TimePickerDialogFragment.
     */
    public static TimePickerDialogFragment newInstance(TextView textView) {
        TimePickerDialogFragment timePickerDialogFragment = new TimePickerDialogFragment();
        timePickerDialogFragment.setTextView(textView);
        return timePickerDialogFragment;
    }

    /**
     * Setter method of TextView to set Time.
     *
     * @param textView TextView that is going to receive time picked.
     */
    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat timeFormatOut = new SimpleDateFormat("HH:mm a");
        String stringTime = hour + ":" + minute;
        Date dateTime = null;
        try {
            dateTime = timeFormat.parse(stringTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        textView.setText(timeFormatOut.format(dateTime));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TimePickerDialogFragment.OnFragmentInteractionListener) {
            this.timePickerFragmentListener = (TimePickerDialogFragment.
                    OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        try {
            this.timePickerFragmentListener.callCheckDateTime();
        } catch (ParseException e) {
            e.printStackTrace();
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
        void callCheckDateTime() throws ParseException;
    }
}
