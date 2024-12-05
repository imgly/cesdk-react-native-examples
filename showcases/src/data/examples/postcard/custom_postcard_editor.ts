import IMGLYEditor, {
  EditorPreset,
  EditorSettingsModel,
  SourceType
} from '@imgly/editor-react-native';
import { Secrets } from '../../../secrets/secrets';

export const customPostcardEditor = async (): Promise<void> => {
  try {
    const settings = new EditorSettingsModel({ license: Secrets.license });
    const result = await IMGLYEditor?.openEditor(
      settings,
      {
        source: require('../../../../assets/bonjour_paris.scene'),
        type: SourceType.SCENE
      },
      EditorPreset.POSTCARD,
      { custom: true }
    );
    console.log(result?.artifact);
  } catch (error) {
    console.log(error);
  }
};
