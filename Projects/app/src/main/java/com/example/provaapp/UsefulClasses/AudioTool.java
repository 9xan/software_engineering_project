package com.example.provaapp.UsefulClasses;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;
import java.io.IOException;

public class AudioTool {//riproduce e registra file audio
    private static final String LOG_TAG = "AudioTool";
    private static String fileName = null;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;


    //riproduce file corrispondente al path dato
    public void startPlaying(String path) {
        fileName=path;
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    //termina riproduzione
    public void stopPlaying() {
        player.release();
        player = null;
    }

    //registra salvando file nel path dato, non serve creare un file a priori lo istanzia lui
    // per quanto riguarda il path da passargli, negli esempi usano getExternalCacheDir().getAbsolutePath()
    // e poi sommano a quella stringa "/nomefile.outputformat" es:  String path = getExternalCacheDir().getAbsolutePath()+"/prova.3gp";

    //usa output format e audio encoder predefiniti
    public void startRecording(String path) {
        fileName=path;
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();
    }

    //dal chiamante, è più generica della prima versione
    //è da valutare se usare la questa versione, facendola gestire alla
    //classe recorder, che conterrà registratore audio e video, o se
    // usare dei valori predefiniti cambiando gli argomenti della prima versione
    // dipende dalle possibilità che ci darà l'editor video

    //usa output format e audio encoder scelti
    public void startRecording(String path, int out_format, int audio_encoder) {
        fileName=path;
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(out_format);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(audio_encoder);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();
    }
    
    public void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }




}
