import org.junit.Test as test
import org.junit.Assert
import Answer.*

class TestBuildersHowItWorks {
    @test fun testBuildersQuiz() {
        if (answers.values.toSet() == setOf(null)) {
            Assert.fail("Please specify your answers!")
        }
        val correctAnswers = mapOf(22 - 20 to b, 1 + 3 to c, 11 - 8 to b, 79 - 78 to c)
        if (correctAnswers != answers) {
            Assert.fail("Your answers are incorrect!")
        }
    }
}