package us.maxpowa.ircclient.utils

import android.app.Activity
import com.beust.klaxon.array
import com.beust.klaxon.string
import com.ircclouds.irc.api.IRCApi
import com.ircclouds.irc.api.domain.IRCServer
import com.ircclouds.irc.api.interfaces.Callback
import com.ircclouds.irc.api.interfaces.IServerParameters
import com.ircclouds.irc.api.negotiators.CompositeNegotiator
import com.ircclouds.irc.api.negotiators.capabilities.SimpleCapability
import com.ircclouds.irc.api.state.IIRCState
import java.util.*
import java.util.logging.Logger


/**
 * Created by max on 9/10/17.
 */
object ConnectionManager {

    private val LOG = Logger.getLogger("ConnectionManager")

    private var activeConnections: MutableMap<String, IRCApi> = mutableMapOf()

    fun connectToServer(context: Activity, uuid: String): IRCApi {
        if (activeConnections.containsKey(uuid)) {
            return activeConnections.get(uuid)!!
        }

        val credentials = CredentialStore.getCredentials(context, uuid)
        val nick = credentials.string("nick")!!
        val altNicks = credentials.array<String>("altNicks")?.toMutableList()!!
        if (altNicks.size < 1) {
            altNicks.add(nick + "_")
        }
        val host = credentials.string("host")!!

        val _api = IRCApi(true)
        activeConnections.put(uuid, _api)

        val caps = ArrayList<CompositeNegotiator.Capability>()
        caps.add(SimpleCapability("away-notify", true))
        caps.add(SimpleCapability("userhost-in-names", true))
        caps.add(SimpleCapability("multi-prefix", true))

        val handler = NegotiationHandler()
        val negotiator = CompositeNegotiator(caps, handler)

        _api.connect(getServerParams(nick, altNicks, "IRC Api", "ident", host, true), object : Callback<IIRCState> {
            override fun onSuccess(aIRCState: IIRCState) {
                println(aIRCState.serverOptions)
//                _api.register(object {
//                    @Handler
//                    fun onChannelMessage(aMsg: ChannelPrivMsg) {
//                        _api.message(aMsg.channelName, Arrays.toString(handler.ackedCaps.toTypedArray()))
//                    }
//                })
//                _api.joinChannel("#Inumuta")
//                _api.changeNick("TestBot123")
//                _api.message("#Inumuta", "It's working, it's working!")
//                _api.notice("maxpowa", "this message is purely a test")
//                _api.act("#Inumuta", "does stuff")
//                _api.message("#Inumuta", aIRCState.toString())
            }

            override fun onFailure(aErrorMessage: Exception) {
                TODO("Handle failure case cleanly ${aErrorMessage}")
            }
        }, negotiator)

        return _api
    }

    private fun getServerParams(aNickname: String, aAlternativeNicks: List<String>, aRealname: String, aIdent: String,
                                aServerName: String, aIsSSLServer: Boolean?): IServerParameters {
        return object : IServerParameters {
            override fun getServer(): IRCServer {
                return IRCServer(aServerName, aIsSSLServer!!)
            }

            override fun getRealname(): String {
                return aRealname
            }

            override fun getNickname(): String {
                return aNickname
            }

            override fun getIdent(): String {
                return aIdent
            }

            override fun getAlternativeNicknames(): List<String> {
                return aAlternativeNicks
            }
        }
    }

    private class NegotiationHandler : CompositeNegotiator.Host {
        internal var acknowledged: MutableList<String> = ArrayList()
        internal var rejected: MutableList<String> = ArrayList()

        override fun acknowledge(cap: CompositeNegotiator.Capability) {
            LOG.info("Capability " + cap.id + " acknowledged.")
            acknowledged.add(cap.id)
            if (rejected.contains(cap.id)) {
                rejected.remove(cap.id)
            }
        }

        override fun reject(cap: CompositeNegotiator.Capability) {
            LOG.info("Capability " + cap.id + " rejected.")
            rejected.add(cap.id)
            if (acknowledged.contains(cap.id)) {
                acknowledged.remove(cap.id)
            }
        }

        val ackedCaps: List<String>
            get() = this.acknowledged
        val rejectedCaps: List<String>
            get() = this.rejected
    }
}