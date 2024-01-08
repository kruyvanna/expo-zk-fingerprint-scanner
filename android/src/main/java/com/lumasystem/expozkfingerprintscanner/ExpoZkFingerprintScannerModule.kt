package com.lumasystem.expozkfingerprintscanner

import android.graphics.Bitmap
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ComponentActivity
import com.fingerprintscanner.ZKUSBManager.ZKUSBManager
import com.fingerprintscanner.ZKUSBManager.ZKUSBManagerListener
import com.zkteco.android.biometric.FingerprintExceptionListener
import com.zkteco.android.biometric.core.device.ParameterHelper
import com.zkteco.android.biometric.core.device.TransportType
import com.zkteco.android.biometric.core.utils.LogHelper
import com.zkteco.android.biometric.core.utils.ToolUtils
import com.zkteco.android.biometric.module.fingerprintreader.FingerprintCaptureListener
import com.zkteco.android.biometric.module.fingerprintreader.FingerprintSensor
import com.zkteco.android.biometric.module.fingerprintreader.FingprintFactory
import com.zkteco.android.biometric.module.fingerprintreader.exception.FingerprintException
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import java.io.ByteArrayOutputStream

class ExpoZkFingerprintScannerModule : Module() {
  // Each module class must implement the definition function. The definition consists of components
  // that describes the module's functionality and behavior.
  // See https://docs.expo.dev/modules/module-api for more details about available components.

  private var zkusbManager: ZKUSBManager? = null
  private var fingerprintSensor: FingerprintSensor? = null
  private val usb_vid = ZKTECO_VID
  private var usb_pid = 0
  private val deviceIndex = 0
  private var isReseted = false
  private var connected = false

  @RequiresApi(Build.VERSION_CODES.O)
  override fun definition() = ModuleDefinition {
    // Sets the name of the module that JavaScript code will use to refer to the module. Takes a string as an argument.
    // Can be inferred from module's class name, but it's recommended to set it explicitly for clarity.
    // The module will be accessible from `requireNativeModule('ExpoZkFingerprintScanner')` in JavaScript.
    Name("ExpoZkFingerprintScanner")

    // Defines event names that the module can send to JavaScript.
    Events("onDeviceConnected", "onDeviceDisconnected", "onGotImage")

    AsyncFunction("connectDevice") {
      connectDevice()
    }

    AsyncFunction("disconnectDevice") {
      disconnectDevice()
    }
    
    // Enables the module to be used as a native view. Definition components that are accepted as part of
    // the view definition: Prop, Events.
    View(ExpoZkFingerprintScannerView::class) {
      // Defines a setter for the `name` prop.
      Prop("name") { view: ExpoZkFingerprintScannerView, prop: String ->
        println(prop)
      }
    }
  }

  @RequiresApi(Build.VERSION_CODES.O)
  fun connectDevice() {
    if(connected) {
      return
    }

    if(zkusbManager == null) {
      zkusbManager = ZKUSBManager(appContext.reactContext!!, zkusbManagerListener)
      zkusbManager?.registerUSBPermissionReceiver()
    }

    enumSensor()
    tryGetUSBPermission()
  }

  fun disconnectDevice() {
    if(!connected) {
      return
    }
    closeDevice()
  }

  fun onDeviceConnected() {
    connected = true
    sendEvent("onDeviceConnected", mapOf())
  }

  fun onDeviceDisconnected() {
    connected = false
    sendEvent("onDeviceDisconnected", mapOf())
  }

  fun onGotImage(result: String) {
    sendEvent("onGotImage", mapOf("base64" to result))
  }

