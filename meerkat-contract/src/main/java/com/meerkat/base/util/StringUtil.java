package com.meerkat.base.util;

import com.meerkat.base.lang.Lists;
import com.meerkat.base.lang.Strings;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wm on 17/3/19.
 */
public class StringUtil {

    private static final Logger log = LoggerFactory.getLogger(StringUtil.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^\\w+[\\w\\.\\-_]*@[\\w\\.]+\\.[a-zA-Z]{2,4}$");

    public StringUtil() {
    }

    public static String paseFloat(String floStr, int location) {
        if(floStr == null) {
            return "";
        } else {
            int index = floStr.indexOf(".");
            if(index == -1) {
                floStr = floStr + ".";
            }

            index = floStr.indexOf(".");

            for(int leave = floStr.length() - index; leave <= location; ++leave) {
                floStr = floStr + "0";
            }

            return floStr.substring(0, index + location + 1);
        }
    }

    public static int truncateInt(String intStr) {
        int len = intStr.length();
        int result = 0;

        for(int i = 0; i < len; ++i) {
            char c = intStr.charAt(i);
            if(c < 48 || c > 57) {
                break;
            }

            result *= 10;
            result += c - 48;
        }

        return result;
    }

    public static int getInt(String str, int defaultValue) {
        return str == null?defaultValue:(isInt(str)?Integer.parseInt(str):defaultValue);
    }

    public static String encodeName(String name) {
        return isBlank(name)?name:(name.length() == 1?name:(name.length() == 2?"*" + name.substring(1, 2):(name.length() == 3?"*" + name.substring(1, 3):(name.length() == 4?"**" + name.substring(2, 4):"**" + name.substring(2, name.length())))));
    }

    public static String encodeIdCard(String idCard) {
        return isBlank(idCard)?idCard:(idCard.length() == 18?idCard.substring(0, 3) + "***********" + idCard.substring(14):idCard.substring(0, 3) + "********" + idCard.substring(11));
    }

    public static int getInt(String str) {
        return getInt(str, -1);
    }

    public static boolean isInt(String str) {
        if(isNum(str)) {
            if(str.length() < 10) {
                return true;
            }

            try {
                Integer.parseInt(str);
                return true;
            } catch (Exception var2) {
                ;
            }
        }

        return false;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isBlank(String str) {
        if(isEmpty(str)) {
            return true;
        } else {
            for(int i = 0; i < str.length(); ++i) {
                if(!Character.isWhitespace(str.charAt(i))) {
                    return false;
                }
            }

            return true;
        }
    }

    public static String notNull(String str) {
        if(str == null) {
            str = "";
        }

        return str;
    }

    public static boolean isequals(String str1, String str2) {
        return str1.equals(str2);
    }

    public static String addzero(long num, int length) {
        String str = "";
        if((double)num < Math.pow(10.0D, (double)(length - 1))) {
            for(int i = 0; i < length - (num + "").length(); ++i) {
                str = str + "0";
            }
        }

        str = str + num;
        return str;
    }

    public static String addzero(int num, int length) {
        String str = "";
        if((double)num < Math.pow(10.0D, (double)(length - 1))) {
            for(int i = 0; i < length - (num + "").length(); ++i) {
                str = str + "0";
            }
        }

        str = str + num;
        return str;
    }

    public static final String escapeHTMLTagOld(String input) {
        if(input == null) {
            return "";
        } else {
            input = input.trim().replaceAll("&", "&amp;");
            input = input.replaceAll("<", "&lt;");
            input = input.replaceAll(">", "&gt;");
            input = input.replaceAll("\r\n", "<br>");
            input = input.replaceAll("\'", "&#39;");
            input = input.replaceAll("\"", "&quot;");
            input = input.replaceAll("\\\\", "&#92;");
            return input;
        }
    }

    public static final String escapeHTMLTag(String input) {
        if(input == null) {
            return "";
        } else if(input.indexOf("<br>") == -1 && input.indexOf("&nbsp;") == -1) {
            return escapeHTMLTagOld(input);
        } else {
            int len = input.length();
            StringBuilder strBuilder = new StringBuilder();
            int pos = 0;

            while(true) {
                while(pos < len) {
                    char c = input.charAt(pos);
                    switch(c) {
                        case '\"':
                            strBuilder.append("&quot;");
                            ++pos;
                            break;
                        case '&':
                            if(pos + 6 < len && "&nbsp;".equals(input.substring(pos, pos + 6))) {
                                strBuilder.append("&nbsp;");
                                pos += 6;
                                break;
                            }

                            strBuilder.append("&amp;");
                            ++pos;
                            break;
                        case '\'':
                            strBuilder.append("&#39;");
                            ++pos;
                            break;
                        case '<':
                            if(pos + 4 < len && "<br>".equals(input.substring(pos, pos + 4))) {
                                strBuilder.append("<br>");
                                pos += 4;
                                break;
                            }

                            strBuilder.append("&lt;");
                            ++pos;
                            break;
                        case '>':
                            strBuilder.append("&gt;");
                            ++pos;
                            break;
                        case '\\':
                            strBuilder.append("&#92;");
                            ++pos;
                            break;
                        default:
                            strBuilder.append(c);
                            ++pos;
                    }
                }

                return strBuilder.toString();
            }
        }
    }

    public static final String unEscapeHTMLTag(String input) {
        if(input == null) {
            return "";
        } else {
            input = input.trim().replaceAll("&amp;", "&");
            input = input.replaceAll("&lt;", "<");
            input = input.replaceAll("&gt;", ">");
            input = input.replaceAll("<br>", "\n");
            input = input.replaceAll("&#39;", "\'");
            input = input.replaceAll("&quot;", "\"");
            input = input.replaceAll("&#92;", "\\\\");
            return input;
        }
    }

    public static String toString(String[] str, String seperator) {
        if(str != null && str.length != 0) {
            StringBuffer buf = new StringBuffer();
            int i = 0;

            for(int n = str.length; i < n; ++i) {
                if(i != 0) {
                    buf.append(seperator);
                }

                buf.append(str[i]);
            }

            return buf.toString();
        } else {
            return "";
        }
    }

    public static String[] split(String str, String seperator) {
        StringTokenizer token = new StringTokenizer(str, seperator);
        int count = token.countTokens();
        String[] ret = new String[count];

        for(int i = 0; i < count; ++i) {
            ret[i] = token.nextToken();
        }

        return ret;
    }

    public static String[] splitHaveEmpty(String str, String seperator) {
        str = str.replaceAll(seperator, " " + seperator + " ");
        return str.split(seperator);
    }

    public static String getSub(String str, int len, String symbol) {
        if(str == null) {
            return "";
        } else {
            try {
                int e = 0;
                byte[] b = str.getBytes("gbk");
                if(b.length <= len) {
                    return str;
                }

                for(int i = 0; i < len; ++i) {
                    if(b[i] < 0) {
                        ++e;
                    }
                }

                if(e % 2 == 0) {
                    str = new String(b, 0, len, "gbk") + symbol;
                } else {
                    str = new String(b, 0, len - 1, "gbk") + symbol;
                }
            } catch (UnsupportedEncodingException var6) {
                log.error(var6.getMessage(), var6);
            }

            return str.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        }
    }

    public static int getLen(String str) {
        try {
            byte[] e = str.getBytes("gbk");
            return e.length;
        } catch (UnsupportedEncodingException var2) {
            log.error(var2.getMessage(), var2);
            return 0;
        }
    }

    public static String getSub(String str, int len) {
        return getSub(str, len, "");
    }

    public static String getAbc(String str, int len) {
        return getAbc(str, len, "...");
    }

    public static String getAbc(String str, int len, String symbol) {
        return str == null?null:(len < 0?"":(str.length() <= len?str:str.substring(0, len).concat(symbol)));
    }

    public static String subBetween(String str, String open, String close) {
        if(str != null && open != null && close != null) {
            int start = str.indexOf(open);
            if(start != -1) {
                int end = str.indexOf(close, start + open.length());
                if(end != -1) {
                    return str.substring(start + open.length(), end);
                }
            }

            return null;
        } else {
            return null;
        }
    }

    public static String subAfterLast(String str, String separator) {
        if(str != null && str.length() != 0) {
            if(separator != null && separator.length() != 0) {
                int pos = str.lastIndexOf(separator);
                return pos != -1 && pos != str.length() - separator.length()?str.substring(pos + separator.length()):"";
            } else {
                return "";
            }
        } else {
            return str;
        }
    }

    public static String subBeforeLast(String str, String separator) {
        if(str != null && separator != null && str.length() != 0 && separator.length() != 0) {
            int pos = str.lastIndexOf(separator);
            return pos == -1?str:str.substring(0, pos);
        } else {
            return str;
        }
    }

    public static String subAfter(String str, String separator) {
        if(str != null && str.length() != 0) {
            if(separator == null) {
                return "";
            } else {
                int pos = str.indexOf(separator);
                return pos == -1?"":str.substring(pos + separator.length());
            }
        } else {
            return str;
        }
    }

    public static String subBefore(String str, String separator) {
        if(str != null && separator != null && str.length() != 0) {
            if(separator.length() == 0) {
                return "";
            } else {
                int pos = str.indexOf(separator);
                return pos == -1?str:str.substring(0, pos);
            }
        } else {
            return str;
        }
    }

    public static boolean containsNone(String str, String invalidChars) {
        return str != null && invalidChars != null?containsNone(str, invalidChars.toCharArray()):true;
    }

    public static boolean containsNone(String str, char[] invalidChars) {
        if(str != null && invalidChars != null) {
            int strSize = str.length();
            int validSize = invalidChars.length;

            for(int i = 0; i < strSize; ++i) {
                char ch = str.charAt(i);

                for(int j = 0; j < validSize; ++j) {
                    if(invalidChars[j] == ch) {
                        return false;
                    }
                }
            }

            return true;
        } else {
            return true;
        }
    }

    public static boolean contains(String str, String searchStr) {
        return str != null && searchStr != null?str.indexOf(searchStr) >= 0:false;
    }

    public static boolean isEmail(String email) {
        if(Strings.isBlank(email)) {
            return false;
        } else if(email.length() > 100) {
            return false;
        } else if(email.indexOf(64) == -1) {
            return false;
        } else {
            log.info("check email: " + email);
            Matcher matcher = EMAIL_PATTERN.matcher(email);
            return matcher.matches();
        }
    }

    public static String htmlSpecialChars(String str) {
        try {
            if(str.trim() == null) {
                return "";
            } else {
                StringBuffer e = new StringBuffer();
                boolean ch = true;

                for(int i = 0; i < str.length(); ++i) {
                    char var5 = str.charAt(i);
                    if(var5 == 38) {
                        e.append("&amp;");
                    } else if(var5 == 60) {
                        e.append("&lt;");
                    } else if(var5 == 62) {
                        e.append("&gt;");
                    } else if(var5 == 34) {
                        e.append("&quot;");
                    } else if(var5 == 39) {
                        e.append("&#039;");
                    } else if(var5 == 40) {
                        e.append("&#040;");
                    } else if(var5 == 41) {
                        e.append("&#041;");
                    } else if(var5 == 64) {
                        e.append("&#064;");
                    } else {
                        e.append(var5);
                    }
                }

                if(e.toString().replaceAll("&nbsp;", "").replaceAll("　", "").trim().length() == 0) {
                    return "";
                } else {
                    return e.toString();
                }
            }
        } catch (Exception var4) {
            return "";
        }
    }

    public static String[] findSpecData(String input, String mark, String bigProIndex) {
        StringBuffer sb = new StringBuffer();
        StringBuffer smallPro = (new StringBuffer("<ol>")).append("\n");
        int index = 1;
        String regex = "(<div class=s_title>)(.*?)(</div>)";

        Matcher testM;
        for(testM = Pattern.compile(regex, 2).matcher(input); testM.find(); ++index) {
            testM.appendReplacement(sb, "<div class=\"s_title\"><a name=\"" + mark + index + "\"></a>$2$3");
            String smallName = testM.group(2);
            smallPro.append("<li><span class=\"menuId\" >").append(bigProIndex).append(".").append(index).append("</span><a href=\"#").append(mark).append(index).append("\">").append(smallName).append("</a></li>").append("\n");
        }

        if(index != 1) {
            smallPro.append("</ol>");
            testM.appendTail(sb);
            return new String[]{sb.toString(), smallPro.toString()};
        } else {
            return null;
        }
    }

    public static int[] random5(int lenth, int num) {
        Random rd = new Random();
        HashSet set = new HashSet();

        do {
            int iter = rd.nextInt(lenth);
            set.add(new Integer(iter));
        } while(set.size() != num);

        Iterator var7 = set.iterator();
        int[] jj = new int[num];

        for(int i = 0; var7.hasNext(); ++i) {
            jj[i] = ((Integer)var7.next()).intValue();
        }

        return jj;
    }

    public static String getRandomString(int length) {
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < length; ++i) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }

        return sb.toString();
    }

