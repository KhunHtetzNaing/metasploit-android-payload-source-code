package com.htetznaing.metesploit;

import java.net.URLConnection;

public class HttpConnection
{
    private static boolean isEmpty(String paramString)
    {
        return (paramString == null) || ("".equals(paramString));
    }

    public static void addRequestHeaders(URLConnection paramURLConnection, String paramString1, String paramString2)
    {
        if (!isEmpty(paramString2)) {
            paramURLConnection.addRequestProperty("User-Agent", paramString2);
        }
        String[] arrayOfString1 = paramString1.split("\r\n");
        for (String str : arrayOfString1) {
            if (!isEmpty(str))
            {
                String[] arrayOfString3 = str.split(": ", 2);
                if ((arrayOfString3.length == 2) && (!isEmpty(arrayOfString3[0])) && (!isEmpty(arrayOfString3[1]))) {
                    paramURLConnection.addRequestProperty(arrayOfString3[0], arrayOfString3[1]);
                }
            }
        }
    }
}
