package com.skinthesia.domain.model

private val AdjectiveByGoal = mapOf(
    SkinGoal.ACNE to "clearer",
    SkinGoal.DARK_SPOTS to "brighter",
    SkinGoal.UNEVEN_TONE to "more even",
    SkinGoal.DRYNESS to "more hydrated",
    SkinGoal.OILINESS to "better balanced",
    SkinGoal.TEXTURE to "smoother",
    SkinGoal.REDNESS to "calmer",
    SkinGoal.FINE_LINES to "firmer",
    SkinGoal.OTHER to "healthier",
)

private const val DefaultStatement = "Healthier, more radiant skin"

/**
 * Suggests a short goal statement from the concerns a person selected. Deterministic, so the same
 * selection always produces the same wording; the statement is always freely editable afterwards.
 */
fun suggestedGoalStatement(goals: List<SkinGoal>): String {
    val adjectives = goals.mapNotNull(AdjectiveByGoal::get).distinct().take(2)
    return when (adjectives.size) {
        0 -> DefaultStatement
        1 -> "${adjectives[0].replaceFirstChar(Char::uppercase)}, healthier skin"
        else -> "${adjectives[0].replaceFirstChar(Char::uppercase)} and ${adjectives[1]} skin"
    }
}
