import { ExampleItem } from '../models/Example';
import { customApparelEditor } from './examples/apparel/custom_apparel_editor';
import { defaultApparelEditor } from './examples/apparel/default_apparel_editor';
import { camera } from './examples/camera/camera';
import { customDesignEditor } from './examples/design/custom_design_editor';
import { defaultDesignEditor } from './examples/design/default_design_editor';
import { customPhotoEditor } from './examples/photo/custom_photo_editor';
import { defaultPhotoEditor } from './examples/photo/default_photo_editor';
import { customPostcardEditor } from './examples/postcard/custom_postcard_editor';
import { defaultPostcardEditor } from './examples/postcard/default_postcard_editor';
import { customVideoEditor } from './examples/video/custom_video_editor';
import { defaultVideoEditor } from './examples/video/default_video_editor';

const cameraExamples: ExampleItem[] = [
  {
      title: "Camera",
      description: "Opens the camera.",
      example: camera
  }
];

const photoExamples: ExampleItem[] = [
    {
        title: "Default Photo Editor",
        description: "Loads empty image.",
        example: defaultPhotoEditor
    },
    {
        title: "Custom Photo Editor",
        description: "Loads a selected image.",
        example: customPhotoEditor
    },
];

const designExamples: ExampleItem[] = [
    {
        title: "Default Design Editor",
        description: "Loads empty design scene.",
        example: defaultDesignEditor
    },
    {
        title: "Custom Design Editor",
        description: "Loads custom design scene and adds Unsplash asset source and library.",
        example: customDesignEditor
    },
];

const apparelExamples: ExampleItem[] = [
    {
        title: "Default Apparel Editor",
        description: "Loads empty apparel scene.",
        example: defaultApparelEditor
    },
    {
        title: "Custom Apparel Editor",
        description: "Loads custom apparel scene and adds Unsplash asset source and library.",
        example: customApparelEditor
    },
];

const postcardExamples: ExampleItem[] = [
    {
        title: "Default Postcard Editor",
        description: "Loads empty postcard scene.",
        example: defaultPostcardEditor
    },
    {
        title: "Custom Postcard Editor",
        description: "Loads custom postcard scene and adds Unsplash asset source and library.",
        example: customPostcardEditor
    },
];

const videoExamples: ExampleItem[] = [
    {
        title: "Default Video Editor",
        description: "Loads empty video scene.",
        example: defaultVideoEditor
    },
    {
        title: "Custom Video Editor",
        description: "Loads custom video scene and adds Unsplash asset source and library.",
        example: customVideoEditor
    },
];

export const examples = [
  {
    title: "Camera",
    description: "Built to capture stunning scenes.",
    data: cameraExamples
  },
  {
    title: 'Photo Editor',
    description: 'Built to edit various photos.',
    data: photoExamples
  },
  {
    title: 'Design Editor',
    description: 'Built to edit various designs.',

    data: designExamples
  },
  {
    title: 'Apparel Editor',
    description:
      'Customize and export a print-ready design with a mobile apparel editor.',

    data: apparelExamples
  },
  {
    title: 'Postcard Editor',
    description:
      'Built to facilitate optimal card design, from changing accent colors to selecting fonts.',

    data: postcardExamples
  },
  {
    title: 'Video Editor',
    description: 'Built to edit various videos.',

    data: videoExamples
  }
];
