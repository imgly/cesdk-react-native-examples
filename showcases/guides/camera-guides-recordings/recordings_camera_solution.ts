import IMGLYCamera, { CameraSettings } from '@imgly/camera-react-native';

export const recordings_camera_solution = async (): Promise<void> => {
  const settings: CameraSettings = {
    license: 'YOUR_LICENSE_KEY' // Get your license from https://img.ly/forms/free-trial, pass null for evaluation mode with watermark
  };

  try {
    const result = await IMGLYCamera.openCamera(settings);
    // highlight-cancelled
    if (result === null) {
      console.log('The editor has been cancelled.');
      return;
    }
    // highlight-cancelled
    // highlight-standard
    result.captures.forEach((capture) => {
      if (capture.photo) {
        capture.photo.images.forEach((image) => {
          console.log(image.uri);
          console.log(image.rect);
        });
      } else if (capture.video) {
        console.log(capture.video.duration);
        capture.video.videos.forEach((video) => {
          console.log(video.uri);
          console.log(video.rect);
        });
      }
    });
    // highlight-standard
  } catch (error) {
    // highlight-failure
    console.log(`Error occurred in the camera session: ${error}.`);
    // highlight-failure
  }
};
