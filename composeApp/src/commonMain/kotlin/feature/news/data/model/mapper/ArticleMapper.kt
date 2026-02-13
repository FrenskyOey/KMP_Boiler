package feature.news.data.model.mapper

import feature.news.data.model.entity.ArticleEntity
import feature.news.data.model.response.ArticleResponse
import feature.news.domain.model.Article

fun ArticleResponse.toDomain(): Article {
    return Article(
        id = id.toLong(),
        title = title,
        content = content,
        imageUrl = imageUrl,
        topic = topic
    )
}

fun ArticleResponse.toEntity(createdAt: Long): ArticleEntity {
    return ArticleEntity(
        id = id.toLong(),
        title = title,
        content = content,
        imageUrl = imageUrl,
        topic = topic,
        createdAt = createdAt
    )
}

fun ArticleEntity.toDomain(): Article {
    return Article(
        id = id,
        title = title,
        content = content,
        imageUrl = imageUrl,
        topic = topic
    )
}
