package com.surya.draganddrop

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.surya.draganddrop.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    var windowwidth = 0
    var windowheight = 0
    private var layoutParams: RelativeLayout.LayoutParams? = null
    private var imagesEncodedList: ArrayList<Uri>? = arrayListOf()
    private lateinit var dataBind: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBind = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        windowheight = displayMetrics.heightPixels
        windowwidth = displayMetrics.widthPixels


        imagesEncodedList!!.clear()

        dataBind.addBtn.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            resultLauncher.launch(intent)
        }


    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data

                if (data!!.clipData != null) {
                    val count: Int = data.clipData!!
                        .itemCount //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                    for (i in 0 until count) {
                        val imageUri: Uri = data.clipData!!.getItemAt(i).uri
                        imagesEncodedList!!.add(imageUri)
                       updateUI(imageUri)
                        Log.d("SURYA", " Create$imageUri")
                        //do something with the image (save it to some directory or whatever you need to do with it here)
                    }
                } else if (data != null) {
                    val imagePath: Uri? = data.data!!

                    if (imagePath != null) {
                        imagesEncodedList!!.add(imagePath)
                        updateUI(imagePath)
                    }
                    Log.d("SURYA", " Create$imagePath")
                    //do something with the image (save it to some directory or whatever you need to do with it here)
                }

            }else {
                Toast.makeText(
                    this, "You haven't picked Image",
                    Toast.LENGTH_LONG
                ).show()
            }

        }


    private fun updateUI(uri: Uri){
        val imageView = ImageView(this@MainActivity)
        imageView.setImageURI(uri)
        addView(imageView)

        imageView.setOnTouchListener(OnTouchListener { v, event ->
            layoutParams = imageView.getLayoutParams() as RelativeLayout.LayoutParams
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {}
                MotionEvent.ACTION_MOVE -> {
                    var x_cord = event.rawX.toInt()
                    var y_cord = event.rawY.toInt()
                    if (x_cord > windowwidth) {
                        x_cord = windowwidth
                    }
                    if (y_cord > windowheight) {
                        y_cord = windowheight
                    }
                    layoutParams!!.leftMargin = x_cord
                    layoutParams!!.topMargin = y_cord
                    imageView.setLayoutParams(layoutParams)
                }
                else -> {}
            }
            true
        })
    }

    private fun addView(imageView: ImageView) {
        val params = RelativeLayout.LayoutParams(400, 400)

        // setting the margin in linearlayout
        params.setMargins(0, 10, 0, 10)
        imageView.layoutParams = params

        // adding the image in layout
        dataBind.layout.addView(imageView)
    }

}