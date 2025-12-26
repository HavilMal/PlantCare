package com.plantCare.plantcare.utils

import android.content.Context
import android.net.Uri
import java.io.File
import kotlin.io.deleteRecursively
import android.util.Log
import android.webkit.MimeTypeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object FileUtil {
    fun makeDir(context: Context, relativePath: String, makeParentDirs: Boolean = true): File {
        val dir = File(context.filesDir, relativePath)
        if(makeParentDirs) dir.mkdirs()
        else dir.mkdir()
        return dir
    }
    fun deleteDir(context: Context, relativePath: String?)  : Boolean{
        if(relativePath == null) return false
        val dir = File(context.filesDir, relativePath)
        return if (dir.exists() && dir.isDirectory) {
            dir.deleteRecursively()
        } else {
            false
        }
    }

    fun getFiles(context: Context, relativePath: String?): List<File> {

        if(relativePath == null) return emptyList()
        val targetDir = File(context.filesDir, relativePath)
        return if (targetDir.exists() && targetDir.isDirectory) {
            targetDir.listFiles()?.filter { it.isFile } ?: emptyList()
        } else {
            emptyList()
        }
    }

    fun drawableAsFile(context: Context, drawableResId: Int, fileName: String): File {
        val inputStream = context.resources.openRawResource(drawableResId)
        val file = File(context.cacheDir, fileName)
        inputStream.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }
    suspend fun copyUriToFile(context: Context, uri: Uri): File = withContext(Dispatchers.IO) {
        val contentResolver = context.contentResolver
        val mimeType = contentResolver.getType(uri)
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "tmp"

        val tempFile = File(context.cacheDir, "picked_${System.currentTimeMillis()}.$extension")

        contentResolver.openInputStream(uri)?.use { inputStream ->
            tempFile.outputStream().use { output ->
                inputStream.copyTo(output)
            }
        }

        tempFile
    }

    fun delete(file: File){
        file.delete()
    }


    fun isVideo(file: File) : Boolean {
        return file.extension == "mp4"
    }
}
