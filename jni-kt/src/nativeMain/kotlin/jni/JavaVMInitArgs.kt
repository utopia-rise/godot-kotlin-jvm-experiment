package jni

class JavaVMInitArgs(
    val version: JNIVersion,
    val options: List<Option>
) {
    class Option(val str: String)

    companion object {
        class JavaVMInitArgsDsl {
            var version = JNIVersion.JNI_1_8
            private val options = mutableListOf<Option>()

            fun option(str: String) {
                options.add(Option(str))
            }

            internal fun build(): JavaVMInitArgs {
                return JavaVMInitArgs(version, options)
            }
        }

        fun create(block: JavaVMInitArgsDsl.() -> Unit): JavaVMInitArgs {
            return JavaVMInitArgsDsl().also { it.block() }.build()
        }
    }
}