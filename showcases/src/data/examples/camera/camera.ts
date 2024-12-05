import IMGLYCamera from '@imgly/camera-react-native';
import { Secrets } from '../../../secrets/secrets';

export const camera = async (): Promise<void> => {
  try {
    const result = await IMGLYCamera.openCamera({
      license: Secrets.license
    });
    console.log(result);
  } catch (error) {
    console.log(error);
  }
};
