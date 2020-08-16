package jni

class JavaVmInitArgs(
    val version: JniVersion,
    val options: List<Option>
) {
    class Option(val str: String)

    companion object {
        class JavaVMInitArgsDsl {
            var version = JniVersion.JNI_1_8
            private val options = mutableListOf<Option>()

            fun option(str: String) {
                options.add(Option(str))
            }

            internal fun build(): JavaVmInitArgs {
                return JavaVmInitArgs(version, options)
            }
        }

        fun create(block: JavaVMInitArgsDsl.() -> Unit): JavaVmInitArgs {
            return JavaVMInitArgsDsl().also { it.block() }.build()
        }
    }
}