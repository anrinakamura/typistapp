package app.typist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import typistapp.composeapp.generated.resources.Res
import typistapp.composeapp.generated.resources.resized_monalisa

sealed class AppUiState {
    object Loading : AppUiState()

    data class Generating(
        val typesetElements: List<TypesetElement>,
    ) : AppUiState()

    data class Success(
        val typistArt: String,
    ) : AppUiState()

    data class Error(
        val message: String,
    ) : AppUiState()
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        var uiState by remember { mutableStateOf<AppUiState>(AppUiState.Loading) }

        LaunchedEffect(Unit) {
            try {
                val typesetElements =
                    withContext(Dispatchers.Default) {
                        readResourceFile()
                    }
                uiState = AppUiState.Generating(typesetElements)
            } catch (e: Exception) {
                e.printStackTrace()
                uiState = AppUiState.Error("Failed to load resources: ${e.message}")
            }
        }

        Column(
            modifier =
                Modifier
                    .safeContentPadding()
                    .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (val state = uiState) {
                is AppUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is AppUiState.Generating -> {
                    val anImage = imageResource(Res.drawable.resized_monalisa)
                    val result = TypistArtConverter(state.typesetElements).convert(32, anImage)
                    uiState = AppUiState.Success(result)
                }
                is AppUiState.Success -> {
                    // TODO: animate the typist-art
                    Text(state.typistArt)
                }
                is AppUiState.Error -> {
                    Text(state.message)
                }
            }

//            val anImage = imageResource(Res.drawable.resized_monalisa)
//            var typesetElements by remember { mutableStateOf(emptyList<TypesetElement>()) }
//            var result by remember { mutableStateOf<String>("") }
//
//            // NOTE: LaunchEffect runs on CoroutineContext of the default thread (UI thread)
//            LaunchedEffect(Unit) {
//                typesetElements = readResourceFile()
//            }
//
//            LaunchedEffect(typesetElements, anImage) {
//                if (typesetElements.isNotEmpty()) {
//                    result = TypistArtConverter(typesetElements).convert(32, anImage)
//                } else {
//                    println("empty typeset list")
//                }
//            }
        }
    }
}
