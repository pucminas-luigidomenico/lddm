package pucminas.computacao.luigi.yourmenu.database.link;

import android.provider.BaseColumns;

public final class LinkContract {
    // Make constructor private to prevent someone accidentally instantiating
    // the contract class.
    private LinkContract() {}

    /* Inner class that defines the table contents */
    public static class LinkEntry implements BaseColumns {
        // Table name
        public static final String TABLE_NAME = "link";

        // Table columns
        public static final String COLUMN_LINK_NAME = "linkName";
        public static final String COLUMN_LINK = "link";
        public static final String COLUMN_MENU_PATH = "menuPath";
    }
}
