package pucminas.computacao.luigi.yourmenu;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pucminas.computacao.luigi.yourmenu.layout.AddDialogFragment;
import pucminas.computacao.luigi.yourmenu.layout.MainFragment;
import pucminas.computacao.luigi.yourmenu.layout.MenuListFragment;
import pucminas.computacao.luigi.yourmenu.layout.SubmenuListFragment;
import pucminas.computacao.luigi.yourmenu.adapter.NavigationDrawerExpandableListAdapter;
import pucminas.computacao.luigi.yourmenu.data.MenuData;

public class MainActivity extends AppCompatActivity implements
        AddDialogFragment.OnAddMenuItemListener {

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ExpandableListView mExpandableListView;
    private List<String> mExpandableListTitle;
    private Map<String, List<Object[]>> mExpandableListData;
    private ExpandableListAdapter mExpandableListAdapter;

    public ExpandableListAdapter getExpandableListAdapter() {
        return mExpandableListAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mExpandableListView = findViewById(R.id.navList);

        LayoutInflater inflater = getLayoutInflater();
        View listHeaderView = inflater.inflate(R.layout.nav_header_main, null, false);
        mExpandableListView.addHeaderView(listHeaderView);

        mExpandableListData = MenuData.getMenuData(this);
        mExpandableListTitle = new ArrayList<>(mExpandableListData.keySet());

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.main_preference_file_key), Context.MODE_PRIVATE);

        String tag = sharedPreferences.getString("FragmentTag", MainFragment.TAG);

        switch (tag) {
            case MainFragment.TAG:
                showFragment(MainFragment.newInstance(), tag, false);
                break;
            case MenuListFragment.TAG:
                showFragment(MenuListFragment.newInstance(), tag, true);
                break;
            default:
                String title = sharedPreferences.getString("Title", getString(R.string.app_name));
                showFragment(SubmenuListFragment.newInstance(title, tag), tag, false);
                break;
        }
   }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.main_preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        editor.putString("FragmentTag", fragment.getTag());

        if (fragment instanceof SubmenuListFragment) {
            editor.putString("Title", ((SubmenuListFragment) fragment).getTitle());
        }

        editor.apply();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Noinspection SimplifiableIfStatement
        else if (id == R.id.action_settings) {
            FragmentManager fragmentManager = getSupportFragmentManager();

            if (fragmentManager.findFragmentByTag(MenuListFragment.TAG) == null) {
                showFragment(MenuListFragment.newInstance(), MenuListFragment.TAG, true);
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void addDrawerItems() {
        mExpandableListAdapter = new NavigationDrawerExpandableListAdapter(this,
                mExpandableListTitle, mExpandableListData);
        mExpandableListView.setAdapter(mExpandableListAdapter);

        mExpandableListView.setOnGroupClickListener((expandableListView, view, position, id) -> {
            String selectedItem = mExpandableListTitle.get(position);

            if (selectedItem.equals(getString(R.string.home))) {
                showFragment(MainFragment.newInstance(), MainFragment.TAG, false);
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }

            return position == 0;
        });


        mExpandableListView.setOnChildClickListener(
                (parent, view, groupPosition, childPosition, id) -> {

            String groupTitle = mExpandableListTitle.get(groupPosition);
            String childTitle = (String) mExpandableListData.get(groupTitle).get(childPosition)[1];

            SubmenuListFragment submenuListFragment = null;
            String tag = "";

            if (childTitle.equals(getString(R.string.movies))) {
                tag = SubmenuListFragment.MOVIE_TAG;

                submenuListFragment = SubmenuListFragment.
                        newInstance(groupTitle + " - " + childTitle, tag);
            } else if (childTitle.equals(getString(R.string.links))) {
                tag = SubmenuListFragment.LINK_TAG;

                submenuListFragment = SubmenuListFragment.
                        newInstance(groupTitle + " - " + childTitle, tag);
            } else if (childTitle.equals(getString(R.string.pdf))) {
                tag = SubmenuListFragment.PDF_TAG;

                submenuListFragment = SubmenuListFragment
                        .newInstance(groupTitle + " - " + childTitle, tag);
            }

            showFragment(submenuListFragment, tag, false);

            mDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    @Override
    public void addMenuItem(String item) {
        MenuListFragment menuListFragment = (MenuListFragment) getSupportFragmentManager().
                findFragmentByTag(MenuListFragment.TAG);
        menuListFragment.addMenuItem(item);
    }

    @Override
    public void addSubmenuItem(String submenuName, String submenuLink, String tag) {
        SubmenuListFragment submenuListFragment = (SubmenuListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragmentContainer);
        submenuListFragment.addSubmenuItem(submenuName, submenuLink);
    }

    private void showFragment(Fragment fragment, String tag, boolean addToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragmentContainer, fragment, tag);

        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null);
        } else {
            fragmentManager.popBackStack();
        }

        fragmentTransaction.commit();
    }
}
