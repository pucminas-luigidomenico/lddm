package pucminas.computacao.luigi.yourmenu.layout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import pucminas.computacao.luigi.yourmenu.R;

public class AddDialogFragment extends DialogFragment {
    public static final String TAG = "AddDialogFragment";

    private String mSubmenu;
    private AppCompatEditText mItemEditText;
    private AppCompatEditText mNameEditText;
    private Button mPositiveButton;
    private Context mContext;
    private Drawable mIconError;
    private OnAddMenuItemListener mOnAddMenuListener;
    private View mView;

    private class EditTextWatcher implements TextWatcher {

        private AppCompatEditText mEditText;

        EditTextWatcher(AppCompatEditText editText) {
            mEditText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.length() == 0) {
                setEditTextError(true, mEditText);
            } else {
                setEditTextError(false, mEditText);
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private void setEditTextError(boolean error, AppCompatEditText editText) {
        Drawable icon = error ? mIconError : null;
        int color = error ?  R.color.colorPrimaryDark : R.color.colorAccent;

        editText.setCompoundDrawables(null, null, icon, null);

        editText.setSupportBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(getContext(), color)
        ));

        if (mNameEditText != null) {
            mPositiveButton.setEnabled(mItemEditText.length() > 0 && mNameEditText.length() > 0);
        } else {
            mPositiveButton.setEnabled(!error);
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddDialogFragment.
     */
    public static AddDialogFragment newInstance(Context context, String title, String hint, String submenu) {
        AddDialogFragment addDialogFragment = new AddDialogFragment();
        addDialogFragment.mContext = context;

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("hint", hint);
        args.putString("submenu", submenu);

        addDialogFragment.setArguments(args);
        return addDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mSubmenu = getArguments().getString("submenu");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mView = inflater.inflate(R.layout.fragment_dialog_add, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent mView because its going in the dialog layout
        builder.setView(mView)
                // Add action buttons
                .setPositiveButton(R.string.confirm,
                        (dialog, id) -> {
                            switch (mSubmenu) {
                                case SubmenuListFragment.MOVIE_TAG:
                                    mOnAddMenuListener.addSubmenuItem(mNameEditText.getText().toString(),
                                            mItemEditText.getText().toString(),
                                            mSubmenu);
                                    break;
                                case SubmenuListFragment.LINK_TAG:
                                    mOnAddMenuListener.addSubmenuItem(mNameEditText.getText().toString(),
                                            mItemEditText.getText().toString(),
                                            mSubmenu);
                                    break;
                                default:
                                    mOnAddMenuListener.addMenuItem(mItemEditText
                                            .getText().toString());
                                    break;
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        (dialog, id) -> AddDialogFragment.this.getDialog().cancel());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Get Title edit text
        mItemEditText = mView.findViewById(R.id.title);
        mItemEditText.addTextChangedListener(new EditTextWatcher(mItemEditText));

        // Get icon error
        mIconError = mContext.getDrawable(R.drawable.ic_error_24dp);
        mIconError.setBounds(0, 0, mIconError.getIntrinsicWidth(), mIconError.getIntrinsicHeight());

        // Get positive button
        mPositiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        setEditTextError(true, mItemEditText);

        if (mSubmenu.equals(SubmenuListFragment.MOVIE_TAG) ||
                mSubmenu.equals(SubmenuListFragment.LINK_TAG)) {
            LinearLayout linearLayout = (LinearLayout) mView;
            mNameEditText = new AppCompatEditText(mContext);
            mNameEditText.setHint(mSubmenu.equals(SubmenuListFragment.MOVIE_TAG)
                    ? mContext.getString(R.string.movie_hint_name)
                    : mContext.getString(R.string.link_hint_name));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins((int) mContext.getResources().getDimension(R.dimen.layout_margin_default),
                    (int) mContext.getResources().getDimension(R.dimen.activity_vertical_margin),
                    (int) mContext.getResources().getDimension(R.dimen.layout_margin_default),
                    (int) mContext.getResources().getDimension(R.dimen.layout_margin_default));
            mNameEditText.setLayoutParams(params);

            mNameEditText.addTextChangedListener(new EditTextWatcher(mNameEditText));
            linearLayout.addView(mNameEditText, 1);

            setEditTextError(true, mNameEditText);
        }

        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();

        final TextView titleTextView = mView.findViewById(R.id.dialogTitle);
        titleTextView.setText(getArguments().getString("title"));
        mItemEditText.setHint(getArguments().getString("hint"));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnAddMenuItemListener) {
            mOnAddMenuListener = (OnAddMenuItemListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAddMenuItemListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOnAddMenuListener = null;
    }

    public interface OnAddMenuItemListener {
        void addMenuItem(String item);
        void addSubmenuItem(String submenuName, String submenuLink, String tag);
    }
}
