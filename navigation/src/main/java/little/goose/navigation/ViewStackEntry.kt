package little.goose.navigation

import android.os.Bundle
import kotlinx.coroutines.Job

/**
 * View stack entry
 *
 * @property args A bundle of arguments from navigation operation.
 * @property job You can create a coroutine with this job which bind View lifecycle.
 */
class ViewStackEntry internal constructor(
    var args: Bundle?,
    var job: Job
)