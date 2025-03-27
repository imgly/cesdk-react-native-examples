package ly.img.editor.reactnative.showcases

import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.compose.runtime.Composable
import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.fabricEnabled
import com.facebook.react.defaults.DefaultReactActivityDelegate
import expo.modules.ReactActivityDelegateWrapper
import kotlinx.coroutines.CancellationException
import ly.img.editor.ApparelEditor
import ly.img.editor.DesignEditor
import ly.img.editor.DismissVideoExportEvent
import ly.img.editor.EditorConfiguration
import ly.img.editor.EngineConfiguration
import ly.img.editor.PhotoEditor
import ly.img.editor.PostcardEditor
import ly.img.editor.VideoEditor
import ly.img.editor.core.library.AssetLibrary
import ly.img.editor.core.library.AssetType
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.library.LibraryContent
import ly.img.editor.core.library.addSection
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.reactnative.module.IMGLYEditorModule
import ly.img.editor.reactnative.module.builder.Builder
import ly.img.editor.reactnative.module.builder.EditorBuilder
import ly.img.editor.reactnative.module.builder.EditorBuilderDefaults
import ly.img.editor.reactnative.module.builder.EditorBuilderResult
import ly.img.editor.reactnative.module.model.EditorPreset
import ly.img.editor.reactnative.module.model.EditorSettings
import ly.img.editor.reactnative.module.model.EditorSourceType
import ly.img.editor.rememberForApparel
import ly.img.editor.rememberForDesign
import ly.img.editor.rememberForPhoto
import ly.img.editor.rememberForPostcard
import ly.img.editor.rememberForVideo
import ly.img.engine.MimeType

