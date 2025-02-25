package com.codepath.articlesearch

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class SearchNewsResponse(
    @SerialName("response")
    val response: BaseResponse?
)

@Keep
@Serializable
data class BaseResponse(
    @SerialName("docs")
    val docs: List<Article>?
)

@Keep
@Serializable
data class Article(
    @SerialName("abstract")
    val abstract: String?,

    @SerialName("byline")
    val byline: Byline?,

    @SerialName("headline")
    val headline: Headline?,

    @SerialName("multimedia")
    val multimedia: List<MultiMedia>?
) : java.io.Serializable {
    val mediaImageUrl: String
        get() = "https://www.nytimes.com/${multimedia?.firstOrNull { it.url != null }?.url ?: ""}"
}

@Keep
@Serializable
data class Byline(
    @SerialName("original")
    val original: String? = null // this could be the author's name or a string containing author info
) : java.io.Serializable

@Keep
@Serializable
data class Headline(
    @SerialName("main")
    val main: String // main headline/title of the article
) : java.io.Serializable

@Keep
@Serializable
data class MultiMedia(
    @SerialName("url")
    val url: String? // URL to the media image
) : java.io.Serializable