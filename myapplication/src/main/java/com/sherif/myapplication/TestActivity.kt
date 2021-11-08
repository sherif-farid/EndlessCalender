package com.sherif.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.sherif.myapplication.databinding.ActivityMainBinding
import com.sherif.mycalender.OnDateSelected

class TestActivity : AppCompatActivity(), OnDateSelected {
    private var binding: ActivityMainBinding? = null
    private var availableList:ArrayList<String>? = null
    private var busyList:ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.calenderRv?.setOnDateSelected(this)
    }

    override fun onStart() {
        super.onStart()
        binding?.calenderRv?.post {
            binding?.calenderRv?.initialize(
                bookedList = arrayListOf("2021-11-24", "2021-11-25" , "2023-11-25"),
                busyList = arrayListOf("2021-11-30", "2021-12-01", "2021-12-02","2023-11-26")
            )
        }
    }

    override fun onSelected(availableList: ArrayList<String>?, busyList: ArrayList<String>?) {
        this.availableList = availableList
        this.busyList = busyList
//        Log.v("MainActivityTag", "availableList : $availableList")
//        Log.v("MainActivityTag", "busyList : $busyList")
    }

    override fun onBookedDatesSelected() {
//        Log.v("MainActivityTag", "onBookedDatesSelected")
    }

    override fun onBackPressed() {
        val availableSize =availableList?.size?:0
        val busySize = busyList?.size?:0
        if (availableSize > 0 || busySize > 0){
            binding?.calenderRv?.clearAll()
        }else {
            super.onBackPressed()
        }
    }
}