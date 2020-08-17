package godot.loader.internal

import godot.loader.registry.NativeClassHandle
import godot.loader.registry.NativeKFunc
import godot.loader.registry.NativeRegistry
import jni.*
import jni.extras.ClassLoader
import jni.extras.currentThread
import jni.extras.newClassLoader
import kotlinx.cinterop.COpaquePointer
import kotlin.native.internal.GC

@ThreadLocal
object NativeBindingContext {
    lateinit var classLoader: ClassLoader
    private var wrappers = listOf(
        NativeKVariant,
        NativeKFunc,
        NativeClassHandle,
        NativeKObject
    )

    fun loadBinding(libraryPath: String) {
        initVm()
        val workingDir = libraryPath.replaceAfterLast("/", "")
        val bootstrapJar = "$workingDir/build/libs/bootstrap.jar"
        println("Loading binding, workingDir = $workingDir")
        with(JavaVm.attach()) {
            println("Creating class loader ...")
            classLoader = newClassLoader(listOf(bootstrapJar)).newGlobalRef()
            println("Setting context class loader ...")
            currentThread().setContextClassLoader(classLoader)
            wrappers.forEach { it.init(classLoader) }
        }

    }

    fun <T> bindScope(block: JniEnv.() -> T): T {
        val env = JavaVm.getEnv()
        env.pushLocalFrame(100)
        val value = env.block()
        env.popLocalFrame()
        return value
    }

    fun unloadBinding() {
        bindScope {
            println("Unloading binding: $classLoader")
            wrappers.forEach { it.teardown() }
            classLoader.deleteGlobalRef()
        }
        JavaVm.detach()
    }

    fun destroy() {
        JavaVm.destroy()
    }

    private fun initVm() {
        val args = JavaVmInitArgs.create {
            version = JniVersion.JNI_1_8
            // useful for developing
            option("-Xcheck:jni")
            option("-XX:+HeapDumpOnOutOfMemoryError")
            option("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005")
        }
        JavaVm.init(args)
    }

    fun callEntryPoint(nativescriptHandle: COpaquePointer) {
        println("Calling entry point ...")
        bindScope {
            val entryClass = classLoader.loadClass("godot.Entry")
            val entryInstance = entryClass.newInstance()
            val handles = entryInstance.callObjectMethod(entryClass.getMethodId("init", "()[Lgodot/registry/ClassHandle;"))
            checkNotNull(handles) { "Failed to get class handles!" }
            NativeRegistry.registerAll(nativescriptHandle, this, JObjectArray.unsafeCast(handles))
        }
    }
}