package com.example.recycleg.ui.article

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recycleg.data.garbage.impl.plastic
import com.example.recycleg.model.*
import com.example.recycleg.ui.theme.RecycleGTheme

private val defaultSpacerSize = 16.dp

@Composable
fun PostContent(
    garbageInfo: GarbageInfoPost,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState()
) {
    LazyColumn(
        modifier = modifier,
        state = state
    ) {
        garbageContentItem(garbageInfo)
    }
}

fun LazyListScope.garbageContentItem(garbageInfo: GarbageInfoPost) {
    item {
        PostHeaderImage(garbageInfo)
        Spacer(Modifier.height(10.dp))
    }
    items(garbageInfo.paragraphs) {
        Box(
            modifier = Modifier.padding(
                horizontal = defaultSpacerSize,
                vertical = 0.dp
            )
        ) {
            Paragraph(paragraph = it)
        }
    }
}

@Composable
private fun PostHeaderImage(post: GarbageInfoPost) {
    val imageModifier = Modifier
        .padding(horizontal = 1.dp, vertical = 2.dp)
        .fillMaxWidth()
    Image(
        painter = painterResource(post.imageId),
        contentDescription = null, // decorative
        modifier = imageModifier,
        contentScale = ContentScale.Crop
    )
}


@Composable
private fun Paragraph(paragraph: Paragraph) {
    val (textStyle, paragraphStyle, trailingPadding) = paragraph.type.getTextAndParagraphStyle()

    val annotatedString = paragraphToAnnotatedString(
        paragraph,
        MaterialTheme.typography,
        MaterialTheme.colorScheme.codeBlockBackground
    )
    Box(modifier = Modifier.padding(bottom = trailingPadding)) {
        when (paragraph.type) {
            ParagraphType.Bullet -> BulletParagraph(
                text = annotatedString,
                textStyle = textStyle,
                paragraphStyle = paragraphStyle
            )
            ParagraphType.CodeBlock -> CodeBlockParagraph(
                text = annotatedString,
                textStyle = textStyle,
                paragraphStyle = paragraphStyle
            )
            ParagraphType.Header -> {
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = annotatedString,
                    style = textStyle.merge(paragraphStyle)
                )
            }
            else -> Text(
                modifier = Modifier.padding(4.dp),
                text = annotatedString,
                style = textStyle
            )
        }
    }
}

@Composable
private fun CodeBlockParagraph(
    text: AnnotatedString,
    textStyle: TextStyle,
    paragraphStyle: ParagraphStyle
) {
    Surface(
        color = MaterialTheme.colorScheme.codeBlockBackground,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = text,
            style = textStyle.merge(paragraphStyle)
        )
    }
}

@Composable
private fun BulletParagraph(
    text: AnnotatedString,
    textStyle: TextStyle,
    paragraphStyle: ParagraphStyle
) {
    Row {
        with(LocalDensity.current) {
            // this box is acting as a character, so it's sized with font scaling (sp)
            Box(
                modifier = Modifier
                    .size(8.sp.toDp(), 8.sp.toDp())
                    .alignBy {
                        // Add an alignment "baseline" 1sp below the bottom of the circle
                        9.sp.roundToPx()
                    }
                    .background(LocalContentColor.current, CircleShape),
            ) { /* no content */ }
        }
        Text(
            modifier = Modifier
                .weight(1f)
                .alignBy(FirstBaseline),
            text = text,
            style = textStyle.merge(paragraphStyle)
        )
    }
}

private data class ParagraphStyling(
    val textStyle: TextStyle,
    val paragraphStyle: ParagraphStyle,
    val trailingPadding: Dp
)

@Composable
private fun ParagraphType.getTextAndParagraphStyle(): ParagraphStyling {
    val typography = MaterialTheme.typography
    var textStyle: TextStyle = typography.bodyLarge
    var paragraphStyle = ParagraphStyle()
    var trailingPadding = 24.dp

    when (this) {
        ParagraphType.Caption -> textStyle = typography.labelMedium
        ParagraphType.Title -> textStyle = typography.headlineLarge
        ParagraphType.Subhead -> {
            textStyle = typography.headlineSmall
            trailingPadding = 16.dp
        }
        ParagraphType.Text -> {
            textStyle = typography.bodyLarge.copy(lineHeight = 28.sp)
        }
        ParagraphType.Header -> {
            textStyle = typography.headlineMedium
            trailingPadding = 16.dp
        }
        ParagraphType.CodeBlock -> textStyle = typography.bodyLarge.copy(
            fontFamily = FontFamily.Monospace
        )
        ParagraphType.Quote -> textStyle = typography.bodyLarge
        ParagraphType.Bullet -> {
            paragraphStyle = ParagraphStyle(textIndent = TextIndent(firstLine = 8.sp))
        }
    }
    return ParagraphStyling(
        textStyle,
        paragraphStyle,
        trailingPadding
    )
}

private fun paragraphToAnnotatedString(
    paragraph: Paragraph,
    typography: Typography,
    codeBlockBackground: Color
): AnnotatedString {
    val styles: List<AnnotatedString.Range<SpanStyle>> = paragraph.markups
        .map { it.toAnnotatedStringItem(typography, codeBlockBackground) }
    return AnnotatedString(text = paragraph.text, spanStyles = styles)
}

fun Markup.toAnnotatedStringItem(
    typography: Typography,
    codeBlockBackground: Color
): AnnotatedString.Range<SpanStyle> {
    return when (this.type) {
        MarkupType.Italic -> {
            AnnotatedString.Range(
                typography.bodyLarge.copy(fontStyle = FontStyle.Italic).toSpanStyle(),
                start,
                end
            )
        }
        MarkupType.Link -> {
            AnnotatedString.Range(
                typography.bodyLarge.copy(textDecoration = TextDecoration.Underline).toSpanStyle(),
                start,
                end
            )
        }
        MarkupType.Bold -> {
            AnnotatedString.Range(
                typography.bodyLarge.copy(fontWeight = FontWeight.Bold).toSpanStyle(),
                start,
                end
            )
        }
        MarkupType.Code -> {
            AnnotatedString.Range(
                typography.bodyLarge
                    .copy(
                        background = codeBlockBackground,
                        fontFamily = FontFamily.Monospace
                    ).toSpanStyle(),
                start,
                end
            )
        }
    }
}

private val ColorScheme.codeBlockBackground: Color
    get() = onSurface.copy(alpha = .15f)

@Preview
@Composable
fun PreviewPost() {
    RecycleGTheme {
        Surface {
            PostContent(garbageInfo = plastic)
        }
    }
}