class MainActivity : ReactActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Set the theme to AppTheme BEFORE onCreate to support
        // coloring the background, status bar, and navigation bar.
        // This is required for expo-splash-screen.
        setTheme(R.style.AppTheme)
        super.onCreate(null)
    }

    /**
     * Returns the name of the main component registered from JavaScript. This is used to schedule
     * rendering of the component.
     */
    override fun getMainComponentName(): String = "main"

    /**
     * Returns the instance of the [ReactActivityDelegate]. We use [DefaultReactActivityDelegate]
     * which allows you to enable New Architecture with a single boolean flags [fabricEnabled]
     */
    override fun createReactActivityDelegate(): ReactActivityDelegate = ReactActivityDelegateWrapper(
        this,
        BuildConfig.IS_NEW_ARCHITECTURE_ENABLED,
        object : DefaultReactActivityDelegate(
            this,
            mainComponentName,
            fabricEnabled,
        ) {},
    )

    /**
     * Align the back button behavior with Android S
     * where moving root activities to background instead of finishing activities.
     * @see <a href="https://developer.android.com/reference/android/app/Activity#onBackPressed()">onBackPressed</a>
     */
    override fun invokeDefaultOnBackPressed() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            if (!moveTaskToBack(false)) {
                // For non-root activities, use the default implementation to finish them.
                super.invokeDefaultOnBackPressed()
            }
            return
        }

        // Use the default back button implementation on Android S
        // because it's doing more than [Activity.moveTaskToBack] in fact.
        super.invokeDefaultOnBackPressed()
    }

    private val unsplashAssetSource = UnsplashAssetSource(Secrets.unsplashHost)
    private val unsplashSection = LibraryContent.Section(
        titleRes = R.string.unsplash,
        sourceTypes = listOf(AssetSourceType(sourceId = unsplashAssetSource.sourceId)),
        assetType = AssetType.Image,
    )
    private val assetLibrary = AssetLibrary.getDefault(
        images = LibraryCategory.Images.addSection(unsplashSection),
    )

    override fun onStart() {
        super.onStart()

        IMGLYEditorModule.builderClosure = { preset, metadata ->
            when (preset) {
                EditorPreset.APPAREL -> {
                    builderOrCustom({ EditorBuilder.apparel() }, {
                        EditorBuilder.custom {
                            CustomApparelEditor(settings, result, onClose)
                        }
                    }, metadata)
                }

                EditorPreset.POSTCARD -> {
                    builderOrCustom({ EditorBuilder.postcard() }, {
                        EditorBuilder.custom {
                            CustomPostcardEditor(settings, result, onClose)
                        }
                    }, metadata)
                }

                EditorPreset.PHOTO -> {
                    builderOrCustom({ EditorBuilder.photo() }, {
                        EditorBuilder.custom {
                            CustomPhotoEditor(settings, result, onClose)
                        }
                    }, metadata)
                }

                EditorPreset.DESIGN -> {
                    builderOrCustom({ EditorBuilder.design() }, {
                        EditorBuilder.custom {
                            CustomDesignEditor(settings, result, onClose)
                        }
                    }, metadata)
                }

                EditorPreset.VIDEO -> {
                    builderOrCustom({ EditorBuilder.video() }, {
                        EditorBuilder.custom {
                            CustomVideoEditor(settings, result, onClose)
                        }
                    }, metadata)
                }

                null -> {
                    builderOrCustom({ EditorBuilder.design() }, {
                        EditorBuilder.custom {
                            CustomDesignEditor(settings, result, onClose)
                        }
                    }, metadata)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        IMGLYEditorModule.builderClosure = null
    }

    @Composable
    fun CustomApparelEditor(
        settings: EditorSettings,
        result: EditorBuilderResult,
        onClose: (Throwable?) -> Unit,
    ) {
        val engineConfiguration = EngineConfiguration.remember(
            license = settings.license,
            baseUri = Uri.parse(settings.baseUri),
            userId = settings.userId,
            onCreate = {
                EditorBuilderDefaults.onCreate(
                    engine = editorContext.engine,
                    eventHandler = editorContext.eventHandler,
                    settings = settings,
                    defaultScene = EngineConfiguration.defaultApparelSceneUri,
                )
                editorContext.engine.asset.addSource(unsplashAssetSource)
            },
            onExport = {
                val export = EditorBuilderDefaults.onExport(
                    engine = editorContext.engine,
                    eventHandler = editorContext.eventHandler,
                    mimeType = MimeType.PDF,
                )
                result(Result.success(export))
            },
        )

        val editorConfiguration = EditorConfiguration.rememberForApparel(
            assetLibrary = assetLibrary,
        )
        ApparelEditor(engineConfiguration = engineConfiguration, editorConfiguration = editorConfiguration) {
            onClose(it)
        }
    }

    @Composable
    fun CustomDesignEditor(
        settings: EditorSettings,
        result: EditorBuilderResult,
        onClose: (Throwable?) -> Unit,
    ) {
        val engineConfiguration = EngineConfiguration.remember(
            license = settings.license,
            baseUri = Uri.parse(settings.baseUri),
            userId = settings.userId,
            onCreate = {
                EditorBuilderDefaults.onCreate(
                    engine = editorContext.engine,
                    eventHandler = editorContext.eventHandler,
                    settings = settings,
                    defaultScene = EngineConfiguration.defaultDesignSceneUri,
                )
                editorContext.engine.asset.addSource(unsplashAssetSource)
            },
            onExport = {
                val export = EditorBuilderDefaults.onExport(
                    engine = editorContext.engine,
                    eventHandler = editorContext.eventHandler,
                    mimeType = MimeType.PDF,
                )
                result(Result.success(export))
            },
        )

        val editorConfiguration = EditorConfiguration.rememberForDesign(
            assetLibrary = assetLibrary,
        )
        DesignEditor(engineConfiguration = engineConfiguration, editorConfiguration = editorConfiguration) {
            onClose(it)
        }
    }

    @Composable
    fun CustomPhotoEditor(
        settings: EditorSettings,
        result: EditorBuilderResult,
        onClose: (Throwable?) -> Unit,
    ) {
        val engineConfiguration = EngineConfiguration.remember(
            license = settings.license,
            baseUri = Uri.parse(settings.baseUri),
            userId = settings.userId,
            onCreate = {
                EditorBuilderDefaults.onCreate(
                    engine = editorContext.engine,
                    eventHandler = editorContext.eventHandler,
                    settings = settings,
                    defaultScene = EditorBuilderDefaults.defaultPhotoUri,
                    sourceType = EditorSourceType.IMAGE,
                )
                editorContext.engine.asset.addSource(unsplashAssetSource)
            },
            onExport = {
                val export = EditorBuilderDefaults.onExport(
                    engine = editorContext.engine,
                    eventHandler = editorContext.eventHandler,
                    mimeType = MimeType.PNG,
                )
                result(Result.success(export))
            },
        )
        val editorConfiguration = EditorConfiguration.rememberForPhoto(
            assetLibrary = assetLibrary,
        )
        PhotoEditor(engineConfiguration = engineConfiguration, editorConfiguration = editorConfiguration) {
            onClose(it)
        }
    }

    @Composable
    fun CustomPostcardEditor(
        settings: EditorSettings,
        result: EditorBuilderResult,
        onClose: (Throwable?) -> Unit,
    ) {
        val engineConfiguration = EngineConfiguration.remember(
            license = settings.license,
            baseUri = Uri.parse(settings.baseUri),
            userId = settings.userId,
            onCreate = {
                EditorBuilderDefaults.onCreate(
                    engine = editorContext.engine,
                    eventHandler = editorContext.eventHandler,
                    settings = settings,
                    defaultScene = EngineConfiguration.defaultPostcardSceneUri,
                )
                editorContext.engine.asset.addSource(unsplashAssetSource)
            },
            onExport = {
                val export = EditorBuilderDefaults.onExport(
                    engine = editorContext.engine,
                    eventHandler = editorContext.eventHandler,
                    mimeType = MimeType.PDF,
                )
                result(Result.success(export))
            },
        )

        val editorConfiguration = EditorConfiguration.rememberForPostcard(
            assetLibrary = assetLibrary,
        )
        PostcardEditor(engineConfiguration = engineConfiguration, editorConfiguration = editorConfiguration) {
            onClose(it)
        }
    }

    @Composable
    fun CustomVideoEditor(
        settings: EditorSettings,
        result: EditorBuilderResult,
        onClose: (Throwable?) -> Unit,
    ) {
        val engineConfiguration = EngineConfiguration.remember(
            license = settings.license,
            baseUri = Uri.parse(settings.baseUri),
            userId = settings.userId,
            onCreate = {
                EditorBuilderDefaults.onCreate(
                    engine = editorContext.engine,
                    eventHandler = editorContext.eventHandler,
                    settings = settings,
                    defaultScene = EngineConfiguration.defaultPostcardSceneUri,
                )
                editorContext.engine.asset.addSource(unsplashAssetSource)
            },
            onExport = {
                try {
                    val export = EditorBuilderDefaults.onExportVideo(
                        engine = editorContext.engine,
                        eventHandler = editorContext.eventHandler,
                        mimeType = MimeType.MP4,
                    )
                    result(Result.success(export))
                } catch (e: Exception) {
                    if (e !is CancellationException) {
                        result(Result.failure(e))
                    } else {
                        editorContext.eventHandler.send(DismissVideoExportEvent)
                    }
                }
            },
        )

        val editorConfiguration = EditorConfiguration.rememberForVideo(
            assetLibrary = assetLibrary,
        )
        VideoEditor(engineConfiguration = engineConfiguration, editorConfiguration = editorConfiguration) {
            onClose(it)
        }
    }

    private inline fun builderOrCustom(
        defaultBuilder: () -> Builder,
        customBuilder: () -> Builder,
        metadata: Map<String, Any>?,
    ): Builder {
        if (metadata?.get("custom") == true) {
            return customBuilder()
        }
        return defaultBuilder()
    }
}
