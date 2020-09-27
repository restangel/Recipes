package com.kode.test

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_photo.*


class PhotoActivity : AppCompatActivity() {
    var url: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val url = intent.getStringExtra("url") ?: throw Exception("No photo url in extras")
        this.url = url
        Picasso.with(this)
            .load(url)
            .placeholder(android.R.drawable.ic_menu_report_image)
            .error(R.drawable.error)
            .into(photo)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.photo_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.download -> {
                download()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun download() {

        val dialog = AlertDialog.Builder(this)
            .setTitle("Where do you want to store this photo?")
            .setItems(arrayOf("Download", "Pictures")) { _, which ->
                val target = if (which == 0)
                    Environment.DIRECTORY_DOWNLOADS
                else
                    Environment.DIRECTORY_PICTURES
                val request = DownloadManager.Request(Uri.parse(url))
                request.setTitle("Downloading photo")
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                request.setDestinationInExternalPublicDir(
                    target,
                    url?.split("/")?.last()
                )
                val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                manager.enqueue(request)
            }
        dialog.create().show()
    }
}