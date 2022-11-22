package com.jackykeke.ownretromusicplayer.providers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jackykeke.ownretromusicplayer.Constants;

/**
 * @author keyuliang on 2022/11/22.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
public class MusicPlaybackQueueStore  extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "music_playback_state.db";

    public static final String PLAYING_QUEUE_TABLE_NAME = "playing_queue";

    public static final String ORIGINAL_PLAYING_QUEUE_TABLE_NAME = "original_playing_queue";

    private static final int VERSION = 12;

    @Nullable
    private static MusicPlaybackQueueStore sInstance = null;

    /**
     * Constructor of <code>MusicPlaybackState</code>
     *
     * @param context The {@link Context} to use
     */
    public MusicPlaybackQueueStore(final @NonNull Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    /**
     * @param context The {@link Context} to use
     * @return A new instance of this class.
     */
    @NonNull
    public static synchronized MusicPlaybackQueueStore getInstance(@NonNull final Context context) {
        if (sInstance == null) {
            sInstance = new MusicPlaybackQueueStore(context.getApplicationContext());
        }
        return sInstance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db,PLAYING_QUEUE_TABLE_NAME);
        createTable(db, ORIGINAL_PLAYING_QUEUE_TABLE_NAME);

    }

    private void createTable(@NonNull final SQLiteDatabase db, final String tableName) {

        //noinspection StringBufferReplaceableByString
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXIST")
          builder      .append(tableName);
          builder      .append("(");

            builder    .append(BaseColumns._ID);
            builder    .append(" INT NOT NULL,");

            builder    .append(MediaStore.Audio.AudioColumns.TITLE);
            builder    .append(" STRING NOT NULL,");

        builder.append(MediaStore.Audio.AudioColumns.TRACK);
        builder.append(" INT NOT NULL,");

        builder.append(MediaStore.Audio.AudioColumns.YEAR);
        builder.append(" INT NOT NULL,");

        builder.append(MediaStore.Audio.AudioColumns.DURATION);
        builder.append(" LONG NOT NULL,");

        builder.append(Constants.DATA);
        builder.append(" STRING NOT NULL,");

        builder.append(MediaStore.Audio.AudioColumns.DATE_MODIFIED);
        builder.append(" LONG NOT NULL,");

        builder.append(MediaStore.Audio.AudioColumns.ALBUM_ID);
        builder.append(" INT NOT NULL,");

        builder.append(MediaStore.Audio.AudioColumns.ALBUM);
        builder.append(" STRING NOT NULL,");

        builder.append(MediaStore.Audio.AudioColumns.ARTIST_ID);
        builder.append(" INT NOT NULL,");

        builder.append(MediaStore.Audio.AudioColumns.ARTIST);
        builder.append(" STRING NOT NULL,");

        builder.append(MediaStore.Audio.AudioColumns.COMPOSER);
        builder.append(" STRING,");

        builder.append("album_artist");
        builder.append(" STRING);");

        db.execSQL(builder.toString());

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
