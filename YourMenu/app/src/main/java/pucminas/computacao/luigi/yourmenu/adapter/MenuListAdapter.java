package pucminas.computacao.luigi.yourmenu.adapter;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;

import pucminas.computacao.luigi.yourmenu.MainActivity;
import pucminas.computacao.luigi.yourmenu.R;

public class MenuListAdapter extends CustomListAdapter {
    public MenuListAdapter(Context context, List<String> listItem) {
        super(context, listItem);
    }

    @Override
    public void add(String menuItem) {
        super.add(menuItem);

        // Add item to SharedPreferences
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mContext
                .getString(R.string.menu_preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(menuItem, menuItem);
        editor.apply();

        // Add item to Navigation Drawer menu
        NavigationDrawerExpandableListAdapter navigationDrawerExpandableListAdapter =
                (NavigationDrawerExpandableListAdapter) ((MainActivity) mContext)
                        .getExpandableListAdapter();
        navigationDrawerExpandableListAdapter.add(menuItem);
    }

    public void remove(String menuItem) {
        super.remove(menuItem);

        // Remove item from SharedPreferences
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mContext
                .getString(R.string.menu_preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove(menuItem);
        editor.apply();

        // Remove item from Navigation Drawer menu
        NavigationDrawerExpandableListAdapter navigationDrawerExpandableListAdapter = (NavigationDrawerExpandableListAdapter)
                ((MainActivity) mContext).getExpandableListAdapter();
        navigationDrawerExpandableListAdapter.remove(menuItem);
    }
}
