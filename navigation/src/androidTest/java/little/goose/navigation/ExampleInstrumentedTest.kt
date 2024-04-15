package little.goose.navigation

import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private val appContext by lazy { InstrumentationRegistry.getInstrumentation().targetContext }

    @Test
    fun testNavigationLogic() {
        testInitLogic()
    }

    private fun testInitLogic() {

    }
}