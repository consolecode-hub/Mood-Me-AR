package com.mood.ar
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.mood.ar.databinding.ActivityFileBinding
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes


const val FRAG_TYPE = "FRAG_TYPE"
const val IMAGE_TYPE = "IMAGE_TYPE"
const val VIDEO_TYPE = "VIDEO_TYPE"
const val AUDIO_TYPE = "AUDIO_TYPE"
const val FILE_TYPE = "FILE_TYPE"

class   FileActivity : AppCompatActivity(){



    private lateinit var adapterPager: MainPagerAdapter



    private lateinit var binding: ActivityFileBinding


    private var currentPath: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_file)
        binding = ActivityFileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get current path
        currentPath = intent.getStringExtra("path")

        if (checkPermission()){
            Log.d(TAG, "onCreate: Permission already granted, create folder")
           // initRecyclerView()
            initViewPager()
        }
        else{
            Log.d(TAG, "onCreate: Permission was not granted, request")
            requestPermission()
        }


        // Check for permissions
    }


    private fun initViewPager() {

        adapterPager = MainPagerAdapter(this).apply {
            addFragment(AllFilesFragment().apply { arguments = Bundle().apply { putString(FRAG_TYPE, IMAGE_TYPE) } })
            addFragment(AllFilesFragment().apply { arguments = Bundle().apply { putString(FRAG_TYPE, VIDEO_TYPE) } })
//            addFragment(AllFilesFragment().apply { arguments = Bundle().apply { putString(FRAG_TYPE, AUDIO_TYPE) } })
//            addFragment(AllFilesFragment().apply { arguments = Bundle().apply { putString(FRAG_TYPE, FILE_TYPE) } })
        }
        binding.vpMain.adapter = adapterPager

        initTabLayout()
    }

    private fun initTabLayout() {

        val tabTitles: Array<String> by lazy {
            arrayOf(
                getString(R.string.tab_images),
                getString(R.string.tab_video),
                getString(R.string.tab_audio),
                getString(R.string.tab_file),
            )
        }

        TabLayoutMediator(
            binding.tlHome, binding.vpMain
        ) { tab, position ->
            tab.text = tabTitles[position]

        }.attach()
    }

        @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty()){
            //check each permission if granted or not
            val write = grantResults[0] == PackageManager.PERMISSION_GRANTED
            val read = grantResults[1] == PackageManager.PERMISSION_GRANTED
            if (write && read){
                //External Storage Permission granted
                Log.d(TAG, "onRequestPermissionsResult: External Storage Permission granted")
                initViewPager()
            }
            else{
                //External Storage Permission denied...
                Log.d(TAG, "onRequestPermissionsResult: External Storage Permission denied...")
                //    toast("External Storage Permission denied...")
            }
        }

    }


    private  val STORAGE_PERMISSION_CODE = 100
    private  val TAG = "PERMISSION_TAG"



    private fun requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            //Android is 11(R) or above
            try {
                Log.d(TAG, "requestPermission: try")
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                val uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                storageActivityResultLauncher.launch(intent)
            }
            catch (e: Exception){
                Log.e(TAG, "requestPermission: ", e)
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                storageActivityResultLauncher.launch(intent)
            }
        }
        else{
            //Android is below 11(R)
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        }
    }

    private val storageActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        Log.d(TAG, "storageActivityResultLauncher: ")
        //here we will handle the result of our intent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            //Android is 11(R) or above
            if (Environment.isExternalStorageManager()){
                //Manage External Storage Permission is granted
                Log.d(TAG, "storageActivityResultLauncher: Manage External Storage Permission is granted")
                //createFolder()
            }
            else{
                //Manage External Storage Permission is denied....
                Log.d(TAG, "storageActivityResultLauncher: Manage External Storage Permission is denied....")
                //toast("Manage External Storage Permission is denied....")
            }
        }
        else{
            //Android is below 11(R)
        }
    }

    private fun checkPermission(): Boolean{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            //Android is 11(R) or above
            Environment.isExternalStorageManager()
        }
        else{
            //Android is below 11(R)
            val write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED
        }
    }


}