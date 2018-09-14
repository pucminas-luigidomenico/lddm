package pucminas.computacao.luigi.yourmenu.database.movie;

import android.provider.BaseColumns;

public final class MovieContract {
    // Make constructor private to prevent someone accidentally instantiating
    // the contract class.
    private MovieContract() {}

    /* Inner class that defines the table contents */
    public static class MovieEntry implements BaseColumns {
        // Table name
        public static final String TABLE_NAME = "movie";

        // Table columns
        public static final String COLUMN_MOVIE_NAME = "movieName";
        public static final String COLUMN_MOVIE_LINK = "movieLink";
        public static final String COLUMN_MENU_PATH = "menuPath";
    }
}
