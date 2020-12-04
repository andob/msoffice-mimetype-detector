package ro.andob.msoffice.mime_type.detector;

public class HashUtils
{
    public static String hexString(byte[] bytes)
    {
        StringBuilder hexString=new StringBuilder();
        for (byte aMessageDigest : bytes)
        {
            String h=Integer.toHexString(0xFF&aMessageDigest);
            while (h.length()<2)
                h="0"+h;
            hexString.append(h);
        }

        return hexString.toString();
    }
}
