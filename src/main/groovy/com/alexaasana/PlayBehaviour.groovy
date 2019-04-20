package com.alexaasana

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum PlayBehaviour {
    ENQUEUE("ENQUEUE"),
    REPLACE_ALL("REPLACE_ALL"),
    REPLACE_ENQUEUED("REPLACE_ENQUEUED")

    private Object value

    private PlayBehavior(Object value) {
        this.value = value
    }

    @JsonValue
    public Object getValue() {
        return this.value
    }

    public String toString() {
        return String.valueOf(this.value)
    }

    @JsonCreator
    public static PlayBehaviour fromValue(String text) {
        PlayBehaviour[] var1 = values()
        int var2 = var1.length

        for (int var3 = 0; var3 < var2; ++var3) {
            PlayBehaviour b = var1[var3]
            if (String.valueOf(b.value).equals(text)) {
                return b
            }
        }

        return null
    }
}