    public static String filtHref(String str) {
        if(str == null) {
            return "";
        } else {
            String regex = "<[a|A] href=\".*?>(.*?)</[a|A]>";
            Pattern pattern = null;
            pattern = Pattern.compile(regex);

            Matcher matcher;
            String ss;
            for(matcher = pattern.matcher(str); matcher.find(); str = str.replaceAll("<[a|A] href=\".*?>" + ss + "</[a|A]>", ss)) {
                ss = matcher.group(1);
            }

            regex = "<[p|P] [^>]*?>(.*?)</[p|P]>";
            pattern = Pattern.compile(regex);

            for(matcher = pattern.matcher(str); matcher.find(); str = str.replaceAll("<[p|P] [^>]*?>" + ss + "</[p|P]>", ss)) {
                ss = matcher.group(1);
            }

            return str;
        }
    }

    public static String addHrefBlank(String str) {
        if(str == null) {
            return "";
        } else {
            String regex = "<[a|A] href=\"([^>]*?)>.*?</[a|A]>";
            Pattern pattern = null;
            pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(str);

            while(matcher.find()) {
                String ss = matcher.group(1);
                if(ss.indexOf("_blank") == -1) {
                    str = str.replaceAll(ss, ss + "  target=\"_blank\"");
                }
            }

            return str;
        }
    }

