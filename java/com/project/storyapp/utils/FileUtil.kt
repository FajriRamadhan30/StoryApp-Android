package com.project.storyapp.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object FileUtil {
    fun uriToFile(uri: Uri, context: Context): File {
        val contentResolver = context.contentResolver
        val tempFile = File.createTempFile("temp_image", ".jpg", context.cacheDir)
        tempFile.deleteOnExit()

        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val outputStream: FileOutputStream = FileOutputStream(tempFile)

        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        return tempFile
    }
}
