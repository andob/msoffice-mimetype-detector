package ro.andob.msoffice.mime_type.detector

import org.apache.tika.metadata.Metadata
import org.apache.tika.parser.AutoDetectParser
import java.io.File
import java.util.*
import kotlin.random.Random

private val tikaParser = AutoDetectParser()

fun File.getMimeType() : String
{
    val file=this
    val msofficeMimeType= MicrosoftOfficeFileMimeTypeDetector.getMimeType(file)
    if (msofficeMimeType!=null)
        return msofficeMimeType

    return file.inputStream().buffered().use { inputStream ->
        tikaParser.detector
                .detect(inputStream, Metadata())
                .toString()
    }
}

fun Random.nextString() = UUID.randomUUID().toString().replace("-", "")
