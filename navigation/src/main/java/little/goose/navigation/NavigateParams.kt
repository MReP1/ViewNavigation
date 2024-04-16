package little.goose.navigation

import android.os.Bundle

class NavigateParams internal constructor() {

    internal constructor(builder: NavigateParams.() -> Unit) : this() {
        this.builder()
    }

    var args: Bundle? = null

}