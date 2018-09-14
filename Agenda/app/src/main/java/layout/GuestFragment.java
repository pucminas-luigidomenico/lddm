package layout;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import pucminas.computacao.luigi.agenda.MainActivity;
import pucminas.computacao.luigi.agenda.Person;
import pucminas.computacao.luigi.agenda.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GuestFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GuestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GuestFragment extends Fragment {
    // Views
    private SortedSet<LinearLayout> guestLinearLayout;

    // Guests
    private SortedSet<Person> guests;

    private OnFragmentInteractionListener guestFragmentListener;

    private View.OnClickListener deleteOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            GuestFragment guestFragment = (GuestFragment) getActivity().getSupportFragmentManager()
                    .findFragmentByTag(MainActivity.GUEST_TAG);

            // Getting correct layout
            LinearLayout layout = null;
            for (LinearLayout linearLayout : guestLinearLayout) {
                if (linearLayout.getChildAt(0).equals(view)) {
                    layout = linearLayout;
                    break;
                }
            }

            guestFragment.removeGuest(layout);

            // Remove from MainActivity SortedSet of Guests
            Person guest = null;
            for (Person p : guests) {
                assert layout != null;
                String textViewName = ((TextView) layout.getChildAt(1)).getText().toString();

                if (p.getName().equalsIgnoreCase(textViewName)) {
                    try {
                        guest = (Person) p.clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }

                    break;
                }
            }

            // Remove LinearLayout from SortedSet
            removeGuestLinearLayout(layout);

            // Remove Guest from SortedSet
            guestFragmentListener.removeGuest(guest);
        }
    };

    public GuestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GuestFragment.
     */
    public static GuestFragment newInstance(SortedSet<Person> guests) {
        GuestFragment fragment = new GuestFragment();
        fragment.setGuests(guests);
        fragment.setGuestLinearLayout(new TreeSet<>(new Comparator<LinearLayout>() {
            @Override
            public int compare(LinearLayout linearLayout, LinearLayout t1) {
                String nameFrom = ((TextView) linearLayout.getChildAt(1)).getText().toString();
                String nameTo   = ((TextView) t1.getChildAt(1)).getText().toString();
                return nameFrom.compareTo(nameTo);
            }
        }));
        return fragment;
    }

    public void setGuests(SortedSet<Person> guests) {
        this.guests = guests;
    }

    public void setGuestLinearLayout(SortedSet<LinearLayout> guestLinearLayout) {
        this.guestLinearLayout = guestLinearLayout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_guest, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        for (Person guest : guests) {
            createGuest(guest);
        }

        FloatingActionButton floatingActionButton = (FloatingActionButton) getActivity()
                .findViewById(R.id.add_guest);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = PersonDialogFragment.newInstance(
                        getString(R.string.title_add_guest), false);
                newFragment.show(getActivity().getSupportFragmentManager(), "AddGuestDialog");
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            guestFragmentListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        for (LinearLayout layout : guestLinearLayout) {
            removeGuest(layout);
        }

        // Clear SortedSet of LinearLayout
        guestLinearLayout.clear();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        guestFragmentListener = null;
    }

    public void createGuest(Person person) {
        // Create LinearLayout horizontal and child elements
        LinearLayout linearLayout   = new LinearLayout(getContext());
        TextView     textView       = new TextView(getContext());
        ImageButton  imageButton    = new ImageButton(getContext());

        // Properties of LinearLayout
        LinearLayout.LayoutParams paramsLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsLayout.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.vertical_margin_10));
        linearLayout.setLayoutParams(paramsLayout);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        // Properties of Text View
        LinearLayout.LayoutParams paramsTextView = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsTextView.weight  = 1;
        paramsTextView.gravity = Gravity.CENTER_VERTICAL;
        textView.setLayoutParams(paramsTextView);
        textView.setClickable(true);
        textView.setGravity(Gravity.CENTER);
        int dimension = (int) getResources().getDimension(R.dimen.padding_5);
        textView.setPadding(dimension, dimension, dimension, dimension);
        textView.setText(person.getName());
        textView.setAllCaps(true);
        textView.setEms(12);
        textView.setBackgroundResource(R.drawable.border_text_view);
        textView.setClickable(false);
        TextViewCompat.setTextAppearance(textView, android.R.style.TextAppearance_Material_Medium);

        // Properties of ImageButton
        LinearLayout.LayoutParams paramsImageButton = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsImageButton.weight = 0;
        paramsImageButton.gravity = Gravity.START;
        imageButton.setLayoutParams(paramsImageButton);
        imageButton.setImageResource(R.drawable.ic_remove_circle_red_24dp);
        imageButton.setBackgroundResource(R.drawable.image_button_bg);
        imageButton.setOnClickListener(deleteOnClickListener);
        imageButton.setId(guestLinearLayout.size());

        // Add Views to Layout
        linearLayout.addView(imageButton);
        linearLayout.addView(textView);

        // Add Guests LinearLayout to ArraySet
        guestLinearLayout.add(linearLayout);

        organizeView();
    }

    private void organizeView() {
        // Get View and ViewGroup
        View view           = getActivity().findViewById(R.id.guests);
        ViewGroup viewGroup = (ViewGroup) view;

        // Remove all views
        viewGroup.removeAllViews();

        // Add views in correct order
        for (LinearLayout layout : guestLinearLayout) {
            viewGroup.addView(layout);
        }
    }

    public void removeGuest(LinearLayout linearLayout) {
        // Get View and ViewGroup
        View view           = getActivity().findViewById(R.id.guests);
        ViewGroup viewGroup = (ViewGroup) view;

        // Remove LinearLayout from view
        viewGroup.removeView(linearLayout);
    }

    /**
     * Remove LinearLayout from SortedSet
     * @param linearLayout LinearLayout to remove from SortedSet
     */
    private void removeGuestLinearLayout(LinearLayout linearLayout) {
        // 1º: Get Array of LinearLayout
        LinearLayout[] arrayLayout = guestLinearLayout.toArray(new LinearLayout[0]);

        // 2º: Do binary search
        String searchName       = ((TextView) linearLayout.getChildAt(1)).getText().toString();
        int indexSearchLayout   = -1;
        int right               = arrayLayout.length - 1;
        int left                = 0;
        int middle;

        while (left <= right && indexSearchLayout == -1) {
            middle                      = (left + right) / 2;
            LinearLayout currentLayout  = arrayLayout[middle];
            String currentName          = ((TextView) currentLayout.getChildAt(1)).getText().toString();

            if (searchName.equalsIgnoreCase(currentName)) {
                indexSearchLayout = middle;
            } else if (searchName.compareToIgnoreCase(currentName) > 0) {
                left = middle + 1;
            } else {
                right = middle - 1;
            }
        }

        // 3º Remove LinearLayout from SortedSet
        guestLinearLayout.remove(arrayLayout[indexSearchLayout]);
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
        boolean removeGuest(Object guest);
    }
}