    public static char[] getEnChar() {
        char[] cs = new char[26];
        char c = 64;

        for(int i = 0; c++ < 90; ++i) {
            cs[i] = c;
        }

        return cs;
    }

    public static boolean isInChar(String c) {
        boolean in = false;
        char[] ch = getEnChar();

        for(int i = 0; i < ch.length; ++i) {
            if(c.equals(ch[i] + "")) {
                in = true;
                break;
            }
        }

        return in;
    }

    public static String getSmallImg(String imgurl) {
        int len = imgurl.lastIndexOf("/");
        return len > 1?imgurl.substring(0, len + 1) + "t_" + imgurl.substring(len + 1, imgurl.length()):imgurl;
    }

    public static char[] toArray(String str) {
        return str.toCharArray();
    }

    public static String replaceStr(String str, String a, String b) {
        return isBlank(str)?"":str.replaceAll(a, b);
    }

    public static boolean getURLFromFile(String str, String filename) {
        String out = "";
        File file = new File(filename);
        if(file.exists()) {
            try {
                long e = file.length();
                if(e == 0L) {
                    return true;
                }

                BufferedReader input = new BufferedReader(new FileReader(file));
                StringBuffer buffer = new StringBuffer();

                String text;
                while((text = input.readLine()) != null) {
                    buffer.append(text);
                }

                out = buffer.toString();
                return filterURL(out, str);
            } catch (FileNotFoundException var9) {
                log.error(var9.getMessage(), var9);
            } catch (IOException var10) {
                log.error(var10.getMessage(), var10);
            }

            return false;
        } else {
            log.error("File is not exist");
            return true;
        }
    }

