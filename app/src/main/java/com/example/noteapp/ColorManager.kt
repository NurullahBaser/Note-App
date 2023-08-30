package com.example.noteapp

import java.util.LinkedList
import java.util.Queue
import kotlin.random.Random

object ColorManager {

    private val usedQueue: Queue<Int> = LinkedList()

    private val usableList = mutableListOf(R.color.note_color_1,
        R.color.note_color_2,
        R.color.note_color_3,
        R.color.note_color_4,
        R.color.note_color_5,
        R.color.note_color_6,
        R.color.note_color_7,
        R.color.note_color_8,
        R.color.note_color_9
    )

    fun randomColor() : Int{
        val seed = System.currentTimeMillis().toInt()
        val randomIndex = Random(seed).nextInt(usableList.size)

        if (usableList.size < 4) {
            usableList.add(usedQueue.poll()!!)
        }
        usedQueue.add(usableList[randomIndex])
        return usableList.removeAt(randomIndex)
    }
}
