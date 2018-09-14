package pucminas.computacao.luigi.yourmenu.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import pucminas.computacao.luigi.yourmenu.R;
import pucminas.computacao.luigi.yourmenu.data.SubmenuData;

public class NavigationDrawerExpandableListAdapter extends BaseExpandableListAdapter {
    private final Context mContext;
    private List<String> mExpandableListTitle;
    private Map<String, List<Object[]>> mExpandableListDetail;
    private LayoutInflater mLayoutInflater;

    public NavigationDrawerExpandableListAdapter(Context context, List<String> expandableListTitle,
                                                 Map<String, List<Object[]>> expandableListDetail) {
        mContext = context;
        mExpandableListTitle = expandableListTitle;
        mExpandableListDetail = expandableListDetail;

        // Home item menu is fixed
        mExpandableListTitle.add(0, mContext.getString(R.string.home));
        mExpandableListDetail.put(mExpandableListTitle.get(0), new ArrayList<>());

        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public List<String> getExpandableListTitle() {
        return mExpandableListTitle;
    }

    void add(String menuItem) {
        // Get Submenu data
        List<Object[]> items = SubmenuData.getSubmenuData(mContext);

        // Add menu and submenu to List and Map
        mExpandableListTitle.add(menuItem);
        mExpandableListDetail.put(menuItem, items);

        // Sort elements after add
        Collections.sort(mExpandableListTitle.subList(1, mExpandableListTitle.size()),
                String::compareToIgnoreCase);

        notifyDataSetChanged();
    }

    void remove(String menuItem) {
        mExpandableListTitle.remove(menuItem);
        mExpandableListDetail.remove(menuItem);

        notifyDataSetChanged();
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return mExpandableListDetail.get(mExpandableListTitle.get(listPosition))
                .get(expandedListPosition)[1];
    }

    private Object[] getChildArray(int listPosition, int expandedListPosition) {
        return mExpandableListDetail.get(mExpandableListTitle.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        Object[] item = getChildArray(listPosition, expandedListPosition);
        final String expandedListText = (String) item[1];

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.nav_list_item, null);
        }

        TextView expandableListTextView = convertView
                .findViewById(R.id.expandedListItem);

        expandableListTextView.setText(expandedListText);
        expandableListTextView.setCompoundDrawablesWithIntrinsicBounds((Drawable) item[0],
                null, null, null);
        expandableListTextView.setCompoundDrawablePadding((int) mContext.getResources()
                .getDimension(R.dimen.layout_padding_default));
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return mExpandableListDetail.get(mExpandableListTitle.get(listPosition)).size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return mExpandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return mExpandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.nav_list_group, null);
        }

        // Group icon
        Drawable groupIcon = listTitle.equals(mContext.getString(R.string.home))
                                ? mContext.getDrawable(R.drawable.ic_home_24dp)
                                : mContext.getDrawable(R.drawable.ic_folder_24dp);

        TextView listTitleTextView = convertView
                .findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        listTitleTextView.setCompoundDrawablesWithIntrinsicBounds(groupIcon, null, null, null);
        listTitleTextView.setCompoundDrawablePadding((int) mContext.getResources()
                .getDimension(R.dimen.layout_padding_default));

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}