    public static boolean filterURL(String str, String regexpURL) {
        Pattern pattern = Pattern.compile(regexpURL);
        Matcher matcher = pattern.matcher(str);
        boolean result = matcher.find();
        return result;
    }

    public static boolean isFine(String str) {
        return str != null && str.trim().length() != 0;
    }

    public static boolean isNum(String str) {
        return str == null?false:str.matches("\\d+");
    }

    public static boolean isNumber(String str) {
        return str == null?false:str.matches("(-)\\d+|\\d+");
    }

    public static int getLenWithoutBlank(String str) {
        return str.replace("\n", "").replace("\t", "").replace(" ", "").replace("　", "").length();
    }

    public static String parserToWord(String str) {
        String result = "";
        if(str != null) {
            result = str.replaceAll("&lt;br&gt;", "&#10;");
            result = result.replaceAll("<br>", "&#10;");
            result = result.replaceAll(" ", "&ensp;");
            result = result.replaceAll("\r", "");
            result = result.replaceAll("\n", "");
        }

        return result;
    }

    public static String parserToHTMLForTextArea(String str) {
        String result = escapeHTMLTag(str);
        result = result.replaceAll(" ", "&ensp;");
        return result;
    }

    public static String doubleToString(double dnum) {
        NumberFormat numformat = NumberFormat.getNumberInstance();
        numformat.setGroupingUsed(false);
        numformat.setMinimumFractionDigits(2);
        numformat.setMaximumFractionDigits(2);
        String valueN = numformat.format(dnum);
        return valueN;
    }

