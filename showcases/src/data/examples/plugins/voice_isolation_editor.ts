import IMGLYEditor, {
  EditorPreset,
  SourceType
} from '@imgly/editor-react-native';
import { Asset } from 'expo-asset';
import { Secrets } from '../../../secrets/secrets';

export const voiceIsolationEditor = async (): Promise<void> => {
  try {
    // An Android release build resolves `require` to a resource name, and the editor rejects a source
    // without a scheme. So copy the clip to a file first.
    const [clip] = await Asset.loadAsync(
      require('../../../../assets/voice_isolation_sample.mp4')
    );
    if (!clip.localUri) {
      throw new Error('The voice isolation sample has no local file.');
    }
    const settings = Secrets.editorSettings();
    const result = await IMGLYEditor?.openEditor(
      settings,
      {
        source: clip.localUri,
        type: SourceType.VIDEO
      },
      EditorPreset.VIDEO,
      { plugin: 'voiceIsolation' }
    );
    console.log(result?.artifact);
  } catch (error) {
    console.log(error);
  }
};
