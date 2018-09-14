package pucminas.computacao.luigi.yourmenu.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pucminas.computacao.luigi.yourmenu.database.link.LinkContract;
import pucminas.computacao.luigi.yourmenu.database.link.LinkDbHelper;
import pucminas.computacao.luigi.yourmenu.database.movie.MovieContract;
import pucminas.computacao.luigi.yourmenu.database.movie.MovieDbHelper;
import pucminas.computacao.luigi.yourmenu.database.pdf.PdfContract;
import pucminas.computacao.luigi.yourmenu.database.pdf.PdfDbHelper;
import pucminas.computacao.luigi.yourmenu.layout.SubmenuListFragment;

public class SubmenuListAdapter extends CustomListAdapter {
    private String mTitle;
    private Map<String, String> mPath;
    private MovieDbHelper mMovieDbHelper;
    private LinkDbHelper mLinkDbHelper;
    private PdfDbHelper mPdfDbHelper;
    private SQLiteDatabase mDatabase;

    public SubmenuListAdapter(Context context, List<String> listItem, String title, String submenuTag) {
        super(context, listItem);

        mTitle = title;
        mPath = new TreeMap<>();

        switch (submenuTag) {
            case SubmenuListFragment.MOVIE_TAG:
                mMovieDbHelper = new MovieDbHelper(mContext);
                mDatabase = mMovieDbHelper.getWritableDatabase();
                break;
            case SubmenuListFragment.LINK_TAG:
                mLinkDbHelper = new LinkDbHelper(mContext);
                mDatabase = mLinkDbHelper.getWritableDatabase();
                break;
            case SubmenuListFragment.PDF_TAG:
                mPdfDbHelper = new PdfDbHelper(mContext);
                mDatabase = mPdfDbHelper.getWritableDatabase();
                break;
        }
    }

    public void addPath(String name, String path) {
        mPath.put(name, path);
    }

    public String getPathItem(int position) {
        String key = mListItem.get(position);
        return mPath.get(key);
    }

    public void add(String item) {
        super.add(item);

        if (mMovieDbHelper != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_NAME, item);
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_LINK, mPath.get(item));
            contentValues.put(MovieContract.MovieEntry.COLUMN_MENU_PATH, mTitle);
            mDatabase.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);
        } else if (mLinkDbHelper != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(LinkContract.LinkEntry.COLUMN_LINK_NAME, item);
            contentValues.put(LinkContract.LinkEntry.COLUMN_LINK, mPath.get(item));
            contentValues.put(LinkContract.LinkEntry.COLUMN_MENU_PATH, mTitle);
            mDatabase.insert(LinkContract.LinkEntry.TABLE_NAME, null, contentValues);
        } else if (mPdfDbHelper != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(PdfContract.PdfEntry.COLUMN_PDF_NAME, item);
            contentValues.put(PdfContract.PdfEntry.COLUMN_PDF_PATH, mPath.get(item));
            contentValues.put(PdfContract.PdfEntry.COLUMN_MENU_PATH, mTitle);
            mDatabase.insert(PdfContract.PdfEntry.TABLE_NAME, null, contentValues);
        }
    }

    public void remove(String item) {
        super.remove(item);
        String link = mPath.get(item);
        mPath.remove(link);

        String tableName = null;
        String[] columns = new String[1];
        String selection = null;
        String where = null;
        String[] whereArgs = new String[1];

        if (mMovieDbHelper != null) {
            tableName = MovieContract.MovieEntry.TABLE_NAME;
            columns[0] = MovieContract.MovieEntry._ID;
            selection = MovieContract.MovieEntry.COLUMN_MOVIE_NAME + " = ? and " +
                    MovieContract.MovieEntry.COLUMN_MOVIE_LINK + " = ?";
            where =  MovieContract.MovieEntry._ID + " = ?";
        } else if (mLinkDbHelper != null) {
            tableName = LinkContract.LinkEntry.TABLE_NAME;
            columns[0] = LinkContract.LinkEntry._ID;
            selection = LinkContract.LinkEntry.COLUMN_LINK_NAME + " = ? and " +
                    LinkContract.LinkEntry.COLUMN_LINK + " = ?";
            where =  LinkContract.LinkEntry._ID + " = ?";
        } else if (mPdfDbHelper != null) {
            tableName = PdfContract.PdfEntry.TABLE_NAME;
            columns[0] = PdfContract.PdfEntry._ID;
            selection = PdfContract.PdfEntry.COLUMN_PDF_NAME + " = ? and " +
                    PdfContract.PdfEntry.COLUMN_PDF_PATH + " = ?";
            where =  PdfContract.PdfEntry._ID + " = ?";
        }

        if (tableName != null) {
            String[] selectionArgs = {item, link};

            Cursor cursor = mDatabase.query(
                    tableName,
                    columns,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null,
                    "1"
            );

            int id = -1;
            if (cursor.moveToFirst()) {
                id = cursor.getInt(0);
            }

            cursor.close();

            whereArgs[0] = String.valueOf(id);
            mDatabase.delete(tableName, where, whereArgs);
        }
    }

    public void populate(Cursor cursor, String[] columns) {
        while (cursor.moveToNext()) {
            String item = cursor.getString(cursor.getColumnIndex(columns[0]));
            super.add(item);
            this.addPath(item, cursor.getString(cursor.getColumnIndex(columns[1])));
        }
    }
}
