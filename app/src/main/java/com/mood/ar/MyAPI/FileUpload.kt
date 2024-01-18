package com.mood.ar.MyAPI


import android.content.Context
import android.net.Uri
import android.util.Log
import android.database.Cursor
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

public class FileUpload(val context: Context) {
    private var selectedfileUri: Uri? = null


    public fun uploadFILE(uri : Uri, tag: String, duration: String) {

        selectedfileUri = uri
        if (selectedfileUri == null) {
            Log.d("Upload_TAG", "Select an file First")
            return
        }

        val parcelFileDescriptor =
            context.contentResolver.openFileDescriptor(selectedfileUri!!, "r", null) ?: return

        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val file = File(context.cacheDir, context.contentResolver.getFileName(selectedfileUri!!))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        var imei: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

        Log.d("Upload_TAG", "Progress 0")
        val body = UploadRequestBody(file, "file", context)
        MyAPI().uploadFILE(
            MultipartBody.Part.createFormData(
                "file",
                file.name,
                body
            ),
            RequestBody.create(MediaType.parse("multipart/form-data"), file.name),
            RequestBody.create(MediaType.parse("multipart/form-data"), file.extension),
            RequestBody.create(MediaType.parse("multipart/form-data"),imei),
            RequestBody.create(MediaType.parse("multipart/form-data"),tag),
                    RequestBody.create(MediaType.parse("multipart/form-data"),duration)
        ).enqueue(object : Callback<UploadResponse> {
            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                Log.d("RequestBody", t.message!!)
                // findViewById<ProgressBar>(R.id.progress_bar).progress = 0
                Log.d("Upload_TAG", "Progress 0")
            }

            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                response.body()?.let {
                    Log.d("response", it.message)
                    //findViewById<ProgressBar>(R.id.progress_bar).progress = 100
                    Log.d("Upload_TAG", "Progress 100")
                }
            }
        })

    }

}