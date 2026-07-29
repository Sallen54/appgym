package com.alejandro.appgym.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File
import java.time.LocalDate

/** Escribe el informe en la caché y abre la hoja de compartir de Android. */
object ReportSharing {

    fun share(context: Context, report: String, today: LocalDate = LocalDate.now()) {
        val dir = File(context.cacheDir, "informes").apply { mkdirs() }
        val stamp = String.format(ES, "%d-%02d-%02d", today.year, today.monthValue, today.dayOfMonth)
        val file = File(dir, "informe-appgym-$stamp.txt")
        file.writeText(report)

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Informe AppGym · ${today.numeric()}")
            putExtra(Intent.EXTRA_TEXT, report)
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(
            Intent.createChooser(intent, "Compartir informe").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }
}
