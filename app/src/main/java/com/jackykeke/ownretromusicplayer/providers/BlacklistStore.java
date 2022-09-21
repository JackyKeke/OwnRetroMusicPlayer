package com.jackykeke.ownretromusicplayer.providers;

import static com.jackykeke.ownretromusicplayer.util.FileUtilsKt.getExternalStoragePublicDirectory;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import androidx.annotation.NonNull;

import com.jackykeke.ownretromusicplayer.util.FileUtil;
import com.jackykeke.ownretromusicplayer.util.PreferenceUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * @author keyuliang on 2022/9/21.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
public class BlacklistStore extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "blacklist.db";
    private static final int VERSION = 2;
    private static BlacklistStore sInstance = null;
    private final Context context;

    public BlacklistStore(final Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.context = context;
    }


    @NonNull
    public static synchronized BlacklistStore getInstance(@NonNull final Context context) {
        if (sInstance == null) {
            sInstance = new BlacklistStore(context.getApplicationContext());
            if (!PreferenceUtil.INSTANCE.isInitializedBlacklist()) {
                // blacklisted by default
                sInstance.addPathImpl(
                        getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS));
                sInstance.addPathImpl(
                        getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS));
                sInstance.addPathImpl(
                        getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES));

                PreferenceUtil.INSTANCE.setInitializedBlacklist(true);
            }
        }
        return sInstance;
    }


    private void addPathImpl(File file) {
        if (file == null || contains(file)) {
            return;
        }
        String path = FileUtil.safeGetCanonicalPath(file);

        final SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();
        try {
            final ContentValues values = new ContentValues(1);
            values.put(BlacklistStoreColumns.PATH, path);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    private boolean contains(File file) {
        if (file == null) return false;
        String path = FileUtil.safeGetCanonicalPath(file);

        final SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(
                BlacklistStoreColumns.NAME,
                new String[]{
                        BlacklistStoreColumns.PATH
                },
                BlacklistStoreColumns.PATH + " =? ",
                new String[]{path},
                null,
                null,
                null,
                null
        );
        boolean containsPath = cursor != null && cursor.moveToFirst();

        if (cursor != null) {
            cursor.close();
        }
        return containsPath;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table if  not exists "
                +BlacklistStoreColumns.NAME
                +" ("
                +BlacklistStoreColumns.PATH
                +" STRING not null);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      //其实这是不规范的 应该是先把现有表改名为 temp 表 ，把temp表数据迁移到新表 ，新表没有问题再删除temp表
        db.execSQL("DROP TABLE IF EXISTS " + BlacklistStoreColumns.NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BlacklistStoreColumns.NAME);
        onCreate(db);
    }

    public void addPath(File file) {
        addPathImpl(file);
        notifyMediaStoreChanged();
    }

    private void notifyMediaStoreChanged() {
        context.sendBroadcast(new Intent(MEDIA_STORE_CHANGED));
    }


    public interface BlacklistStoreColumns {
        String NAME = "blacklist";

        String PATH = "path";
    }

    public ArrayList<String> getPaths(){
        Cursor cursor = getReadableDatabase()
                .query(
                        BlacklistStoreColumns.NAME,
                        new String[] {BlacklistStoreColumns.PATH},
                        null,
                        null,
                        null,
                        null,
                        null
                );
        ArrayList<String> paths = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                paths.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        if (cursor != null) cursor.close();
        return paths;
    }

}
