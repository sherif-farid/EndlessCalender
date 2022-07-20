package com.sherif.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sherif.myapplication.databinding.ActivityMainBinding
import com.sherif.mycalender.GathernReservationModel
import com.sherif.mycalender.OnDateSelected

class TestActivity : AppCompatActivity(), OnDateSelected {
    private var binding: ActivityMainBinding? = null
    private var availableList: ArrayList<String>? = null
    private var busyList: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.calenderRv?.setOnDateSelected(this)
    }

    override fun onStart() {
        super.onStart()
        val bookedList = ArrayList<GathernReservationModel?>()
        bookedList.add(
            GathernReservationModel(
                checkInDate = "2022-07-01",
                checkoutDate = "2022-07-01",
                clientName = "تامر"
            )
        )
        bookedList.add(
            GathernReservationModel(
                checkInDate = "2022-07-05",
                checkoutDate = "2022-07-05",
                clientName = "احمد"
            )
        )
        bookedList.add(
            GathernReservationModel(
                checkInDate = "2022-07-07",
                checkoutDate = "2022-07-07",
                clientName = "محمد"
            )
        )
        bookedList.add(
            GathernReservationModel(
                checkInDate = "2022-07-08",
                checkoutDate = "2022-07-08",
                clientName = "محمد"
            )
        )
        bookedList.add(
            GathernReservationModel(
                checkInDate = "2022-07-10",
                checkoutDate = "2022-07-12",
                clientName = "محمد"
            )
        )
        bookedList.add(
            GathernReservationModel(
                checkInDate = "2022-08-02",
                checkoutDate = "2022-08-09",
                clientName = "هاني"
            )
        )
        bookedList.add(
            GathernReservationModel(
                checkInDate = "2022-08-12",
                checkoutDate = "2022-08-13",
                clientName = "عبدالله احمد محمد"
            )
        )
        binding?.calenderRv?.post {
            binding?.calenderRv?.initialize(
                bookedList = bookedList,
                busyList = arrayListOf("2022-07-30", "2022-07-26", "2022-07-21")
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
        val availableSize = availableList?.size ?: 0
        val busySize = busyList?.size ?: 0
        if (availableSize > 0 || busySize > 0) {
            binding?.calenderRv?.clearAll()
        } else {
            super.onBackPressed()
        }
    }
}