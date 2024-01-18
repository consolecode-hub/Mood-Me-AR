package com.mood.ar.Recoder;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.PixelCopy;
import android.view.Surface;
import android.widget.Toast;

import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.ux.ArFragment;
import com.mood.ar.MyAPI.FileUpload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VideoRecorder {
    private static final String TAG = "VideoRecorder";
    private static final int DEFAULT_BITRATE = 10000000;
    private static final int DEFAULT_FRAMERATE = 30;

    private boolean recordingVideoFlag;

    private MediaRecorder mediaRecorder;

    private Size videoSize;

    private SceneView sceneView;
    private int videoCodec;
    private File videoDirectory;
    private String videoBaseName;
    private File videoPath;
    private int bitRate = DEFAULT_BITRATE;
    private int frameRate = DEFAULT_FRAMERATE;
    private Surface encoderSurface;

    private static final int[] FALLBACK_QUALITY_LEVELS = {
            CamcorderProfile.QUALITY_HIGH,
            CamcorderProfile.QUALITY_2160P,
            CamcorderProfile.QUALITY_1080P,
            CamcorderProfile.QUALITY_720P,
            CamcorderProfile.QUALITY_480P
    };

    public VideoRecorder() {
        recordingVideoFlag = false;
    }

    public File getVideoPath() {
        return videoPath;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
    }

    public void setSceneView(SceneView sceneView) {
        this.sceneView = sceneView;
    }

    public boolean onToggleRecord() {
        if (recordingVideoFlag) {
            stopRecordingVideo();
        } else {
            startRecordingVideo();
        }
        return recordingVideoFlag;
    }

    private void startRecordingVideo() {
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
        }

        try {
            buildFilename();
            setUpMediaRecorder();
        } catch (IOException e) {
            Log.e(TAG, "Exception setting up recorder", e);
            return;
        }

        encoderSurface = mediaRecorder.getSurface();

        sceneView.startMirroringToSurface(
                encoderSurface, 0, 0, videoSize.getWidth(), videoSize.getHeight());

        recordingVideoFlag = true;
    }

    private void buildFilename() {
        if (videoDirectory == null) {
            videoDirectory =
                    new File(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                                    + "/MoodVideos");
        }
        if (videoBaseName == null || videoBaseName.isEmpty()) {
            videoBaseName = "Sample";
        }
        videoPath =
                new File(
                        videoDirectory, videoBaseName + Long.toHexString(System.currentTimeMillis()) + ".mp4");
        File dir = videoPath.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private void stopRecordingVideo() {
        // UI
        recordingVideoFlag = false;

        if (encoderSurface != null) {
            sceneView.stopMirroringToSurface(encoderSurface);
            encoderSurface = null;
        }
        // Stop recording
        mediaRecorder.stop();
        mediaRecorder.reset();
    }

    private void setUpMediaRecorder() throws IOException {

        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        mediaRecorder.setOutputFile(videoPath.getAbsolutePath());
        mediaRecorder.setVideoEncodingBitRate(bitRate);
        mediaRecorder.setVideoFrameRate(frameRate);
        mediaRecorder.setVideoSize(videoSize.getWidth(), videoSize.getHeight());
        mediaRecorder.setVideoEncoder(videoCodec);

        mediaRecorder.prepare();

        try {
            mediaRecorder.start();
        } catch (IllegalStateException e) {
            Log.e(TAG, "Exception starting capture: " + e.getMessage(), e);
        }
    }

    public void setVideoSize(int width, int height) {
        videoSize = new Size(width, height);
    }

    public void setVideoQuality(int quality, int orientation) {
        CamcorderProfile profile = null;
        if (CamcorderProfile.hasProfile(quality)) {
            profile = CamcorderProfile.get(quality);
        }
        if (profile == null) {
            // Select a quality  that is available on this device.
            for (int level : FALLBACK_QUALITY_LEVELS) {
                if (CamcorderProfile.hasProfile(level)) {
                    profile = CamcorderProfile.get(level);
                    break;
                }
            }
        }
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight);
        } else {
            setVideoSize(profile.videoFrameHeight, profile.videoFrameWidth);
        }
        setVideoCodec(profile.videoCodec);
        setBitRate(profile.videoBitRate);
        setFrameRate(profile.videoFrameRate);
    }

    public void setVideoCodec(int videoCodec) {
        this.videoCodec = videoCodec;
    }

    public boolean isRecording() {
        return recordingVideoFlag;
    }
    private String generateFilename() {

        String date =
                new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault()).format(new Date());
        return Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS) + File.separator + "MoodIMAGES/" + date + "_screenshot.jpg";
    }

    private void saveBitmapToDisk(Bitmap bitmap, String filename) throws IOException {

        File out = new File(filename);
        if (!out.getParentFile().exists()) {
            out.getParentFile().mkdirs();
        }
        try (FileOutputStream outputStream = new FileOutputStream(filename);
             ByteArrayOutputStream outputData = new ByteArrayOutputStream()) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputData);
            outputData.writeTo(outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (IOException ex) {
            throw new IOException("Failed to save bitmap to disk", ex);
        }
    }

    public void takePhoto(ArFragment arFragment){
        final String filename = generateFilename();
        ArSceneView view = arFragment.getArSceneView();

        final Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),view.getHeight(),
                Bitmap.Config.ARGB_8888);

        final HandlerThread handlerThread = new HandlerThread("PixelCopier");
        handlerThread.start();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            PixelCopy.request(view, bitmap, (copyResult) -> {
                if (copyResult == PixelCopy.SUCCESS) {
                    try {
                        saveBitmapToDisk(bitmap, filename);
                        Uri uri = Uri.parse("file://" + filename);
                      //  new FileUpload(view.getContext()).uploadFILE(uri);
                        //Toast.makeText(view.getContext(), filename.toString(),   Toast.LENGTH_LONG).show();
                        //Media Scanning 실시
//                        Uri uri = Uri.parse("file://" + filename);
//                        Intent i = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                        i.setData(uri);
//                        sendBroadcast(i);

                    } catch (IOException e) {
//                        Toast toast = Toast.makeText(this, e.toString(),
//                                Toast.LENGTH_LONG);
//                        toast.show();
                        return;
                    }

                } else {
//                    Toast toast = Toast.makeText(this,
//                            "Result: " + copyResult, Toast.LENGTH_LONG);
//                    toast.show();
                }
                handlerThread.quitSafely();
            }, new Handler(handlerThread.getLooper()));
        }
    }
}
