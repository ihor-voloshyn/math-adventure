package com.ihorvoloshyn.mathadventure

import android.content.Context

class PrototypeProgressStore(context: Context) {
    private val prefs = context.getSharedPreferences("math_adventure_progress", Context.MODE_PRIVATE)

    var totalCorrect: Int
        get() = prefs.getInt("total_correct", 0)
        private set(value) = prefs.edit().putInt("total_correct", value).apply()

    var totalIncorrect: Int
        get() = prefs.getInt("total_incorrect", 0)
        private set(value) = prefs.edit().putInt("total_incorrect", value).apply()

    fun recordCorrect() { totalCorrect = totalCorrect + 1 }
    fun recordIncorrect() { totalIncorrect = totalIncorrect + 1 }
}
