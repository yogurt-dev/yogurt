package pub.utils;

import com.github.jyoghurt.security.securitySyslogT.enums.ClientType;
import com.github.jyoghurt.security.securitySyslogT.enums.SystemType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jtwu on 2015/9/16.
 */
public class UserAgentUtils {


    public static ClientType getClientType(String userAgent) {
        if (regex(ClientType.OPERA.getValue(), userAgent)) return ClientType.OPERA;
        if (regex(ClientType.CHROME.getValue(), userAgent)) return ClientType.CHROME;
        if (regex(ClientType.FIREFOX.getValue(), userAgent)) return ClientType.FIREFOX;
        if (regex(ClientType.SAFARI.getValue(), userAgent)) return ClientType.SAFARI;
        if (regex(ClientType.SE360.getValue(), userAgent)) return ClientType.SE360;
        if (regex(ClientType.GREEN.getValue(), userAgent)) return ClientType.GREEN;
        if (regex(ClientType.QQ.getValue(), userAgent)) return ClientType.QQ;
        if (regex(ClientType.MAXTHON.getValue(), userAgent)) return ClientType.MAXTHON;
        if (regex(ClientType.IE11.getValue(), userAgent)) return ClientType.IE11;
        if (regex(ClientType.IE10.getValue(), userAgent)) return ClientType.IE10;
        if (regex(ClientType.IE9.getValue(), userAgent)) return ClientType.IE9;
        if (regex(ClientType.IE8.getValue(), userAgent)) return ClientType.IE8;
        if (regex(ClientType.IE7.getValue(), userAgent)) return ClientType.IE7;
        if (regex(ClientType.IE6.getValue(), userAgent)) return ClientType.IE6;
        return ClientType.OTHER;
    }

    public static SystemType getSystemType(String userAgent) {
        if (regex(SystemType.WIN10.getValue(), userAgent)) return SystemType.WIN10;
        if (regex(SystemType.WIN8_1.getValue(), userAgent)) return SystemType.WIN8_1;
        if (regex(SystemType.WIN8.getValue(), userAgent)) return SystemType.WIN8;
        if (regex(SystemType.WIN7.getValue(), userAgent)) return SystemType.WIN7;
        if (regex(SystemType.WIN_VISTA.getValue(), userAgent)) return SystemType.WIN_VISTA;
        if (regex(SystemType.WIN2003.getValue(), userAgent)) return SystemType.WIN2003;
        if (regex(SystemType.WIN_XP.getValue(), userAgent)) return SystemType.WIN_XP;
        if (regex(SystemType.WIN2000.getValue(), userAgent)) return SystemType.WIN2000;
        if (regex(SystemType.ANDROID.getValue(), userAgent)) return SystemType.ANDROID;
        if (regex(SystemType.IOS.getValue(), userAgent)) return SystemType.IOS;
        return SystemType.OTHER;
    }

    public static boolean regex(String regex, String str) {
        String[] regexArray = regex.split(",");
        boolean match;
        for (String r : regexArray) {
            Pattern p = Pattern.compile(r.toLowerCase(), Pattern.MULTILINE);
            Matcher m = p.matcher(str.toLowerCase());
            match = m.find();
            if (match) {
                return true;
            }
        }
        return false;
    }

}
