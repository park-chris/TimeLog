package com.crystal.timeisgold.util

import android.content.Context
import org.json.JSONArray

class ContextUtil {
    companion object {
        private val prefName = "timeIsGoldPref"
        private const val TYPE_TAG = "type_tag"
        fun setTypeListPref(context: Context,list: ArrayList<String>) {

            val jsonArr = JSONArray()
            for (i in list) {
                jsonArr.put(i)
            }

            val result = jsonArr.toString()

            val pref = context.getSharedPreferences (prefName, Context.MODE_PRIVATE) ?: return
            with(pref.edit()) {
                putString(TYPE_TAG, result)
                commit()
            }
        }

        fun getTypeListPref(context: Context): ArrayList<String> {
            val pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)

            val getString = pref.getString(TYPE_TAG, "")
            val resultArr: ArrayList<String> = ArrayList()

            if (getString == "") {
                resultArr.add("default")
                return resultArr
            }

            val arrJson = JSONArray(getString)

            for (i in 0 until arrJson.length()) {
                resultArr.add(arrJson.optString(i))
            }

            return resultArr
        }
    }
}