@file:Suppress("ktlint:standard:import-ordering")

package ly.img.editor.reactnative.showcases.guides

// highlight-import-compose
import androidx.compose.runtime.Composable
// highlight-import-compose
import ly.img.camera.core.CaptureVideo
import ly.img.camera.core.EngineConfiguration
import ly.img.camera.reactnative.module.IMGLYCameraModule
import ly.img.camera.reactnative.module.model.CameraResult
// highlight-import-editor
import ly.img.editor.reactnative.module.IMGLYEditorModule
import ly.img.editor.reactnative.module.builder.EditorBuilder
import ly.img.editor.reactnative.module.builder.EditorBuilderResult
import ly.img.editor.reactnative.module.model.EditorSettings
// highlight-import-editor

private fun useCustomEditor() {
    // highlight-closure
    IMGLYEditorModule.builderClosure = { _, metadata ->
        if (metadata?.get("custom") == true) {
            EditorBuilder.custom {
                CustomEditor(settings, result, onClose)
            }
        } else {
            EditorBuilder.design()
        }
    }
    // highlight-closure
}

private fun customizeCamera() {
    // highlight-camera-configuration
    // highlight-camera-configuration-closure
    // Configure the [CaptureVideo.Input].
    IMGLYCameraModule.configurationClosure = { metadata ->
        val engineConfiguration = EngineConfiguration("MY_LICENSE")
        CaptureVideo.Input(engineConfiguration)
    }

    // highlight-camera-configuration-closure
    // highlight-camera-result-closure
    // Modify the [CameraResult].
    IMGLYCameraModule.resultClosure = { result ->
        CameraResult(result?.recording, mapOf("MY_CUSTOM_KEY" to "MY_CUSTOM_VALUE"))
    }
    // highlight-camera-result-closure
    // highlight-camera-configuration
}

@Composable
private fun CustomEditor(
    settings: EditorSettings,
    result: EditorBuilderResult,
    onClose: (Throwable?) -> Unit,
) {}
