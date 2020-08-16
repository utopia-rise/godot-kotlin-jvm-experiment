package jni.extras

import jni.JObject
import jni.JniEnv
import jni.sys.jobject


fun JniEnv.newFile(path: String): File {
    return File(this, newInstance("java/io/File", "(Ljava/lang/String;)V", newString(path)).handle)
}

class File(env: JniEnv, handle: jobject) : JObject(env, handle) {
    fun toURL(): JObject {
        val fileClass = env.findClass("java/io/File")
        val toURLMethod = fileClass.getMethodID("toURL", "()Ljava/net/URL;")
        return checkNotNull(callObjectMethod(toURLMethod)) { "Failed to convert file into a URL" }
    }
}