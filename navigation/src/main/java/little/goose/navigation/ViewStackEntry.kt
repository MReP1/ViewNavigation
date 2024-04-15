package little.goose.navigation

import android.os.Bundle
import kotlinx.coroutines.Job

class ViewStackEntry internal constructor(
    var args: Bundle?,
    var job: Job
)