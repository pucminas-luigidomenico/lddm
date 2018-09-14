package layout;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;

import pucminas.computacao.luigi.agenda.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MessageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageFragment extends Fragment {
    private static final String ARG_DESCRIPTION = "Description Message";
    private static final String ARG_WHATSAPP = "Send Wpp message";
    private static final String ARG_FACEBOOK = "Post on Facebook";
    private static final String ARG_LINKEDIN = "Post on Linkedin";

    private String description;
    private boolean sendWpp;
    private boolean postFacebook;
    private boolean postLinkedin;

    private OnFragmentInteractionListener messageFragmentListener;

    public MessageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param description Message description.
     * @param sendWpp Boolean value to determine whether organizer wanna send sendWpp or not.
     * @return A new instance of fragment MessageFragment.
     */
    public static MessageFragment newInstance(String description, boolean sendWpp,
                                              boolean postFacebook, boolean postLinkedin) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putString(MessageFragment.ARG_DESCRIPTION, description);
        args.putBoolean(MessageFragment.ARG_WHATSAPP, sendWpp);
        args.putBoolean(MessageFragment.ARG_FACEBOOK, postFacebook);
        args.putBoolean(MessageFragment.ARG_LINKEDIN, postLinkedin);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.description    = getArguments().getString(MessageFragment.ARG_DESCRIPTION);
            this.sendWpp        = getArguments().getBoolean(MessageFragment.ARG_WHATSAPP);
            this.postFacebook   = getArguments().getBoolean(MessageFragment.ARG_FACEBOOK);
            this.postLinkedin   = getArguments().getBoolean(MessageFragment.ARG_LINKEDIN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        EditText descriptionEditText = (EditText) getActivity().findViewById(R.id.description);
        descriptionEditText.setText(this.description);
        descriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                description = editable.toString();
                messageFragmentListener.setDescription(description);
            }
        });

        SwitchCompat wppSwitch = (SwitchCompat) getActivity().findViewById(R.id.send_wpp);
        wppSwitch.setChecked(this.sendWpp);
        wppSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                sendWpp = isChecked;
                messageFragmentListener.setSendWpp(sendWpp);
            }
        });

        SwitchCompat facebookSwitch = (SwitchCompat) getActivity().findViewById(R.id.post_facebook);
        facebookSwitch.setChecked(this.postFacebook);
        facebookSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                postFacebook = isChecked;
                messageFragmentListener.setPostFacebook(postFacebook);
            }
        });

        SwitchCompat linkedinSwitch = (SwitchCompat) getActivity().findViewById(R.id.post_linkedin);
        linkedinSwitch.setChecked(this.postLinkedin);
        linkedinSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                postLinkedin = isChecked;
                messageFragmentListener.setPostLinkedin(postLinkedin);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            messageFragmentListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        messageFragmentListener = null;
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
        void setDescription(String description);
        void setSendWpp(boolean sendWpp);
        void setPostFacebook(boolean postFacebook);
        void setPostLinkedin(boolean postLinkedin);
    }
}
