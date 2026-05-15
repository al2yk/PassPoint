package com.example.passpoint.domain.utils

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object CertificatePdfGenerator {
    // BrandColor – оранжевый
    private val brandColor = 0xFFFF5A00.toInt()
    private val lightBrand = 0xFFFFF2EB.toInt()
    private val textColor  = 0xFFFF5A00.toInt()
    private val white      = 0xFFFFFFFF.toInt()
    private val darkBlue   = 0xFFFF5A00.toInt()

    fun generate(context: Context, fullName: String, courseName: String, date: String): File {
        val pdf = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(1169, 827, 1).create() // A4 landscape
        val page = pdf.startPage(pageInfo)
        val canvas = page.canvas

        // === Фон ===
        canvas.drawColor(lightBrand)

        // === Белая карточка по центру ===
        val cardLeft = 80f
        val cardTop = 60f
        val cardRight = 1169f - 80f
        val cardBottom = 827f - 60f
        val cardRect = RectF(cardLeft, cardTop, cardRight, cardBottom)

        // Тень (имитация)
        val shadowPaint = Paint().apply {
            color = Color.argb(30, 0, 0, 0)
            isAntiAlias = true
        }
        canvas.drawRoundRect(RectF(cardLeft + 8f, cardTop + 8f, cardRight + 8f, cardBottom + 8f), 30f, 30f, shadowPaint)

        // Основная карточка
        val cardPaint = Paint().apply {
            color = white
            isAntiAlias = true
        }
        canvas.drawRoundRect(cardRect, 30f, 30f, cardPaint)

        // === Рамка ===
        val borderPaint = Paint().apply {
            color = brandColor
            style = Paint.Style.STROKE
            strokeWidth = 6f
            isAntiAlias = true
        }
        canvas.drawRoundRect(cardRect, 30f, 30f, borderPaint)

        // === Заголовок "PassPoint" ===
        val titlePaint = Paint().apply {
            textSize = 56f
            color = brandColor
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        canvas.drawText("PassPoint", 1169f / 2f, cardTop + 85f, titlePaint)

        // === Надпись "Сертификат о прохождении курса" ===
        val subtitlePaint = Paint().apply {
            textSize = 28f
            color = darkBlue
            isFakeBoldText = false
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("Сертификат о прохождении курса", 1169f / 2f, cardTop + 135f, subtitlePaint)

        // === Иконка (звезда или круг) ===
        val iconPaint = Paint().apply {
            color = brandColor
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawCircle(1169f / 2f, cardTop + 180f, 12f, iconPaint)

        // === Имя участника ===
        val namePaint = Paint().apply {
            textSize = 42f
            color = brandColor
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            typeface = Typeface.DEFAULT_BOLD
        }
        val maxNameWidth = cardRight - cardLeft - 160f
        var nameTextSize = 42f
        namePaint.textSize = nameTextSize
        while (namePaint.measureText(fullName) > maxNameWidth && nameTextSize > 24f) {
            nameTextSize -= 2f
            namePaint.textSize = nameTextSize
        }
        canvas.drawText(fullName, 1169f / 2f, cardTop + 240f, namePaint)

        // === Разделительная линия ===
        val linePaint = Paint().apply {
            color = brandColor
            strokeWidth = 2f
            isAntiAlias = true
        }
        canvas.drawLine(cardLeft + 200f, cardTop + 265f, cardRight - 200f, cardTop + 265f, linePaint)

        // === "успешно прошёл(шла) курс" ===
        val passedPaint = Paint().apply {
            textSize = 26f
            color = textColor
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("успешно прошёл(шла) курс", 1169f / 2f, cardTop + 305f, passedPaint)

        // === Название курса ===
        val coursePaint = Paint().apply {
            textSize = 36f
            color = darkBlue
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            typeface = Typeface.DEFAULT_BOLD
        }
        val courseLines = wrapText(courseName, coursePaint, maxNameWidth)
        var yOffset = cardTop + 355f
        for (line in courseLines) {
            canvas.drawText(line, 1169f / 2f, yOffset, coursePaint)
            yOffset += 45f
        }

        // === Дата ===
        val datePaint = Paint().apply {
            textSize = 24f
            color = textColor
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        val formattedDate = try {
            LocalDate.parse(date).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        } catch (e: Exception) { date }
        canvas.drawText("Дата проведения: $formattedDate", 1169f / 2f, yOffset + 40f, datePaint)

        // === Подвал: "Куратор" и "Организация" ===
        val footerPaint = Paint().apply {
            textSize = 20f
            color = textColor
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("PassPoint © ${java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)}", 1169f / 2f, cardBottom - 15f, footerPaint)

        pdf.finishPage(page)
        val file = File(context.cacheDir, "certificate_${System.currentTimeMillis()}.pdf")
        pdf.writeTo(FileOutputStream(file))
        pdf.close()
        return file
    }

    private fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = StringBuilder()
        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            if (paint.measureText(testLine) <= maxWidth) {
                currentLine.append(if (currentLine.isEmpty()) word else " $word")
            } else {
                if (currentLine.isNotEmpty()) lines.add(currentLine.toString())
                currentLine = StringBuilder(word)
            }
        }
        if (currentLine.isNotEmpty()) lines.add(currentLine.toString())
        return lines
    }
}