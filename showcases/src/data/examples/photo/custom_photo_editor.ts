import IMGLYEditor, {
  EditorPreset,
  EditorSettingsModel,
  SourceType
} from '@imgly/editor-react-native';
import { Secrets } from '../../../secrets/secrets';

export const customPhotoEditor = async (): Promise<void> => {
  try {
    const settings = new EditorSettingsModel({ license: Secrets.license });
    const result = await IMGLYEditor?.openEditor(
      settings,
      {
        source: require('../../../../assets/sample_image.jpg'),
        type: SourceType.IMAGE
      },
      EditorPreset.PHOTO,
      { custom: true }
    );
    console.log(result?.artifact);
  } catch (error) {
    console.log(error);
  }
};
