package com.example.organizer.entities

import com.example.organizer.recycler.CaseItem
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import java.io.Serializable


data class Case(
    @SerializedName("id")
    internal var id: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("description")
    var description: String = "",
    @SerializedName("date_start")
    var dateStart: Long = 0,
    @SerializedName("date_finish")
    var dateFinish: Long = 0,
) : Serializable, CaseItem.HavingInterval {
    constructor(
        id: String,
        dateStart: DateTime,
        dateFinish: DateTime,
        name: String,
        description: String
    ) : this(
        id,
        name,
        description,
        dateStart.millis,
        dateFinish.millis
    )

    constructor(
        dateStart: DateTime,
        dateFinish: DateTime,
        name: String,
        description: String
    ) : this(
        "",
        name,
        description,
        dateStart.millis,
        dateFinish.millis
    )

    override fun getDateTimeStart(): DateTime = DateTime(dateStart)
    override fun getDateTimeFinish(): DateTime = DateTime(dateFinish)

}