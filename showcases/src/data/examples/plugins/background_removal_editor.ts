import IMGLYEditor, {
  EditorPreset,
  SourceType
} from '@imgly/editor-react-native';
import { Secrets } from '../../../secrets/secrets';

export const backgroundRemovalEditor = async (): Promise<void> => {
  try {
    const settings = Secrets.editorSettings();
    const result = await IMGLYEditor?.openEditor(
      settings,
      {
        source: `${settings.baseUri}/ly.img.image/images/sample_14.jpg`,
        type: SourceType.IMAGE
      },
      EditorPreset.PHOTO,
      { plugin: 'backgroundRemoval' }
    );
    console.log(result?.artifact);
  } catch (error) {
    console.log(error);
  }
};
