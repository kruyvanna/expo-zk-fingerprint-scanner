import {
  NativeModulesProxy,
  EventEmitter,
  Subscription,
} from "expo-modules-core";

// Import the native module. On web, it will be resolved to ExpoZkFingerprintScanner.web.ts
// and on native platforms to ExpoZkFingerprintScanner.ts
import ExpoZkFingerprintScannerModule from "./ExpoZkFingerprintScannerModule";
import ExpoZkFingerprintScannerView from "./ExpoZkFingerprintScannerView";
import {
  ChangeEventPayload,
  ExpoZkFingerprintScannerViewProps,
  GotImageEventPayload,
} from "./ExpoZkFingerprintScanner.types";

// Get the native constant value.
export const PI = ExpoZkFingerprintScannerModule.PI;

export function hello(): string {
  return ExpoZkFingerprintScannerModule.hello();
}

export function connectDevice(): void {
  ExpoZkFingerprintScannerModule.connectDevice();
}

export function disconnectDevice(): void {
  ExpoZkFingerprintScannerModule.disconnectDevice();
}

export async function setValueAsync(value: string) {
  return await ExpoZkFingerprintScannerModule.setValueAsync(value);
}

const emitter = new EventEmitter(
  ExpoZkFingerprintScannerModule ?? NativeModulesProxy.ExpoZkFingerprintScanner
);

export function addChangeListener(
  listener: (event: ChangeEventPayload) => void
): Subscription {
  return emitter.addListener<ChangeEventPayload>("onChange", listener);
}

export function addOnGotImageListener(
  listener: (event: GotImageEventPayload) => void
): Subscription {
  return emitter.addListener<GotImageEventPayload>("onGotImage", listener);
}

export function addOnDeviceConnectedListener(
  listener: () => void
): Subscription {
  return emitter.addListener("onDeviceConnected", listener);
}

export function addOnDeviceDisconnectedListener(
  listener: () => void
): Subscription {
  return emitter.addListener("onDeviceDisconnected", listener);
}

export {
  ExpoZkFingerprintScannerView,
  ExpoZkFingerprintScannerViewProps,
  ChangeEventPayload,
};
