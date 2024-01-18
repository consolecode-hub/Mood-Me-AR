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


##Fill Access

![Screenshot_20240118-152734](https://github.com/consolecode-hub/Mood-Me-AR/assets/3745464/b09d452b-3812-4d46-98b3-8c27891ba2bb)

## Allow app to take pichtues and record videos 

![Screenshot_20240118-152636](https://github.com/consolecode-hub/Mood-Me-AR/assets/3745464/ede742fe-c767-45ba-94f1-841d3551b3a3)


## Screenshots
![Screenshot_20240118-153605](https://github.com/consolecode-hub/Mood-Me-AR/assets/3745464/ea3e070f-4ef3-4b70-8ee3-bcfd0ef4e328)
![Screenshot_20240118-152727](https://github.com/consolecode-hub/Mood-Me-AR/assets/3745464/515e5678-5499-430d-88ff-1bf6a4a44183)
![Screenshot_20240118-152658](https://github.com/consolecode-hub/Mood-Me-AR/assets/3745464/969e3a83-1f3d-4f3f-9157-b183fa3b78b7)
![Screenshot_20240118-154728](https://github.com/consolecode-hub/Mood-Me-AR/assets/3745464/f7e93e0b-4b69-4517-93ac-dd27510e00a1)
![Screenshot_20240118-153452](https://github.com/consolecode-hub/Mood-Me-AR/assets/3745464/95f2a3e6-9131-4355-a18f-369c8c987373)
![Screenshot_20240118-153445](https://github.com/consolecode-hub/Mood-Me-AR/assets/3745464/6d8a4dc8-9da4-4a1b-97b9-b867efe1aa36)


