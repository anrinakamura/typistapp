package app.typist

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import typistapp.composeapp.generated.resources.NotoSansJP_Regular
import typistapp.composeapp.generated.resources.Res
import typistapp.composeapp.generated.resources.resized_monalisa

sealed class AppUiState {
    object Loading : AppUiState()

    data class Generating(
        val typesetElements: List<TypesetElement>,
    ) : AppUiState()

    data class Success(
        val typistArt: List<String>,
    ) : AppUiState()

    data class Error(
        val message: String,
    ) : AppUiState()
}

@Composable
@Preview
fun App(
    viewModel: TypistViewModel = viewModel { TypistViewModel() }
) {
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
            modifier = Modifier.fillMaxSize().padding(vertical = 128.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // TODO: move to ViewModel
            when (val state = uiState) {
                is AppUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        CircularProgressIndicator(modifier = Modifier.width(64.dp))
                        Spacer(modifier = Modifier.height(32.dp))
                        Text("Generating typist-art...")
                    }
                }
                is AppUiState.Generating -> {
                    // TODO: move to ViewModel
                    val anImage = imageResource(Res.drawable.resized_monalisa)
                    val result = TypistArtConverter(state.typesetElements).convert(32, anImage)
                    uiState = AppUiState.Success(result)
                }
                is AppUiState.Success -> {
                    // TODO: animate the typist-art
                    // PrintTypistArt(state.typistArt)
                    AnimateTypistArt(state.typistArt.joinToString("\n"))
                }
                is AppUiState.Error -> {
                    Text(state.message)
                }
            }
        }
    }
}

@Composable
fun AnimateTypistArt(text: String) {
    // https://developer.android.com/develop/ui/compose/quick-guides/content/animate-text

    val notoSanJP = FontFamily(Font(Res.font.NotoSansJP_Regular, FontWeight.Normal))

    var subString by remember { mutableStateOf("") }
    val delayInMs = 10L

    LaunchedEffect(text) {
        for (i in 1..text.length) {
            subString = text.substring(0, i)
            delay(delayInMs)
        }
    }

    Text(
        text = subString,
        fontSize = 12.sp,
        fontFamily = notoSanJP, // FontFamily.Monospace,
        lineHeight = 12.sp,
    )
}

@Composable
fun PrintTypistArt(lines: List<String>) {
    val notoSanJP = FontFamily(Font(Res.font.NotoSansJP_Regular, FontWeight.Normal))

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        // val maxWidth = constraints.maxWidth
        // val maxHeight = constraints.maxHeight
        // val fontSizePx =
        //     minOf(
        //         // TODO: get columns from ViewModel
        //         maxWidth / 32,
        //         maxHeight / lines.size,
        //     ) * 0.99f
        // println("maxWidth: $maxWidth, maxHeight: $maxHeight, fontSize: $fontSizePx")

        // val fontSizeSp = with(LocalDensity.current) { fontSizePx.toSp() }

        Box(modifier = Modifier.fillMaxSize()) {
            // TODO: allow to use input image
            Image(
                painter = painterResource(Res.drawable.resized_monalisa),
                contentDescription = "original image converted to typist-art",
                contentScale = ContentScale.Fit,
                // modifier = Modifier.fillMaxSize(),
            )

            BasicText(
                text = lines.joinToString("\n"),
                style = TextStyle(fontFamily = notoSanJP),
                maxLines = 1,
                autoSize = TextAutoSize.StepBased(),
            )
        }
    }
}
