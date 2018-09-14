package pucminas.computacao.luigi.agenda;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.widget.ShareDialog;

import org.apache.commons.lang3.text.WordUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import layout.DatePickerDialogFragment;
import layout.GuestFragment;
import layout.EventFragment;
import layout.MessageFragment;
import layout.PersonDialogFragment;
import layout.TimePickerDialogFragment;

public class MainActivity extends AppCompatActivity
                          implements EventFragment.OnFragmentInteractionListener,
                                     GuestFragment.OnFragmentInteractionListener,
                                     MessageFragment.OnFragmentInteractionListener,
                                     PersonDialogFragment.OnFragmentInteractionListener,
                                     DatePickerDialogFragment.OnFragmentInteractionListener,
                                     TimePickerDialogFragment.OnFragmentInteractionListener {

    // Bottom Navigation
    private BottomNavigationView navigation;
    private int selectedItemId;

    // Request Codes
    private static final int CALENDAR_REQUEST  = 1;
    private static final int WPP_REQUEST       = 2;
    private static final int FACEBOOK_REQUEST  = 3;
    private static final int LINKEDIN_REQUEST  = 4;

    // Fragment Tags
    public static final String EVENT_TAG    = "EventFragment";
    public static final String GUEST_TAG    = "GuestFragment";
    public static final String MESSAGE_TAG  = "MessageFragment";

    // Event Data
    private String nameEvent;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;

    // Contacts
    private Person organizer;
    private SortedSet<Person> guests;

    // Message data
    private String description;
    private boolean sendWpp;
    private boolean postFacebook;
    private boolean postLinkedin;

    // Listener to bottom navigation items
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_event:
                    switchFragment(EventFragment.newInstance(nameEvent, startDate, endDate,
                            startTime, endTime, organizer), EVENT_TAG);
                    return true;
                case R.id.navigation_guests:
                    switchFragment(GuestFragment.newInstance(guests), GUEST_TAG);
                    return true;
                case R.id.navigation_message:
                    switchFragment(MessageFragment.newInstance(description, sendWpp, postFacebook,
                            postLinkedin), MESSAGE_TAG);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set bottom navigation view
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        // Create empty HashSet of guests
        guests = new TreeSet<>();

        if (savedInstanceState != null) {
            // Get selectedItemId
            selectedItemId = savedInstanceState.getInt("selectedItemId", R.id.navigation_event);

            // Get Event Fragment data
            this.getEventFragmentData(savedInstanceState);

            // Get Guest Fragment data
            this.getGuestFragmentData(savedInstanceState);

            // Get Message Fragment data
            this.getMessageFragmentData(savedInstanceState);
        } else {
            // Initial selectedItemId
            selectedItemId = R.id.navigation_event;

            // Format for EventFragment
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd, MMMM - yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm a");

            // Values for EventFragment
            nameEvent = "";
            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            try {
                // Start values
                Date date = new SimpleDateFormat("dd/MM/yyyy").parse(day + "/" + month + "/" + year);
                Date time = new SimpleDateFormat("HH:mm").parse(hour + ":" + minute);
                startDate = WordUtils.capitalize(dateFormat.format(date));
                startTime = timeFormat.format(time.getTime());

                // End values
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                date = new SimpleDateFormat("dd/MM/yyyy").parse(day + "/" + month + "/" + year);
                endDate = WordUtils.capitalize(dateFormat.format(date));
                endTime = timeFormat.format(time.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Values for MessageFragment
            this.description    = "";
            this.sendWpp        = false;
            this.postFacebook   = false;
            this.postLinkedin   = false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        this.saveEventFragmentData(outState);
        this.saveGuestFragmentData(outState);
        this.saveMessageFragmentData(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();

        switch(selectedItemId) {
            case R.id.navigation_event:
                switchFragment(EventFragment.newInstance(this.nameEvent, this.startDate,
                        this.endDate, this.startTime, this.endTime, this.organizer),
                        MainActivity.EVENT_TAG);
                break;
            case R.id.navigation_guests:
                switchFragment(GuestFragment.newInstance(guests), MainActivity.GUEST_TAG);
                break;
            case R.id.navigation_message:
                switchFragment(MessageFragment.newInstance(this.description,
                        this.sendWpp, this.postFacebook, this.postLinkedin),
                        MainActivity.MESSAGE_TAG);
        }

        navigation.setSelectedItemId(selectedItemId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            String message = "Evento: " +
                    "\n\tNome: " + this.nameEvent +
                    "\n\tData de início: " + startDate + " " + startTime +
                    "\n\tData de término: " + endDate + " " + endTime +
                    "\n\n\nOrganizador: " +
                    "\n\tNome: " + this.organizer.getName() +
                    "\n\tEmail: " + this.organizer.getEmail() +
                    "\n\tTelefone: " + this.organizer.getPhone() +
                    "\n\n\nDescrição: " +
                    "\t" + this.description;

            // Save event on calendar
            this.saveEvent(message);

            // Sending wpp message
            if (this.sendWpp) {
                this.sendWppMessage(message);
            }

            // Post on facebook
            if (this.postFacebook) {
                this.postFacebook(message);
            }

            // Post on linkedin
            if (this.postLinkedin) {
                this.postLinkedin(message);
            }
        } catch (NullPointerException nullPointerException) {
            // Show error
            Context context = getApplicationContext();
            Toast toastError = Toast.makeText(context, getString(R.string.error_message_finish),
                    Toast.LENGTH_LONG);
            toastError.show();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Toast to simple notification of success
        Toast toast;

        // Calendar request
        if (requestCode == MainActivity.CALENDAR_REQUEST) {
            String message = resultCode == 1
                    ? getString(R.string.calendar_success)
                    : getString(R.string.calendar_error);
            toast = Toast.makeText(getApplicationContext(), message,
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * Get value of year on a String.
     * That String need to be in format yyyy/MM/dd HH:mm
     *
     * @param dateTime DateTime to get value of year.
     * @return an Integer value of year.
     */
    public static int getYear(String dateTime) {
        return Integer.parseInt(dateTime.substring(0, dateTime.indexOf("/")));
    }

    /**
     * Get value of month on a String.
     * That String need to be in format yyyy/MM/dd HH:mm
     *
     * @param dateTime DateTime to get value of month.
     * @return an Integer value of month.
     */
    public static int getMonth(String dateTime) {
        return Integer.parseInt(dateTime.substring(dateTime.indexOf("/") + 1,
                dateTime.lastIndexOf("/")));
    }

    /**
     * Get value of day on a String.
     * That String need to be in format yyyy/MM/dd HH:mm
     *
     * @param dateTime DateTime to get value of day.
     * @return an Integer value of day.
     */
    public static int getDay(String dateTime) {
        return Integer.parseInt(dateTime.substring(dateTime.lastIndexOf("/") + 1,
                dateTime.indexOf(" ")));
    }

    /**
     * Get value of minute on a String.
     * That String need to be in format yyyy/MM/dd HH:mm
     *
     * @param dateTime DateTime to get value of minute.
     * @return an Integer value of minute.
     */
    public static int getMinute(String dateTime) {
        return Integer.parseInt(dateTime.substring(dateTime.indexOf(" ") + 1,
                dateTime.indexOf(":")));

    }

    /**
     * Get value of second on a String.
     * That String need to be in format yyyy/MM/dd HH:mm
     *
     * @param dateTime DateTime to get value of second.
     * @return an Integer value of second.
     */
    public static int getSecond(String dateTime) {
        return Integer.parseInt(dateTime.substring(dateTime.indexOf(":") + 1));
    }

    /**
     * Save event on calendar app of android.
     *
     * @param message Message to put into description field.
     * @throws ParseException Exception that DateFormat throws on parse method.
     */
    private void saveEvent(String message) throws ParseException {
        // Create DateFormats to get begin and end time
        SimpleDateFormat parseDateTime  = new SimpleDateFormat("EEE dd, MMMM - yyyy HH:mm a");
        SimpleDateFormat formatDateTime = new SimpleDateFormat("yyyy/MM/dd HH:mm");

        // Get start and End dateTime
        String startDateTime = startDate + " " + startTime;
        String endDateTime   = endDate + " " + endTime;

        // Parse DateTime
        Date start = parseDateTime.parse(startDateTime);
        Date end   = parseDateTime.parse(endDateTime);

        // Format DateTime
        startDateTime = formatDateTime.format(start);
        endDateTime   = formatDateTime.format(end);

        // Create Calendar beginTime and endTime
        Calendar beginTime = Calendar.getInstance();
        Calendar endTime   = Calendar.getInstance();

        // Set calendar data
        beginTime.set(MainActivity.getYear(startDateTime), MainActivity.getMonth(startDateTime),
                MainActivity.getDay(startDateTime), MainActivity.getMinute(startDateTime),
                MainActivity.getSecond(startDateTime));

        endTime.set(MainActivity.getYear(endDateTime), MainActivity.getMonth(endDateTime),
                MainActivity.getDay(endDateTime), MainActivity.getMinute(endDateTime),
                MainActivity.getSecond(endDateTime));

        // Get email of all guests
        StringBuilder emailGuests = new StringBuilder();
        for (Person guest : guests) {
            emailGuests.append(guest.getEmail());
        }

        // Remove character "," at the end of string
        if (emailGuests.length() > 0) {
            emailGuests.deleteCharAt(emailGuests.length() - 1);
        }

        // Create a new intent and try to open calendar app to finish saving event.
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE, nameEvent)
                .putExtra(CalendarContract.Events.DESCRIPTION, message)
                .putExtra(CalendarContract.Events.OWNER_ACCOUNT, organizer.getName())
                .putExtra(CalendarContract.Events.ORGANIZER, organizer.getEmail())
                .putExtra(Intent.EXTRA_EMAIL, emailGuests.toString())
                .putExtra(CalendarContract.Events.ALL_DAY, 0);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, MainActivity.CALENDAR_REQUEST);
        }
    }

    /**
     * Send message to guests by whatsapp.
     * The whatsapp intent open whatsapp to let user select all the contacts
     * he wanna send message.
     *
     * @param message Description of the event.
     */
    private void sendWppMessage(String message) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.setPackage("com.whatsapp");

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, MainActivity.WPP_REQUEST);
        }
    }

    /**
     * Post new event on organizer's facebook.
     *
     * @param message Message with event's details.
     */
    private void postFacebook(String message) {
        // Create an object
        ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                .putString("og:type", "books.book")
                .putString("og:title", this.nameEvent)
                .putString("og:description", message)
                .build();

        // Create an action
        ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                .setActionType("books.read")
                .putObject("book", object)
                .build();

        // Create the content
        ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                .setPreviewPropertyName("book")
                .setAction(action)
                .build();

        ShareDialog.show(MainActivity.this, content);
    }

    /**
     * Post new event on organizer's linkedin.
     *
     * @param message Message with event's details.
     */
    private void postLinkedin(String message) {
        Intent intent = ShareCompat.IntentBuilder.from(MainActivity.this)
                .setType("text/plain")
                .setText(message)
                .getIntent();
        intent.setPackage("com.linkedin.android");
        intent.setAction(Intent.ACTION_SEND);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, MainActivity.LINKEDIN_REQUEST);
        }
    }

    /**
     * Method to save data of event fragment on app pause.
     *
     * @param bundle Bundle to save data.
     */
    private void saveEventFragmentData(Bundle bundle) {
        // Get selected item id
        selectedItemId = navigation.getSelectedItemId();

        // Save selectedItemId on SharedPreferences
        bundle.putInt("selectedItemId", selectedItemId);

        // Save event name
        bundle.putString("name_event", nameEvent);

        // Save start date and hour
        bundle.putString("start_date", startDate);
        bundle.putString("start_hour", startTime);

        // Save end date and hour
        bundle.putString("end_date", endDate);
        bundle.putString("end_hour", endTime);

        // Save organizer data
        if (organizer != null) {
            bundle.putString("organizerName", organizer.getName());
            bundle.putString("organizerEmail", organizer.getEmail());
            bundle.putString("organizerPhone", organizer.getPhone());
            bundle.putString("organizerPersonType", organizer.getPersonType());
        }
    }

    /**
     * Method to get event fragment data (with bundle of onCreate method lifecycle).
     *
     * @param bundle Bundle to get data.
     */
    private void getEventFragmentData(Bundle bundle) {
        // Get name event
        nameEvent = bundle.getString("name_event", nameEvent);

        // Get start date and hour
        startDate = bundle.getString("start_date", startDate);
        startTime = bundle.getString("start_hour", startTime);

        // Get end date and hour
        endDate = bundle.getString("end_date", endDate);
        endTime = bundle.getString("end_hour", endTime);

        // Get organizer
        String name  = bundle.getString("organizerName", "");
        String email = bundle.getString("organizerEmail", "");
        String phone = bundle.getString("organizerPhone", "");
        String type  = bundle.getString("organizerPersonType", "");
        organizer    = name.length() > 0
                        ? new Person(name, email, phone, type)
                        : null;
    }

    /**
     * Method to save data of guest fragment on app pause.
     *
     * @param bundle Bundle to save data.
     */
    private void saveGuestFragmentData(Bundle bundle) {
        // Save guests data
        int i = 0;
        for (Person guest : guests) {
            bundle.putString("guestName - " + i, guest.getName());
            bundle.putString("guestEmail - " + i, guest.getEmail());
            bundle.putString("guestPhone - " + i, guest.getPhone());
            bundle.putString("guestPersonType - " + i, guest.getPersonType());
            i++;
        }

        // Save number of guests
        bundle.putInt("numberGuests", i);
    }

    /**
     * Method to get guest fragment data (with bundle of onCreate method lifecycle).
     *
     * @param bundle Bundle to get data.
     */
    private void getGuestFragmentData(Bundle bundle) {
        // Get number of guests
        int numberGuests = bundle.getInt("numberGuests", 0);

        // Get Guests data
        for (int i = 0; i < numberGuests; i++) {
            String name  = bundle.getString("guestName - " + i, "");
            String email = bundle.getString("guestEmail - " + i, "");
            String phone = bundle.getString("guestPhone - " + i, "");
            String type  = bundle.getString("guestPersonType - " + i, "");
            Person guest = new Person(name, email, phone, type);

            // Add on SortedSet
            guests.add(guest);
        }
    }

    /**
     * Method to save data of message fragment on app pause.
     *
     * @param bundle Bundle to save data.
     */
    private void saveMessageFragmentData(Bundle bundle) {
        // Save description message
        bundle.putString("description", this.description);

        // Save send whatsapp boolean
        bundle.putBoolean("sendWpp", this.sendWpp);

        // Save post facebook boolean
        bundle.putBoolean("postFacebook", this.postFacebook);

        // Save post linkedin boolean
        bundle.putBoolean("postLinkedin", this.postLinkedin);
    }

    /**
     * Method to get message fragment data (with bundle of onCreate method lifecycle).
     *
     * @param bundle Bundle to get data.
     */
    private void getMessageFragmentData(Bundle bundle) {
        // Get description message
        this.description = bundle.getString("description", "");

        // Get send whatsapp boolean
        this.sendWpp = bundle.getBoolean("sendWpp", false);

        // Get post facebook boolean
        this.postFacebook = bundle.getBoolean("postFacebook", false);

        // Get post linkedin boolean
        this.postLinkedin = bundle.getBoolean("postLinkedin", false);
    }

    /**
     * Method to switch fragments on click of bottom navigation's item.
     *
     * @param fragment Fragment to show.
     * @param tag Tag of fragment.
     */
    private void switchFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, tag);
        fragmentTransaction.commit();
    }

    /**
     * Method of interface for communication by MainActivity and MessageFragment.
     * This method is used to persist event data on switch fragment.
     *
     * @param description Event's description
     */
    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Method of interface for communication by MainActivity and MessageFragment.
     * This method is used to persist event data on switch fragment.
     *
     * @param sendWpp Boolean value to determine whether organizer wanna send wpp message or not.
     */
    public void setSendWpp(boolean sendWpp) {
        this.sendWpp = sendWpp;
    }

    /**
     * Method of interface for communication by MainActivity and MessageFragment.
     * This method is used to persist event data on switch fragment.
     *
     * @param postFacebook Boolean value to determine whether organizer wanna post event on
     *                     facebook or not.
     */
    public void setPostFacebook(boolean postFacebook) {
        this.postFacebook = postFacebook;
    }

    /**
     * Method of interface for communication by MainActivity and MessageFragment.
     * This method is used to persist event data on switch fragment.
     *
     * @param postLinkedin Boolean value to determine whether organizer wanna post event on
     *                     linkedin or not.
     */
    public void setPostLinkedin(boolean postLinkedin) {
        this.postLinkedin = postLinkedin;
    }

    /**
     * Method of interface for communication by MainActivity and EventFragment.
     * This method is used to persist event datas on switch fragment.
     *
     * @param nameEvent Name of new event
     * @param startDate Start date
     * @param endDate   End date
     * @param startHour Start hour
     * @param endHour   End hour
     */
    @Override
    public void setEventData(String nameEvent, String startDate, String endDate,
                             String startHour, String endHour) {
        this.nameEvent  = nameEvent;
        this.startDate  = startDate;
        this.endDate    = endDate;
        this.startTime  = startHour;
        this.endTime    = endHour;
    }

    /**
     * Method of interface for communication by MainActivity and PersonDialogFragment.
     * This method is used to add new organizer or new guests.
     *
     * @param person Person to add
     * @throws CloneNotSupportedException Cloneable not implemented
     */
    @Override
    public void setPersonData(Person person) throws CloneNotSupportedException {
        if (person.getPersonType().equals("Organizer")) {
            organizer = person;
            EventFragment eventFragment = (EventFragment) getSupportFragmentManager()
                    .findFragmentByTag(EVENT_TAG);
            eventFragment.createOrganizer(organizer);
        } else {
            guests.add(person);
            GuestFragment guestFragment = (GuestFragment) getSupportFragmentManager()
                    .findFragmentByTag(GUEST_TAG);
            guestFragment.setGuests(guests);
            guestFragment.createGuest(person);
        }
    }

    /**
     * Method to remove organizer from MainActivity.
     *
     * @return true
     */
    @Override
    public boolean removeOrganizer() {
        this.organizer = null;
        return true;
    }

    /**
     * Method of interface for communication by MainActivity and GuestFragment.
     * This method is used to remove guest.
     *
     * @param guest Guest is going to be removed.
     * @return True if removed guest with success, false if not.
     */
    @Override
    public boolean removeGuest(Object guest) {
        final boolean remove = guests.remove(guest);
        GuestFragment guestFragment = (GuestFragment) getSupportFragmentManager()
                .findFragmentByTag(GUEST_TAG);
        guestFragment.setGuests(guests);

        return remove;
    }

    /**
     * Method of interface for communication by MainActivity and DatePickerDialogFragment
     * or TimePickerDialogFragment.
     * This method is used to call date and time validator.
     */
    @Override
    public void callCheckDateTime() throws ParseException {
        ((EventFragment) getSupportFragmentManager().findFragmentByTag(EVENT_TAG))
                .checkDateTime();
    }
}
