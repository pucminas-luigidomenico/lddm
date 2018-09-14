package pucminas.computacao.luigi.yourmenu.database.pdf;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class PdfDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "pdf.db";
    private static final int DATABASE_VERSION = 1;

    public PdfDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                PdfContract.PdfEntry.TABLE_NAME + " (" +
                PdfContract.PdfEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PdfContract.PdfEntry.COLUMN_PDF_NAME + " TEXT NOT NULL," +
                PdfContract.PdfEntry.COLUMN_PDF_PATH + " TEXT NOT NULL," +
                PdfContract.PdfEntry.COLUMN_MENU_PATH + " TEXT NOT NULL" +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PdfContract.PdfEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
