package pucminas.computacao.luigi.yourmenu.layout;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import pucminas.computacao.luigi.yourmenu.adapter.NavigationDrawerExpandableListAdapter;
import pucminas.computacao.luigi.yourmenu.adapter.MenuListAdapter;

/**
 * A simple {@link ListFragment} subclass.
 * Use the {@link MenuListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuListFragment extends ListFragment {
    public static final String TAG = "MenuListFragment";
    private int mCountSelected;
    private MenuListAdapter mMenuListAdapter;
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
                mListView.getChildAt(mMenuListAdapter.getItemPosition(menu))
                        .setBackgroundColor(Color.TRANSPARENT);

                mMenuListAdapter.remove(menu);
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
            for (int i = 0; i < mMenuListAdapter.getCount(); i++) {
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
     * @return A new instance of fragment MenuListFragment.
     */
    public static MenuListFragment newInstance() {
        MenuListFragment menuListFragment = new MenuListFragment();
        menuListFragment.mCountSelected = 0;
        menuListFragment.mListRemove = new ArrayList<>();
        return menuListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Context context = getContext();
        MainActivity mainActivity = (MainActivity) getActivity();
        NavigationDrawerExpandableListAdapter navigationDrawerExpandableListAdapter =
                (NavigationDrawerExpandableListAdapter) mainActivity.getExpandableListAdapter();

        List<String> listMenuItem = new ArrayList<>(navigationDrawerExpandableListAdapter
                .getExpandableListTitle());
        // Remove Home menu item, because it's fixed.
        listMenuItem.remove(0);

        mMenuListAdapter = new MenuListAdapter(context, listMenuItem);

        setListAdapter(mMenuListAdapter);

        mListView = getListView();
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setMultiChoiceModeListener(mMultiChoiceModeListener);

        final FloatingActionButton addMenu = mainActivity.findViewById(R.id.addMenuItem);
        addMenu.setOnClickListener(view -> {
            AddDialogFragment addDialogFragment = AddDialogFragment
                    .newInstance(context, context.getString(R.string.add_menu_title),
                            context.getString(R.string.menu_hint), "");
            addDialogFragment.show(getActivity().getSupportFragmentManager(),
                    AddDialogFragment.TAG);
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setEnabled(false);
        item.setVisible(false);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name);
    }

    public void addMenuItem(String title) {
        mMenuListAdapter.add(title);
    }
}
