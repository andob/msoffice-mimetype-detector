package ro.andob.msoffice.mime_type.detector;

public class ByteArrayEncoder
{
    private final byte[] bytes;

    public ByteArrayEncoder(byte[] bytes)
    {
        this.bytes=bytes;
    }

    public String toHexString()
    {
        StringBuilder hexString=new StringBuilder();
        for (byte aMessageDigest : bytes)
        {
            String h=Integer.toHexString(0xFF&aMessageDigest);
            while (h.length()<2)
                h="0"+h;
            hexString.append(h);
        }

        return hexString.toString().toUpperCase();
    }
}
