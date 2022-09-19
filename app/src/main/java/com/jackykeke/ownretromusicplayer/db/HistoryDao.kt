package com.jackykeke.ownretromusicplayer.db

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 *
 * @author keyuliang on 2022/9/19.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
@Dao
interface HistoryDao {

    companion object{
    private const val  HISTORY_LIMIT=100
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongInHistory(historyEntity: HistoryEntity)

    @Query("DELETE FROM HistoryEntity WHERE id= :songId")
    fun deleteSongInHistory(songId:Long)

    @Query("SELECT * FROM HistoryEntity WHERE id= :songId LIMIT 1")
    suspend fun isSongPresentInHistory(songId: Long):HistoryEntity?

    @Update
    suspend fun updateHistorySong(historyEntity: HistoryEntity)

    @Query("SELECT * FROM HistoryEntity ORDER BY time_played DESC LIMIT $HISTORY_LIMIT ")
    fun historySongs():List<HistoryEntity>

    @Query("SELECT * FROM HistoryEntity ORDER BY time_played DESC LIMIT $HISTORY_LIMIT ")
    fun observableHistorySongs():LiveData<List<HistoryEntity>>

    @Query("DELETE FROM HistoryEntity")
    suspend fun clearHistory()

}