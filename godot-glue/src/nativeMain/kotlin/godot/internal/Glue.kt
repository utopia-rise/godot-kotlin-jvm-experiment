package godot.internal

import jni.*

@ThreadLocal
object Glue {
    private val jvm: JavaVM by lazy {
        val args = JavaVMInitArgs.create {
            version = JNIVersion.JNI_1_8
            option("-Xcheck:jni")
            option("-verbose:jni")
            option("-verbose:gc")
            option("-Xlog:gc")
        }
        (JavaVM.getExisting(args.version) ?: JavaVM.create(args)).also {
            it.detach()
        }
    }

    fun loadBinding(libraryPath: String) {
        val workingDir = libraryPath.replaceAfterLast("/", "")
        val bootstrapJar = "$workingDir/build/libs/bootstrap.jar"
        println("Loading binding, workingDir = $workingDir")
        jvm.attach {
            val bootstrapJarFile = createFile(bootstrapJar)
            val bootstrapJarFileURL = fileToURL(bootstrapJarFile)
            println("$bootstrapJarFileURL")
        }

    }

    fun unloadBinding() {
        println("Unloading binding: $jvm")
    }

    fun destroy() {
        jvm.destroy()
    }

    private fun JNIEnv.createURLClassLoader(): JObject {
        val jclass = findClass("java/net/URLClassLoader")
        requireNotNull(jclass) { "Could not load class java.net.URLClassLoader" }
        TODO()
    }

    private fun JNIEnv.createFile(bootstrapJar: String): JObject {
        val jclass = findClass("java/io/File")
        requireNotNull(jclass) { "Could not load class java.io.File" }
        val ctor = jclass.getConstructorMethodID("(Ljava/lang/String;)V")
        requireNotNull(ctor) { "Could find <init>(Ljava/lang/String;)V in java.io.File" }
        return jclass.newInstance(ctor, JValue.Object(newStringUTF(bootstrapJar).handle))
    }

    private fun JNIEnv.fileToURL(file: JObject): JObject {
        val jclass = findClass("java/io/File")
        requireNotNull(jclass) { "Could not load class java.io.File" }
        val method = jclass.getMethodID("toURL", "()Ljava/net/URL;")
        requireNotNull(method) { "Failed to find File.toURL" }
        val url = method.callObjectMethod(file)
        return requireNotNull(url) { "Failed to convert file into a URL" }
    }

}