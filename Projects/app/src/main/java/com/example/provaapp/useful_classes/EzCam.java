package com.example.provaapp.useful_classes;

import android.net.Uri;
import android.util.Log;
import android.view.ScaleGestureDetector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import VideoHandle.EpEditor;
import VideoHandle.OnEditorListener;

// TODO: stopPreview();

/* The idea behind EzCamera is to provide an interface to the front camera (preview displaying, video recording, photo taking) which is closer to what a human expects
   CameraX lies under the hood, and camera2 is working under CameraX

   Example usage inside OnCreate() :

    try {
        EzCamera camera = new EzCamera(this, findViewById(R.id.myPreviewView));
    } catch (Permissions.PermissionDeniedException e) {
        // here if the user did not allow CAMERA and RECORD_AUDIO permissions
        ...
    }

    ...
    camera.startPreview();
    ...

    // isRecording is a boolean flag which is used as an example here to provide a simple recording logic
    findViewById(R.id.myRecordingButton).setOnClickListener(
        (v) -> {
            ...
            if (isRecording)
                camera.startRecording();
            else
                camera.stopRecording();
            isRecording = !isRecording;
            ...
        }
    );

   TODO: taking photos
*/
public class EzCam {

    public static int REQUEST_CODE = 420;

    // An interface that must be implemented when calling ezCamera.startRecording(...) to make sure a certain piece of code defined by the user is executed AFTER the video is saved
    public interface OnRecordingSavedCallback {
        void onRecordingSaved(Uri videoUri);
    }

    /*public enum Mode {
        VIDEO, MUTED_VIDEO
    }*/

    public static final int ACTION_ERROR = 0;
    public static final int VIDEO_ACTION = 1;
    public static final int MUTED_VIDEO_ACTION = 2;

    // This is the context of the activity that requested an EzCamera instance
    private AppCompatActivity context;

    private Camera camera;  // CameraX instance
    private CameraControl cameraControl;    // provides asynchronous features (bound after Camera)
    private CameraInfo cameraInfo;  // provides info on zoom state, torch state, exposure state, sensor rotation (bound after Camera)

    // this is the same as a TextureView for camera1 API, we link the camera to it so it displays a preview of what the camera is seeing
    private PreviewView previewView;

    // reference to the modifiable video capture use case, it is a layer between the camera and the filesystem to produce a video/audio .mp4 recording from camera input
    /* its usage is simple:
        - create an instance of VideoCapture
        - link the Camera instance to it
        - startRecording(outputFile, ExecutorThread, someCallbacks)
        - stopRecording() which produces an .mp4 to outputFile
     */
    private VideoCapture videoCapture;

    private File outputFile;    // this is used both for providing the user with the latest recording and to make the start/stop recording logic work reliably
    private File lastVideo;     // this records the last saved video

    public EzCam(@NonNull AppCompatActivity context) throws Permissions.PermissionDeniedException {

        // request CAMERA and RECORD_AUDIO permissions:
        //  - throws an exception if permissions were not available in the first place and the user denied them
        //  developers must try/catch this when creating new instances of EzCamera, so that crashes are easily prevented before even trying to interact with the camera at all
        // A useful string containing all required permissions for recording with a camera
        String[] ALL_REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.RECORD_AUDIO"};
        int PERMISSIONS_REQUEST_CODE = 10;
        Permissions.runIfAllowed(context, ALL_REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE,
                () -> {
                    this.context = context;
                    this.outputFile = null; // set outputFile flag for recording logic
                    this.lastVideo = null;  // set last video recording reference
                }
        );
    }

