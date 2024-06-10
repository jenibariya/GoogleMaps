package com.example.myapplication.util

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity

class PrefData(context: Context) {

    private val prefsName = "MyPrefs"
    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    private val KEY_EXAMPLE_BOOLEAN = "update_boolean"

    private val KEY_SELECTED_RADIO_BUTTON_ID = "selectedRadioButtonId"


    fun saveBoolean(key: String, value: Boolean) {
        sharedPref.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPref.getBoolean(key, defaultValue)
    }

    fun saveExampleBoolean(value: Boolean) {
        saveBoolean(KEY_EXAMPLE_BOOLEAN, value)
    }

    fun getExampleBoolean(): Boolean {
        return getBoolean(KEY_EXAMPLE_BOOLEAN)
    }

    fun saveSelectedRadioButtonId(radioButtonId: Int) {
        sharedPref.edit().putInt(KEY_SELECTED_RADIO_BUTTON_ID, radioButtonId).apply()
    }

    fun getSelectedRadioButtonId(): Int {
        return sharedPref.getInt(KEY_SELECTED_RADIO_BUTTON_ID, -1)
    }
}