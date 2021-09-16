package com.example.cloudapp.otheractivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.cloudapp.MainActivity
import com.example.cloudapp.R
import com.example.cloudapp.controller.SharedApp
import org.json.JSONObject

class NewFolderActivity : AppCompatActivity() {
    private lateinit var elements:List<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_folder)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        elements = intent.extras?.getString("FOLDERS_ELEMENTS").toString().split("/")
        findViewById<TextView>(R.id.titleNewFolder).animation = AnimationUtils.loadAnimation(this, R.anim.anim_common_down)
        val folderName = findViewById<EditText>(R.id.folderName)
        folderName.animation = AnimationUtils.loadAnimation(this, R.anim.anim_common_up)
        val name = folderName
        val supportText = findViewById<TextView>(R.id.supportText)
        val createBTN = findViewById<TextView>(R.id.uploadFileBTN)
        createBTN.animation = AnimationUtils.loadAnimation(this, R.anim.anim_common_up)
        findViewById<EditText>(R.id.folderName).addTextChangedListener{object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    if (s.indexOf(".") != -1 && s.indexOf("/") != -1 && s.indexOf("\\") != -1){
                        supportText.text = "Invalid Characters"
                    }else{
                        supportText.text = ""
                    }
                    for (i in 0 .. elements.size){
                        if (elements[i] == s){
                            supportText.text = "This name already exist"
                            break
                        }
                    }
                }
            }
        }
        }
        createBTN.setOnClickListener {
            //println(this.elements)
            if (name.text.toString().trim().isNotEmpty() && name.text.toString().indexOf(".") == -1 && name.text.toString().indexOf("/") == -1 && name.text.toString().indexOf("\\") == -1 && elements.filter { it == name.text.toString() }.isEmpty()){
                createFolder()
            }else{
                Toast.makeText(this, "You have errors", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createFolder(){
        var route = SharedApp.prefs.getPath()
        if (route == "/")
            route = ""
        else{
            route = route.replace("/", "|")
        }
        val name = findViewById<EditText>(R.id.folderName).text.toString()
        if (route == "")
            route = name
        else
            route += "|$name"
        val queue = Volley.newRequestQueue(this)
        val url = "http://192.168.1.100:4000/createdir/${route}"
        val request = JsonObjectRequest(
            Request.Method.POST,
            url,
            null,
            { response: JSONObject ->
                val res = response
                Toast.makeText(this, "Folder created successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            },
            { error ->
                Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
            }
        )

        // Add the request to the RequestQueue.
        queue.add(request)
    }
}