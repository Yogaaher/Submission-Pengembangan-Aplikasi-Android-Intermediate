package com.dicoding.picodiploma.loginwithanimation.utils

import id.zelory.compressor.Compressor
import android.content.Context
import java.io.File

suspend fun reduceFileImage(imageFile: File, context: Context): File {
    return Compressor.compress(context, imageFile)
}
