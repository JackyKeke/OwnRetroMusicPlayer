package com.jackykeke.ownretromusicplayer.providers;

/**
 * @author keyuliang on 2022/9/22.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;

/**
 * This database tracks the number of play counts for an individual song. This is used to drive the
 * top played tracks as well as the playlist images
 */

public class SongPlayCountStore extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "song_play_count.db";
    private static final int VERSION = 3;

    // how many weeks worth of playback to track
    private static final int NUM_WEEK = 52;

    private static SongPlayCountStore sInstance = null;

    // interpolator curve applied for measuring the curve
    @NonNull
    private static final Interpolator sInterpolator = new AccelerateInterpolator(1.5f);
    // how high to multiply the interpolation curve
    private static final int INTERPOLATOR_HEIGHT = 50;

    // how high the base value is. The ratio of the Height to Base is what really matters
    private static final int INTERPOLATOR_BASE = 25;

    private static final int ONE_WEEK_IN_MS = 1000 * 60 * 60 * 24 * 7;

    @NonNull
    private static final String WHERE_ID_EQUALS = SongPlayCountColumns.ID + "=?";

    // number of weeks since epoch time
    private final int mNumberOfWeeksSinceEpoch;

    // used to track if we've walked through the db and updated all the rows
    private boolean mDatabaseUpdated;

    public SongPlayCountStore(final Context context) {
        super(context, DATABASE_NAME, null, VERSION);

        long msSinceEpoch = System.currentTimeMillis();
        mNumberOfWeeksSinceEpoch = (int) (msSinceEpoch / ONE_WEEK_IN_MS);

        mDatabaseUpdated = false;
    }

    /**
     * @param context The {@link Context} to use
     * @return A new instance of this class.
     */
    @NonNull
    public static synchronized SongPlayCountStore getInstance(@NonNull final Context context) {
        if (sInstance == null) {
            sInstance = new SongPlayCountStore(context.getApplicationContext());
        }
        return sInstance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // create the play count table
        // WARNING: If you change the order of these columns
        // please update getColumnIndexForWeek

        StringBuilder builder = new StringBuilder();
        builder.append("create table if not exists ").append(SongPlayCountColumns.NAME).append(" (").append(SongPlayCountColumns.ID)
                .append(" int unique, ");

        for (int i = 0; i < NUM_WEEK; i++) {
            builder.append(getColumnNameForWeek(i));
            builder.append(" int default 0,");
        }
    }

    /**
     * Gets the column name for each week #
     *
     * @param week number
     * @return the column name
     */
    private String getColumnNameForWeek(int week) {
        return SongPlayCountColumns.WEEK_PLAY_COUNT + week;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SongPlayCountColumns.NAME);
        onCreate(db);

    }


    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // If we ever have downgrade, drop the table to be safe
        db.execSQL("DROP TABLE IF EXISTS " + SongPlayCountColumns.NAME);
        onCreate(db);

    }

    public interface SongPlayCountColumns {

        String NAME = "song_play_count";

        String ID = "song_id";

        String WEEK_PLAY_COUNT = "week";

        String LAST_UPDATED_WEEK_INDEX = "week_index";

        String PLAY_COUNT_SCORE = "play_count_score";

    }

    /**
     * Increases the play count of a song by 1
     *
     * @param songId The song id to increase the play count
     */
    public void bumpPlayCount(final long songId) {
        if (songId == -1) return;

        final SQLiteDatabase database = getWritableDatabase();
        updateExistingRow(database, songId, true);
    }

    private void updateExistingRow(SQLiteDatabase database, long id, boolean bumpCount) {
        String stringId = String.valueOf(id);

        //begin the transaction
        database.beginTransaction();

        final Cursor cursor =
                database.query(
                        SongPlayCountColumns.NAME,
                        null,
                        WHERE_ID_EQUALS,
                        new String[]{stringId},
                        null,
                        null,
                        null
                );


        // if we have a result
        if (cursor != null && cursor.moveToFirst()) {
            // figure how many weeks since we last updated 计算自我们上次更新以来的周数
            int lastUpdatedIndex = cursor.getColumnIndex(SongPlayCountColumns.LAST_UPDATED_WEEK_INDEX);
            int lastUpdatedWeek = cursor.getInt(lastUpdatedIndex);
            int weekDiff = mNumberOfWeeksSinceEpoch - lastUpdatedWeek;

            // if it's more than the number of weeks we track, delete it and create a new entry
            // 如果超过我们跟踪的周数，删除它并创建一个新条目
            if (Math.abs(weekDiff) >= NUM_WEEK) {
                // this entry needs to be dropped since it is too outdated 此条目需要删除，因为它太过时了
                deleteEntry(database, stringId);
                if (bumpCount) {
                    createNewPlayedEntry(database, id);
                }
            } else if (weekDiff != 0) {
                int[] playCount = new int[NUM_WEEK];

                if (weekDiff > 0) {
                    // time is shifted forwards
                    for (int i = 0; i < NUM_WEEK - weekDiff; i++) {
                        playCount[i + weekDiff] = cursor.getInt(getColumnIndexForWeek(i));
                    }
                } else {

                    // time is shifted backwards (by user) - nor typical behavior but we
                    // will still handle it

                    // since weekDiff is -ve, NUM_WEEKS + weekDiff is the real # of weeks we have to
                    // transfer.  Then we transfer the old week i - weekDiff to week i
                    // for example if the user shifted back 2 weeks, ie -2, then for 0 to
                    // NUM_WEEKS + (-2) we set the new week i = old week i - (-2) or i+2

                    //时间向后移动（由用户） - 也不是典型的行为，但我们仍然会处理它，因为 weekDiff 是 -ve，
                    // NUM_WEEKS + weekDiff 是我们必须转移的真实周数。然后我们将旧周 i - weekDiff 转移到第 i 周，
                    // 例如，如果用户向后移动 2 周，即 -2，那么对于 0 到 NUM_WEEKS + (-2)，我们设置新周 i = 旧周 i - (-2 ) 或 i+2

                    for (int i = 0; i < NUM_WEEK + weekDiff; i++) {
                        playCount[i] = cursor.getInt(getColumnIndexForWeek(i - weekDiff));
                    }

                }


                if (bumpCount) {
                    playCount[0]++;
                }

                float score = calculateScore(playCount);

                if (score < .01f) {
                    deleteEntry(database, stringId);
                } else {
                    // create the content values
                    ContentValues values = new ContentValues(NUM_WEEK + 2);
                    values.put(SongPlayCountColumns.LAST_UPDATED_WEEK_INDEX, mNumberOfWeeksSinceEpoch);
                    values.put(SongPlayCountColumns.PLAY_COUNT_SCORE, score);

                    for (int i = 0; i < NUM_WEEK; i++) {
                        values.put(getColumnNameForWeek(i), playCount[i]);
                    }
                    database.update(SongPlayCountColumns.NAME, values, WHERE_ID_EQUALS, new String[]{stringId});

                }
            }else if (bumpCount){
                // else no shifting, just update the scores 否则不换，只更新分数
                ContentValues values =new ContentValues(2);

                // increase the score by a single score amount
                int scoreIndex = cursor.getColumnIndex(SongPlayCountColumns.PLAY_COUNT_SCORE);
                float score = cursor.getFloat(scoreIndex) + getScoreMultiplierForWeek(0);
                values.put(SongPlayCountColumns.PLAY_COUNT_SCORE,score);

                // increase the play count by 1
                values.put(getColumnNameForWeek(0),cursor.getInt(getColumnIndexForWeek(0)) + 1);

                database.update(SongPlayCountColumns.NAME, values, WHERE_ID_EQUALS, new String[]{stringId});
            }
            cursor.close();
        }else if(bumpCount){
            createNewPlayedEntry(database,id);
        }

        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public void clear() {
        final SQLiteDatabase database = getWritableDatabase();
        database.delete(SongPlayCountColumns.NAME, null, null);
    }


    /**
     * Calculates the score of the song given the play counts
     * 根据播放次数计算歌曲的得分
     *
     * @param playCounts an array of the # of times a song has been played for each week where
     *                   playCounts[N] is the # of times it was played N weeks ago
     * @return the score
     */
    private float calculateScore(int[] playCounts) {
        if (playCounts == null)
            return 0;

        float score = 0;
        for (int i = 0; i < Math.min(playCounts.length, NUM_WEEK); i++) {
            score += playCounts[i] * getScoreMultiplierForWeek(i);
        }
        return score;
    }


    public Cursor getTopPlayedResults(int numResults){
        updateResult();

        final SQLiteDatabase database =getReadableDatabase();
        return database.query(
                SongPlayCountColumns.NAME,
                new String[]{SongPlayCountColumns.ID},
                null,
                null,
                null,
                null,
                SongPlayCountColumns.PLAY_COUNT_SCORE+" DESC ",
                (numResults<=0? null:String.valueOf(numResults))
        );
    }

    private synchronized void updateResult() {

        if (mDatabaseUpdated)
            return;

        final SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();

        int oldestWeekWeCareAbout = mNumberOfWeeksSinceEpoch - NUM_WEEK+1;

        // delete rows we don't care about anymore
        database.delete(SongPlayCountColumns.NAME,
                SongPlayCountColumns.LAST_UPDATED_WEEK_INDEX+"<"+oldestWeekWeCareAbout,
                null);


        // get the remaining rows
        Cursor cursor =
                database.query(SongPlayCountColumns.NAME,
                        new String[]{SongPlayCountColumns.ID},
                        null,
                        null,
                        null,
                        null,
                        null);

        if (cursor!=null && cursor.moveToFirst()){
            // for each row, update it
            do {
                updateExistingRow(database,cursor.getLong(0),false);
            }while (cursor.moveToNext());

            cursor.close();
        }

        mDatabaseUpdated = true ;
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public void removeItem(final long songId){
        final SQLiteDatabase database = getWritableDatabase();
        deleteEntry(database, String.valueOf(songId));
    }

    /**
     * For some performance gain, return a static value for the column index for a week
     * WARNING: This
     * function assumes you have selected all columns for it to work
     * 为了获得一些性能提升，为列索引返回一个一周的静态值
     * 警告：此函数假定您已选择所有列以使其工作
     *
     * @param week number
     * @return column index of that week
     */
    private int getColumnIndexForWeek(int week) {
        return 1 + week;
    }

    /**
     * This creates a new entry that indicates a song has been played once as well as its score
     * 这将创建一个新条目，指示一首歌曲已经播放过一次以及它的乐谱
     *
     * @param database a write able database
     * @param songId   the id of the track
     */
    private void createNewPlayedEntry(SQLiteDatabase database, long songId) {
        // no row exists, create a new one
        float newScore = getScoreMultiplierForWeek(0);
        int newPlayCount = 1;

        final ContentValues values = new ContentValues(3);
        values.put(SongPlayCountColumns.ID, songId);
        values.put(SongPlayCountColumns.PLAY_COUNT_SCORE, newScore);
        values.put(SongPlayCountColumns.LAST_UPDATED_WEEK_INDEX, mNumberOfWeeksSinceEpoch);
        values.put(getColumnNameForWeek(0), newPlayCount);

        database.insert(SongPlayCountColumns.NAME, null, values);
    }

    /**
     * Gets the score multiplier for each week
     * 获取每周的分数乘数
     *
     * @param week number
     * @return the multiplier to apply
     */
    private float getScoreMultiplierForWeek(int week) {
        return sInterpolator.getInterpolation(1 - (week / (float) NUM_WEEK)) * INTERPOLATOR_HEIGHT + INTERPOLATOR_BASE;
    }

    private void deleteEntry(SQLiteDatabase database, String stringId) {
        database.delete(SongPlayCountColumns.NAME, WHERE_ID_EQUALS, new String[]{stringId});
    }
}
