package com.example.cloudapp.otheractivities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cloudapp.MainActivity
import com.example.cloudapp.R
import com.example.cloudapp.controller.SharedApp
import com.example.cloudapp.helpers.UriPathHelper
import okhttp3.*
import java.io.File
import java.lang.Exception

class UploadActivity : AppCompatActivity() {
    lateinit var finalPath:String
    lateinit var progressBar:ProgressBar
    lateinit var fileNameText:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        findViewById<TextView>(R.id.titleUpload).animation = AnimationUtils.loadAnimation(this, R.anim.anim_common_down)
        progressBar = findViewById(R.id.progressBarUpload)
        fileNameText = findViewById(R.id.fileSelectedText)
        fileNameText.animation = AnimationUtils.loadAnimation(this, R.anim.anim_common_up)
        findViewById<TextView>(R.id.browseBTN).animation = AnimationUtils.loadAnimation(this, R.anim.anim_common_up)
        findViewById<TextView>(R.id.uploadFileBTN).animation = AnimationUtils.loadAnimation(this, R.anim.anim_common_up)
        findViewById<TextView>(R.id.browseBTN).setOnClickListener {
            checkFilePermission()
        }
        findViewById<TextView>(R.id.uploadFileBTN).setOnClickListener {
            if (finalPath != null){
                val res = AsyncTaskUpload().execute(finalPath)
            }
        }
    }

    private fun checkFilePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermission()
        } else {
            //El permiso está aceptado.
            pickFile()
        }
    }

    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Go to settings to enable storage", Toast.LENGTH_SHORT).show()
        } else {
            //El usuario nunca ha aceptado ni rechazado, así que le pedimos que acepte el permiso.
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                0)
        }
    }

    private fun pickFile(){
        val intent = Intent()
        intent.type = "*/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(Intent.createChooser(intent, "Choose a file to upload"), 102)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == 102){
                if (data == null){
                    return
                }
                val uri = data.data
                val filePath = uri?.let { UriPathHelper().getPath(this, it) }
                if (filePath != null) {
                    finalPath = filePath
                }
                fileNameText.text = File(filePath).name
            }
        }
    }

    inner class AsyncTaskUpload(): AsyncTask<String, String, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.visibility = View.VISIBLE
        }
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result != null){
                Toast.makeText(this@UploadActivity, "File uploaded successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@UploadActivity, MainActivity::class.java))
            }else{
                Toast.makeText(this@UploadActivity, "Error during upload", Toast.LENGTH_SHORT).show()
            }
            progressBar.visibility = View.GONE
        }
        override fun doInBackground(vararg p0: String?): String {
            val file = File(p0[0])
            try {
                val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.name, RequestBody.create(MediaType.parse("*/*"), file))
                    .build()
                var path = SharedApp.prefs.getPath()
                if (path == "/")
                    path = "*"
                else{
                    path = path.replace("/", "|")
                }
                val request = Request.Builder()
                    .url("http://192.168.1.100:4000/upload/$path")
                    .post(requestBody)
                    .build()
                val response = OkHttpClient().newCall(request).execute()
                if (response != null && response.isSuccessful()){
                    return "success"
                    startActivity(Intent(this@UploadActivity, MainActivity::class.java))
                }else{
                    Toast.makeText(this@UploadActivity, "Error to upload the file", Toast.LENGTH_SHORT).show()
                }
            }catch (e: Exception){
                println(e)
            }
            return ""
        }
    }

    //@SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            0 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //El usuario ha aceptado el permiso, no tiene porqué darle de nuevo al botón, podemos lanzar la funcionalidad desde aquí.
                    pickFile()
                } else {
                    //El usuario ha rechazado el permiso, podemos desactivar la funcionalidad o mostrar una vista/diálogo.
                    //startActivity(Intent(this, MainActivity::class.java))
                    Toast.makeText(this, "Nop", Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> {
                // Este else lo dejamos por si sale un permiso que no teníamos controlado.
            }
        }
    }
}