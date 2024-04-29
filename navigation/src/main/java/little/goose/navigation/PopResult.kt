package little.goose.navigation

sealed class PopResult {

    data object DoNothing: PopResult()

    data object Intercept: PopResult()

    data class Redirect(
        val targetRoute: String
    ): PopResult()

}