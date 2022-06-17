package com.example.labfinal

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var buttonPlay : Button
    private lateinit var buttonStop : Button
    private lateinit var buttonPrev : Button
    private lateinit var buttonNext : Button
    private lateinit var label : TextView

    private var paused = true
    private lateinit var mediaPlayer : MediaPlayer
    private var iterator = 0
    
    private var files : MutableList<DocumentFile> = mutableListOf()

    companion object{
        var OPEN_DIRECTORY_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startActivityForResult(intent, OPEN_DIRECTORY_REQUEST_CODE)

        buttonPlay = findViewById(R.id.buttonPlay)
        buttonStop = findViewById(R.id.buttonStop)
        buttonNext = findViewById(R.id.buttonNext)
        buttonPrev = findViewById(R.id.buttonPrev)

        setOnClickListeners(this)
    }

    private fun setOnClickListeners(context: Context) {
        buttonPlay.setOnClickListener {
            if(paused){
                paused = false
                mediaPlayer.start()
                Toast.makeText(context, "Reproduciendo...", Toast.LENGTH_SHORT).show()
            }else{
                paused = true
                mediaPlayer.pause()
                Toast.makeText(context, "Pausando...", Toast.LENGTH_SHORT).show()
            }
        }

        buttonStop.setOnClickListener {
            iterator = 0
            paused = true
            mediaPlayer.stop()
            Toast.makeText(context, "Parando...", Toast.LENGTH_SHORT).show()
            mediaPlayer = MediaPlayer.create(this,files[iterator].uri)
        }

        buttonNext.setOnClickListener{
            if(iterator+1 > files.size-1){
                iterator = 0
                mediaPlayer.stop()
                mediaPlayer = MediaPlayer.create(context,files[iterator].uri)
                mediaPlayer.start()
                label.text = files[iterator].name
            }else{
                iterator++
                mediaPlayer.stop()
                mediaPlayer = MediaPlayer.create(context,files[iterator].uri)
                mediaPlayer.start()
                label.text = files[iterator].name
            }
        }

        buttonPrev.setOnClickListener{
            if(iterator-1 < 0){
                iterator = files.size - 1
                mediaPlayer.stop()
                mediaPlayer = MediaPlayer.create(context,files[iterator].uri)
                mediaPlayer.start()
                label.text = files[iterator].name
            }else{
                mediaPlayer.stop()
                iterator--
                mediaPlayer = MediaPlayer.create(context,files[iterator].uri)
                mediaPlayer.start()
                label.text = files[iterator].name
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == OPEN_DIRECTORY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                
                var directoryUri = data?.data ?:return
                val rootTree = DocumentFile.fromTreeUri(this,directoryUri )
                
                for(file in rootTree!!.listFiles()){
                    try {
                        file.name?.let { Log.e("Archivo", it) }
                        files.add(file)
                    }catch (e: Exception){
                        Log.e("Error", "No pude ejecutar el archivo" + file.uri)
                    }
                }
                mediaPlayer = MediaPlayer.create(this,files[iterator].uri)
                label = findViewById(R.id.textView)
                label.text = files[iterator].name
            }
        }
    }
}