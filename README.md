# react-native-zk-fingerprint-scanner

This library let you connect to ZK Fingerprint scanner in your Expo app.
This library is only for Android platform.

## Installation

```sh
npm install expo-zk-fingerprint-scanner
```

## Target SDK version

Change target sdk version to 33 in /android/build.gradle


## Usage

```js
import { Button, Image, StyleSheet, Text, View } from "react-native";

import * as ExpoZkFingerprintScanner from "expo-zk-fingerprint-scanner";
import { useEffect, useState } from "react";
import { GotImageEventPayload } from "expo-zk-fingerprint-scanner/ExpoZkFingerprintScanner.types";

export default function App() {
  const [connected, setConnected] = useState(false);
  const [image, setImage] = useState("");

  useEffect(() => {
    ExpoZkFingerprintScanner.addOnDeviceConnectedListener(() => {
      console.log("Device connected");
      setConnected(true);
    });

    ExpoZkFingerprintScanner.addOnDeviceDisconnectedListener(() => {
      console.log("Device disconnected");
      setConnected(false);
    });

    ExpoZkFingerprintScanner.addOnGotImageListener(
      (payload: GotImageEventPayload) => {
        console.log("Got image", payload);
        setImage(payload.base64);
      }
    );
  }, []);

  return (
    <View style={styles.container}>
      <Image
        source={{ uri: `data:image/png;base64,${image}` }}
        style={{ width: 100, height: 100, backgroundColor: "blue" }}
      />
      <Text>{connected ? "Connected" : "Disconnected"}</Text>
      <Button
        title="Connect"
        onPress={() => ExpoZkFingerprintScanner.connectDevice()}
      />
      <Button
        title="Disconnect"
        onPress={() => ExpoZkFingerprintScanner.disconnectDevice()}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#fff",
    alignItems: "center",
    justifyContent: "center",
    gap: 20,
  },
});


```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

