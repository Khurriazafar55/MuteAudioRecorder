package camera.dslr.hdpicture.beauty.audiorecorder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class BluetoothRecorder extends AppCompatActivity {

    private static final String TAG = "ffff" ;
    Button record;
    OnBluetoothRecording BluetoothRecording;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_recorder);
        record = findViewById(R.id.Recorder);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Recording recording = new Recording();
                //recording.checkAndRecord(getApplicationContext(),BluetoothRecording ,true);

            }
        });
    }

    private BroadcastReceiver mBluetoothScoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);

            if (state == AudioManager.SCO_AUDIO_STATE_CONNECTED) {
                // Start recording audio
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);
        Intent intent = registerReceiver(mBluetoothScoReceiver, intentFilter);
        if (intent == null) {
            Toast.makeText(getApplicationContext(),"Failed to register bluetooth sco receiver",Toast.LENGTH_LONG).show();
            Log.e(TAG, "Failed to register bluetooth sco receiver...");
            return;
        }

        int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
        if (state == AudioManager.SCO_AUDIO_STATE_CONNECTED) {
            // Start recording
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
}
