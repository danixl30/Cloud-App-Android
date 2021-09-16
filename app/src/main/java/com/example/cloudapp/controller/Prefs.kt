package com.example.cloudapp.controller

import android.content.Context

class Prefs (context:Context) {
    private val SHARED_NAME = "IDS"
    private val PATH = "/"
    private val CLIPBOARD_NAME = "CLIP_NAME"
    private val CLIPBOARD_PATH = "CLIP_PATH"
    private val CLIPBOARD_ACTTION = "acttion"
    private val SELECTED_ITEM_NAME = "Item_name"
    private val SELECTED_ITEM_PATH = "Item_path"
    private val TYPE_VIEW= "TYPE"

    val storage = context.getSharedPreferences(SHARED_NAME, 0)

    fun getTypeView():Int{
        return storage.getInt(TYPE_VIEW, 1)
    }

    fun setTypeView(type:Int){
        storage.edit().putInt(TYPE_VIEW, type).apply()
    }

    fun setItemClipBoard(name: String, path: String, acction:String){
        storage.edit().putString(CLIPBOARD_NAME, name).apply()
        storage.edit().putString(CLIPBOARD_PATH, path).apply()
        storage.edit().putString(CLIPBOARD_ACTTION, acction).apply()
    }

    fun getItemClipName():String{
        return storage.getString(CLIPBOARD_NAME, "")!!
    }

    fun getItemClipPath():String{
        return storage.getString(CLIPBOARD_PATH, "")!!
    }

    fun getItemClipAcction():String{
        return storage.getString(CLIPBOARD_ACTTION, "")!!
    }

    fun setPath(path: String){
        storage.edit().putString(PATH, path).apply()
    }

    fun getPath(): String {
        return storage.getString(PATH, "/")!!
    }

    fun unSetItem(){
        storage.edit().putString(SELECTED_ITEM_NAME, "").apply()
        storage.edit().putString(SELECTED_ITEM_PATH, "").apply()
    }

    fun setItem(name:String, path: String){
        storage.edit().putString(SELECTED_ITEM_NAME, name).apply()
        storage.edit().putString(SELECTED_ITEM_PATH, path).apply()
    }

    fun getSelectedName():String{
        return storage.getString(SELECTED_ITEM_NAME, "")!!
    }
}