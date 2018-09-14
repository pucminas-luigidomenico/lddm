package pucminas.computacao.luigi.yourmenu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import pucminas.computacao.luigi.yourmenu.R;

public class CustomListAdapter extends BaseAdapter {
    List<String> mListItem;
    private LayoutInflater mLayoutInflater;
    Context mContext;

    CustomListAdapter(Context context, List<String> listItem) {
        mContext = context;
        mListItem = listItem;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getItemPosition(String item) {
        return mListItem.indexOf(item);
    }

    public void add(String item) {
        // Add item to List and Adapter
        mListItem.add(item);

        // Sort elements after add
        Collections.sort(mListItem, String::compareToIgnoreCase);

        notifyDataSetChanged();
    }

    public void remove(String item) {
        // Remove item from List and Adapter
        mListItem.remove(item);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mListItem.size();
    }

    @Override
    public Object getItem(int position) {
        return mListItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        String itemTitle = mListItem.get(position);

        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.menu_list_item, null);
        }

        TextView listTextView = view.findViewById(R.id.menuListItem);
        listTextView.setText(itemTitle);

        return view;
    }
}
