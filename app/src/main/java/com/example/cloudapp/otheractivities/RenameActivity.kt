package com.example.cloudapp.otheractivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.cloudapp.MainActivity
import com.example.cloudapp.R
import com.example.cloudapp.controller.SharedApp
import org.json.JSONObject

class RenameActivity : AppCompatActivity() {
    private lateinit var elements:List<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rename)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        elements = intent.extras?.getString("FOLDERS_ELEMENTS").toString().split("/")
        findViewById<TextView>(R.id.titleRename).animation = AnimationUtils.loadAnimation(this, R.anim.anim_common_down)
        val itemName = findViewById<EditText>(R.id.itemNameRename)
        itemName.animation = AnimationUtils.loadAnimation(this, R.anim.anim_common_up)
        itemName.setText(SharedApp.prefs.getSelectedName())
        findViewById<Button>(R.id.renameChangeBtn).animation = AnimationUtils.loadAnimation(this, R.anim.anim_common_up)
        findViewById<Button>(R.id.renameChangeBtn).setOnClickListener {
            if (itemName.text.toString().indexOf(".") != -1 && itemName.text.toString().trim().isNotEmpty() && itemName.text.toString().indexOf("/") == -1 && itemName.text.toString().indexOf("\\") == -1 && elements.filter { it == itemName.text.toString() }.isEmpty()){
                changeName()
            }else{
                Toast.makeText(this, "You have erros", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun changeName() {
        val name = SharedApp.prefs.getSelectedName()
        var route = SharedApp.prefs.getPath()
        if (route == "/")
            route = ""
        else{
            route = route.replace("/", "|")
        }
        if (route == "")
            route = name
        else
            route += "|$name"
        var newRoute = SharedApp.prefs.getPath()
        if (newRoute == "/")
            newRoute = ""
        if (newRoute == "")
            newRoute = findViewById<EditText>(R.id.itemNameRename).text.toString()
        else
            newRoute += "/"+ findViewById<EditText>(R.id.itemNameRename).text.toString()
        val queue = Volley.newRequestQueue(this)
        var url = "http://192.168.1.100:4000/movefile/${route}"
        println(SharedApp.prefs.getItemClipAcction())
        val jsonObject = JSONObject()
        jsonObject.put("newpath", newRoute)
        val request = JsonObjectRequest(
            Request.Method.PUT,
            url,
            jsonObject,
            { response: JSONObject ->
                val res = response
                Toast.makeText(this, "Element operation successfully", Toast.LENGTH_SHORT).show()
                SharedApp.prefs.unSetItem()
                startActivity(Intent(this, MainActivity::class.java))
            },
            { error ->
                Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
            }
        )

        // Add the request to the RequestQueue.
        queue.add(request)
    }
}