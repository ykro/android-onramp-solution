package onramp.paso2

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class StudyCounterScreenTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun `CA-2_3 - StudyCounterScreen suma sesiones y se reinicia con el botón Reiniciar`() {
        compose.setContent { StudyCounterScreen() }

        compose.onNodeWithText("0 sesiones completadas").assertExists()

        compose.onNodeWithText("Sumar sesión").performClick()
        compose.onNodeWithText("1 sesión completada").assertExists(
            "Después de un clic en «Sumar sesión» la pantalla debe decir «1 sesión completada». " +
                "Si sigue en 0, el contador se vuelve a crear en cada recomposición.",
        )

        compose.onNodeWithText("Sumar sesión").performClick()
        compose.onNodeWithText("2 sesiones completadas").assertExists()

        compose.onNodeWithText("Reiniciar").assertExists(
            "Falta un botón con el texto «Reiniciar».",
        )
        compose.onNodeWithText("Reiniciar").performClick()
        compose.onNodeWithText("0 sesiones completadas").assertExists()
    }
}
