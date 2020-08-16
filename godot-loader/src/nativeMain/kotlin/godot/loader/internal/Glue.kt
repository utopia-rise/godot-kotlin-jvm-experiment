package godot.loader.internal

import jni.*
import jni.extras.ClassLoader
import jni.extras.currentThread
import jni.extras.newClassLoader

@ThreadLocal
object Glue {
    fun loadBinding(libraryPath: String) {
        initVm()
        val workingDir = libraryPath.replaceAfterLast("/", "")
        val bootstrapJar = "$workingDir/build/libs/bootstrap.jar"
        println("Loading binding, workingDir = $workingDir")
        JavaVm.attach {
            println("Creating class loader ...")
            val classloader = newClassLoader(listOf(bootstrapJar))
            println("Setting context class loader ...")
            currentThread().setContextClassLoader(classloader)
            println("Calling Glue.sayHello()")
            callGlue(classloader)
        }

    }

    fun unloadBinding() {
        println("Unloading binding: $JavaVm")
    }

    fun destroy() {
        JavaVm.destroy()
    }

    private fun initVm() {
        val args = JavaVmInitArgs.create {
            version = JniVersion.JNI_1_8
            // useful for developing
            option("-Xcheck:jni")
            option("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005")
        }
        JavaVm.init(args)
    }

//    private fun JniEnv.createURLClassLoader(urls: JObject): JObject {
//        return newInstance("java/net/URLClassLoader", "([Ljava/net/URL;)V", urls)
//    }
//
//    private fun JniEnv.createFile(bootstrapJar: String): JObject {
//        return newInstance("java/io/File", "(Ljava/lang/String;)V", newString(bootstrapJar))
//    }
//
//    private fun JniEnv.fileToURLArray(file: JObject): JObjectArray {
//        val fileClass = findClass("java/io/File")
//        val toURLMethod = fileClass.getMethodID("toURL", "()Ljava/net/URL;")
//        val url = file.callObjectMethod(toURLMethod)
//        checkNotNull(url) { "Failed to convert file into a URL" }
//
//        return newObjectArray("java/net/URL", 1).also {
//            it[0] = url
//        }
//    }

//    private fun JniEnv.setContextClassLoader(classLoader: JObject) {
//        val threadClass = findClass("java/lang/Thread")
//        val currentThreadMethodID = threadClass.getStaticMethodID("currentThread", "()Ljava/lang/Thread;")
//        val currentThread = threadClass.callStaticObjectMethod(currentThreadMethodID)
//        checkNotNull(currentThread) { "Failed to fetch current thread!" }
//        println("Got current thread $currentThread")
//        val setContextClassLoaderMethodId = threadClass.getMethodID("setContextClassLoader", "(Ljava/lang/ClassLoader;)V")
//        threadClass.callStaticObjectMethod(setContextClassLoaderMethodId, classLoader)
//        println("Context classloader set!")
//    }

    private fun JniEnv.callGlue(classLoader: ClassLoader) {
        val glueClass = classLoader.loadClass("godot.loader.internal.Glue")
        val ctor = glueClass.getConstructorMethodID("()V")
        val glueInstance = glueClass.newInstance(ctor)
        val sayHelloMethodID = glueClass.getMethodID("sayHello", "()V")
        glueInstance.callObjectMethod(sayHelloMethodID)
    }
}