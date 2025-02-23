package com.mrl.pixiv.common.mmkv

import com.ctrip.flight.mmkv.mmkvWithID
import kotlinx.serialization.KSerializer
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private val mmkvProtobuf = ProtoBuf {
    encodeDefaults = false
}

class MMKVSerializableProperty<V>(
    private val serializer: KSerializer<V>,
    private val defaultValue: V
) : ReadWriteProperty<MMKVApp, V> {
    override fun getValue(thisRef: MMKVApp, property: KProperty<*>): V =
        mmkvWithID(thisRef.id).takeByteArray(property.name)?.let {
            mmkvProtobuf.decodeFromByteArray(serializer, it)
        } ?: defaultValue

    override fun setValue(thisRef: MMKVApp, property: KProperty<*>, value: V) {
        if (value != null) {
            mmkvWithID(thisRef.id)[property.name] =
                mmkvProtobuf.encodeToByteArray(serializer, value)
        } else {
            mmkvWithID(thisRef.id).removeValueForKey(property.name)
        }
    }
}