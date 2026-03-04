package com.example.newsapp.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.newsapp.dashboard.data.model.GetNewsDetailsResponse
import com.google.gson.Gson

class PreferenceManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(VariableBag.PREF_NAME, Context.MODE_PRIVATE)

    private val gson = Gson()

    fun setNewsApiData(response: GetNewsDetailsResponse) {
        val json = gson.toJson(response)
        sharedPreferences.edit {
            putString(VariableBag.KEY_NEWS_DATA, json)
        }
    }

    fun getNewsApiData(): GetNewsDetailsResponse? {
        val json = sharedPreferences.getString(VariableBag.KEY_NEWS_DATA, null)
        return if (json != null) {
            gson.fromJson(json, GetNewsDetailsResponse::class.java)
        } else {
            null
        }
    }

    fun clear() {
        sharedPreferences.edit {
            clear()
        }
    }
}
