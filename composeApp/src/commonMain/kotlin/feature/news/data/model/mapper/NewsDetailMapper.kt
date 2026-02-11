package feature.news.data.model.mapper

import feature.news.data.model.entity.ArticleDetailEntity
import feature.news.data.model.response.ArticleDetailResponse
import feature.news.data.model.response.AuthorResponse
import feature.news.data.model.response.ContentItemResponse
import feature.news.domain.model.NewsContent
import feature.news.domain.model.NewsDetail

fun ArticleDetailResponse.toEntity(): ArticleDetailEntity {
    return ArticleDetailEntity(
        id = id,
        title = title,
        category = category,
        image = image,
        author = author,
        publishedAt = publishedAt,
        readTime = readTime,
        content = content,
        tags = tags,
        shareUrl = shareUrl
    )
}

fun ArticleDetailEntity.toDomain(): NewsDetail {
    return NewsDetail(
        id = id.toLong(),
        title = title,
        category = category,
        image = image,
        author = author.toDomain(),
        publishedAt = publishedAt,
        readTime = readTime,
        content = content.map { it.toDomain() },
        tags = tags,
        shareUrl = shareUrl
    )
}

fun AuthorResponse.toDomain(): NewsDetail.Author {
    return NewsDetail.Author(
        name = name,
        avatar = avatar,
        publication = publication
    )
}

fun ContentItemResponse.toDomain(): NewsContent {
    return when (type) {
        "paragraph" -> NewsContent.Paragraph(text = text)
        "quote" -> NewsContent.Quote(
            text = text,
            highlighted = highlighted ?: false,
            emphasis = emphasis ?: false
        )
        else -> NewsContent.Paragraph(text = text) // Fallback
    }
}
