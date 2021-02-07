package ro.andob.msoffice.mime_type.detector

import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import java.util.zip.ZipInputStream

object MicrosoftOfficeFileMimeTypeDetector
{
    @JvmStatic
    fun getMimeType(file : File) : String?
    {
        try
        {
            if (file.hasMagicBytesSignature(magicBytesHexString = "504B"))
                return getMimeTypeFromMicrosoftOfficeDocumentFile(file)

            if (file.hasMagicBytesSignature(magicBytesHexString = "D0CF11E0A1B1"))
                return getMimeTypeFromMicrosoftOfficeLegacyDocumentFile(file)
        }
        catch (ex : Exception)
        {
            ex.printStackTrace()
        }

        return null
    }

    @Throws(Exception::class)
    private fun getMimeTypeFromMicrosoftOfficeDocumentFile(file : File) : String
    {
        //OpenXML file format: DOCX, XLSX, PPTX
        ZipInputStream(FileInputStream(file)).use { zipInputStream ->
            var fileInsideZip=zipInputStream.nextEntry
            while (fileInsideZip!=null)
            {
                if (fileInsideZip.name.toLowerCase().startsWith("word/"))
                    return MimeTypes.APPLICATION_OFFICE_WORD //DOCX

                if (fileInsideZip.name.toLowerCase().startsWith("xl/"))
                    return MimeTypes.APPLICATION_OFFICE_EXCEL //XLSX

                if (fileInsideZip.name.toLowerCase().startsWith("ppt/"))
                    return MimeTypes.APPLICATION_OFFICE_POWER_POINT //PPTX

                fileInsideZip=zipInputStream.nextEntry
            }
        }

        throw RuntimeException("Cannot detect file type!")
    }

    @Throws(Exception::class)
    private fun getMimeTypeFromMicrosoftOfficeLegacyDocumentFile(file : File) : String
    {
        //legacy file format: DOC, XLS, PPT - https://stackoverflow.com/a/48318648
        DataInputStream(BufferedInputStream(FileInputStream(file))).use { inputStream ->
            // Get sector size (2 byte uint) at offset 30 in the header
            inputStream.mark(Int.MAX_VALUE)
            inputStream.reset()
            inputStream.skipBytes(30)
            val sectorSize=1 shl inputStream.readUInt16().toInt()

            // Get first directory sector index at offset 48 in the header
            inputStream.reset()
            inputStream.skipBytes(48)
            val rootDirectoryIndex=inputStream.readUInt32()

            // File header is one sector wide. After that we can address the sector directly using the sector index
            val rootDirectoryAddress=sectorSize+rootDirectoryIndex*sectorSize

            // Object type field is offset 80 bytes into the directory sector. It is a 128 bit GUID, encoded as "DWORD, WORD, WORD, BYTE[8]".
            inputStream.reset()
            inputStream.skipBytes(rootDirectoryAddress+80)
            val guid=inputStream.readMicrosoftGUID()

            if (guid=="0002090600000000C000000000000046")
                return MimeTypes.APPLICATION_OFFICE_WORD_LEGACY //DOC

            if (guid=="0002081000000000C000000000000046"||guid=="0002082000000000C000000000000046")
                return MimeTypes.APPLICATION_OFFICE_EXCEL_LEGACY //XLS

            if (guid=="64818D104F9B11CF86EA00AA00B929E8")
                return MimeTypes.APPLICATION_OFFICE_POWER_POINT_LEGACY //PPT
        }

        throw RuntimeException("Cannot detect file type!")
    }

    private fun DataInputStream.readUInt16() : Short
    {
        val bytes=ByteArray(size = 2)
        read(bytes)

        val byteBuffer=ByteBuffer.wrap(bytes)
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
        return byteBuffer.short
    }

    private fun DataInputStream.readUInt32() : Int
    {
        val bytes=ByteArray(size = 4)
        read(bytes)

        val byteBuffer=ByteBuffer.wrap(bytes)
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
        return byteBuffer.int
    }

    private fun DataInputStream.readMicrosoftGUID() : String
    {
        val bytes=ByteArray(size = 16)
        read(bytes)

        //128 bit GUID, encoded as "DWORD, WORD, WORD, BYTE[8]".
        val source=ByteBuffer.wrap(bytes)
        val target=ByteBuffer.allocate(16)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(source.int) //first component: 32-bit little endian integer (DWORD)
            .putShort(source.short) //second component: 16-bit little endian integer (WORD)
            .putShort(source.short) //third component: 16-bit little endian integer (WORD)
            .order(ByteOrder.BIG_ENDIAN)
            .putLong(source.long) //forth component: 64-bit big endian integer (BYTE[8])
        target.flip()

        return UUID(target.long, target.long).toString()
                .replace("-", "").toUpperCase()
    }

    private fun File.hasMagicBytesSignature(magicBytesHexString : String) : Boolean
    {
        if (magicBytesHexString.length%2!=0)
            throw RuntimeException("Invalid magic bytes code!")

        FileInputStream(this).use { inputStream ->
            val firstBytes=ByteArray(magicBytesHexString.length/2)
            inputStream.read(firstBytes, 0, firstBytes.size)
            val fileMagicBytesHexString=ByteArrayEncoder(firstBytes).toHexString()
            return fileMagicBytesHexString==magicBytesHexString
        }
    }
}