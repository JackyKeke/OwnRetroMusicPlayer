package com.jackykeke.ownretromusicplayer.providers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author keyuliang on 2022/9/22.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
public class HistoryStore  extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "history.db";
    private static final int MAX_ITEMS_IN_DB = 100;
    private static final int VERSION = 1;
    private static HistoryStore sInstance = null;

    public HistoryStore(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    public static synchronized HistoryStore getInstance(@NonNull final Context context){
        if (sInstance==null){
            sInstance=new HistoryStore(context);
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table if not exists "
                + RecentStoreColumns.NAME
                +" ( "
                +RecentStoreColumns.ID
                +" LONG NOT NULL, "
                + RecentStoreColumns.TIME_PLAYED
                + " LONG NOT NULL);"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+RecentStoreColumns.NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+RecentStoreColumns.NAME);
        onCreate(db);
    }

    public void addSongId(final long songId){

        if (songId == -1)
            return;

        final SQLiteDatabase database =getWritableDatabase();
        database.beginTransaction();

        try {
            // remove previous entries
            removeSongId(songId);

            // add the entry
            ContentValues values=new ContentValues(2);
            values.put(RecentStoreColumns.ID,songId);
            values.put(RecentStoreColumns.TIME_PLAYED,System.currentTimeMillis());
            database.insert(RecentStoreColumns.NAME,null,values);

            // if our db is too large, delete the extra items
            try (Cursor oldest = database .query(
                    RecentStoreColumns.NAME,
                    new String[]{RecentStoreColumns.TIME_PLAYED},
                    null,
                    null,
                    null,
                    null,
                    RecentStoreColumns.TIME_PLAYED + " ASC"
                    )) {

                if (oldest != null && oldest.getCount() > MAX_ITEMS_IN_DB){
                        oldest.moveToPosition(oldest.getCount()- MAX_ITEMS_IN_DB);
                        long timeOfRecordToKeep = oldest.getLong(0);

                        database.delete(RecentStoreColumns.NAME,
                                RecentStoreColumns.TIME_PLAYED+" < ?",
                                new String[]{String.valueOf(timeOfRecordToKeep)});
                }
            }

        }finally {
            database.setTransactionSuccessful();
            database.endTransaction();
        }

    }

    private void removeSongId(final long songId) {
        final SQLiteDatabase database = getWritableDatabase();
        database.delete(RecentStoreColumns.NAME,RecentStoreColumns.ID+" = ? ", new String[]{String.valueOf(songId)});
    }

    public interface RecentStoreColumns{
        String NAME = "recent_history";

        String ID = "song_id";

        String TIME_PLAYED = "time_played";
    }
}
