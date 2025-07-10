package app.typist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import typistapp.composeapp.generated.resources.Res
import typistapp.composeapp.generated.resources.compose_multiplatform
import typistapp.composeapp.generated.resources.resized_monalisa

@Composable
@Preview
fun App() {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            val anImage = imageResource(Res.drawable.resized_monalisa)
            var typesetElements by remember { mutableStateOf(emptyList<Typeset>()) }
            LaunchedEffect(Unit) {
                typesetElements = readResourceFile()
            }

            val converter = TypistArtConverter(typesetElements).convert(anImage)
            Text(converter)

//            var sample by remember { mutableStateOf(emptyList<SampleData>()) }
//            LaunchedEffect(Unit) {
//                sample = getSampleData()
//            }
//            sample.forEach {
//                Text(it.character)
//            }

//            Button(onClick = { showContent = !showContent }) {
//                Text("Click me!")
//            }
//            AnimatedVisibility(showContent) {
//                var greeting by remember { mutableStateOf("Loading...") }
//                LaunchedEffect(Unit) {
//                    greeting = try {
//                        Greeting().greet()
//                    } catch (e: Exception) {
//                        "Error: loading JSON: ${e.message}"
//                    }
//                }
//                // val greeting = remember { Greeting().greet() }
//                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
//                    Image(painterResource(Res.drawable.compose_multiplatform), null)
//                    Text("Compose: $greeting")
//                }
//            }
        }
    }
}