package us.maxpowa.ircclient.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.ircclouds.irc.api.domain.IRCServer
import com.ircclouds.irc.api.interfaces.IServerParameters
import com.samclarke.android.util.HashUtils

/**
 * Created by max on 9/5/17.
 */
object CredentialStore {
    internal var STORE_KEY = "us.maxpowa.ircclient.credentials"

    fun pushCredentials(context: Activity, host: String, nick: String, password: String) {
        val prefs = context.getSharedPreferences(STORE_KEY, Context.MODE_PRIVATE)
        val editor = prefs.edit() as SharedPreferences.Editor
        editor.putStringSet(HashUtils.sha1(host + nick), setOf(nick, password))
        editor.commit()
    }

    fun getAllCredentials(context: Activity): MutableMap<String, *>? {
        val prefs = context.getSharedPreferences(STORE_KEY, Context.MODE_PRIVATE)
        return prefs.all;
    }
}

