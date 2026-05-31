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

  @Test
  fun `validate bot selection works`() {
    val model = MainViewModel(ApplicationProvider.getApplicationContext())
    val bot = model.botCharacters.first { it.name == "Doraemon" }
    model.selectBotOpponent(bot)
    assertEquals("Doraemon", model.selectedBot.name)
    assertEquals("Smart", model.selectedBot.level)
    assertEquals(0.94f, model.selectedBot.accuracy)
  }

  @Test
  fun `validate duel question respects selected cart operations`() {
    val model = MainViewModel(ApplicationProvider.getApplicationContext())
    model.duelOpAddition = false
    model.duelOpSubtraction = false
    model.duelOpMultiplication = true
    model.duelOpDivision = true
    model.duelOpCubes = false
    model.duelOpSequences = false
    model.duelOpCompare = false
    model.duelOpWordQuest = false
    model.duelRangeMin = 0
    model.duelRangeMax = 30

    model.generateNextDuelQuestion()
    val question = model.duelCurrentQuestion
    org.junit.Assert.assertNotNull(question)
    assertTrue("Op should be multiplication or division", question!!.op == "*" || question.op == "/")
  }

  @Test
  fun `validate submit duel answer tracks score and loads next question`() {
    val model = MainViewModel(ApplicationProvider.getApplicationContext())
    model.duelOpAddition = true
    model.duelOpCubes = false
    model.duelOpSequences = false
    model.duelOpCompare = false
    model.duelOpWordQuest = false
    model.duelRangeMin = 0
    model.duelRangeMax = 10
    
    model.generateNextDuelQuestion()
    val quest = model.duelCurrentQuestion
    org.junit.Assert.assertNotNull(quest)
    
    model.submitDuelOption(quest!!.correctAnswer)
    assertEquals(1, model.duelUserScore)
    assertEquals(true, model.duelUserRecent.first())
  }
}
