package com.tichuclub.tichuclub

import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import java.io.File
import com.squareup.moshi.JsonAdapter
import java.util.*

class DialogOverlord(private val dialogFile: String) {

    private fun readDialogFromFile() : Dialogs {

        val dialogText = File(ClassLoader.getSystemResource("dialog.json").file).readText()

        val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

        val jsonAdapter : JsonAdapter<Dialogs> = moshi.adapter(Dialogs::class.java)
        val dialogs : Dialogs = jsonAdapter.fromJson(dialogText)!!
        //TODO: Research if this nullable is OK

        return dialogs

    }

    fun getRandomDialogForEvent(event: TichuEvent) : Dialog? {

        var ret: Dialog? = null

        val dialogs : Dialogs = readDialogFromFile()

        val possibleSelections = ArrayList<Dialog>()

        for(dialog in dialogs.dialogs) {
            if(dialog.type == event.name) {
                possibleSelections.add(dialog)
            }
        }

        if(possibleSelections.count() > 0) {
            val randomObject = Random()
            val randomNumber = randomObject.nextInt(possibleSelections.size)
            ret = possibleSelections[randomNumber]
        }

        return ret

    }

}