package pucminas.computacao.luigi.yourmenu.layout;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pucminas.computacao.luigi.yourmenu.MainActivity;
import pucminas.computacao.luigi.yourmenu.R;
import pucminas.computacao.luigi.yourmenu.adapter.SubmenuListAdapter;
import pucminas.computacao.luigi.yourmenu.database.link.LinkContract;
import pucminas.computacao.luigi.yourmenu.database.link.LinkDbHelper;
import pucminas.computacao.luigi.yourmenu.database.movie.MovieContract;
import pucminas.computacao.luigi.yourmenu.database.movie.MovieDbHelper;
import pucminas.computacao.luigi.yourmenu.database.pdf.PdfContract;
import pucminas.computacao.luigi.yourmenu.database.pdf.PdfDbHelper;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link ListFragment} subclass.
 * Use the {@link SubmenuListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubmenuListFragment extends ListFragment {
    public static final String MOVIE_TAG = "SubmenuMovie";
    public static final String LINK_TAG = "SubmenuLink";
    public static final String PDF_TAG = "SubmenuPDF";
    private static final int PDF_CODE = 1;

    private SQLiteDatabase mDatabase;

    private String mSubmenu;
    private String mTitle;

    private SubmenuListAdapter mSubmenuListAdapter;
    private int mCountSelected;
    private ListView mListView;
    private List<String> mListRemove;

    private AbsListView.MultiChoiceModeListener mMultiChoiceModeListener =
            new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                      long id, boolean checked) {
                    LinearLayout menuLinearLayout = (LinearLayout) mListView.getChildAt(position);
                    TextView menuTextView = (TextView) menuLinearLayout.getChildAt(0);

                    // Change background color accordingly with boolean checked
                    if (checked) {
                        menuLinearLayout.setBackgroundColor(
                                ContextCompat.getColor(getContext(), R.color.long_click)
                        );

                        mListRemove.add(menuTextView.getText().toString());
                        mCountSelected++;
                    } else {
                        menuLinearLayout.setBackgroundColor(Color.TRANSPARENT);

                        mListRemove.remove(menuTextView.getText().toString());
                        mCountSelected--;
                    }

                    String selectedMessage = mCountSelected == 1
                            ? getContext().getString(R.string.item_selected)
                            : getContext().getString(R.string.items_selected);
                    mode.setTitle(mCountSelected + " " + selectedMessage);
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    for (String menu : mListRemove) {
                        mListView.getChildAt(mSubmenuListAdapter.getItemPosition(menu))
                                .setBackgroundColor(Color.TRANSPARENT);

                        mSubmenuListAdapter.remove(menu);
                    }

                    mCountSelected = 0;
                    mListRemove.clear();

                    mode.finish();
                    return true;
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    // Inflate the menu for the CAB
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.context, menu);
                    return true;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    for (int i = 0; i < mSubmenuListAdapter.getCount(); i++) {
                        LinearLayout menuLinearLayout = (LinearLayout) mListView.getChildAt(i);
                        TextView menuTextView = (TextView) menuLinearLayout.getChildAt(0);

                        menuLinearLayout.setBackgroundColor(Color.TRANSPARENT);
                        mListRemove.remove(menuTextView.getText().toString());
                    }

                    mCountSelected = 0;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    // Here you can perform updates to the CAB due to
                    // an invalidate() request
                    return false;
                }
            };

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SubmenuListFragment.
     */
    public static SubmenuListFragment newInstance(String title, String submenu) {
        SubmenuListFragment submenuListFragment = new SubmenuListFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("tag", submenu);
        submenuListFragment.setArguments(args);
        submenuListFragment.mCountSelected = 0;
        submenuListFragment.mListRemove = new ArrayList<>();
        return submenuListFragment;
    }

    public String getTitle() {
        return mTitle;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mTitle = getArguments().getString("title");
            mSubmenu = getArguments().getString("tag");
        }

        mSubmenuListAdapter = new SubmenuListAdapter(getContext(), new ArrayList<>(), mTitle, mSubmenu);
        setListAdapter(mSubmenuListAdapter);

        switch (mSubmenu) {
            case MOVIE_TAG: {
                MovieDbHelper movieDbHelper = new MovieDbHelper(getContext());
                mDatabase = movieDbHelper.getReadableDatabase();
                Cursor cursor = getMovies();

                String[] columns = {MovieContract.MovieEntry.COLUMN_MOVIE_NAME,
                        MovieContract.MovieEntry.COLUMN_MOVIE_LINK};
                mSubmenuListAdapter.populate(cursor, columns);
                break;
            }
            case LINK_TAG: {
                LinkDbHelper linkDbHelper = new LinkDbHelper(getContext());
                mDatabase = linkDbHelper.getReadableDatabase();
                Cursor cursor = getLinks();

                String[] columns = {LinkContract.LinkEntry.COLUMN_LINK_NAME,
                        LinkContract.LinkEntry.COLUMN_LINK};
                mSubmenuListAdapter.populate(cursor, columns);
                break;
            }
            case PDF_TAG:
                PdfDbHelper pdfDbHelper = new PdfDbHelper(getContext());
                mDatabase = pdfDbHelper.getReadableDatabase();
                Cursor cursor = getPdfs();

                String[] columns = {PdfContract.PdfEntry.COLUMN_PDF_NAME,
                        PdfContract.PdfEntry.COLUMN_PDF_PATH};
                mSubmenuListAdapter.populate(cursor, columns);
                break;
        }
    }

    private Cursor getPdfs() {
        String[] columns = {PdfContract.PdfEntry.COLUMN_PDF_NAME,
                PdfContract.PdfEntry.COLUMN_PDF_PATH};
        String selection = PdfContract.PdfEntry.COLUMN_MENU_PATH + " = ?";
        String[] selectionArgs = {mTitle};

        return mDatabase.query(
                PdfContract.PdfEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                PdfContract.PdfEntry._ID);
    }

    private Cursor getLinks() {
        String[] columns = {LinkContract.LinkEntry.COLUMN_LINK_NAME,
                LinkContract.LinkEntry.COLUMN_LINK};
        String selection = LinkContract.LinkEntry.COLUMN_MENU_PATH + " = ?";
        String[] selectionArgs = {mTitle};

        return mDatabase.query(
                LinkContract.LinkEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                LinkContract.LinkEntry._ID
        );
    }

    private Cursor getMovies() {
        String[] columns = {MovieContract.MovieEntry.COLUMN_MOVIE_NAME,
                MovieContract.MovieEntry.COLUMN_MOVIE_LINK};
        String selection = MovieContract.MovieEntry.COLUMN_MENU_PATH + " = ?";
        String[] selectionArgs = {mTitle};

        return mDatabase.query(
                MovieContract.MovieEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                MovieContract.MovieEntry._ID
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_submenu_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Context context = getContext();
        mListView = getListView();
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setMultiChoiceModeListener(mMultiChoiceModeListener);

        mListView.setOnItemClickListener((parent, view, position, id) -> {
            switch (mSubmenu) {
                case MOVIE_TAG:
                    YoutubeFragment youtubeFragment = YoutubeFragment.newInstance(
                            mSubmenuListAdapter.getPathItem(position));

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, youtubeFragment, "YoutubeFragment")
                            .addToBackStack(null)
                            .commit();
                    break;
                case LINK_TAG:
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(mSubmenuListAdapter.getPathItem(position)));

                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(Intent.createChooser(intent, getString(R.string.open_link)));
                    }
                    break;
                case PDF_TAG:
                    fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    String path = mSubmenuListAdapter.getPathItem(position);
                    PdfFragment pdfFragment = PdfFragment.newInstance(path,
                            (String) mSubmenuListAdapter.getItem(position));
                    fragmentTransaction.replace(R.id.fragmentContainer, pdfFragment)
                            .addToBackStack(null)
                            .commit();
                    break;
            }
        });

        final FloatingActionButton addMenu = getActivity().findViewById(R.id.addMenuItem);

        addMenu.setOnClickListener(view -> {
            switch (mSubmenu) {
                case MOVIE_TAG: {
                    String title = context.getString(R.string.add_movie_title);
                    String hint = context.getString(R.string.movie_hint);
                    showAddDialog(title, hint);
                    break;
                }
                case LINK_TAG: {
                    String title = context.getString(R.string.add_link_title);
                    String hint = context.getString(R.string.link_hint);
                    showAddDialog(title, hint);
                    break;
                }
                case PDF_TAG:
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/pdf");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);

                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivityForResult(intent, PDF_CODE);
                    }
                    break;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PDF_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String uriString = uri.toString();
            String displayName = getPdfName(uri);

            mSubmenuListAdapter.addPath(displayName, uriString);
            mSubmenuListAdapter.add(displayName);
        }
    }



    private String getPdfName(Uri uri) {
        String displayName = null;

        Cursor cursor = getActivity().getContentResolver()
                .query(uri, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
        }

        cursor.close();

        return displayName.replace(".pdf", "");
    }

    @Override
    public void onResume() {
        super.onResume();

        String title = getArguments().getString("title");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
    }

    private void showAddDialog(String title, String hint) {
        Context context = getContext();
        AddDialogFragment addDialogFragment = AddDialogFragment
                .newInstance(context, title, hint, mSubmenu);
        addDialogFragment.show(getActivity().getSupportFragmentManager(),
                AddDialogFragment.TAG);
    }

    public void addSubmenuItem(String submenuName, String submenuLink) {
        mSubmenuListAdapter.addPath(submenuName, submenuLink);
        mSubmenuListAdapter.add(submenuName);
    }
}
