package camera.dslr.hdpicture.beauty.audiorecorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

import camera.dslr.hdpicture.beauty.audiorecorder.ForegroundServices.ForegroundService;

public class MainActivity extends AppCompatActivity
{
    private Button play, stop, record,mute;
    private MediaRecorder myAudioRecorder;
    private String outputFile;
    public static final int request_code = 1000;
    Boolean isMuted = false;
    private static final String TAG = "ffff" ;
    Button btnStartService, btnStopService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        play = (Button) findViewById(R.id.play);
        stop = (Button) findViewById(R.id.stop);
        record = (Button) findViewById(R.id.record);
        mute = (Button) findViewById(R.id.mute);

        btnStartService = findViewById(R.id.buttonStartService);
        btnStopService = findViewById(R.id.buttonStopService);
        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService();
            }
        });
        btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService();
            }
        });
        /* IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter);*/
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
                record();
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAudioRecorder.stop();
                myAudioRecorder.release();
                myAudioRecorder = null;
              record.setEnabled(true);
                stop.setEnabled(false);
                play.setEnabled(true);
           //     Toast.makeText(getApplicationContext(), "Audio Recorder successfully", Toast.LENGTH_LONG).show();
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
                    //mediaPlayer.setDataSource;
               //     Toast.makeText(getApplicationContext(), "Playing Audio", Toast.LENGTH_LONG).show();
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
    public void record()
    {
        if (checkPermissionFromDevice())
        {
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
        //    Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
        }
    }

    private BroadcastReceiver mBluetoothScoReceiver = new BroadcastReceiver()
    {
          @Override
          public void onReceive(Context context, Intent intent) {
              String action = intent.getAction();
             int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);

              if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
                  int stated = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, -1);
                  boolean result = stated == BluetoothHeadset.STATE_CONNECTED;
                  record();
                 // mCallback.onConnected(result);
              }
              if (state == AudioManager.SCO_AUDIO_STATE_CONNECTED)
              {
                  Toast.makeText(getApplicationContext(),"recording with bluetooth",Toast.LENGTH_LONG).show();
                //  record();
                  // Start recording audio
              }
          }
      };

      @Override
      protected void onResume()
     {
          super.onResume();
          IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);
          Intent intent = registerReceiver(mBluetoothScoReceiver, intentFilter);

          if (intent == null)
          {
              Toast.makeText(getApplicationContext(),"Failed to register bluetooth sco receiver",Toast.LENGTH_LONG).show();
              Log.e(TAG, "Failed to register bluetooth sco receiver...");
              return;
          }
         String action = intent.getAction();
          int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
          if (state == AudioManager.SCO_AUDIO_STATE_CONNECTED)
          {
              record();
              // Start recording
          }
         if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
             Toast.makeText(getApplicationContext(),"bluetooth connected",Toast.LENGTH_LONG).show();
             int stated = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, -1);
             boolean result = stated == BluetoothHeadset.STATE_CONNECTED;
             record();
         }

             // Ensure the SCO audio connection stays active in case the
          // current initiator stops it.
          AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
          audioManager.startBluetoothSco();
      }

      @Override
      protected void onPause()
      {
          super.onPause();
          unregisterReceiver(mBluetoothScoReceiver);
          AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
          audioManager.stopBluetoothSco();
      }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
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

    private boolean checkPermissionFromDevice()
    {
        int storage_permission= ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int recorder_permssion= ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO);
        return storage_permission == PackageManager.PERMISSION_GRANTED && recorder_permssion == PackageManager.PERMISSION_GRANTED;
    }

    public void startService()
    {
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);
        Intent intent = registerReceiver(mBluetoothScoReceiver, intentFilter);
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.putExtra("inputExtra", "Bluetooth HeadSet Connected");
        ContextCompat.startForegroundService(this, serviceIntent);
    }
    public void stopService()
    {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        stopService(serviceIntent);
    }



}