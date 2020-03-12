package camera.dslr.hdpicture.beauty.audiorecorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity

  {

    private Button play, stop, record,mute;
    private MediaRecorder myAudioRecorder;
    private String outputFile;
    public static final int request_code = 1000;
    Boolean isMuted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        play = (Button) findViewById(R.id.play);
        stop = (Button) findViewById(R.id.stop);
        record = (Button) findViewById(R.id.record);
        mute = (Button) findViewById(R.id.mute);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter);


        stop.setEnabled(false);
        play.setEnabled(false);
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissionFromDevice()) {
                    try {
                        myAudioRecorder.prepare();
                        myAudioRecorder.start();
                    } catch (IllegalStateException ise) {
                        // make something ...
                    } catch (IOException ioe) {
                        // make something
                    }
                    record.setEnabled(false);
                    stop.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAudioRecorder.stop();
                myAudioRecorder.release();
                myAudioRecorder = null;
//                record.setEnabled(true);
                stop.setEnabled(false);
                play.setEnabled(true);
                Toast.makeText(getApplicationContext(), "Audio Recorder successfully", Toast.LENGTH_LONG).show();
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer mediaPlayer = new MediaPlayer();
                try {

                    //comen
                    mediaPlayer.setDataSource(outputFile);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    Toast.makeText(getApplicationContext(), "Playing Audio", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    // make something
                }
            }
        });

      mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AudioManager audioManager = (AudioManager)
                        getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
           // get original mode
                int originalMode = audioManager.getMode();


                // if we put MODE_IN_CALL it will mute microhpne on call also
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
          // change mute
                boolean state = !audioManager.isMicrophoneMute();
                audioManager.setMicrophoneMute(state);
          // set mode back
                audioManager.setMode(originalMode);


         /*       AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                if(!isMuted){

                    Toast.makeText(MainActivity.this, "muted", Toast.LENGTH_SHORT).show();

                    if((audioManager.getMode() == AudioManager.MODE_IN_CALL)||(audioManager.getMode()== AudioManager.MODE_IN_COMMUNICATION)){
                        audioManager.setMicrophoneMute(true);
                    }
                    isMuted = true;

                }else{
                    Toast.makeText(MainActivity.this, "muted", Toast.LENGTH_SHORT).show();

                    if((audioManager.getMode()== AudioManager.MODE_IN_CALL)||(audioManager.getMode()== AudioManager.MODE_IN_COMMUNICATION)){
                        audioManager.setMicrophoneMute(false);
                    }
                    isMuted = false;
                }*/

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){

            case request_code:
            {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                    Toast.makeText(getApplicationContext(),"permission granted...",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"permission denied...",Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    private boolean checkPermissionFromDevice() {
        int storage_permission= ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int recorder_permssion= ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO);
        return storage_permission == PackageManager.PERMISSION_GRANTED && recorder_permssion == PackageManager.PERMISSION_GRANTED;
    }


      private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
          @Override
          public void onReceive(Context context, Intent intent) {
              String action = intent.getAction();
              BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

              if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                  Toast.makeText(context, "Bluetooth ACTION_FOUND", Toast.LENGTH_SHORT).show();
            //Device found
              }
              else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                  Toast.makeText(context, "ACTION_ACL_CONNECTED", Toast.LENGTH_SHORT).show();
            //Device is now connected
                  AudioManager audioManager = (AudioManager)
                          getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                  // get original mode
                  int originalMode = audioManager.getMode();


                  // if we put MODE_IN_CALL it will mute microhpne on call also
                  audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                  // change mute
                  boolean state = !audioManager.isMicrophoneMute();
                  audioManager.setMicrophoneMute(state);
                  // set mode back
                  audioManager.setMode(originalMode);


              }
              else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                  Toast.makeText(context, "ACTION_DISCOVERY_FINISHED", Toast.LENGTH_SHORT).show();
            //Done searching
              }
              else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                  Toast.makeText(context, "ACTION_ACL_DISCONNECTED", Toast.LENGTH_SHORT).show();
            //Device is about to disconnect
              }
              else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                  Toast.makeText(context, "ACTION_ACL_DISCONNECTED", Toast.LENGTH_SHORT).show();
            //Device has disconnected
              }
          }
      };
}