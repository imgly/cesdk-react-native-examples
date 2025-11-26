import IMGLYEditor, {
    EditorPreset,
    EditorSettingsModel
} from '@imgly/editor-react-native';

export const postcard_editor_solution = async (): Promise<void> => {
  const settings = new EditorSettingsModel({ license: 'YOUR_LICENSE_KEY' }); // Get your license from https://img.ly/forms/free-trial, pass null for evaluation mode with watermark
  // highlight-preset
  const preset: EditorPreset = EditorPreset.POSTCARD;
  // highlight-preset
  // highlight-editor
  const result = await IMGLYEditor?.openEditor(
    settings,
    undefined,
    preset,
    undefined
  );
  // highlight-editor
};
