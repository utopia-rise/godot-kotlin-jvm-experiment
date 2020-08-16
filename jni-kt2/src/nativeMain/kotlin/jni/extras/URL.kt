package jni.extras

import jni.JniEnv


class URL {
    companion object {
        fun jclass(env: JniEnv) = env.findClass("java/net/URL")
    }
}