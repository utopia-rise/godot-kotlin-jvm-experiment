package godot.wire

import com.google.protobuf.Value
import godot.internal.meta.JniExposed
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer

actual class TransferContext {
    private var threadLocalBuffer = ThreadLocal.withInitial { ByteBuffer.allocateDirect(100) }
    @JniExposed
    var buffer: ByteBuffer
        get() = threadLocalBuffer.get()
        set(value) { threadLocalBuffer.set(value) }

    private var outputStream = object : OutputStream() {
        override fun write(b: Int) {
            buffer.put(b.toByte())
        }
    }

    private var inputStream  = object : InputStream() {
        override fun read(): Int {
            return buffer.get().toInt()
        }
    }

    actual fun writeArguments(vararg values: TValue): Boolean {
        val argsBuilder = Wire.KFuncArgs.newBuilder()
        for (value in values) {
            argsBuilder.addArgs(value.data)
        }
        val args = argsBuilder.build()
        val bufferChanged = ensureCapacity(args.serializedSize)
        args.writeDelimitedTo(outputStream)
        buffer.rewind()
        return bufferChanged
    }

    actual fun readArguments(): List<TValue> {
        val args = Wire.KFuncArgs.parseDelimitedFrom(inputStream)
        buffer.rewind()
        val values = mutableListOf<TValue>()

        for (tArg in args.argsList) {
            val value = TValue(tArg)
            values.add(value)
        }
        return values.toList()
    }

    actual fun writeReturnValue(value: TValue): Boolean {
        val returnValue = Wire.KReturnValue.newBuilder()
            .setData(value.data)
            .build()

        val bufferChanged = ensureCapacity(returnValue.serializedSize)
        returnValue.writeDelimitedTo(outputStream)
        buffer.rewind()
        return bufferChanged
    }

    actual fun readReturnValue(): TValue {
        val returnValue = Wire.KReturnValue.parseDelimitedFrom(inputStream)
        buffer.rewind()
        return TValue(returnValue.data)
    }

    /*
     * Returns true if the underlying buffer object was changed.
     */
    @JniExposed
    fun ensureCapacity(capacity: Int): Boolean {
        val actualCapacity = getRequiredCapacity(capacity)
        if (buffer.capacity() < actualCapacity) {
            buffer = ByteBuffer.allocateDirect(actualCapacity)
            return true
        }
        return false
    }

    private fun getRequiredCapacity(capacity: Int): Int {
        // extra bytes used for the delimiter
        val prepend = Value.newBuilder()
            .setNumberValue(capacity.toDouble())
            .build()
            .serializedSize
        return prepend + capacity;
    }
}