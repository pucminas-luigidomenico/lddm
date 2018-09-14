package layout;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.DatePicker;
import android.widget.TextView;

import org.apache.commons.lang3.text.WordUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * DatePickerDialog to pick start and date of event.
 */
public class DatePickerDialogFragment extends android.support.v4.app.DialogFragment
                                implements DatePickerDialog.OnDateSetListener {

    private TextView textView;
    private OnFragmentInteractionListener datePickerFragmentListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param textView TextView to set date
     * @return A new instance of fragment DatePickerDialogFragment.
     */
    public static DatePickerDialogFragment newInstance(TextView textView) {
        DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
        datePickerDialogFragment.setTextView(textView);
        return datePickerDialogFragment;
    }

    /**
     * Setter method of TextView to set value of start/end date.
     *
     * @param textView TextView that is going to receive date value.
     */
    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat formatOut = new SimpleDateFormat("EEE dd, MMMM - yyyy");
        String stringDate = day + "-" + (month + 1) + "-" + year;
        Date date = null;

        try {
            date = dateFormat.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        textView.setText(WordUtils.capitalize(formatOut.format(date)));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DatePickerDialogFragment.OnFragmentInteractionListener) {
            this.datePickerFragmentListener = (DatePickerDialogFragment.
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
            this.datePickerFragmentListener.callCheckDateTime();
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
