import IMGLYEditor, { EditorSettingsModel } from '@imgly/editor-react-native';

export const apparel_editor_solution = async (): Promise<void> => {
  // highlight-configuration
  const settings = new EditorSettingsModel({ license: 'YOUR_LICENSE_KEY' });
  // highlight-configuration
  // highlight-editor
  const result = await IMGLYEditor?.openEditor(settings);
  // highlight-editor
};
