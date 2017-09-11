package us.maxpowa.ircclient.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.beust.klaxon.JSON
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.json
import java.util.*

/**
 * Created by max on 9/5/17.
 */
object CredentialStore {
    internal var STORE_KEY = "us.maxpowa.ircclient.connectioninfo"

    fun pushCredentials(context: Activity, host: String, nick: String, altNicks: List<String>, password: String) {
        val prefs = context.getSharedPreferences(STORE_KEY, Context.MODE_PRIVATE)
        val editor = prefs.edit() as SharedPreferences.Editor
        val inputData = json {
            obj("host" to host,
                "nick" to nick,
                "altNicks" to array(altNicks),
                "password" to password
            )
        }
        editor.putString(UUID.randomUUID().toString(), inputData.toJsonString())
        editor.commit()
    }

    fun getAllCredentials(context: Activity): List<Pair<String, JsonObject>> {
        val prefs = context.getSharedPreferences(STORE_KEY, Context.MODE_PRIVATE)
        return prefs.all.mapNotNull { entry ->
            if (entry.value !is String) {
                // bad, shouldn't ever happen
                return@mapNotNull null
            }
            return@mapNotNull entry.key to (Parser().parse(StringBuilder(entry.value as String)) as JsonObject)
        }
    }

    fun getCredentials(context: Activity, uuid: String): JsonObject {
        val prefs = context.getSharedPreferences(STORE_KEY, Context.MODE_PRIVATE)
        return Parser().parse(StringBuilder(prefs.getString(uuid, ""))) as JsonObject
    }

    fun removeCredentials(context: Activity, uuid: String) {
        val prefs = context.getSharedPreferences(STORE_KEY, Context.MODE_PRIVATE)
        val editor = prefs.edit() as SharedPreferences.Editor
        editor.remove(uuid)
        editor.commit()
    }
}

