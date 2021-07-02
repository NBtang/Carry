package me.laotang.carry.demo.model.adapter

import com.squareup.moshi.*
import me.laotang.carry.demo.model.entity.UserType

class UserTypeAdapter : JsonAdapter<UserType>() {
    @FromJson
    override fun fromJson(reader: JsonReader): UserType {
        return when (reader.nextString()) {
            "User" -> {
                UserType.User
            }
            else -> {
                UserType.Admin
            }
        }
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: UserType?) {
        writer.value(value?.name)
    }
}