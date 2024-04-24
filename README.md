# ViewNavigation

A navigation library of View ui system which not supported in jetpack navigation.

If you want to use DSL to define a navigation but can't use Jetpack Compose, maybe you should consider this library. But If you need to write so many logic in a router, I recommend you to use Navigation for fragment which support DSL too.

The reason for this library is because the communication between fragments is too complicated, we can put state flows down and events flow up to build a unidirectional data flow which can make code more maintainable. ref: [Architecting](https://developer.android.com/develop/ui/compose/architecture)

```Kotlin
ViewNavigator(...) {
   navView(SCREEN_ONE) { ... }
   navViewBinding(SCREEN_TWO) { ... }
   navViewController(SCREEN_THREE) { ... }
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

2. add dependencies.

[![](https://jitpack.io/v/FnGoose/ViewNavigation.svg)](https://jitpack.io/#FnGoose/ViewNavigation)
```groovy
dependencies {
    implementation 'com.github.FnGoose:ViewNavigation:Version'
}
```

## Start

1. create ViewNavigatorController.
   ```Kotlin
   val navController = ViewNavigatorController()
   ```
2. create ViewNavigator with some screen.
   ```Kotlin
   ViewNavigator(
       navController = navController,
       initRoute = "ScreenOne",
       name = "MainNavigator"
   ) {
       navView("ScreenOne") {
           linearLayout(context) {
               addButton("Navigate to Screen Two", onClick = { navController.navigateTo("ScreenTwo) })
           }
       }
       navView("ScreenTwo") {
           linearLayout(context) {
               addButton("Pop", onClick = { navController.pop() })
           }
       }
   }
   ```
3. optional, bind backpress to navController.
   ```Kotlin
   onBackPressedDispatcher.addCallback(this) {
       // Intercept back pressed event from MainActivity.
       navController.pop()
   }
   ```
## View & ViewBinding & ViewController

TODO...

## Translation Animation

TODO...

## Custom View Container (ViewGroup)

TODO...

## Add listener

TODO...
