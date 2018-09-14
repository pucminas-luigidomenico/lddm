package pucminas.computacao.luigi.yourmenu.database.link;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LinkDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "link.db";
    private static final int DATABASE_VERSION = 1;

    public LinkDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                LinkContract.LinkEntry.TABLE_NAME + " (" +
                LinkContract.LinkEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                LinkContract.LinkEntry.COLUMN_LINK_NAME + " TEXT NOT NULL," +
                LinkContract.LinkEntry.COLUMN_LINK + " TEXT NOT NULL," +
                LinkContract.LinkEntry.COLUMN_MENU_PATH + " TEXT NOT NULL" +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LinkContract.LinkEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
