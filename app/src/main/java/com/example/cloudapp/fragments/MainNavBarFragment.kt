package com.example.cloudapp.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import com.example.cloudapp.MainActivity
import com.example.cloudapp.R
import com.example.cloudapp.controller.SharedApp
import com.example.cloudapp.interfaces.ListenerNavBar
import com.example.cloudapp.otheractivities.NewFolderActivity
import com.example.cloudapp.otheractivities.UploadActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainNavBarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainNavBarFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var onClickButton: ListenerNavBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root: View = inflater.inflate(R.layout.fragment_main_nav_bar, container, false)
        Companion.view = root
        root.findViewById<ImageButton>(R.id.goUpBtn).setOnClickListener {
            onClickButton.onClickButton("update")
        }
        root.findViewById<ImageButton>(R.id.refreshBtn).setOnClickListener {
            onClickButton.onClickButton("refresh")
        }
        root.findViewById<ImageButton>(R.id.newFolderBTN).setOnClickListener {
            onClickButton.onClickButton("newFolder")
        }
        root.findViewById<ImageButton>(R.id.uploadBtn).setOnClickListener {
            startActivity(Intent(activity, UploadActivity::class.java))
        }
        root.findViewById<ImageButton>(R.id.typeViewBtn).setOnClickListener {
            onClickButton.onClickButton("changeView")
        }
        return root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainNavBarFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainNavBarFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        private lateinit var view: View
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = (context as Activity)
        try {
            onClickButton = (activity as ListenerNavBar)
        }catch (e:Exception){
            println(e.toString())
        }
    }
}