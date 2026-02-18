package feature.news.data.dao

import feature.news.data.model.entity.NewsRemoteKeysEntity

class FakeNewsRemoteKeysDao : NewsRemoteKeysDao {

    var keys = mutableListOf<NewsRemoteKeysEntity>()
    var clearCalled = false

    override suspend fun insertAll(remoteKeys: List<NewsRemoteKeysEntity>) {
        // Remove existing for same IDs (replace strategy)
        val newIds = remoteKeys.map { it.articleId }
        keys.removeAll { it.articleId in newIds }
        keys.addAll(remoteKeys)
    }

    override suspend fun getRemoteKeys(articleId: Long): NewsRemoteKeysEntity? {
        return keys.find { it.articleId == articleId }
    }

    override suspend fun clearRemoteKeys() {
        clearCalled = true
        keys.clear()
    }

    override suspend fun getLastRemoteKey(): NewsRemoteKeysEntity? {
        return keys.maxByOrNull { it.orderIndex }
    }
    
    override suspend fun getRemoteKeyByOrderIndex(orderIndex: Int): NewsRemoteKeysEntity? {
        return keys.find { it.orderIndex == orderIndex }
    }

    override suspend fun clearRemoteKeysAfter(orderIndex: Int) {
        keys.removeAll { it.orderIndex > orderIndex }
    }

    override suspend fun getCount(): Int {
        return keys.size
    }
}
