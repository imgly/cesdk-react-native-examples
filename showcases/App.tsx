import { StatusBar } from 'expo-status-bar';
import { ScrollView, StyleSheet, Text, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { ExampleListItem } from './src/components/ExampleListItem';
import { examples } from './src/data/examples';
import { ExampleItem } from './src/models/Example';

function HomeScreen() {
  const renderItem = ({ item }: { item: ExampleItem }) => (
    <ExampleListItem
      key={item.title}
      example={item}
      onPress={() => {
        item.example?.();
      }}
    />
  );

  const sectionHeader = ({
    section: { title, description }
  }: {
    section: { title: string; description: string };
  }) => (
    <View style={styles.headerContainer}>
      <Text style={styles.header}>{title}</Text>
      <Text style={styles.description}>{description}</Text>
    </View>
  );

  return (
    <SafeAreaView edges={['top']} style={styles.container}>
      <ScrollView testID='scrollView' accessibilityLabel='scrollView'>
        {examples.map((section) => (
          <View key={section.title}>
            {sectionHeader({
              section: {
                title: section.title,
                description: section.description
              }
            })}

            {section.data.map((example) => renderItem({ item: example }))}
          </View>
        ))}
      </ScrollView>
      <StatusBar hidden={process.env.EXPO_PUBLIC_TESTING === "YES"} />
    </SafeAreaView>
  );
}

export default function App() {
  return HomeScreen();
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff'
  },
  headerContainer: {
    backgroundColor: '#fff'
  },
  header: {
    paddingLeft: 15,
    fontSize: 25,
    fontWeight: 'bold',
    paddingBottom: 2,
    backgroundColor: '#fff'
  },
  description: {
    paddingLeft: 15,
    paddingBottom: 10
  }
});
