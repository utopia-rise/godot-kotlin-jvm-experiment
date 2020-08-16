package godot.loader.internal

import godot.loader.registry.Registry
import jni.*
import jni.extras.ClassLoader
import jni.extras.currentThread
import jni.extras.newClassLoader

@ThreadLocal
object Loader {
    lateinit var classLoader: ClassLoader

    fun loadBinding(libraryPath: String) {
        initVm()
        val workingDir = libraryPath.replaceAfterLast("/", "")
        val bootstrapJar = "$workingDir/build/libs/bootstrap.jar"
        println("Loading binding, workingDir = $workingDir")
        JavaVm.attach {
            println("Creating class loader ...")
            classLoader = newClassLoader(listOf(bootstrapJar)).newGlobalRef()
            println("Setting context class loader ...")
            currentThread().setContextClassLoader(classLoader)
            println("Registering native methods ...")
            registerNatives(classLoader)
            println(classLoader)
        }

    }

    fun unloadBinding() {
        JavaVm.attach {
            println("Unloading binding: $classLoader")
            classLoader.deleteGlobalRef()
        }
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

    private fun registerNatives(classLoader: ClassLoader) {
        val registryClass = classLoader.loadClass("godot.registry.Registry")
        registryClass.registerNatives(Registry.nativeMethods())
    }

    fun callEntryPoint() {
        println("Calling entry point ...")
        JavaVm.attach {
            val entryClass = classLoader.loadClass("godot.Entry")
            val entryInstance = entryClass.newInstance()
            entryInstance.callVoidMethod(entryClass.getMethodID("init", "()V"))
        }
    }
}