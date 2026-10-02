package com.skinthesia.domain.model

/**
 * A person's SkinPrint history, ordered oldest to newest by [SkinPrint.version]. Not a new model —
 * a plain view over the same [SkinPrint]s a future repository will persist and append to, so
 * history can grow over time without ever overwriting an earlier version.
 */
typealias SkinPrintHistory = List<SkinPrint>

/** True once there is more than the single baseline SkinPrint to compare against. */
fun SkinPrintHistory.hasComparableHistory(): Boolean = size >= 2
