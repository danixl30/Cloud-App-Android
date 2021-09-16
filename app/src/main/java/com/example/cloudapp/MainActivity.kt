package com.example.cloudapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.cloudapp.RecyclerView.DataItems
import com.example.cloudapp.RecyclerView.DataPath
import com.example.cloudapp.RecyclerView.ExplorerAdapter
import com.example.cloudapp.RecyclerView.PathAdapter
import com.example.cloudapp.controller.SharedApp
import com.example.cloudapp.fragments.MainNavBarFragment
//import com.example.cloudapp.controller.SharedApp
import com.example.cloudapp.fragments.SecondNavFragment
import com.example.cloudapp.fragments.ThirdNavFragment
import com.example.cloudapp.interfaces.ClickRecyclerListener
import com.example.cloudapp.interfaces.ListenerNavBar
import com.example.cloudapp.interfaces.SecondRecyclerListener
import com.example.cloudapp.otheractivities.DownloadActivity
import com.example.cloudapp.otheractivities.NewFolderActivity
import com.example.cloudapp.otheractivities.RenameActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity(), ListenerNavBar {
    lateinit var elements:ArrayList<DataItems>
    lateinit var paths:ArrayList<DataPath>
    lateinit var sheetBotton: BottomSheetDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        createSheet()
        setContentView(R.layout.activity_main)
        if (SharedApp.prefs.getSelectedName() === "")
            setNavBar(1)
        setPathBar()
        getElements(this)

    }

    @SuppressLint("WrongConstant")
    fun setPathBar(){
        var pathItems = SharedApp.prefs.getPath().split("/")
        val items = ArrayList<DataPath>()
        items.add(DataPath("root"))
        for (i in pathItems.indices){
            if (pathItems[i].isNotEmpty())
                items.add(DataPath(pathItems[i]))
        }
        //println(items)
        paths = items
        val pathContainer = findViewById<RecyclerView>(R.id.pathContainer)
        pathContainer.layoutManager = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)
        val adapter = PathAdapter(items, object : SecondRecyclerListener{
            override fun onClickPath(view: View, index: Int) {
                Toast.makeText(this@MainActivity, index.toString(), Toast.LENGTH_LONG).show()
            }
        })
        pathContainer.adapter = adapter
    }

    fun setNewPath(index:Int){
        println("here")
        var newPath = ""
        for (i in 1 .. index){
            newPath += paths[i].name
            if (i < index)
                newPath += "/"
        }
        if (newPath == "")
            newPath = "/"
        println(newPath.isEmpty())
        SharedApp.prefs.setPath(newPath)
        SharedApp.prefs.unSetItem()
        setNavBar(1)
        getElements(this)
    }

    @SuppressLint("WrongConstant")
    fun getElements(context:Context){
        var route = SharedApp.prefs.getPath()
        if (route == "/")
            route = "*"
        else{
            route = route.replace("/", "|")
        }
        val queue = Volley.newRequestQueue(context)
        val url = "http://192.168.1.100:4000/getdir/${route}"
        //println(prefs.getPath())
        val recyclerView = findViewById<RecyclerView>(R.id.itemsContainer)

        // Request a JSONObject response from the provided URL.
        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response: JSONObject ->
                val res = response
                val files = res.getJSONArray("files")
                val folders = res.getJSONArray("folders")
                val items = ArrayList<DataItems>()
                var i: Int = 0
                if (SharedApp.prefs.getTypeView() == 1)
                    recyclerView.layoutManager = GridLayoutManager(this, 4)
                else
                    recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
                for (i in 0 until folders.length()) {
                    if (SharedApp.prefs.getSelectedName() == folders[i]){
                        items += DataItems(folders[i] as String, true)
                    }else{
                        items += DataItems(folders[i] as String, false)
                    }
                }
                for (i in 0 until files.length()) {
                    if (SharedApp.prefs.getSelectedName() == files[i])
                        items += DataItems(files[i] as String, true)
                    else
                        items += DataItems(files[i] as String, false)
                }
                elements = items
                val adapter = ExplorerAdapter(items, object:ClickRecyclerListener{
                    override fun onClick(view: View, index: Int) {
                        //Toast.makeText(applicationContext, elements[index].name, Toast.LENGTH_LONG).show()
                        computePath(elements[index].name)
                    }
                })
                recyclerView.adapter = adapter
            },
            { error ->
                Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
            }
        )

        // Add the request to the RequestQueue.
        queue.add(request)
    }

    fun computePath(name:String){
        if (SharedApp.prefs.getSelectedName() == name){
            if (name.indexOf(".") == -1) {
                var route = SharedApp.prefs.getPath()
                if (route == "/") route = name
                else route = route + "/" + name
                SharedApp.prefs.setPath(route)
                SharedApp.prefs.unSetItem()
                if (SharedApp.prefs.getItemClipName().isEmpty())
                    setNavBar(1)
                setPathBar()
                getElements(this)
            }else{
                startActivity(Intent(this, DownloadActivity::class.java))
            }
        }else{
            SharedApp.prefs.setItem(name, SharedApp.prefs.getPath())
            if (SharedApp.prefs.getItemClipName().isEmpty())
                setNavBar(2)
            getElements(this)
        }
    }

    fun setNavBar(type:Int){
        if (type == 1){
            val mainNav = MainNavBarFragment()
            val fragmentTrasition = supportFragmentManager.beginTransaction().add(R.id.fragmentContainerView, mainNav)
            fragmentTrasition.commit()
        }

        if (type == 2){
            val secondNav = SecondNavFragment()
            val fragmentTrasition = supportFragmentManager.beginTransaction().add(R.id.fragmentContainerView, secondNav)
            fragmentTrasition.commit()
        }

        if (type == 3){
            val thirdNav = ThirdNavFragment()
            val fragmentTrasition = supportFragmentManager.beginTransaction().add(R.id.fragmentContainerView, thirdNav)
            fragmentTrasition.commit()
        }
    }

    override fun onClickButton(type: String) {
        if (type == "update"){
            var path = SharedApp.prefs.getPath().trim()
            var elements = path.split("/")
            path = ""
            for (i in 0..elements.size-2){
                if (elements[i].isNotEmpty())
                    path += elements[i]
                if (i < elements.size-2) path += "/"
            }
            if (path != "")
                SharedApp.prefs.setPath(path)
            else
                SharedApp.prefs.setPath("/")
            setPathBar()
            getElements(this)
        }

        if (type == "refresh"){
            setPathBar()
            getElements(this)
        }

        if (type == "unselect"){
            SharedApp.prefs.unSetItem()
            setNavBar(1)
            getElements(this)
        }

        if (type == "newFolder"){
            var folders: String = ""
            for (i in 0 .. elements.size-1){
                println(elements[i].name)
                if (elements[i].name.indexOf(".") == -1)
                    folders += elements[i].name

                if (i < elements.size-1)
                    folders+="/"
            }
            Toast.makeText(this, folders, Toast.LENGTH_LONG).show()
            val newFolderView = Intent(this, NewFolderActivity::class.java)
            newFolderView.putExtra("FOLDERS_ELEMENTS", folders)
            startActivity(newFolderView)
        }

        if (type == "delete"){
            createAlert()
        }

        if (type == "copy" || type == "move"){
            SharedApp.prefs.setItemClipBoard(SharedApp.prefs.getSelectedName(), SharedApp.prefs.getPath(), type)
            setNavBar(3)
        }

        if (type == "cancel"){
            SharedApp.prefs.setItemClipBoard("", "", "")
            SharedApp.prefs.unSetItem()
            getElements(this)
            setNavBar(1)
        }

        if (type == "paste"){
            var control = true
            if (elements.size > 0){
                for (i in 0 until elements.size){
                    if (elements[i].name == SharedApp.prefs.getItemClipName()){
                        control = false
                        break
                    }
                }
            }
            if (control){
                setAcctionItem()
            }else{
                Toast.makeText(this, "This file already exist", Toast.LENGTH_SHORT).show()
            }
        }

        if (type == "rename"){
            var items:String = ""
            for (i in 0 .. elements.size-1){
                items+= elements[i].name

                if (i < elements.size-1)
                    items+="/"
            }
            val renameView = Intent(this, RenameActivity::class.java)
            renameView.putExtra("FOLDERS_ELEMENTS", items)
            startActivity(renameView)
        }

        if (type == "changeView"){
            sheetBotton.show()
            sheetBotton.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    private fun createSheet(){
        val view = LayoutInflater.from(this).inflate(R.layout.button_sheet_type_view, null)
        view.findViewById<LinearLayout>(R.id.mosaicViewBtn).setOnClickListener {
            //Toast.makeText(this, "Mosaic", Toast.LENGTH_SHORT).show()
            SharedApp.prefs.setTypeView(1)
            getElements(this)
        }
        view.findViewById<LinearLayout>(R.id.listViewBtn).setOnClickListener {
            //Toast.makeText(this, "List", Toast.LENGTH_SHORT).show()
            SharedApp.prefs.setTypeView(2)
            getElements(this)
        }
        sheetBotton = BottomSheetDialog(this, R.style.BottomSheetTheme)
        sheetBotton.setContentView(view)
    }

    private fun setAcctionItem() {
        val name = SharedApp.prefs.getItemClipName()
        var route = SharedApp.prefs.getItemClipPath()
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
            newRoute = SharedApp.prefs.getItemClipName()
        else
            newRoute += "/"+SharedApp.prefs.getItemClipName()
        val queue = Volley.newRequestQueue(this)
        var url = ""
        println(SharedApp.prefs.getItemClipAcction())
        if (SharedApp.prefs.getItemClipAcction() == "copy"){
            url = "http://192.168.1.100:4000/copyfile/${route}"
        }else{
            url = "http://192.168.1.100:4000/movefile/${route}"
        }
        val jsonObject = JSONObject()
        fun getRequestType(type:String): Int {
            if (type == "copy"){
                jsonObject.put("newPath", newRoute)
                return Request.Method.POST
            }else{
                jsonObject.put("newpath", newRoute)
                return Request.Method.PUT
            }
        }
        val request = JsonObjectRequest(
            getRequestType(SharedApp.prefs.getItemClipAcction()),
            url,
            jsonObject,
            { response: JSONObject ->
                val res = response
                Toast.makeText(this, "Element operation successfully", Toast.LENGTH_SHORT).show()
                SharedApp.prefs.setItemClipBoard("", "", "")
                SharedApp.prefs.unSetItem()
                setNavBar(1)
                getElements(this)
            },
            { error ->
                Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
            }
        )

        // Add the request to the RequestQueue.
        queue.add(request)
    }

    private fun createAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Are you sure that you want to delete this item?")
        builder.setMessage(SharedApp.prefs.getSelectedName())
        builder.setPositiveButton("Yes",DialogInterface.OnClickListener{ dialog, id ->
            deleteItem()
        })
        builder.setNegativeButton("No", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun deleteItem() {
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
        val queue = Volley.newRequestQueue(this)
        var url = ""
        if (name.indexOf(".") == -1)
            url = "http://192.168.1.100:4000/deletedir/${route}"
        else
            url = "http://192.168.1.100:4000/deletefile/${route}"
        val request = JsonObjectRequest(
            Request.Method.DELETE,
            url,
            null,
            { response: JSONObject ->
                val res = response
                Toast.makeText(this, "Element deleted successfully", Toast.LENGTH_SHORT).show()
                getElements(this)
            },
            { error ->
                Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
            }
        )

        // Add the request to the RequestQueue.
        queue.add(request)
    }
}
