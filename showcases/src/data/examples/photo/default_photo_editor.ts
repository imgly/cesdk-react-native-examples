import IMGLYEditor, {
  EditorPreset,
  EditorSettingsModel
} from '@imgly/editor-react-native';

export const defaultPhotoEditor = async (): Promise<void> => {
  try {
    // Use undefined license to test nil handling (runs in evaluation mode with watermark)
    const settings = new EditorSettingsModel({ license: undefined });
    
    const result = await IMGLYEditor?.openEditor(
      settings,
      undefined,
      EditorPreset.PHOTO,
      undefined
    );
    console.log(result?.artifact);
  } catch (error) {
    console.log(error);
  }
};
