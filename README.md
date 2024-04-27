A navigation library of View ui system which not supported in jetpack navigation.

If you want to use DSL to define a navigation but can't use Jetpack Compose, maybe you should consider this library. But If you need to write so many logic in a router, I recommend you to use Navigation for fragment which support DSL too.

The reason for this library is because the communication between fragments is too complicated, we can put state flows down and events flow up to build a unidirectional data flow which can make code more maintainable. ref: [Architecting](https://developer.android.com/develop/ui/compose/architecture "Architecting")

```kotlin
ViewNavigator( /*...*/ ) {
   navView(SCREEN_ONE) { /*...*/ }
   navViewBinding(SCREEN_TWO) { /*...*/ }
   navViewController(SCREEN_THREE) { /*...*/ }
}
```

- support DSL to build a navigation graph.
- support build View Router with View, ViewBinding and ViewController (something like light fragment).
- support args / stack restore when Activity recreate.
- support coroutine by using a job binding view lifecycle.

## Gradle Setup

1. add jitpack to your root repositories.
   ```groovy
   dependencyResolutionManagement {
       repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
       repositories {
           mavenCentral()
           maven { url 'https://jitpack.io' }
       }
   }
   ```
2. add dependencies.&#x20;

   ![](https://jitpack.io/v/FnGoose/ViewNavigation.svg)
   ```groovy
   dependencies {
       implementation 'com.github.FnGoose:ViewNavigation:Version'
   }
   ```

## Start

1. create ViewNavigatorController.
   ```kotlin
   val navController = ViewNavigatorController()
   ```
2. create ViewNavigator with some screen.
   ```kotlin
   ViewNavigator(
       navController = navController,
       initRoute = "ScreenOne",
       name = "MainNavigator"
   ) {
       navView("ScreenOne") {
           linearLayout(context) {
               addButton(
                   text = "Navigate to Screen Two",
                   onClick = { navController.navigateTo("ScreenTwo") }
               )
           }
       }
       navView("ScreenTwo") {
           linearLayout(context) {
               addButton(
                   text = "Pop",
                   onClick = { navController.pop() }
               )
           }
       }
   }
   ```
3. optional, bind backpress to navController.
   ```kotlin
   onBackPressedDispatcher.addCallback(this) {
       // Intercept back pressed event from MainActivity.
       navController.pop()
   }
   ```

## Create Route

Every router need a view. you can provide a View, no matter how you generate it. In this library, we support three way to generate a route.

1. View
   ```kotlin
   ViewNavigator() {
       navView("ScreenOne") {
           View(context)
       }
   }
   ```
2. ViewBinding
   ```kotlin
   ViewNavigator() {
       navViewBinding("ScreenOne") {
           ScreenOneBinding.inflate(layoutInflater)
       }
   }
   ```
3. navViewController
   ```kotlin
   ViewNavigator() {
       navViewController("ScreenOne") {
           ScreenViewController()
       }
   }
   ```
   If you use this way, you need to overwrite buildView function of ViewController to provide a view.

In activity or fragment, we have onResume function to do something when the screen is foreground. In ViewNavigation, I use onAttachWindow and onDetachWindow to provide two function update and onDetach.

# Nested Pop

you can overwrite onPop to support nested Navigation.

```kotlin
class NestedController(

    private var subNavController: ViewNavigatorController? = null
   
    override fun onPop(view: View, targetRoute: String?): Boolean {
        val subPopRet = subNavController.pop()
        if (!subPopRet) {
            // current pop logc.
        }
        return super.onPop(view, targerRoute)
    }
    
}
```

## Translation Animation

Translation of Navigation is very important.

ViewNavigation provide very flexible translation animation api.

```kotlin
fun interface NavViewAnimation {
     /**
     * On animate
     *
     * @param view the view you need to operate.
     * @param progress from 0 to 1.
     * @param width with of parent.
     * @param height height of parent.
     */
    fun onAnimate(
        view: View,
        progress: Float,
        width: Int,
        height: Int
    )
}

```

And you can hign level api to create a interface:

```kotlin
inline fun alphaViewAnimation(
    crossinline transform: (progress: Float) -> Float
) = NavViewAnimation { view, progress, _, _ -> view.alpha = transform(progress) }

inline fun horizontalViewAnimation(
    crossinline transform: (progress: Float) -> Float
) = horizontalTranslationViewAnimation { progress, width, layoutDirection ->
    width * layoutDirection.ldFlag * transform(progress)
}

inline fun horizontalTranslationViewAnimation(
    crossinline transform: (progress: Float, width: Int, layoutDirection: Int) -> Float
) = NavViewAnimation { view, progress, width, _ ->
    view.translationX = transform(progress, width, view.layoutDirection)
}

inline fun verticalViewAnimation(
    crossinline transform: (progress: Float) -> Float
) = verticalTranslationViewAnimation { progress, height ->
    height * transform(progress)
}

inline fun verticalTranslationViewAnimation(
    crossinline transform: (progress: Float, height: Int) -> Float
) = NavViewAnimation { view, progress, _, height ->
    view.translationY = transform(progress, height)
}
```

also you can comine two animation with \`+\`

```kotlin
NavViewAnimation.HorizontalEnterViewAnimation + NavViewAnimation.FadeEnterViewAnimation
```

## Custom View Container (ViewGroup)

TODO progress ...

## Add listener

TODO...


