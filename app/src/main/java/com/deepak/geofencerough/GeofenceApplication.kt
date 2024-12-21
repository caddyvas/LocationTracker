package com.deepak.geofencerough

import android.app.Application
import com.deepak.geofencerough.model.Items
import com.deepak.geofencerough.model.LocationDetails
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

class GeofenceApplication : Application() {

    companion object {
        lateinit var realm: Realm
    }

    override fun onCreate() {
        super.onCreate()
        val realmConfiguration: RealmConfiguration = RealmConfiguration.create(
            schema = setOf(
                Items::class,
                LocationDetails::class,
            )
        )
        realm = Realm.open(configuration = realmConfiguration)

    }
}