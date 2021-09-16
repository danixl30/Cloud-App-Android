package com.example.cloudapp.otheractivities

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.cloudapp.MainActivity
import com.example.cloudapp.R
import com.example.cloudapp.RecyclerView.DataItems
import com.example.cloudapp.RecyclerView.ExplorerAdapter
import com.example.cloudapp.controller.SharedApp
import com.example.cloudapp.interfaces.ClickRecyclerListener
import org.json.JSONObject

class DownloadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        getDetails()
        val title = findViewById<TextView>(R.id.downloadTitle)
        title.animation = AnimationUtils.loadAnimation(this, R.anim.anim_common_down)
        val fileName = findViewById<TextView>(R.id.fileNameText)
        fileName.animation = AnimationUtils.loadAnimation(this, R.anim.anim_common_up)
        fileName.text = SharedApp.prefs.getSelectedName()
        findViewById<TextView>(R.id.dateText).animation = AnimationUtils.loadAnimation(this, R.anim.anim_common_up)
        findViewById<TextView>(R.id.sizeText).animation = AnimationUtils.loadAnimation(this, R.anim.anim_common_up)
        val downloadBTN = findViewById<TextView>(R.id.downloadBTN)
        downloadBTN.animation = AnimationUtils.loadAnimation(this, R.anim.anim_common_up)
        downloadBTN.setOnClickListener {
            checkFilePermission()
        }
    }

    private fun getDetails(){
        var path = SharedApp.prefs.getPath()
        if (path == "/")
            path = SharedApp.prefs.getSelectedName()
        else{
            path = path.replace("/", "|")
            path += "|${SharedApp.prefs.getSelectedName()}"
        }
        val queue = Volley.newRequestQueue(this)
        val url = "http://192.168.1.100:4000/props/${path}"
        // Request a JSONObject response from the provided URL.
        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response: JSONObject ->
                val res = response
                val stats = res.getJSONObject("stats")
                var date = stats.getString("atime")
                var size = stats.getDouble("size")
                //println(date)
                //println(size)
                date = date.slice(0..9).replace("-", "/")
                findViewById<TextView>(R.id.dateText).text = "Date: $date"
                if (size >= 1000000000){
                    size /= 1000000000
                    findViewById<TextView>(R.id.sizeText).text = "Size: ${size.toString().slice(0..5)} GB"
                }else if (size >= 1000000){
                    size /= 1000000
                    findViewById<TextView>(R.id.sizeText).text = "Size: ${size.toString().slice(0..5)} MB"
                }else if (size >= 1000){
                    size /= 1000
                    findViewById<TextView>(R.id.sizeText).text = "Size: ${size.toString().slice(0..5)} KB"
                }else {
                    //size = `${size.toString()} bytes`;
                    findViewById<TextView>(R.id.sizeText).text = "Size: $size Bytes"
                }
            },
            { error ->
                Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
            }
        )

        // Add the request to the RequestQueue.
        queue.add(request)
    }

    private fun downloadFile() {
       var path = SharedApp.prefs.getPath()
       if (path == "/")
           path = SharedApp.prefs.getSelectedName()
        else{
            path = path.replace("/", "|")
           path += "|${SharedApp.prefs.getSelectedName()}"
       }
        val request = DownloadManager.Request(Uri.parse("http://192.168.1.100:4000/file/$path"))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        request.setTitle("Downloading: ${SharedApp.prefs.getSelectedName()}")
        request.setDescription("The file is downloading...")

        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
            SharedApp.prefs.getSelectedName()
        )

        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)

        Toast.makeText(this, "Download started...", Toast.LENGTH_SHORT).show()
    }

    private fun checkFilePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermission()
        } else {
            //El permiso está aceptado.
            downloadFile()
        }
    }

    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Go to settings to enable storage", Toast.LENGTH_SHORT).show()
        } else {
            //El usuario nunca ha aceptado ni rechazado, así que le pedimos que acepte el permiso.
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                0)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            0 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //El usuario ha aceptado el permiso, no tiene porqué darle de nuevo al botón, podemos lanzar la funcionalidad desde aquí.
                    downloadFile()
                } else {
                    //El usuario ha rechazado el permiso, podemos desactivar la funcionalidad o mostrar una vista/diálogo.
                    //startActivity(Intent(this, MainActivity::class.java))
                    startActivity(Intent(this, MainActivity::class.java))
                }
                return
            }
            else -> {
                // Este else lo dejamos por si sale un permiso que no teníamos controlado.
            }
        }
    }
}