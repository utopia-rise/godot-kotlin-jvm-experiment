package godot.internal

import jni.*

@ThreadLocal
object Glue {
    private val jvm: JavaVM by lazy {
        val args = JavaVMInitArgs.create {
            version = JNIVersion.JNI_1_8
//            option("-Xcheck:jni")
//            option("-verbose:jni")
            option("-Xdebug")
            option("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005")
        }
        JavaVM.getExisting(args.version) ?: JavaVM.create(args)
    }

    fun loadBinding(libraryPath: String) {
        val workingDir = libraryPath.replaceAfterLast("/", "")
        val bootstrapJar = "$workingDir/build/libs/bootstrap.jar"
        println("Loading binding, workingDir = $workingDir")
        jvm.attach {
            val bootstrapJarFile = createFile(bootstrapJar)
            val classLoaderUrls = fileToURLArray(bootstrapJarFile)
            val classloader = createURLClassLoader(classLoaderUrls)
            setContextClassLoader(classloader)
            callGlue(classloader)
        }

    }

    fun unloadBinding() {
        println("Unloading binding: $jvm")
    }

    fun destroy() {
        jvm.destroy()
    }

    private fun JNIEnv.createURLClassLoader(urls: JObject): JObject {
        val jclass = findClass("java/net/URLClassLoader")
        val ctor = jclass.getConstructorMethodID("([Ljava/net/URL;)V")
        return jclass.newInstance(ctor, JValue.Object(urls.handle))
    }

    private fun JNIEnv.createFile(bootstrapJar: String): JObject {
        val jclass = findClass("java/io/File")
        val ctor = jclass.getConstructorMethodID("(Ljava/lang/String;)V")
        return jclass.newInstance(ctor, JValue.Object(newStringUTF(bootstrapJar).handle))
    }

    private fun JNIEnv.fileToURLArray(file: JObject): JArray {
        val fileClass = findClass("java/io/File")
        val urlClass = findClass("java/net/URL")
        val method = fileClass.getMethodID("toURL", "()Ljava/net/URL;")
        val url = method.callObjectMethod(file)
        requireNotNull(url) { "Failed to convert file into a URL" }
        return urlClass.newObjectArray(1, url).also {
            setObjectArrayElement(it, 0, url)
        }
    }

    private fun JNIEnv.setContextClassLoader(classLoader: JObject) {
        val jclass = findClass("java/lang/Thread")
        val currentThreadMethodID = jclass.getStaticMethodID("currentThread", "()Ljava/lang/Thread;")
        val currentThread = jclass.callStaticObjectMethod(currentThreadMethodID)
        requireNotNull(currentThread) { "Failed to fetch current thread!" }
        println("Got current thread $currentThread")
        val setContextClassLoaderMethodId = jclass.getMethodID("setContextClassLoader", "(Ljava/lang/ClassLoader;)V")
        setContextClassLoaderMethodId.callVoidMethod(currentThread, JValue.Object(classLoader.handle))
        println("Context classloader set!")
    }

    private fun JNIEnv.callGlue(classLoader: JObject) {
        val jclass = loadClass(classLoader, "godot.internal.Glue")
        val ctor = jclass.getConstructorMethodID("()V")
        val instance = jclass.newInstance(ctor)
        val sayHelloMethodID = jclass.getMethodID("sayHello", "()V")
        sayHelloMethodID.callObjectMethod(instance)
    }
}