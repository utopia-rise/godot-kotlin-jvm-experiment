package godot.loader.internal

import godot.loader.registry.Registry
import jni.*
import jni.extras.ClassLoader
import jni.extras.currentThread
import jni.extras.newClassLoader

@ThreadLocal
object Loader {
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
            println("Registering native methods ...")
            registerNatives(classloader)
            println("Invoking entry point ...")
            callEntryPoint(classloader)
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

    private fun JniEnv.registerNatives(classLoader: ClassLoader) {
        val registryClass = classLoader.loadClass("godot.registry.Registry")
        registryClass.registerNatives(Registry.nativeMethods())
    }

    private fun JniEnv.callEntryPoint(classLoader: ClassLoader) {
        val entryClass = classLoader.loadClass("godot.Entry")
        val entryInstance = entryClass.newInstance()
        entryInstance.callVoidMethod(entryClass.getMethodID("init", "()V"))
    }
}