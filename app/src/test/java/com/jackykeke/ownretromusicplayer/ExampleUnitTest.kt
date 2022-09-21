package com.jackykeke.ownretromusicplayer

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }


    @Test
    fun algo(){

        val  selectionValues = arrayOf("1","2","3","4")
        val  paths =  arrayListOf("a","b","c","d","e")

        var selectionValuesFinal = selectionValues
        if (selectionValuesFinal == null) {
            selectionValuesFinal = emptyArray()
        }
        val  newSelectionValues = Array(selectionValuesFinal.size+paths.size) {
            "n = $it"
        }
        System.arraycopy(selectionValuesFinal,0,newSelectionValues,0 ,selectionValuesFinal.size)

        for(i in selectionValuesFinal.size until newSelectionValues.size){
            newSelectionValues[i] = paths[i - selectionValuesFinal.size] + "%"
        }
        println(newSelectionValues)
    }
}