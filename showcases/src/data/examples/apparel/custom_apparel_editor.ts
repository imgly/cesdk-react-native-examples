import IMGLYEditor, {
  EditorPreset,
  EditorSettingsModel,
  SourceType
} from '@imgly/editor-react-native';
import { Secrets } from '../../../secrets/secrets';

export const customApparelEditor = async (): Promise<void> => {
  try {
    const settings = new EditorSettingsModel({ license: Secrets.license });
    const result = await IMGLYEditor?.openEditor(
      settings,
      {
        source: require('../../../../assets/apparel-ui-b-4.scene'),
        type: SourceType.SCENE
      },
      EditorPreset.APPAREL,
      { custom: true }
    );
    console.log(result?.artifact);
  } catch (error) {
    console.log(error);
  }
};
