package com.skinthesia.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/** Thin-line icons on a 24-unit grid. Single colour; they take the tint of the component using them. */
object SkinthesiaIcons {

    /** Forward arrow used on primary calls to action. */
    val ArrowForward: ImageVector by lazy { lineIcon("ArrowForward", "M5 12h14M13 6l6 6-6 6") }

    /** Back arrow used in onboarding top bars. */
    val ArrowBack: ImageVector by lazy { lineIcon("ArrowBack", "M19 12H5M11 6l-6 6 6 6") }

    /** Selected state on chips and confirmations. */
    val Check: ImageVector by lazy { lineIcon("Check", "M5 12.5l4.5 4.5L19 7", strokeWidth = 1.8f) }

    /** Camera capture affordance. */
    val Camera: ImageVector by lazy {
        lineIcon(
            "Camera",
            "M4 8.5A1.5 1.5 0 0 1 5.5 7h2.2l1.4-2h5.8l1.4 2h2.2A1.5 1.5 0 0 1 20 8.5v9a1.5 1.5 0 0 1-1.5 1.5h-13A1.5 1.5 0 0 1 4 17.5z",
            circlePath(cx = 12f, cy = 12.5f, r = 3.2f),
        )
    }

    /** Generic wireless connection: a small point radiating two arcs, used instead of a literal Bluetooth glyph. */
    val Signal: ImageVector by lazy {
        lineIcon(
            "Signal",
            circlePath(cx = 12f, cy = 18f, r = 1.4f),
            "M8 14A4 4 0 0 1 16 14",
            "M5 10.5A8 8 0 0 1 19 10.5",
        )
    }

    /** Battery level affordance. */
    val Battery: ImageVector by lazy {
        lineIcon(
            "Battery",
            "M4 8.5h13v7h-13z",
            "M19 10.5v3",
        )
    }

    /** Bottom navigation: home. */
    val Home: ImageVector by lazy {
        lineIcon("Home", "M4 11.5l8-7l8 7", "M6.5 10.5v9h11v-9")
    }

    /** Bottom navigation: analyze — a magnifying glass, distinct from the selfie [Camera] icon. */
    val Analyze: ImageVector by lazy {
        lineIcon("Analyze", circlePath(cx = 10.5f, cy = 10.5f, r = 5.5f), "M14.8 14.8l5.2 5.2")
    }

    /** Bottom navigation: journey — a path toward a destination marker. */
    val Journey: ImageVector by lazy {
        lineIcon("Journey", "M4 18c4-1.5 6-7.5 9-10c2-1.7 4.5-2 6.5-0.5", circlePath(cx = 19f, cy = 6.5f, r = 1.4f))
    }

    /** Bottom navigation: learn — an open book. */
    val Learn: ImageVector by lazy {
        lineIcon(
            "Learn",
            "M12 6.5v13",
            "M12 6.5c-2.3-1.6-5.7-1.8-8-0.6v12.4c2.3-1.2 5.7-1 8 0.6",
            "M12 6.5c2.3-1.6 5.7-1.8 8-0.6v12.4c-2.3-1.2-5.7-1-8 0.6",
        )
    }

    /** Bottom navigation: profile. */
    val Profile: ImageVector by lazy {
        lineIcon("Profile", circlePath(cx = 12f, cy = 8.2f, r = 3.4f), "M5 20c0-4.3 3.1-7 7-7s7 2.7 7 7")
    }

    /** SVG path for a full circle, built from two arcs, for use inside a multi-stroke icon. */
    private fun circlePath(cx: Float, cy: Float, r: Float): String =
        "M${cx - r} ${cy}a$r $r 0 1 0 ${2 * r} 0a$r $r 0 1 0 ${-2 * r} 0"

    private fun lineIcon(name: String, vararg pathData: String, strokeWidth: Float = 1.5f): ImageVector = ImageVector.Builder(
        name = "Skinthesia.$name",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        pathData.forEach { data ->
            addPath(
                pathData = addPathNodes(data),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = strokeWidth,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
        }
    }.build()
}
