package com.example.msgapp

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

fun createImageUri(context: Context): Uri {
    // Cria um arquivo de imagem temporário no diretório de cache
    val imageFile = File(
        context.cacheDir,
        "images/${System.currentTimeMillis()}_camera_image.jpg"
    )
    // Garante que o diretório pai exista
    imageFile.parentFile?.mkdirs()

    // Retorna o URI para esse arquivo usando o FileProvider
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider", // Deve corresponder ao 'authorities' no Manifest
        imageFile
    )
}