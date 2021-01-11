package ro.andob.msoffice.mime_type.detector

import junit.framework.TestCase
import org.junit.Test
import java.io.File
import java.io.FileOutputStream
import kotlin.random.Random

class MimeTypeDetectorTest : TestCase()
{
    private val randomizer = Random(seed = System.currentTimeMillis())

    @Test
    fun testMimeTypeDetector()
    {
        val filesToMimeTypesMap=mapOf(
            "sample.doc" to "application/msword",
            "sample.docx" to "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "sample.html" to "text/html",
            "sample.pdf" to "application/pdf",
            "sample.ppt" to "application/vnd.ms-powerpoint",
            "sample.pptx" to "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "sample.txt" to "text/plain",
            "sample.xls" to "application/vnd.ms-excel",
            "sample.xlsx" to "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        )

        for ((fileName, mimeType) in filesToMimeTypesMap)
        {
            val file=File(randomizer.nextString())
            file.deleteOnExit()

            javaClass.classLoader.getResourceAsStream(fileName)!!.use { inputStreamFromResource ->
                FileOutputStream(file).use { outputStreamToFile ->
                    outputStreamToFile.write(inputStreamFromResource.readBytes())
                }
            }

            print("Checking mime type on file $fileName. Expecting $mimeType...")
            assertEquals(mimeType, file.getMimeType())
            print(" OK\n")
        }
    }
}
