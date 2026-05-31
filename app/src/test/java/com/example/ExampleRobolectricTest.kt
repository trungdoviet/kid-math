package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ui.MainViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Kidmath", appName)
  }

  @Test
  fun `validate addition range generateMathQuest`() {
    val model = MainViewModel(ApplicationProvider.getApplicationContext())
    model.selectedMathOp = "+"
    model.selectedRangeMin = 20
    model.selectedRangeMax = 50

    model.generateMathQuest()
    assertEquals(5, model.mathQuestions.size)
    for (question in model.mathQuestions) {
      assertEquals("+", question.op)
      val sum = question.val1 + question.val2
      assertTrue("val1 ${question.val1} should be >= 20", question.val1 >= 20)
      assertTrue("sum $sum should be <= 50", sum <= 50)
    }
  }

  @Test
  fun `validate multiplication defaults for easy explorer`() {
    val model = MainViewModel(ApplicationProvider.getApplicationContext())
    model.selectMathOpAndNavigate("*")
    assertEquals("*", model.selectedMathOp)
    assertEquals(0, model.selectedRangeMin)
    assertEquals(10, model.selectedRangeMax)
  }
}
