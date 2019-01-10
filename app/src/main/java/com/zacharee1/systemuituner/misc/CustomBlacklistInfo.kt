package com.zacharee1.systemuituner.misc

data class CustomBlacklistInfo(
        var key: String,
        var name: String
) {
    override fun equals(other: Any?): Boolean {
        return other is CustomBlacklistInfo &&
                key == other.key &&
                name == other.name
    }

    override fun hashCode(): Int {
        return key.hashCode() + name.hashCode()
    }
}