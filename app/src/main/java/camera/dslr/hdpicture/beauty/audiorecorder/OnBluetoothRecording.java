package camera.dslr.hdpicture.beauty.audiorecorder;

public interface OnBluetoothRecording {
    void onStartRecording(boolean state,boolean bluetoothFlag);
    void onCancelRecording();
}
