package jni

import jni.sys.jclass
import jni.sys.jfieldID
import jni.sys.jmethodID
import jni.sys.jobject

inline class JObject(val handle: jobject)
inline class JFieldID (val handle: jfieldID)
inline class JMethodID(val handle: jmethodID)
typealias JClass = JObject
typealias JString = JObject