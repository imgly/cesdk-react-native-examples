import IMGLYEditor, {
  EditorPreset,
  EditorSettingsModel
} from '@imgly/editor-react-native';

export const defaultDesignEditor = async (): Promise<void> => {
  try {
    // Use undefined license to test nil handling (runs in evaluation mode with watermark)
    const settings = new EditorSettingsModel({ license: undefined });
    const result = await IMGLYEditor?.openEditor(
      settings,
      undefined,
      EditorPreset.DESIGN,
      undefined
    );
    console.log(result?.artifact);
  } catch (error) {
    console.log(error);
  }
};
