package ru.smasung.itacademy.mdev.defaultcameraappexample

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private var button: Button? = null
    private var imageView: ImageView? = null
    private var imageFilePath = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.button)
        imageView = findViewById(R.id.image)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_PERMISSION)
        }
        button?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                openCameraIntent()
            }
        })
    }

    private fun openCameraIntent() {
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        Log.d("MAIN_LOG", pictureIntent.resolveActivity(packageManager).toString())
        if (pictureIntent.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            photoFile = try {
                createImageFile()
            } catch (e: IOException) {
                e.printStackTrace()
                return
            }
            val photoUri: Uri = FileProvider.getUriForFile(this, "$packageName.provider", photoFile)
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            Log.d("MAIN_LOG", photoUri.toString())
            startActivityForResult(pictureIntent, REQUEST_IMAGE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION && grantResults.size > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Thanks for granting Permission", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                imageView?.setImageURI(Uri.parse(imageFilePath))
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "You cancelled the operation", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image: File = File.createTempFile(imageFileName, ".jpg", storageDir)
        imageFilePath = image.getAbsolutePath()
        //Log.d("MAIN_LOG", imageFileName.toString());
        return image
    }

    companion object {
        const val REQUEST_IMAGE = 100
        const val REQUEST_PERMISSION = 200
    }
}