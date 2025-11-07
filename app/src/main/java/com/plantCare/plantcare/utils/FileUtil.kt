package com.plantCare.plantcare.utils

import android.content.Context
import java.io.File

object FileUtil {
    fun makeDir(context: Context, relativePath: String, makeParentDirs: Boolean = true): File {
        val dir = File(context.filesDir, relativePath)
        if(makeParentDirs) dir.mkdirs()
        else dir.mkdir()
        return dir
    }
}
