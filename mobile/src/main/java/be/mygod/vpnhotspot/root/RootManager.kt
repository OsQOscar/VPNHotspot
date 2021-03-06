package be.mygod.vpnhotspot.root

import android.os.Parcelable
import android.util.Log
import be.mygod.librootkotlinx.RootCommandNoResult
import be.mygod.librootkotlinx.RootServer
import be.mygod.librootkotlinx.RootSession
import be.mygod.vpnhotspot.App.Companion.app
import be.mygod.vpnhotspot.BuildConfig
import be.mygod.vpnhotspot.util.Services
import eu.chainfire.librootjava.RootJava
import kotlinx.android.parcel.Parcelize
import timber.log.Timber

object RootManager : RootSession() {
    @Parcelize
    class RootInit : RootCommandNoResult {
        override suspend fun execute(): Parcelable? {
            RootServer.DEBUG = BuildConfig.DEBUG
            Timber.plant(object : Timber.DebugTree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    if (t == null) {
                        Log.println(priority, tag, message)
                    } else {
                        Log.println(priority, tag, message)
                        Log.d(tag, message, t)
                    }
                }
            })
            Services.init(RootJava.getSystemContext())
            return null
        }
    }

    override fun createServer() = RootServer { Timber.w(it) }
    override suspend fun initServer(server: RootServer) {
        RootServer.DEBUG = BuildConfig.DEBUG
        try {
            server.init(app)
        } finally {
            server.readUnexpectedStderr()?.let { Timber.e(it) }
        }
        server.execute(RootInit())
    }
}
