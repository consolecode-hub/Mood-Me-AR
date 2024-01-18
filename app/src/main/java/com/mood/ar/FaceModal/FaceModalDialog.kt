package com.mood.ar.FaceModal

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mood.ar.MainActivity
import com.mood.ar.R

abstract class FaceModalDialog (
    private var context: Context,
    private var list: ArrayList<FaceModel>,
) : Dialog(context){

    // This method is called when the Dialog is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())

        // Use the LayoutInflater to inflate the
        // dialog_list layout file into a View object
        val view = LayoutInflater.from(context).inflate(R.layout.item_bottomsheet, null)

        // Set the dialog's content view
        // to the newly created View object
        setContentView(view)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Allow the dialog to be dismissed
        // by touching outside of it
        setCanceledOnTouchOutside(true)

        // Allow the dialog to be canceled
        // by pressing the back button
        setCancelable(true)
        // Set up the RecyclerView in the dialog
        setUpRecyclerView(view)

    }


    // This method sets up the RecyclerView in the dialog
    private fun setUpRecyclerView(view: View) {
        // Find the RecyclerView in the layout file and set
        // its layout manager to a LinearLayoutManager
        view.findViewById<RecyclerView>(R.id.rvList).layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
// LinearLayoutManager(context)
        // Create a new instance of the EmployeeAdapter
        // and set it as the RecyclerView's adapter
       val adapter = MediaItemAdapter(context, list)
        view.findViewById<RecyclerView>(R.id.rvList).adapter = adapter

        adapter.setOnClickListener(object :
            MediaItemAdapter.OnClickListener {
            override fun onClick(position: Int, model: FaceModel) {
                (context as MainActivity).updateTexture(position, model)
               dismiss()
            }
        })
    }
}