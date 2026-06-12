import IMGLYEditor, { EditorPreset } from '@imgly/editor-react-native';
import { Secrets } from '../../../secrets/secrets';

export const defaultVideoEditor = async (): Promise<void> => {
  try {
    // Use undefined license to test nil handling (runs in evaluation mode with watermark)
    const settings = Secrets.evaluationModeSettings();
    const result = await IMGLYEditor?.openEditor(
      settings,
      undefined,
      EditorPreset.VIDEO,
      undefined
    );
    console.log(result?.artifact);
  } catch (error) {
    console.log(error);
  }
};
