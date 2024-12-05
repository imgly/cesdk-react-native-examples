export interface ExampleCategory {
  title: string;
  examples: ExampleItem[];
}

export interface ExampleItem {
  title: string;
  description: string;
  example?: () => void;
  component?: ({
    visible,
    onFinish
  }: {
    visible: boolean;
    onFinish: () => void;
  }) => JSX.Element;
}
