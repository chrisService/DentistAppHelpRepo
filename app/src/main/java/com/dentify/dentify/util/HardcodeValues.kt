package com.dentify.dentify.util

import android.content.Context
import com.dentify.dentify.R
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream


object HardcodeValues {


    fun getTextPathSV(context: Context): String{

        val textFile = context.getResources().openRawResource(R.raw.tnc_sv)
        val tcText = readTextFile(textFile)

        return tcText
    }

    fun getTextPathEN(context: Context): String{

        val textFile = context.getResources().openRawResource(R.raw.tnc_en)
        val tcText = readTextFile(textFile)

        return tcText
    }


    fun getTextPathDeactivationSV(context: Context): String{

        val textFile = context.getResources().openRawResource(R.raw.deactivate_info_sv)
        val tcText = readTextFile(textFile)

        return tcText
    }

    fun getTextPathDeactivationEN(context: Context): String{

        val textFile = context.getResources().openRawResource(R.raw.deactivate_info_en)
        val tcText = readTextFile(textFile)

        return tcText
    }

    fun getTextPathDeactivationConfirmSV(): String{
        return "Skriv in **DEACTIVATE** nedan för att bekräfta din begäran om inaktivering"
    }

    fun getTextPathDeactivationConfirmEN(): String{
        return "Type in **DEACTIVATE** below to confirm your deactivation request"
    }



    fun readTextFile(inputStream: InputStream): String {
        val outputStream = ByteArrayOutputStream()
        val buf = ByteArray(1024)
        var len: Int
        try {
            while (inputStream.read(buf).also { len = it } != -1) {
                outputStream.write(buf, 0, len)
            }
            outputStream.close()
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return outputStream.toString()
    }

}