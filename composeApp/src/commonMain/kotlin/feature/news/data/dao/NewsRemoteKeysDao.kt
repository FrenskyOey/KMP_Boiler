package feature.news.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import feature.news.data.model.entity.NewsRemoteKeysEntity

@Dao
interface NewsRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<NewsRemoteKeysEntity>)

    @Query("SELECT * FROM news_remote_keys WHERE articleId = :articleId")
    suspend fun getRemoteKeys(articleId: Long): NewsRemoteKeysEntity?

    @Query("DELETE FROM news_remote_keys")
    suspend fun clearRemoteKeys()

    @Query("SELECT * FROM news_remote_keys ORDER BY orderIndex DESC LIMIT 1")
    suspend fun getLastRemoteKey(): NewsRemoteKeysEntity?
    
    @Query("DELETE FROM news_remote_keys WHERE orderIndex > :orderIndex")
    suspend fun clearRemoteKeysAfter(orderIndex: Int)
}
