package com.plantCare.plantcare.utils

import android.content.Context
import java.io.File
import kotlin.io.deleteRecursively
object FileUtil {
    fun makeDir(context: Context, relativePath: String, makeParentDirs: Boolean = true): File {
        val dir = File(context.filesDir, relativePath)
        if(makeParentDirs) dir.mkdirs()
        else dir.mkdir()
        return dir
    }
    fun deleteDir(context: Context, relativePath: String) {
        File(context.filesDir, relativePath).deleteRecursively()
    }
//    fun deleteDir(dir: File){
//        if(dir.exists() && dir.isDirectory) {
//            dir.listFiles()?.forEach { file ->
//                if (file.isDirectory) {
//                    deleteDir(file)
//                } else {
//                    file.delete()
//                }
//            }
//            dir.delete()
//        }
//    }
}
