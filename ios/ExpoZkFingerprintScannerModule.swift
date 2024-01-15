import ExpoModulesCore

public class ExpoZkFingerprintScannerModule: Module {
  // Each module class must implement the definition function. The definition consists of components
  // that describes the module's functionality and behavior.
  // See https://docs.expo.dev/modules/module-api for more details about available components.
  public func definition() -> ModuleDefinition {
    // Sets the name of the module that JavaScript code will use to refer to the module. Takes a string as an argument.
    // Can be inferred from module's class name, but it's recommended to set it explicitly for clarity.
    // The module will be accessible from `requireNativeModule('ExpoZkFingerprintScanner')` in JavaScript.
    Name("ExpoZkFingerprintScanner")
      
    Events("onDeviceConnected", "onDeviceDisconnected", "onGotImage")

      
    AsyncFunction("connectDevice") {
            
    }

    AsyncFunction("disconnectDevice") {
        
    }

    // Defines a JavaScript function that always returns a Promise and whose native code
    // is by default dispatched on the different thread than the JavaScript runtime runs on.
//    AsyncFunction("setValueAsync") { (value: String) in
//      // Send an event to JavaScript.
//      self.sendEvent("onChange", [
//        "value": value
//      ])
//    }

    // Enables the module to be used as a native view. Definition components that are accepted as part of the
    // view definition: Prop, Events.
    View(ExpoZkFingerprintScannerView.self) {
      // Defines a setter for the `name` prop.
      Prop("name") { (view: ExpoZkFingerprintScannerView, prop: String) in
        print(prop)
      }
    }
  }
}
