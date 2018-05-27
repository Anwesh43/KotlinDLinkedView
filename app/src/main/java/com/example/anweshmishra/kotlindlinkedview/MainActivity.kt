package com.example.anweshmishra.kotlindlinkedview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.dlinkedview.DLinkedView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DLinkedView.create(this)
    }
}
