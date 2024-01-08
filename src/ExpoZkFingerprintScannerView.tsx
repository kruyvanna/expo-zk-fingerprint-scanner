import { requireNativeViewManager } from 'expo-modules-core';
import * as React from 'react';

import { ExpoZkFingerprintScannerViewProps } from './ExpoZkFingerprintScanner.types';

const NativeView: React.ComponentType<ExpoZkFingerprintScannerViewProps> =
  requireNativeViewManager('ExpoZkFingerprintScanner');

export default function ExpoZkFingerprintScannerView(props: ExpoZkFingerprintScannerViewProps) {
  return <NativeView {...props} />;
}
