import IMGLYEditor, {
    EditorPreset,
    EditorSettingsModel
} from '@imgly/editor-react-native';

export const photo_editor_solution = async (): Promise<void> => {
  const settings = new EditorSettingsModel({ license: 'YOUR_LICENSE_KEY' });
  // highlight-preset
  const preset: EditorPreset = EditorPreset.PHOTO;
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