    public static String getcurrentdate() {
        String current = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        current = df.format(new Date());
        return current;
    }

    public static boolean rechargeString(String str) {
        Pattern pattern = Pattern.compile("(^([1-9][0-9]{0,7})$)");
        Matcher match = pattern.matcher(str);
        return match.matches();
    }

    public static boolean isNumOfStockCount(String str) {
        return rechargeString(str);
    }

    public static boolean withdrawString(String str) {
        if(NumberUtils.isNumber(str)) {
            Float money = Float.valueOf(Float.parseFloat(str));
            return (double)money.floatValue() >= 1.0D;
        } else {
            return false;
        }
    }

    public static boolean cellcodeString(String str) {
        Pattern pattern = Pattern.compile("(^([0-9]{6})$)");
        Matcher match = pattern.matcher(str);
        return match.matches();
    }

    public static boolean bankCardIdString(String str) {
        Pattern pattern = Pattern.compile("(^([0-9]{16,19})$)");
        Matcher match = pattern.matcher(str);
        return match.matches();
    }

    public static boolean iscellPhoneString(String str) {
        if(isBlank(str)) {
            return false;
        } else {
            Pattern pattern = Pattern.compile("(^(13[0-9]|14[57]|15[0-9]|18[0-9]|17[0-9])[0-9]{8}$)");
            Matcher match = pattern.matcher(str);
            return match.matches();
        }
    }

    public static boolean isRealNameString(String str) {
        Pattern pattern = Pattern.compile("(^([一-龥]{2,20})$)");
        Matcher match = pattern.matcher(str);
        return match.matches();
    }

    public static boolean isIdCardString(String str) {
        Pattern pattern = Pattern.compile("(^(\\d{15}$|^\\d{18}$|^\\d{17}(\\d|X|x))$)");
        Matcher match = pattern.matcher(str);
        return match.matches();
    }

    public static boolean isBankBranch(String str) {
        Pattern pattern = Pattern.compile("(^([一-龥]{2,40})$)");
        Matcher match = pattern.matcher(str);
        return match.matches();
    }

    public static boolean isNickString(String str) {
        Pattern pattern = Pattern.compile("(^((\\w|[\\u4e00-\\u9fa5]|\\.|@){1,25})$)");
        Matcher match = pattern.matcher(str);
        return match.matches();
    }

    public static boolean isGaunWangPwdString(String str) {
        Pattern pattern = Pattern.compile("(^([\\p{Graph}]{6,16})$)");
        Matcher match = pattern.matcher(str);
        return match.matches();
    }

    public static boolean isPwdString(String str) {
        Pattern pattern = Pattern.compile("(^([\\w]{6,50})$)");
        Matcher match = pattern.matcher(str);
        return match.matches();
    }

    public static boolean isTradePwd(String str) {
        Pattern pattern = Pattern.compile("(^([\\d]{6})$)");
        Matcher match = pattern.matcher(str);
        return match.matches();
    }