    private void startCamera() {
        // We need some handler for the camera
        // Since CameraX is activity lifecycle-aware, we just have to bind our context to the camera and it will trigger our callbacks together with the activity's onCreate and onDestroy
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(context);

        // We set a listener that displays a camera preview in the PreviewView when the activity is created
        // We leverage CameraX's lifecycle awareness and adopt a functional approach to the camera preview problem
        // cameraProviderFuture.addListener(...);
        // lifecycle-aware listener
        cameraProviderFuture.addListener(
                () -> {
                    try {
                        // get camera provider
                        ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                        cameraProvider.unbindAll();
                        videoCapture = new VideoCapture.Builder().build();
                        camera = bindPreview(cameraProvider, videoCapture);
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }

                    /** get camera control and camera info */
                    cameraControl = camera.getCameraControl();
                    cameraInfo = camera.getCameraInfo();                // we get a CameraInfo (useful for zooming)

                    /** enable Pinch-to-zoom feature */
                    ScaleGestureDetector.SimpleOnScaleGestureListener scaleGestureListener = enablePinchZoom();
                    ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(context, scaleGestureListener);

                    // attach the pinch gesture listener to the viewFinder provided by the user to the EzCamera constructor
                    previewView.setOnTouchListener(
                            (v, event) -> {
                                scaleGestureDetector.onTouchEvent(event);   // each incoming event is passed to a ScaleGestureDetector
                                return true;
                            }
                    );

                    /** enable camera torch feature (disabled for now) */
                    // use the cameraControl instance to enable the torch (put this in a button)
                    //ListenableFuture<Void> enableTorchListenableFuture = enableTorch();
                    //enableTorch();

                }, ContextCompat.getMainExecutor(context)
        );
    }

    private Camera bindPreview(@NonNull ProcessCameraProvider cameraProvider, @NonNull VideoCapture videoCapture) {
        Preview preview = new Preview.Builder()
                .build();

        cameraProvider.unbindAll();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        return cameraProvider.bindToLifecycle(context, cameraSelector, preview, videoCapture);
    }


    public void startPreview(@NonNull PreviewView previewView) {
        this.previewView = previewView;
        // initialize camera and plot it to the PreviewView (given to the constructor)
        startCamera();
    }

