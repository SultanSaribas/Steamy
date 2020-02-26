package com.st.steamy.data.permanent

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import androidx.core.graphics.createBitmap
import com.st.steamy.data.myContext
import com.st.steamy.data.myResources
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import android.R



class AddPictureToDevice(val path: String, val drawable: Drawable) : AsyncTask<Any, Any, Int>() {
    override fun doInBackground(vararg params: Any?): Int {
        if (drawable is BitmapDrawable) {

            var fos: FileOutputStream? = null
            try {
                fos = myContext!!.openFileOutput(path+".png", Context.MODE_PRIVATE)
                drawable.bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
                fos.close()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    fos?.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
        return 0
    }
}

fun addPictureToDevice(path: String, drawable: Drawable) {
    //AddPictureToDevice(path, drawable).execute()
}
