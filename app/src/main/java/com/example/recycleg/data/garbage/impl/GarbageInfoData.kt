@file:Suppress("ktlint:max-line-length")
package com.example.recycleg.data.garbage.impl

import com.example.recycleg.R
import com.example.recycleg.model.GarbagePostsFeed
import com.example.recycleg.model.GarbageInfoPost
import com.example.recycleg.model.GarbageType
import com.example.recycleg.model.ParagraphType
import com.example.recycleg.model.Paragraph

val plasticParagraphs = listOf(
    Paragraph(
        ParagraphType.Text,
        "Пластик несет серьезную угрозу для окружающей среды. Проблема его использования в качестве вторсырья заслуживает особого внимания."
    ),
    Paragraph(
        ParagraphType.Text,
        "На территории Российской Федерации на переработку отправляется не более 2,5% пластиковых отходов. Оставшаяся часть мусора собирается на полигонах и разлагается в природе, выделяя отравляющие вещества."
    ),
    Paragraph(
        ParagraphType.Text,
        "Общество должно знать, какие отходы из пластмассы безопасны и могут быть использованы в качестве вторичного сырья, а какие вредны и не подлежат переработке."
    )
)

val paperParagraphs = listOf(
    Paragraph(
        ParagraphType.Text,
        "Больше трети состава твердых бытовых отходов — макулатура. Использование бумаги и картона в качестве вторсырья позволяет позаботиться о сохранности лесных массивов."
    )
)

val metalParagraphs = listOf(
    Paragraph(
        ParagraphType.Text,
        "Пришедшие в негодность металлические изделия подходят для вторичной переработки. Полученное сырье позволяет рационально использовать невозобновляемые природные ресурсы, а также сократить площадь отходов."
    )
)

val organicParagraphs = listOf(
    Paragraph(
        ParagraphType.Text,
        "Органические отходы – это остатки продуктов питания. Их еще называют биотходами. С 2019 года возле жилых и нежилых объектов РФ постепенно вводятся дополнительные контейнеры. Помимо традиционных появляются разноцветные контейнеры для мусора. Среди них – баки коричневого цвета. Они предназначены для биоотходов. И именно этот последний контейнер вызывает больше всего вопросов."
    )
)

val glassParagraphs = listOf(
    Paragraph(
        ParagraphType.Text,
        "Переработка стекла особенно актуальна, и для того чтобы она имела массовый характер существует ряд причин:\n" +
"* стеклянная тара почти не разлагается (для ее распада требуется около 1 млн. лет);\n" +
"* земля, в которой производилось захоронение стеклянных отходов, становится непригодна для сельского хозяйства;\n" +
"* получаемое сырье отличается низкой себестоимостью;\n" +
"* материал может перерабатываться до бесконечности.\n",
    )
)
val plastic = GarbageInfoPost(GarbageType.Plastic,
    "Plastic",
    "Пластик несет серьезную угрозу для окружающей среды.",
    plasticParagraphs,
    R.drawable.plastic
)
val paper = GarbageInfoPost(GarbageType.Paper,
    "Paper",
    "Больше трети состава твердых бытовых отходов — макулатура.",
    paperParagraphs,
    R.drawable.paperboard
)
val metal = GarbageInfoPost(GarbageType.Metal,
    "Metal",
    "Пришедшие в негодность металлические изделия.",
    metalParagraphs,
    R.drawable.metal
)

val glass = GarbageInfoPost(GarbageType.Glass,
    "Glass",
    "Переработка стекла особенно актуальна.",
    glassParagraphs,
    R.drawable.glass
)

val organic = GarbageInfoPost(GarbageType.Organic,
    "Organic",
    "Органические отходы – это остатки продуктов питания.",
    organicParagraphs,
    R.drawable.bio_apple
)

val garbagePostsFeed: GarbagePostsFeed = GarbagePostsFeed(
    reducedInfo = emptyList(),
    info = listOf(plastic)
)