package com.example.recycleg.model

import androidx.annotation.DrawableRes

data class GarbageInfoPost(
    val type: GarbageType,
    val title: String,
    val subtitle: String? = null,
    val paragraphs: List<Paragraph> = emptyList(),
    @DrawableRes val imageId: Int
)
data class Paragraph(
    val type: ParagraphType,
    val text: String,
    val markups: List<Markup> = emptyList()
)

data class Markup(
    val type: MarkupType,
    val start: Int,
    val end: Int,
    val href: String? = null
)

enum class MarkupType {
    Link,
    Code,
    Italic,
    Bold,
}

enum class GarbageType {
    Paper,
    Glass,
    Metal,
    Organic,
    Plastic
}

enum class ParagraphType {
    Title,
    Caption,
    Header,
    Subhead,
    Text,
    CodeBlock,
    Quote,
    Bullet,
}