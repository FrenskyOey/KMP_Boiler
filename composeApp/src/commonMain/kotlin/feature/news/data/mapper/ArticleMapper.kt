package feature.news.data.mapper

import feature.news.data.local.entity.ArticleEntity
import feature.news.data.model.ArticleResponse
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

fun ArticleResponse.toEntity(): ArticleEntity {
    return ArticleEntity(
        id = id,
        title = title,
        content = content,
        imageUrl = imageUrl,
        topic = topic
    )
}

fun ArticleEntity.toDomain(): Article {
    return Article(
        id = id.toLong(),
        title = title,
        content = content,
        imageUrl = imageUrl,
        topic = topic
    )
}
