package ly.img.editor.reactnative.showcases

import android.os.Build
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.net.toUri
import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.fabricEnabled
import com.facebook.react.defaults.DefaultReactActivityDelegate
import expo.modules.ReactActivityDelegateWrapper
import ly.img.editor.Editor
import ly.img.editor.configuration.apparel.ApparelConfigurationBuilder
import ly.img.editor.configuration.apparel.callback.onCreate
import ly.img.editor.configuration.apparel.callback.onExport
import ly.img.editor.configuration.apparel.callback.onLoadAssetSources
import ly.img.editor.configuration.design.DesignConfigurationBuilder
import ly.img.editor.configuration.design.callback.onCreate
import ly.img.editor.configuration.design.callback.onExport
import ly.img.editor.configuration.design.callback.onLoadAssetSources
import ly.img.editor.configuration.photo.PhotoConfigurationBuilder
import ly.img.editor.configuration.photo.callback.onCreate
import ly.img.editor.configuration.photo.callback.onExport
import ly.img.editor.configuration.photo.callback.onLoadAssetSources
import ly.img.editor.configuration.postcard.PostcardConfigurationBuilder
import ly.img.editor.configuration.postcard.callback.onCreate
import ly.img.editor.configuration.postcard.callback.onExport
import ly.img.editor.configuration.postcard.callback.onLoadAssetSources
import ly.img.editor.configuration.video.VideoConfigurationBuilder
import ly.img.editor.configuration.video.callback.onCreate
import ly.img.editor.configuration.video.callback.onExport
import ly.img.editor.configuration.video.callback.onLoadAssetSources
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
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
        Editor(
            license = settings.license,
            baseUri = settings.baseUri.toUri(),
            userId = settings.userId,
            configuration = {
                EditorConfiguration.remember(::ApparelConfigurationBuilder) {
                    onCreate = {
                        onCreate(
                            createScene = {
                                EditorBuilderDefaults.onCreateScene(
                                    scope = this@Editor,
                                    settings = settings,
                                    defaultUri = "file:///android_asset/scene/apparel.scene".toUri(),
                                )
                            },
                            loadAssetSources = {
                                onLoadAssetSources()
                                editorContext.engine.asset.addSource(unsplashAssetSource)
                            },
                        )
                    }
                    onExport = {
                        onExport(
                            postExport = {
                                val result = EditorBuilderDefaults.getExportResult(
                                    scope = this@Editor,
                                    byteBuffer = it,
                                )
                                result(Result.success(result))
                            },
                            error = {
                                result(Result.failure(it))
                            },
                        )
                    }
                    assetLibrary = {
                        remember {
                            AssetLibrary.getDefault(
                                images = LibraryCategory.Images.addSection(unsplashSection),
                            )
                        }
                    }
                }
            },
        ) {
            onClose(it)
        }
    }

    @Composable
    fun CustomDesignEditor(
        settings: EditorSettings,
        result: EditorBuilderResult,
        onClose: (Throwable?) -> Unit,
    ) {
        Editor(
            license = settings.license,
            baseUri = settings.baseUri.toUri(),
            userId = settings.userId,
            configuration = {
                EditorConfiguration.remember(::DesignConfigurationBuilder) {
                    onCreate = {
                        onCreate(
                            createScene = {
                                EditorBuilderDefaults.onCreateScene(
                                    scope = this@Editor,
                                    settings = settings,
                                    defaultUri = "file:///android_asset/scene/design.scene".toUri(),
                                )
                            },
                            loadAssetSources = {
                                onLoadAssetSources()
                                editorContext.engine.asset.addSource(unsplashAssetSource)
                            },
                        )
                    }
                    onExport = {
                        onExport(
                            postExport = {
                                val result = EditorBuilderDefaults.getExportResult(
                                    scope = this@Editor,
                                    byteBuffer = it,
                                )
                                result(Result.success(result))
                            },
                            error = {
                                result(Result.failure(it))
                            },
                        )
                    }
                    assetLibrary = {
                        remember {
                            AssetLibrary.getDefault(
                                images = LibraryCategory.Images.addSection(unsplashSection),
                            )
                        }
                    }
                }
            },
        ) {
            onClose(it)
        }
    }

    @Composable
    fun CustomPhotoEditor(
        settings: EditorSettings,
        result: EditorBuilderResult,
        onClose: (Throwable?) -> Unit,
    ) {
        Editor(
            license = settings.license,
            baseUri = settings.baseUri.toUri(),
            userId = settings.userId,
            configuration = {
                EditorConfiguration.remember(::PhotoConfigurationBuilder) {
                    onCreate = {
                        onCreate(
                            createScene = {
                                EditorBuilderDefaults.onCreateScene(
                                    scope = this@Editor,
                                    settings = settings,
                                    defaultUri = EditorBuilderDefaults.defaultPhotoUri,
                                    sourceType = EditorSourceType.IMAGE,
                                )
                            },
                            loadAssetSources = {
                                onLoadAssetSources()
                                editorContext.engine.asset.addSource(unsplashAssetSource)
                            },
                        )
                    }
                    onExport = {
                        onExport(
                            postExport = {
                                val result = EditorBuilderDefaults.getExportResult(
                                    scope = this@Editor,
                                    byteBuffer = it,
                                )
                                result(Result.success(result))
                            },
                            error = {
                                result(Result.failure(it))
                            },
                        )
                    }
                    assetLibrary = {
                        remember {
                            AssetLibrary.getDefault(
                                images = LibraryCategory.Images.addSection(unsplashSection),
                            )
                        }
                    }
                }
            },
        ) {
            onClose(it)
        }
    }

    @Composable
    fun CustomPostcardEditor(
        settings: EditorSettings,
        result: EditorBuilderResult,
        onClose: (Throwable?) -> Unit,
    ) {
        Editor(
            license = settings.license,
            baseUri = settings.baseUri.toUri(),
            userId = settings.userId,
            configuration = {
                EditorConfiguration.remember(::PostcardConfigurationBuilder) {
                    onCreate = {
                        onCreate(
                            createScene = {
                                EditorBuilderDefaults.onCreateScene(
                                    scope = this@Editor,
                                    settings = settings,
                                    defaultUri = "file:///android_asset/scene/postcard.scene".toUri(),
                                )
                            },
                            loadAssetSources = {
                                onLoadAssetSources()
                                editorContext.engine.asset.addSource(unsplashAssetSource)
                            },
                        )
                    }
                    onExport = {
                        onExport(
                            postExport = {
                                val result = EditorBuilderDefaults.getExportResult(
                                    scope = this@Editor,
                                    byteBuffer = it,
                                )
                                result(Result.success(result))
                            },
                            error = {
                                result(Result.failure(it))
                            },
                        )
                    }
                    assetLibrary = {
                        remember {
                            AssetLibrary.getDefault(
                                images = LibraryCategory.Images.addSection(unsplashSection),
                            )
                        }
                    }
                }
            },
        ) {
            onClose(it)
        }
    }

    @Composable
    fun CustomVideoEditor(
        settings: EditorSettings,
        result: EditorBuilderResult,
        onClose: (Throwable?) -> Unit,
    ) {
        Editor(
            license = settings.license,
            baseUri = settings.baseUri.toUri(),
            userId = settings.userId,
            configuration = {
                EditorConfiguration.remember(::VideoConfigurationBuilder) {
                    onCreate = {
                        onCreate(
                            createScene = {
                                EditorBuilderDefaults.onCreateScene(
                                    scope = this@Editor,
                                    settings = settings,
                                    defaultUri = "file:///android_asset/scene/video.scene".toUri(),
                                )
                            },
                            loadAssetSources = {
                                onLoadAssetSources()
                                editorContext.engine.asset.addSource(unsplashAssetSource)
                            },
                        )
                    }
                    onExport = {
                        onExport(
                            postExport = {
                                val result = EditorBuilderDefaults.getExportResult(
                                    scope = this@Editor,
                                    byteBuffer = it,
                                )
                                result(Result.success(result))
                            },
                            error = {
                                result(Result.failure(it))
                            },
                        )
                    }
                    assetLibrary = {
                        remember {
                            AssetLibrary.getDefault(
                                includeAVResources = true,
                                images = LibraryCategory.Images.addSection(unsplashSection),
                            )
                        }
                    }
                }
            },
        ) {
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
