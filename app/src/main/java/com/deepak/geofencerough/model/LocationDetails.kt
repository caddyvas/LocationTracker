package com.deepak.geofencerough.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class LocationDetails : RealmObject {

    @PrimaryKey
    var id: ObjectId = ObjectId()
    var locationName: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var address: String = ""
    var city: String = ""
    //var items: RealmList<Items> = realmListOf()
}