package app.typist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import typistapp.composeapp.generated.resources.Res
import typistapp.composeapp.generated.resources.resized_monalisa

@Composable
@Preview
fun App() {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        Column(
            modifier =
                Modifier
                    .safeContentPadding()
                    .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val anImage = imageResource(Res.drawable.resized_monalisa)
            var typesetElements by remember { mutableStateOf(emptyList<TypesetElement>()) }
            var result by remember { mutableStateOf<String>("") }

            LaunchedEffect(Unit) {
                typesetElements = readResourceFile()
            }

            LaunchedEffect(typesetElements, anImage) {
                if (typesetElements.isNotEmpty()) {
                    result = TypistArtConverter(typesetElements).convert(32, anImage)
                } else {
                    println("empty typeset list")
                }
            }
        }
    }
}