    // Assumes CAMERA and RECORD_AUDIO permissions are granted! (undefined behaviour otherwise, it will likely crash)
    /*  @params: File userFile -> file provided by the user as the destination of the recorded video
                                  if null then a sane default file path location is chosen
     */
    public void startRecording(Uri outputUri, int mode, EzCam.OnRecordingSavedCallback onRecordingSavedCallback) {
        // NOTE: outputFile is used here as a recording flag aswell
        //       this means that until outputFile is set to null a recording cannot start, and outputFile can only be set to null inside a callback triggered when the last recording was stopped and saved successfully
        //       (see below for the implementation)
        if (outputFile == null) {  // if not already recording (a.k.a. there are no outputFiles to be consumed by the callback below). This means the camera is not recording anything!
            // set sane file path if none was provided
            // default path is /stroage/emulated/0/Android/media/[your.package.name]/$filename
            outputFile = (outputUri == null || mode == MUTED_VIDEO_ACTION) ? getOutputFile() : new File(outputUri.getPath()); // produce outputFile reference which will be consumed by the callback below

            videoCapture.startRecording(new VideoCapture.OutputFileOptions.Builder(outputFile).build(), ContextCompat.getMainExecutor(context), new VideoCapture.OnVideoSavedCallback() {
                        @Override
                        public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults) {
                            // if there was a previous video recording delete it
                            /*
                            if (lastVideo != null)
                                new File(lastVideo.getPath()).delete();   // delete last recording

                            // save current recording as the last video
                            lastVideo = outputFile;
                            */
                            if(outputFileResults.getSavedUri() == null) return;
                            switch (mode) {
                                case MUTED_VIDEO_ACTION:
                                    // here if a muted video recording was requested
                                    // the video is ready but audio must be stripped from it
                                    stripAudio(outputFileResults.getSavedUri(), outputUri, onRecordingSavedCallback);   // strip audio and invoke user-defined callback when done
                                    break;
                                case VIDEO_ACTION:
                                    // here if a video with audio recording was requested
                                    // the video is ready so we call the user defined callback directly
                                    Log.i("Video done", "saved to " + outputFileResults.getSavedUri().getPath());
                                    onRecordingSavedCallback.onRecordingSaved(outputFileResults.getSavedUri()); // call user-defined callback directly
                                    break;
                            }
                            outputFile = null;  // consume outputFile reference (this tells startRecording() that a new recording is possible if requested by the user)
                        }

                        @Override
                        public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
                            Log.i("Video recording", "failure " + outputFile.getName());
                            onRecordingSavedCallback.onRecordingSaved(null);
                            outputFile = null;
                        }
                    }
            );
        }
    }

    // an overload for the sake of an easier usage
    public void startRecording(int mode, EzCam.OnRecordingSavedCallback onRecordingSavedCallback) {
        startRecording(null, mode, onRecordingSavedCallback);
    }

    public void stopRecording() {
        if (outputFile != null) // only if we are recording
            videoCapture.stopRecording();
    }

    private File getOutputFile() {
        Date currentDate = Calendar.getInstance().getTime();    // get current date
        String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
        String filename = new SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(currentDate); // format current date based on FILENAME_FORMAT

        for (File s : context.getExternalMediaDirs()) {
            Log.i("getExternalMediaDirs", s.getName());
        }

        return new File(context.getExternalMediaDirs()[0], "ezcamera.mp4");
        // return new File(context.getExternalMediaDirs()[0], filename + ".mp4");    // prepare output file to /stroage/emulated/0/Android/media/[your.package.name]/$filename
    }

    private ListenableFuture<Void> enableTorch() {
        /** enable camera torch feature */
        // use the cameraControl instance to enable the torch (put this in a button)
        ListenableFuture<Void> enableTorchLF = cameraControl.enableTorch(true); // this switches on/off the torch
        enableTorchLF.addListener(
                () -> {
                    try {
                        enableTorchLF.get();
                        // the torch has been enabled
                    } catch (InterruptedException | ExecutionException e) {
                        // handle torch exception
                        e.printStackTrace();
                    }
                }, ContextCompat.getMainExecutor(context) // executor where the runnable callback code is run
        );
        return enableTorchLF;
    }

    private ScaleGestureDetector.SimpleOnScaleGestureListener enablePinchZoom() {
        return new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                // here is where the camera's zoom ratio is updated
                // the current camera zoo ratio is retrieved using CameraInfo, and then multiplied by the gesture's scale data, which depends on the pinch gesture
                // the result is passed to CameraControl.zetZoomRatio() which changes the output of the preview and any other bound use cases
                float currentZoomRatio = (cameraInfo.getZoomState().getValue() == null) ? 0F : cameraInfo.getZoomState().getValue().getZoomRatio(); // get the camear's current zoom ratio
                float delta = detector.getScaleFactor();  // get the pinch gesture's scaling factor
                cameraControl.setZoomRatio(currentZoomRatio * delta); // update camera zoom ratio. Asynchronous operation that returns a ListenableFuture, allowing to listen to when the operation completes
                return true;
            }
        };
    }

    private void stripAudio(@NonNull Uri videoSource, Uri videoTarget, OnRecordingSavedCallback userCallback) {
        String outputPath = (videoTarget == null) ? getOutputFile().getPath() : videoTarget.getPath();  // setup video target path
        File outputFile = (videoTarget == null) ? new File(context.getExternalMediaDirs()[0], "ezcamera-noaudio.mp4") : new File(videoTarget.getPath());

        EpEditor.demuxer(videoSource.getPath(), outputFile.getPath(), EpEditor.Format.MP4, new OnEditorListener() { // if MUTED_VIDEO was requested, video+audio was recorded in a temporary file, and video without audio is saved in the path specified by the user
            @Override
            public void onProgress(float progress) {
                Log.i("Muted video progress", "saved to " + outputFile.getPath());
            }

            @Override
            public void onSuccess() {
                Log.i("Muted video done", "saved to " + outputFile.getPath());
                new File(videoSource.getPath()).delete();   // delete video+audio file  (TODO: find a better way)
                userCallback.onRecordingSaved(Uri.fromFile(outputFile)); // call user-defined callback when muted video is ready
            }

            @Override
            public void onFailure() {
                Log.e("Muted video failed", "saved to " + outputFile.getPath());
            }
        });
    }

}


