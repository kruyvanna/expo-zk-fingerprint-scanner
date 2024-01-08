import * as React from 'react';

import { ExpoZkFingerprintScannerViewProps } from './ExpoZkFingerprintScanner.types';

export default function ExpoZkFingerprintScannerView(props: ExpoZkFingerprintScannerViewProps) {
  return (
    <div>
      <span>{props.name}</span>
    </div>
  );
}
