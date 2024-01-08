import { StyleSheet, Text, View } from 'react-native';

import * as ExpoZkFingerprintScanner from 'expo-zk-fingerprint-scanner';

export default function App() {
  return (
    <View style={styles.container}>
      <Text>{ExpoZkFingerprintScanner.hello()}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
