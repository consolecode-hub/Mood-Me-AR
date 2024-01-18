# Mood Me AR
This is a demo app for testing the Google [ARCore](https://developers.google.com/ar/) library with [Sceneform](https://github.com/google-ar/sceneform-android-sdk). 

## Augmented Faces developer guide for Android

ARCore > Documentation [ARCore](https://developers.google.com/ar/develop/java/augmented-faces/developer-guide)

## Introduction to ARCore Augmented Faces, Android 
Learn how to use ARCore’s Augmented Faces APIs to create face effects for Android.  [ARCore] https://www.youtube.com/watch?v=-4EvaCQpVEQ&ab_channel=GoogleforDevelopers

## Dependencies

*app/build.gradle*
```gradle
dependencies {
     implementation "com.gorisse.thomas.sceneform:sceneform:1.15.0"
}
```

Using Augmented Faces in Android

Configure the ARCore session
```kotlin
// Set a camera configuration that usese the front-facing camera.
val filter = CameraConfigFilter(session).setFacingDirection(CameraConfig.FacingDirection.FRONT)
val cameraConfig = session.getSupportedCameraConfigs(filter)[0]
session.cameraConfig = cameraConfig
```
Enable AugmentedFaceMode:
```kotlin
// Set a camera configuration that usese the front-facing camera.
val filter = CameraConfigFilter(session).setFacingDirection(CameraConfig.FacingDirection.FRONT)
val cameraConfig = session.getSupportedCameraConfigs(filter)[0]
session.cameraConfig = cameraConfig
```
Get access to the detected face
```kotlin
// ARCore's face detection works best on upright faces, relative to gravity.
val faces = session.getAllTrackables(AugmentedFace::class.java)
}

faces.forEach { face ->
  if (face.trackingState == TrackingState.TRACKING) {
    // UVs and indices can be cached as they do not change during the session.
    val uvs = face.meshTextureCoordinates
    val indices = face.meshTriangleIndices
    // Center and region poses, mesh vertices, and normals are updated each frame.
    val facePose = face.centerPose
    val faceVertices = face.meshVertices
    val faceNormals = face.meshNormals
    // Render the face using these values with OpenGL.
  }
}
```


### Config your `AndroidManifest.xml`

*AndroidManifest.xml*
```xml
    <uses-feature
        android:name="android.hardware.camera"
        android:required="true" 
    <uses-permission android:name="android.permission.CAMERA" />

    <uses-feature
        android:name="android.hardware.camera.ar"
        android:required="true" />
    <uses-feature android:name="android.hardware.camera.autofocus" />
    <uses-feature
        android:glEsVersion="0x00030000"
        android:required="true" />
```
[**more...**](https://github.com/consolecode-hub/Mood-Me-AR/blob/master/app/src/main/AndroidManifest.xml)
