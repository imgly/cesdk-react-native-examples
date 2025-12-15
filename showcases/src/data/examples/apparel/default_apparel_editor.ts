import IMGLYEditor, {
  EditorPreset,
  EditorSettingsModel
} from '@imgly/editor-react-native';

export const defaultApparelEditor = async (): Promise<void> => {
  try {
    // Use undefined license to test nil handling (runs in evaluation mode with watermark)
    const settings = new EditorSettingsModel({ license: undefined });
    const result = await IMGLYEditor?.openEditor(
      settings,
      undefined,
      EditorPreset.APPAREL,
      undefined
    );
    console.log(result?.artifact);
  } catch (error) {
    console.log(error);
  }
};