    public static boolean isDateString(String str) {
        if(!isFine(str)) {
            return false;
        } else {
            Pattern pattern = Pattern.compile("(^([1][7-9][0-9][0-9]|[2][0][0-9][0-9])(-)([1-9]|[0][0-9]|[1][0-2])(-)([0-9]|[0-2][0-9]|[3][0-1])$)");
            Matcher match = pattern.matcher(str);
            return match.matches();
        }
    }

    public static String[] emailSubstring(String str) {
        String emailSubString = null;
        Pattern pattern = Pattern.compile("(@(\\w+)\\.)");

        for(Matcher matcher = pattern.matcher(str); matcher.find(); emailSubString = matcher.group(2)) {
            ;
        }

        String[] email = new String[2];
        if(emailSubString.equals("gmail")) {
            email[0] = "http://gmail.com";
            email[1] = "GMail";
            return email;
        } else if(!emailSubString.equals("qq") && !emailSubString.equals("foxmail")) {
            if(emailSubString.equals("sina")) {
                email[0] = "http://mail.sina.com";
                email[1] = "新浪";
                return email;
            } else if(emailSubString.equals("aliyun")) {
                email[0] = "http://mail.aliyun.com";
                email[1] = "阿里云";
                return email;
            } else if(emailSubString.equals("163")) {
                email[0] = "http://mail.163.com";
                email[1] = "网易163";
                return email;
            } else if(emailSubString.equals("126")) {
                email[0] = "http://mail.126.com";
                email[1] = "网易126";
                return email;
            } else if(emailSubString.equals("yeah")) {
                email[0] = "http://www.yeah.net";
                email[1] = "网易yeah";
                return email;
            } else if(emailSubString.equals("188")) {
                email[0] = "http://www.188.com";
                email[1] = "网易财富邮";
                return email;
            } else if(emailSubString.equals("yahoo")) {
                email[0] = "http://mail.yahoo.com";
                email[1] = "雅虎";
                return email;
            } else if(!emailSubString.equals("outlook") && !emailSubString.equals("hotmail")) {
                return null;
            } else {
                email[0] = "http://mail.live.com";
                email[1] = "Microsoft";
                return email;
            }
        } else {
            email[0] = "http://mail.qq.com";
            email[1] = "QQ";
            return email;
        }
    }

    public static String moneyFormat(BigDecimal bigDecimal) {
        if(bigDecimal == null) {
            return "0.00";
        } else {
            int intValue = bigDecimal.intValue();
            BigDecimal bigDecimalValue = bigDecimal.subtract(BigDecimal.valueOf((long)intValue));
            String intString = String.valueOf(intValue);
            String bigDecimalString = bigDecimalValue.toString();
            if(bigDecimalValue.toString().length() > 2) {
                bigDecimalString = bigDecimalValue.toString().substring(2, bigDecimalValue.toString().length());
            }

            String money = numberFormat(intString);
            bigDecimalString = bigDecimalFormat(bigDecimalString);
            if(bigDecimalString.length() == 1) {
                bigDecimalString = bigDecimalString + "0";
            } else if(bigDecimalString.length() == 0) {
                bigDecimalString = "00";
            }

            return money + "." + bigDecimalString;
        }
    }

    public static String moneyFormat(BigDecimal bigDecimal, int num) {
        if(bigDecimal == null) {
            return "0.00";
        } else {
            int intValue = bigDecimal.intValue();
            BigDecimal bigDecimalValue = bigDecimal.subtract(BigDecimal.valueOf((long)intValue));
            String intString = String.valueOf(intValue);
            String money = numberFormat(intString);
            String bigDecimalString = bigDecimalValue.toString();
            if(bigDecimalValue.toString().length() > 2) {
                bigDecimalString = bigDecimalValue.toString().substring(2, bigDecimalValue.toString().length());
            }

            bigDecimalString = bigDecimalFormat(bigDecimalString, num);
            return money + "." + bigDecimalString;
        }
    }

