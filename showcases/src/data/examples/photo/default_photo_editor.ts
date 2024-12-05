import IMGLYEditor, {
  EditorPreset,
  EditorSettingsModel
} from '@imgly/editor-react-native';
import { Secrets } from '../../../secrets/secrets';

export const defaultPhotoEditor = async (): Promise<void> => {
  try {
    const settings = new EditorSettingsModel({ license: Secrets.license });
    
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
