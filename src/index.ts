import { NativeModulesProxy, EventEmitter, Subscription } from 'expo-modules-core';

// Import the native module. On web, it will be resolved to ExpoZkFingerprintScanner.web.ts
// and on native platforms to ExpoZkFingerprintScanner.ts
import ExpoZkFingerprintScannerModule from './ExpoZkFingerprintScannerModule';
import ExpoZkFingerprintScannerView from './ExpoZkFingerprintScannerView';
import { ChangeEventPayload, ExpoZkFingerprintScannerViewProps } from './ExpoZkFingerprintScanner.types';

// Get the native constant value.
export const PI = ExpoZkFingerprintScannerModule.PI;

export function hello(): string {
  return ExpoZkFingerprintScannerModule.hello();
}

export async function setValueAsync(value: string) {
  return await ExpoZkFingerprintScannerModule.setValueAsync(value);
}

const emitter = new EventEmitter(ExpoZkFingerprintScannerModule ?? NativeModulesProxy.ExpoZkFingerprintScanner);

export function addChangeListener(listener: (event: ChangeEventPayload) => void): Subscription {
  return emitter.addListener<ChangeEventPayload>('onChange', listener);
}

export { ExpoZkFingerprintScannerView, ExpoZkFingerprintScannerViewProps, ChangeEventPayload };