    public static String numberFormat(String intString) {
        if(!isNum(intString)) {
            return "0";
        } else {
            int len = intString.length() % 3;
            int forInt = intString.length() / 3;
            String tj = "，";
            String integerValue = "";

            for(int i = 0; i <= forInt; ++i) {
                if(i == 0 && len != 0 && forInt != 0) {
                    integerValue = integerValue + intString.substring(0, len) + tj;
                } else if(i != 0 && i != forInt) {
                    integerValue = integerValue + intString.substring((i - 1) * 3 + len, i * 3 + len) + tj;
                } else if(i == 0 && forInt == 0) {
                    integerValue = integerValue + intString.substring(0, len);
                } else if(i == forInt) {
                    integerValue = integerValue + intString.substring((i - 1) * 3 + len, i * 3 + len);
                }
            }

            return integerValue;
        }
    }

    public static String bigDecimalFormat(String bigDecimalString) {
        if(!isNum(bigDecimalString)) {
            return "";
        } else {
            if(bigDecimalString.length() > 6) {
                bigDecimalString = bigDecimalString.substring(0, 6);
            }

            char[] chs = bigDecimalString.toCharArray();
            int length = bigDecimalString.length();

            for(int i = bigDecimalString.length(); i > 0; --i) {
                String str = String.valueOf(chs[i - 1]);
                if(!str.equals("0")) {
                    break;
                }

                --length;
            }

            bigDecimalString = bigDecimalString.substring(0, length);
            return bigDecimalString;
        }
    }

    public static String bigDecimalFormat(String bigDecimalString, int num) {
        if(!isNum(bigDecimalString)) {
            return "";
        } else {
            char[] chs = bigDecimalString.toCharArray();
            int length = bigDecimalString.length();

            for(int zero = bigDecimalString.length(); zero > 0; --zero) {
                String j = String.valueOf(chs[zero - 1]);
                if(!j.equals("0")) {
                    break;
                }

                --length;
            }

            bigDecimalString = bigDecimalString.substring(0, length);
            if(bigDecimalString.length() >= num) {
                bigDecimalString = bigDecimalString.substring(0, num);
            } else {
                String var6 = "";

                for(int var7 = 0; var7 < num - bigDecimalString.length(); ++var7) {
                    var6 = var6 + "0";
                }

                bigDecimalString = bigDecimalString + var6;
            }

            return bigDecimalString;
        }
    }

