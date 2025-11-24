import IMGLYEditor, {
  EditorPreset,
  EditorSettingsModel
} from '@imgly/editor-react-native';

export const basicEditor = async (): Promise<void> => {
  // highlight-configuration
  const settings = new EditorSettingsModel({
    // highlight-license
    license: 'YOUR_LICENSE_KEY', // Get your license from https://img.ly/forms/free-trial, pass null for evaluation mode with watermark
    // highlight-license
    // highlight-sceneBaseUri
    sceneBaseUri: 'YOUR_SCENE_BASE_URI',
    // highlight-sceneBaseUri
    // highlight-assetBaseUri
    assetBaseUri: 'YOUR_ASSET_BASE_URI',
    // highlight-assetBaseUri
    // highlight-userId
    userId: 'YOUR_USER_ID'
    // highlight-userId
  });
  // highlight-configuration

  // highlight-source
  const source = require('MY_CUSTOM_SOURCE');
  // highlight-source
  // highlight-preset
  const preset: EditorPreset = EditorPreset.DESIGN;
  // highlight-preset
  // highlight-metadata
  const metadata = {
    MY_KEY: 'MY_VALUE'
  };
  // highlight-metadata

  const result = await IMGLYEditor?.openEditor(
    settings,
    source,
    preset,
    metadata
  );
};
