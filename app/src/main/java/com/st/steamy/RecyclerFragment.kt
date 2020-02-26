package com.st.steamy


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerFragment(val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recycler, container, false)
        // TODO get the views and initialize
        val recv = view.findViewById<RecyclerView>(R.id.recyclerViewFragment)
        recv.adapter = adapter
        return view

    }


}
