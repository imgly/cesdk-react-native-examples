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
    // highlight-baseUri
    baseUri: 'YOUR_BASE_URI',
    // highlight-baseUri
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
