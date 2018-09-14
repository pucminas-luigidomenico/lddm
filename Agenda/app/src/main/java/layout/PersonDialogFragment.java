package layout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.support.v7.widget.AppCompatEditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.text.WordUtils;

import pucminas.computacao.luigi.agenda.Person;
import pucminas.computacao.luigi.agenda.R;

public class PersonDialogFragment extends DialogFragment {

    private boolean isOrganizer;
    private String title;

    // EditTexts
    private AppCompatEditText nameEditText;
    private AppCompatEditText emailEditText;
    private AppCompatEditText phoneEditText;

    // Buttons
    private Button positiveButton;

    // Icon Error
    Drawable iconError;

    // Fragment Listener
    OnFragmentInteractionListener personFragmentListener;

    // Private class to watch input texts and show error
    private class EditTextWatcher implements TextWatcher {

        // EditText which is being watched
        private AppCompatEditText editText;

        /**
         * Constructor for EditTextWatcher
         * @param editText EditText
         */
        EditTextWatcher(AppCompatEditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        /**
         * If edit text is empty, show error icon
         * @param editable Editable
         */
        @Override
        public void afterTextChanged(Editable editable) {
            if (!isValid(editText)) { // EditText empty, put icon error
                // Set icon error
                editText.setCompoundDrawables(null, null, iconError, null);

                // Set bottom line error
                editText.setSupportBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(getContext(), R.color.colorError)));

                // Disable the show graph button
                positiveButton.setEnabled(false);
            } else { // EditText has something
                // Remove icon error
                editText.setCompoundDrawables(null, null, null, null);

                // Remove bottom line error
                editText.setSupportBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(getContext(), R.color.colorAccent)));
            }

            // If all of EditTexts are valid, enable the button to save person
            boolean nameValid   = isValid(nameEditText);
            boolean emailValid  = isValid(emailEditText);
            boolean phoneValid  = isValid(phoneEditText);

            if (nameValid && emailValid && phoneValid) {
                positiveButton.setEnabled(true);
            }
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title String Title of Dialog
     * @return A new instance of fragment PersonDialogFragment
     */
    public static PersonDialogFragment newInstance(String title, boolean isOrganizer) {
        PersonDialogFragment personDialogFragment = new PersonDialogFragment();
        personDialogFragment.setTitle(title);
        personDialogFragment.setIsOrganizer(isOrganizer);
        return personDialogFragment;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIsOrganizer(boolean isOrganizer) {
        this.isOrganizer = isOrganizer;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_person, (ViewGroup) getView());
        builder.setView(view);

        // Get Title of Dialog
        TextView titleTextView = (TextView) view.findViewById(R.id.title_person_dialog);
        titleTextView.setText(title);

        // Add action buttons
        builder.setPositiveButton(R.string.add_message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Get values of new person
                String name         = WordUtils.capitalize(nameEditText.getText().toString());
                String email        = emailEditText.getText().toString();
                String phone        = phoneEditText.getText().toString();
                String personType   = isOrganizer ? "Organizer" : "Guest";
                Person person       = new Person(name, email, phone, personType);

                try {
                    // Call method implemented on MainActivity to set person data
                    personFragmentListener.setPersonData(person);

                    // A fast toast to show success message
                    Toast toast = Toast.makeText(getContext(),
                            getResources().getString(R.string.person_success),
                            Toast.LENGTH_SHORT);
                    toast.show();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();

                    // A fast toast to show error message
                    Toast toast = Toast.makeText(getContext(),
                            getResources().getString(R.string.person_error),
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        builder.setNegativeButton(R.string.cancel_message, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // A fast toast to show cancel message
                Toast toast = Toast.makeText(getContext(),
                        getResources().getString(R.string.person_cancel),
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        // Create AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Get EditTexts
        nameEditText  = (AppCompatEditText) view.findViewById(R.id.name_contact);
        emailEditText = (AppCompatEditText) view.findViewById(R.id.email_contact);
        phoneEditText = (AppCompatEditText) view.findViewById(R.id.phone_contact);

        // Set Listener to format phone
        phoneEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        // Set Listener to edit texts
        nameEditText.addTextChangedListener(new EditTextWatcher(nameEditText));
        emailEditText.addTextChangedListener(new EditTextWatcher(emailEditText));
        phoneEditText.addTextChangedListener(new EditTextWatcher(phoneEditText));

        // Get icon error
        iconError = ContextCompat.getDrawable(getContext(), R.drawable.ic_error_red_24dp);
        iconError.setBounds(0, 0, iconError.getIntrinsicWidth(), iconError.getIntrinsicHeight());

        // Get Positive button
        positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

        // Set positiveButton disabled on init
        positiveButton.setEnabled(false);

        // Return the new dialog
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            personFragmentListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * Return whether an edit text is valid or not.
     * This function works on nameEdiText, emailEditText
     * or phoneEditText.
     *
     * @param editText EditText
     * @return True if it is valid, false if it isn't.
     */
    private boolean isValid(AppCompatEditText editText) {
        int editTextId  = editText.getId();
        boolean isValid = false;

        if (editTextId == R.id.name_contact) {
            isValid = (editText.length() > 0);
        } else if (editTextId == R.id.email_contact) {
            String str = editText.getText().toString();

            // Count number of character '@' in mail
            int countMailCharacter = str.length() - str.replace("@", "").length();

            // If has more then one or hasn't character '@', is invalid
            isValid = countMailCharacter == 1;

            // Verifying if mail has anything before character '@'
            isValid = isValid && str.substring(0, str.indexOf('@')).length() > 0;

            // Verifying if mail has anything after character '@'
            isValid = isValid && str.substring(str.indexOf('@') + 1, str.length())
                                    .length() > 0;

            /*
             * Verifying if mail has blank space.
             * If mail has blank space, it isn't valid.
             */
            isValid = isValid && !str.contains(" ");

            /*
             * Verifying if mail has character '.' after character '@',
             * like @something.com
             */
            isValid = isValid && str.substring(str.indexOf('@') + 1, str.length())
                                    .contains(".");

            /*
             * Verifying if mail has something after character '.', like
             * .br or .com or .gov
             */
            isValid = isValid && str.substring(str.lastIndexOf(".") + 1, str.length())
                                    .length() > 1;
        } else if (editTextId == R.id.phone_contact) {
            isValid = (editText.length() > 8 && editText.length() < 13);
        }

        return isValid;
    }

    /**
     * Interface to communicate with Main Activity and pass person data.
     *
     */
    public interface OnFragmentInteractionListener {
        void setPersonData(Person person) throws CloneNotSupportedException;
    }
}
