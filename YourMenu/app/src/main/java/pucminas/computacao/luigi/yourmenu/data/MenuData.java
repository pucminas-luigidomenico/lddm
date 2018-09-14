package pucminas.computacao.luigi.yourmenu.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pucminas.computacao.luigi.yourmenu.R;

public class MenuData {
    public static Map<String, List<Object[]>> getMenuData(Context context) {
        Map<String, List<Object[]>> expandableListData = new TreeMap<>();

        SharedPreferences sharedPreferences = context.getSharedPreferences(context
                .getString(R.string.menu_preference_file_key), Context.MODE_PRIVATE);
        Map<String, ?> itemsMenu = sharedPreferences.getAll();

        // Default menus
        if (itemsMenu.size() == 0) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            for (int i = 1; i <= 8; i++) {
                String key = i + "º período";
                String value = i + "º período";
                editor.putString(key, value);
                editor.apply();

                itemsMenu = sharedPreferences.getAll();
            }
        }

        List<Object> titles = new ArrayList<>(itemsMenu.values());
        List<Object[]> items = SubmenuData.getSubmenuData(context);

        for (Object title : titles) {
            expandableListData.put((String) title, items);
        }

        return expandableListData;
    }
}
