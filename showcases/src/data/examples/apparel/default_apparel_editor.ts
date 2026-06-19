import IMGLYEditor, { EditorPreset } from '@imgly/editor-react-native';
import { Secrets } from '../../../secrets/secrets';

export const defaultApparelEditor = async (): Promise<void> => {
  try {
    // Use undefined license to test nil handling (runs in evaluation mode with watermark)
    const settings = Secrets.evaluationModeSettings();
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
