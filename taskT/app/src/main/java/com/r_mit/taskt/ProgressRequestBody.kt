package com.r_mit.taskt.com.r_mit.taskt

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.Buffer
import okio.BufferedSink
import okio.source

class ProgressRequestBody(
    private val data: ByteArray,
    private val contentType: MediaType?,
    private val progressListener: (progress: Int) -> Unit
) : RequestBody() {

    override fun contentType(): MediaType? = contentType

    override fun contentLength(): Long = data.size.toLong()

    override fun writeTo(sink: BufferedSink) {
        val source = data.inputStream().source() // Convert InputStream to Okio Source
        val totalBytes = contentLength()
        var uploadedBytes = 0L
        val buffer = Buffer() // Okio Buffer instance
        val segmentSize = 2048L // Size of data to read at a time

        try {
            var read: Long
            while (source.read(buffer, segmentSize).also { read = it } != -1L) {
                sink.write(buffer, read) // Write the buffer content to the sink
                uploadedBytes += read
                val progress = (100 * uploadedBytes / totalBytes).toInt()

                // Call progress listener with updated progress
                progressListener(progress)
            }
        } finally {
            source.close()
        }
    }
}
