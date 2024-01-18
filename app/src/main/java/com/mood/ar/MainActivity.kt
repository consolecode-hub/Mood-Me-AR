package com.mood.ar

import android.Manifest
import android.app.ActivityManager
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.CamcorderProfile
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.ar.core.ArCoreApk
import com.google.ar.core.AugmentedFace
import com.google.ar.core.RecordingStatus
import com.google.ar.core.TrackingState
import com.google.ar.core.exceptions.RecordingFailedException
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.Texture
import com.google.ar.sceneform.ux.AugmentedFaceNode
import com.mood.ar.FaceModal.Constants
import com.mood.ar.FaceModal.CustomFaceNode
import com.mood.ar.FaceModal.FaceModalDialog
import com.mood.ar.FaceModal.FaceModel
import com.mood.ar.MyAPI.FileUpload
import com.mood.ar.Recoder.VideoRecorder
import com.mood.ar.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(){

    companion object {
        const val MIN_OPENGL_VERSION = 3.0
    }

    var facemode: Int = 0

    private val TAG = MainActivity::class.java.simpleName
    private var videoRecorder: VideoRecorder? = null
    private  lateinit var binding: ActivityMainBinding

    lateinit var arFragment: MoodArFragment
    private var faceMeshTexture: Texture? = null
    private var faceMeshTexture1: Texture? = null
    private var faceMeshTexture2: Texture? = null


    var faceNodeMap = HashMap<AugmentedFace, AugmentedFaceNode>()
    var isFox: Boolean? = false
    var changeModel: Boolean? = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!checkIsSupportedDeviceOrFinish()) {
            return
        }


        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        binding.face.setOnClickListener {
            facemode=0
        }

        binding.glass.setOnClickListener {
            facemode=1
        }
        arFragment = supportFragmentManager.findFragmentById(R.id.face_fragment) as MoodArFragment
        //  arFragment = binding.face_fragment as FaceArFragment


            Texture.builder()
                .setSource(this, R.drawable.makeup)
                .build()
                .thenAccept { texture -> faceMeshTexture =texture }
          //  posssi++;


        Texture.builder()
            .setSource(this, R.drawable.makeup)
            .build()
            .thenAccept { texture -> faceMeshTexture1 = texture }

        Texture.builder()
            .setSource(this, R.drawable.img)
            .build()
            .thenAccept { texture -> faceMeshTexture2 = texture }

        CreateModelRenderable()

        val sceneView = arFragment.arSceneView
        sceneView.cameraStreamRenderPriority = Renderable.RENDER_PRIORITY_FIRST
        val scene = sceneView.scene

        scene.addOnUpdateListener {
            faceMeshTexture.let {
                sceneView.session
                    ?.getAllTrackables(AugmentedFace::class.java)?.let {
                        for (f in it) {
                           if(facemode==0) {
                               if (!faceNodeMap.containsKey(f)) {
                                   val faceNode = AugmentedFaceNode(f)
                                   faceNode.setParent(scene)
                                   faceNode.faceMeshTexture = faceMeshTexture
                                   faceNodeMap.put(f, faceNode)
                                   //  }
                               } else if (changeModel == true) {    // check whether we want to change texture
                                   if (isFox == true)
                                       faceNodeMap.get(f)?.setFaceMeshTexture(faceMeshTexture1);
                                   else
                                       faceNodeMap.get(f)?.setFaceMeshTexture(faceMeshTexture2);
                               }
                           }

                           else if(facemode==1) {
                               if (!faceNodeMap.containsKey(f)) {
                                   val faceNode = CustomFaceNode(f, this)
                                   faceNode.setParent(scene)
                                   faceNodeMap.put(f, faceNode)
                               }
                           }

                            val iter = faceNodeMap.entries.iterator()
                            while (iter.hasNext()) {
                                val entry = iter.next()
                                val face = entry.key
                                if (face.trackingState == TrackingState.STOPPED) {
                                    val faceNode = entry.value
                                    faceNode.setParent(null)
                                    iter.remove()
                                }
                            }
                        }
                    }
            }
        }

        videoRecorder = VideoRecorder()
        val orientation = resources.configuration.orientation
        videoRecorder!!.setVideoQuality(CamcorderProfile.QUALITY_2160P, orientation)
        videoRecorder!!.setSceneView(arFragment.arSceneView)


        binding.startRecordingButton.setOnClickListener {
            startRecording()
        }
        binding.stopRecordingButton.setOnClickListener {
            stopRecording()
        }

        binding.photoButton.setOnClickListener {
           // videoRecorder!!.takePhoto(arFragment)

            popUpTagEditText(videoRecorder!!.takePhoto(this@MainActivity, arFragment))
        }

        binding.galleryBtn.setOnClickListener {
        startActivity(Intent(this, FileActivity::class.java))
        }

        if (!checkSelfPermission()) {
            requestPermission()
        }
        changeModel = true;
        isFox = false;
        setupModalButtons()
        if(videoRecorder!!.isRecording()){
            binding.startRecordingButton.setVisibility(View.GONE);
            binding.stopRecordingButton.setVisibility(View.VISIBLE);
        }else{
            binding.startRecordingButton.setVisibility(View.VISIBLE);
            binding.stopRecordingButton.setVisibility(View.GONE);
        }

    }

    fun updateTexture(position: Int, model: FaceModel){
        Texture.builder()
            .setSource(this, model.image)
            .build()
            .thenAccept { texture -> faceMeshTexture1 =texture }
        isFox = true;
        changeModel = true;
    }



    private fun setupModalButtons() {
            binding.fileBtn.setOnClickListener {
                facemode=0
                ListDialough()
            }
    }
     lateinit var listDialog: FaceModalDialog
    fun ListDialough() {
        val DataList: ArrayList<FaceModel> = Constants.getFaceModelData()
         listDialog = object : FaceModalDialog(
            this@MainActivity,
            DataList
        ) {

        }
        listDialog.show()
    }


    private fun startRecording() {
        try {
            val recording = videoRecorder!!.onToggleRecord()
            if (recording) {
                binding.startRecordingButton.setVisibility(View.GONE);
                binding.stopRecordingButton.setVisibility(View.VISIBLE);
            }
            else {
                val videoPath = videoRecorder!!.videoPath.absolutePath
                Toast.makeText(this, "Video saved: $videoPath", Toast.LENGTH_SHORT).show()
                Log.d(TAG,
                    "Video saved: $videoPath"
                )
            }
        } catch (e: RecordingFailedException) {
            val errorMessage = "Failed to start recording. $e"
            Log.e(TAG, errorMessage, e)
            return
        }
    }

    fun uploadFile(context: Context, uri : Uri) {
        val dialog = Dialog(context)
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Are you sure you want to upload to the server?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                dialog.dismiss()
               // FileUpload(context).uploadFILE(uri)

            }
            .setNegativeButton("No") { dialog, id ->
                // Dismiss the dialog
                dialog.dismiss()
            }
        val alert = builder.create()
        builder.show()
    }

    private fun popUpVideoTagEditText() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Tag:")
        val input = EditText(this)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        input.layoutParams = lp
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("Save") { dialog, which ->
            // do something here on OK

            this.contentResolver.query(videoRecorder!!.getVideoURI(this), arrayOf(MediaStore.Video.Media.DURATION), null, null, null)?.use {
                val durationColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                it.moveToFirst()

                FileUpload(applicationContext).uploadFILE(videoRecorder!!.getVideoURI(this),  input.text.toString(), it.getLong(durationColumn).toString())
            }

            Toast.makeText(applicationContext, "\uD83D\uDCF7 Successfully Captured!",   Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog.dismiss() }
        builder.show()
    }

    private fun popUpTagEditText(uri: Uri?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Tag:")
        val input = EditText(this)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        input.layoutParams = lp
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("Save") { dialog, which ->
            // do something here on OK
            if (uri != null) {
                FileUpload(applicationContext).uploadFILE(uri, input.text.toString(),"")
            }

                Toast.makeText(applicationContext, "\uD83D\uDCF7 Successfully Captured!",   Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog.dismiss() }
        builder.show()
    }

    /** Performs action when stop_recording button is clicked.  */
    private fun stopRecording() {
        try {
            val recording = videoRecorder!!.onToggleRecord()
            if (recording) {
                binding.startRecordingButton.setVisibility(View.GONE);
                binding.stopRecordingButton.setVisibility(View.VISIBLE);
            }
            else {
                binding.startRecordingButton.setVisibility(View.VISIBLE);
                binding.stopRecordingButton.setVisibility(View.GONE);
                //val videoPath = videoRecorder!!.videoPath.absolutePath
             //   uploadFile(this, Uri.fromFile(videoRecorder!!.videoPath));

                 //   popUpVideoTagEditText()

            }
            arFragment.arSceneView.session!!.stopRecording()
            popUpVideoTagEditText()
        } catch (e: RecordingFailedException) {
            val errorMessage = "Failed to stop recording. $e"
            Log.e(TAG, errorMessage, e)
            return
        }
        if (arFragment.arSceneView.session!!.recordingStatus == RecordingStatus.OK) {
            Log.e(TAG,
                "Failed to stop recording, recording status is " + arFragment.arSceneView.session!!.recordingStatus)
            return
        }

        binding.startRecordingButton.setVisibility(View.VISIBLE);
        binding.stopRecordingButton.setVisibility(View.GONE);
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 6036)
    }


    private fun checkSelfPermission(): Boolean {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return false
        } else
            return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            6036 -> {
                if (grantResults.size > 0) {
                    var permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (permissionGranted) {

                    } else {
                       // Toast.makeText(this, "Permission Denied! Cannot load images.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    fun checkIsSupportedDeviceOrFinish() : Boolean {
        if (ArCoreApk.getInstance().checkAvailability(this) == ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE) {
            Toast.makeText(this, "Augmented Faces requires ARCore", Toast.LENGTH_LONG).show()
            finish()
            return false
        }
        val openGlVersionString =  (getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager)
            ?.deviceConfigurationInfo
            ?.glEsVersion

        openGlVersionString?.let { s ->
            if (java.lang.Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
                Toast.makeText(this, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show()
                finish()
                return false
            }
        }
        return true
    }

    fun CreateModelRenderable(){

    }
}
