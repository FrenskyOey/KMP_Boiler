package feature.news.data.local

import androidx.room.TypeConverter
import feature.news.data.model.response.AuthorResponse
import feature.news.data.model.response.ContentItemResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class NewsDetailConverters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromAuthor(author: AuthorResponse): String {
        return json.encodeToString(author)
    }

    @TypeConverter
    fun toAuthor(data: String): AuthorResponse {
        return json.decodeFromString(data)
    }

    @TypeConverter
    fun fromContentList(content: List<ContentItemResponse>): String {
        return json.encodeToString(content)
    }

    @TypeConverter
    fun toContentList(data: String): List<ContentItemResponse> {
        return json.decodeFromString(data)
    }

    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        return list?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toStringList(data: String?): List<String>? {
        return data?.let { json.decodeFromString(it) }
    }
}
