import IMGLYEditor, { EditorSettingsModel } from '@imgly/editor-react-native';

export const apparel_editor_solution = async (): Promise<void> => {
  // highlight-configuration
  const settings = new EditorSettingsModel({ license: 'YOUR_LICENSE_KEY' }); // Get your license from https://img.ly/forms/free-trial, pass null for evaluation mode with watermark
  // highlight-configuration
  // highlight-editor
  const result = await IMGLYEditor?.openEditor(settings);
  // highlight-editor
};
