import IMGLYCamera, { CameraSettings } from '@imgly/camera-react-native';

export const camera = async (): Promise<void> => {
  // highlight-configuration
  const settings: CameraSettings = {
    license: 'YOUR_LICENSE_KEY'
  };
  // highlight-configuration
  // highlight-camera
  const result = await IMGLYCamera.openCamera(settings);
  // highlight-camera
};