    public static void main(String[] args) {
        System.out.println(withdrawString("0.2"));
        System.out.println(withdrawString("0.20"));
        System.out.println(withdrawString(".2"));
        System.out.println(withdrawString(".20"));
        System.out.println(withdrawString("10.2"));
        System.out.println(withdrawString("10.20"));
        System.out.println(withdrawString("10.02"));
        System.out.println(withdrawString("10.0"));
        System.out.println(withdrawString("0.00"));
        System.out.println(withdrawString("1000.2"));
        System.out.println(withdrawString("10000.2"));
        System.out.println(withdrawString("100000.2"));
        System.out.println(withdrawString("1000000.2"));
        System.out.println(withdrawString("a.2"));
        System.out.println(withdrawString("2a"));
        System.out.println(withdrawString("fxxx.2"));
        System.out.println(withdrawString("2"));
        System.out.println(withdrawString("100"));
        System.out.println(withdrawString("abc"));
        System.out.println(withdrawString("672"));
        String[] emails = new String[]{"zzzhc.cn+1@gmail.com", "Memory308", "hacker@hacker.org%\' and 3=3 and \'%\'=\'", "../../../../../../../../boot.ini", null, "", "zzzhc@zzzhc", "zzzhc@zzzhc.com", "zzzhc.cn@gmail.com", "zzzhc-cn@zzzhc.com.cn", "hesx_007@163.com", "yes.v.no@163.com", "1522278231@qq.com"};
        String[] arr$ = emails;
        int len$ = emails.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            String email = arr$[i$];
            boolean pass = isEmail(email);
            System.out.println(email + " is email? " + pass);
            if(pass) {
                subEmailFormat(email);
            }
        }

    }

    public static String subEmailFormat(String nick) {
        String emailSubString = "";
        String emailSubString2 = "";
        Pattern pattern = Pattern.compile("((\\w+)(@\\w+.\\w+))");

        for(Matcher matcher = pattern.matcher(nick); matcher.find(); emailSubString2 = matcher.group(3)) {
            emailSubString = matcher.group(2);
        }

        int lengh = emailSubString.length();
        switch(lengh) {
            case 2:
            case 3:
                emailSubString = emailSubString.substring(0, lengh - 1) + "*";
                break;
            case 4:
            case 5:
                emailSubString = emailSubString.substring(0, lengh - 2) + "**";
                break;
            case 6:
            case 7:
                emailSubString = emailSubString.substring(0, lengh - 3) + "***";
                break;
            default:
                emailSubString = emailSubString.substring(0, 4) + "*****";
        }

        return emailSubString + emailSubString2;
    }

    public static String splitAndFilterString(String input, int length) {
        if(input != null && !input.trim().equals("")) {
            String str = input.replaceAll("\\&[a-zA-Z]{1,10};", "").replaceAll("<[^>]*>", "");
            str = str.replaceAll("[(/>)<]", "");
            int len = str.length();
            if(len <= length) {
                return str;
            } else {
                str = str.substring(0, length);
                str = str + "...";
                return str;
            }
        } else {
            return "";
        }
    }

    public static List<String> getStrings(String str) {
        return getStrings(str, ",");
    }

    public static List<Long> getLongs(String str) {
        if(StringUtils.isBlank(str)) {
            return Lists.newArrayList();
        } else {
            String[] strs = str.split(",");
            ArrayList strList = new ArrayList();

            for(int i = 0; i < strs.length; ++i) {
                String t = strs[i];
                if(isNum(t)) {
                    strList.add(Long.valueOf(strs[i]));
                }
            }

            return strList;
        }
    }

    public static List<Integer> getIntegers(String str) {
        if(StringUtils.isBlank(str)) {
            return Lists.newArrayList();
        } else {
            String[] strs = str.split(",");
            ArrayList strList = new ArrayList();

            for(int i = 0; i < strs.length; ++i) {
                String t = strs[i];
                if(isNumber(t)) {
                    strList.add(Integer.valueOf(strs[i]));
                }
            }

            return strList;
        }
    }

    public static List<BigDecimal> getBigDecimals(String str) {
        if(StringUtils.isBlank(str)) {
            return Lists.newArrayList();
        } else {
            String[] strs = str.split(",");
            ArrayList strList = new ArrayList();

            for(int i = 0; i < strs.length; ++i) {
                String t = strs[i];
                if(withdrawString(t)) {
                    strList.add(BigDecimal.valueOf(Double.valueOf(strs[i]).doubleValue()));
                }
            }

            return strList;
        }
    }

    public static List<String> getStrings(String str, String split) {
        if(StringUtils.isBlank(str)) {
            return Lists.newArrayList();
        } else {
            String[] strs = str.split(split);
            ArrayList strList = new ArrayList();

            for(int i = 0; i < strs.length; ++i) {
                strList.add(strs[i]);
            }

            return strList;
        }
    }

    public boolean CharIsLetter(String word) {
        boolean sign = true;

        for(int i = 0; i < word.length(); ++i) {
            if(!Character.isLetter(word.charAt(i))) {
                sign = false;
            }
        }

        return sign;
    }

    public String substr(String str, int beginIndex, int endIndex) {
        if(isBlank(str)) {
            return "";
        } else if(endIndex == -1) {
            return str.substring(beginIndex);
        } else {
            if(endIndex > str.length()) {
                endIndex = str.length();
            }

            return str.substring(beginIndex, endIndex);
        }
    }

    public String substr(String str, int beginIndex, int endIndex, String endMark) {
        if(isBlank(str)) {
            return "";
        } else if(endIndex == -1) {
            return str.substring(beginIndex);
        } else {
            if(endIndex > str.length()) {
                endIndex = str.length();
            }

            String restr = str.substring(beginIndex, endIndex);
            if(endIndex < str.length()) {
                restr = restr + endMark;
            }

            return restr;
        }
    }

    public String toUpperCase(String str) {
        return isBlank(str)?"":str.toUpperCase();
    }

    public String toLowerCase(String str) {
        return isBlank(str)?"":str.toLowerCase();
    }

}