  fun bitmapToBase64(bitmap: Bitmap): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
  }

  private val fingerprintCaptureListener: FingerprintCaptureListener =
    object : FingerprintCaptureListener {
      override fun captureOK(fpImage: ByteArray) {
        val bitmap = ToolUtils.renderCroppedGreyScaleBitmap(
          fpImage,
          fingerprintSensor!!.imageWidth,
          fingerprintSensor!!.imageHeight
        )
        val base64Image = bitmapToBase64(bitmap)
        onGotImage(base64Image)
      }

      override fun captureError(e: FingerprintException) {
        // nothing to do
        e.printStackTrace()
      }

      override fun extractOK(fpTemplate: ByteArray) {

      }

      override fun extractError(i: Int) {
        // nothing to do
        Log.i(TAG, "extractError $i")
      }
    }

  private val fingerprintExceptionListener = FingerprintExceptionListener {
    LogHelper.e("[] usb exception!!!")
    if (!isReseted) {
      try {
        fingerprintSensor!!.openAndReboot(deviceIndex)
      } catch (e: FingerprintException) {
        e.printStackTrace()
      }
      isReseted = true
    }
  }
  private val zkusbManagerListener: ZKUSBManagerListener = object : ZKUSBManagerListener {
    override fun onCheckPermission(result: Int) {
      afterGetUsbPermission()
    }

    override fun onUSBArrived(device: UsbDevice?) {
      tryGetUSBPermission()
    }

    override fun onUSBRemoved(device: UsbDevice?) {
      closeDevice()
    }

  }

  private fun createFingerprintSensor() {
    if (null != fingerprintSensor) {
      FingprintFactory.destroy(fingerprintSensor)
      fingerprintSensor = null
    }
    // Define output log level
    LogHelper.setLevel(Log.ERROR)
    LogHelper.setNDKLogLevel(Log.ASSERT)
    // Start fingerprint sensor
    val deviceParams: MutableMap<String, Any?> = HashMap<String, Any?>()
    //set vid
    deviceParams[ParameterHelper.PARAM_KEY_VID] = usb_vid
    //set pid
    deviceParams[ParameterHelper.PARAM_KEY_PID] = usb_pid
    fingerprintSensor = FingprintFactory.createFingerprintSensor(
      this.appContext.reactContext,
      TransportType.USB,
      deviceParams
    )
  }

  private fun enumSensor(): Boolean {
    val usbManager = this.appContext.reactContext?.getSystemService(ComponentActivity.USB_SERVICE) as UsbManager
    for (device in usbManager.deviceList.values) {
      val device_vid = device.vendorId
      val device_pid = device.productId
      if (device_vid == ZKTECO_VID && (device_pid == LIVE20R_PID || device_pid == LIVE10R_PID)) {
        usb_pid = device_pid
        return true
      }
    }
    return false
  }

  private fun tryGetUSBPermission() {
    zkusbManager?.initUSBPermission(usb_vid, usb_pid)
  }

  private fun afterGetUsbPermission() {
    openDevice()
  }

  private fun openDevice() {
    createFingerprintSensor()
    isReseted = false
    try {
      //fingerprintSensor.setCaptureMode(1);
      fingerprintSensor?.open(deviceIndex)

      run {
        // device parameter
        LogHelper.d("sdk version" + fingerprintSensor!!.sdK_Version)
        LogHelper.d("firmware version" + fingerprintSensor!!.firmwareVersion)
        LogHelper.d("serial:" + fingerprintSensor!!.strSerialNumber)
        LogHelper.d("width=" + fingerprintSensor!!.imageWidth + ", height=" + fingerprintSensor!!.imageHeight)
//        LogHelper.setNDKLogLevel(Log.ERROR)
//        LogHelper.setLevel(Log.ERROR)
      }
      fingerprintSensor?.setFingerprintCaptureListener(
        deviceIndex,
        fingerprintCaptureListener
      )
      fingerprintSensor?.SetFingerprintExceptionListener(fingerprintExceptionListener)
      fingerprintSensor?.startCapture(deviceIndex)
//      scanViewModel.setDeviceConnected(true)
      onDeviceConnected()
    } catch (e: FingerprintException) {
      e.printStackTrace()
      // try to  reboot the sensor
      try {
        fingerprintSensor!!.openAndReboot(deviceIndex)
      } catch (ex: FingerprintException) {
        ex.printStackTrace()
      }
      onDeviceDisconnected()
    }
  }

  private fun closeDevice() {
    try {
      fingerprintSensor?.stopCapture(deviceIndex)
      fingerprintSensor?.close(deviceIndex)
      onDeviceDisconnected()

    } catch (e: FingerprintException) {
      e.printStackTrace()
    }
  }

  companion object {
    private const val ZKTECO_VID = 0x1b55
    private const val LIVE20R_PID = 0x0120
    private const val LIVE10R_PID = 0x0124
    private const val TAG = "Fingerprint"
  }
}
