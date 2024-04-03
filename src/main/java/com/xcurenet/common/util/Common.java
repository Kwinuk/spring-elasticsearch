package com.xcurenet.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.*;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Slf4j
public class Common {

    public static final String UTF8 = "UTF-8";

    public static final String EUCKR = "EUC-KR";

    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    public static final int SIZEOF_SHORT = Short.SIZE / Byte.SIZE;

    public static final String EMPTY = "";

    public static final String EMPTY_LINE = "\n";

    public static final String SESSION_CREDENTIAL = "_USERCREDENTIAL_";

    public static final String SHA_256 = "SHA-256";

    public static final String SHA_512 = "SHA-512";

    public static final char[] HEXARRAY = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static boolean isWindow() {
        String os_name = System.getProperty("os.name").toLowerCase();
        if (os_name.startsWith("windows")) return true;
        else return false;
    }

    public static boolean isOrEquals(Object source, String[] target) {
        if (source == null) return false;
        boolean result = false;
        for (int i = 0; i < target.length; i++) {
            result = source.equals(Common.nvl(target[i]));
            if (result) break;
        }
        return result;
    }

    /**
     * @param target
     * @return
     */
    public static boolean isOrEquals(Object source, Object... target) {
        if (source == null) return false;
        boolean result = false;
        for (int i = 0; i < target.length; i++) {
            result = source.equals(Common.nvl(target[i]));
            if (result) break;
        }
        return result;
    }

    /**
     * @param target
     * @return
     */
    public static boolean isEquals(Object source, Object target) {
        return ((source == null) ? false : (target == null) ? true : source.equals(target));
    }

    /**
     * @param target
     * @return
     */
    public static boolean isNotEquals(Object source, Object target) {
        return ((source == null) ? true : (target == null) ? false : !source.equals(target));
    }

    /**
     * Java String isEmpty This Java String isEmpty shows how to check whether
     * the given string is empty or not using isEmpty method of Java String
     * class.
     *
     * @param target
     * @return
     */
    public static boolean isEmpty(Object target) {
        return nvl(target).isEmpty();
    }

    /**
     * Java String isEmpty This Java String isEmpty shows how to check whether
     * the given string is empty or not using isEmpty method of Java String
     * class.
     *
     * @param target
     * @return
     */
    public static boolean isNotEmpty(Object target) {
        return !isEmpty(target);
    }

    /**
     * Null to Empty String
     *
     * @param target
     * @return
     */
    public static String nvl(Object target) {
        return nvl(target, Common.EMPTY);
    }

    /**
     * Null to Empty String
     *
     * @param target
     * @return
     */
    public static String nvl(Object target, String defaultStr) {
        String result = defaultStr;
        if (target != null) {
            if (!String.valueOf(target).toLowerCase().equals("null")) return String.valueOf(target);
        }
        return result;
    }

    /**
     * Null to Empty String
     *
     * @param target
     * @return
     */
    public static int nvz(Object target) {
        return nvz(target, 0);
    }

    /**
     * Null to Empty String
     *
     * @param target
     * @return
     */
    public static int nvz(Object target, int defaultNum) {
        int result = defaultNum;
        if (target != null) {
            if (!String.valueOf(target).toLowerCase().equals("null") && !String.valueOf(target).equals("")) {
                try {
                    return Integer.parseInt(String.valueOf(target));
                } catch (Exception e) {
                    return defaultNum;
                }
            }
        }
        return result;
    }

    /**
     * Null to Empty String
     *
     * @param target
     * @return
     */
    public static short nvs(Object target) {
        return nvs(target, (short) 0);
    }

    /**
     * Null to Empty String
     *
     * @param target
     * @return
     */
    public static short nvs(Object target, short defaultNum) {
        short result = defaultNum;
        if (target != null) {
            if (!String.valueOf(target).toLowerCase().equals("null") && !String.valueOf(target).equals("")) return Short.parseShort(String.valueOf(target));
        }
        return result;
    }

    /**
     * Null to Empty String
     *
     * @param target
     * @return
     */
    public static long nvn(Object target) {
        return nvn(target, 0L);
    }

    /**
     * Null to Empty String
     *
     * @param target
     * @return
     */
    public static long nvn(Object target, long defaultNum) {
        long result = defaultNum;
        if (target != null) {
            if (!String.valueOf(target).toLowerCase().equals("null") && !String.valueOf(target).equals("")) return Long.parseLong(String.valueOf(target));
        }
        return result;
    }

    /**
     * Trim All
     *
     * @param target
     * @return
     */
    public static String trimAll(Object target) {
        return nvl(target).replaceAll("\\s+", "");
    }

    /**
     * 특정 문자열의 자릿수 만큼 원하는 문자열을 오른쪽에 채워 넣는다.
     *
     * @param nValue
     * @param nLength
     * @param nDefault
     * @return
     */
    public static String formatString(String nValue, int nLength, String nDefault) {
        String result = nValue;
        if (nValue.length() < nLength) {
            int i = nLength - nValue.length();
            for (int j = 0; j < i; j++) {
                result += nDefault;
            }
        }
        return result;
    }

    /**
     * 지정된 자리수 만큼 왼쪽에 값을 채워 넣는다.
     *
     * @param str
     * @param len
     * @param addStr
     * @return
     */
    public static String lPad(Object str, int len, String addStr) {
        String result = "";
        if (str == null) result = "";
        else result = String.valueOf(str);

        int templen = len - result.length();
        for (int i = 0; i < templen; i++) {
            result = addStr + result;
        }
        return result;
    }

    public static String rPad(String str, int size, String fStr) {

        byte[] b = str.getBytes();
        int len = b.length;
        int tmp = size - len;
        for (int i = 0; i < tmp; i++) {
            str += fStr;
        }
        return str;
    }

    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {
        }
        return false;
    }

    public static String numberFormatter(String str) {
        if (str == null || str.equals("")) str = "0";
        NumberFormat nf = NumberFormat.getInstance();
        return nf.format(Double.valueOf(str));
    }

    public static String numberFormatter(Double str) {
        NumberFormat nf = NumberFormat.getInstance();
        return nf.format(str);
    }

    public static String numberFormatter(long str) {
        NumberFormat nf = NumberFormat.getInstance();
        return nf.format(str);
    }

    /**
     * SNMP %(률) 계산
     * fileSize
     * @return
     */
    public static String convertSnmpVal(int rate) {
        float rateF = rate;
        return Common.nvl((float) (Math.round((rateF / 100) * 100) / 100.0));
    }

    public static String convertFileSize(Object size) {
        return convertFileSize(nvn(size));
    }

    public static String convertSnmpSize(Object size) {
        return convertSnmpSize(nvn(size));
    }

    /**
     * 용량계산
     *
     * byte
     * @return
     */
    public static String convertSnmpSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[] {" KB", " MB", " GB", " TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + units[digitGroups];
    }

    /**
     * 용량계산
     *
     * byte
     * @return
     */
    public static String convertFileSize(long size) {
        if (size <= 0) return "0 KB";
        final String[] units = new String[] {" Byte", " KB", " MB", " GB", " TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + units[digitGroups];
    }

    /**
     * YYYYMMDD 형식의 문자열을 YYYY-MM-DD 형식으로 변경
     *
     * @param fromDate
     * @return
     */
    public static String formatDate(String fromDate) {
        if (fromDate != null && fromDate.length() == 8) {
            return fromDate.substring(0, 4) + "-" + fromDate.substring(4, 6) + "-" + fromDate.substring(6, 8);
        }
        return fromDate;
    }

    /**
     * YYYYMMDD 형식의 문자열을 MM/DD 형식으로 변경
     *
     * @param fromDate
     * @return
     */
    public static String formatDate2(String fromDate) {
        if (fromDate != null && fromDate.length() == 8) {
            return fromDate.substring(4, 6) + "/" + fromDate.substring(6, 8);
        }
        return fromDate;
    }

    /**
     * yyyyMMddHHmmss 형식의 문자열을 yyyy-MM-dd HH:mm:ss 형식으로 변경
     *
     * fromDate
     * @return
     */
    public static String formatDate3(String orgDate) throws Exception {
        DateTimeFormatter org = DateTimeFormat.forPattern("yyyyMMddHHmmss");
        DateTimeFormatter newF = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime date = DateTime.parse(orgDate.replaceAll("-", "").replaceAll(" ", ""), org);
        return newF.print(date);
    }

    /**
     * YYYYMMDD 형식의 문자열을 YYYY-MM 형식으로 변경
     *
     * @param fromDate
     * @return
     */
    public static String formatMonth(String fromDate) {
        if (fromDate != null && (fromDate.length() == 8 || fromDate.length() == 6)) {
            return fromDate.substring(0, 4) + "-" + fromDate.substring(4, 6);
        }
        return fromDate;
    }

    public static int parseIp(String address) {
        if (Common.isEmpty(address)) return 0;
        int result = 0;
        for (String part : address.split(Pattern.quote("."))) {
            result = result << 8;
            result |= Integer.parseInt(part);
        }
        return result;
    }

    /**
     * 초를 시,분,초로 변환
     *
     * @param difference
     * @return
     */
    public static String getTimeFormat(long difference) {
        if (difference == 0 || difference < 0) return "-";

        if (TimeUnit.SECONDS.toHours(difference) > 0) return String.format("%02d:%02d:%02d", TimeUnit.SECONDS.toHours(difference), TimeUnit.SECONDS.toMinutes(difference) - TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(difference)), TimeUnit.SECONDS.toSeconds(difference) - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(difference)));
        else return String.format("%02d:%02d", TimeUnit.SECONDS.toMinutes(difference) - TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(difference)), TimeUnit.SECONDS.toSeconds(difference) - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(difference)));

        // final int[] TIME_UNIT = { 3600, 60, 1 };
        // final String[] TIME_UNIT_NAME = { ":", ":", "" };
        // String tmp = "";
        // for ( int i = 0 ; i < TIME_UNIT.length ; i++ )
        // {
        // if ( difference / TIME_UNIT[i] > 0 ) tmp += lPad ( ( difference /
        // TIME_UNIT[i] ), 2, "0" ) + TIME_UNIT_NAME[i];
        // difference %= TIME_UNIT[i];
        // }
        // if ( isEmpty ( tmp ) ) tmp = "0";
        // return tmp;
    }

    public static String getCurrentHour() throws Exception {
        DateTimeFormatter yyyyMMdd = DateTimeFormat.forPattern("HH");
        return yyyyMMdd.print(System.currentTimeMillis());
    }

    public static String getCurrentFullHour() throws Exception {
        DateTimeFormatter yyyyMMdd = DateTimeFormat.forPattern("yyyy-MM-dd HH");
        return yyyyMMdd.print(System.currentTimeMillis());
    }

    public static String getCurrentTime(String format) throws Exception {
        DateTimeFormatter yyyyMMdd = DateTimeFormat.forPattern(format);
        return yyyyMMdd.print(System.currentTimeMillis());
    }

    public static String getCurrentDate() throws Exception {
        DateTimeFormatter yyyyMMdd = DateTimeFormat.forPattern("yyyyMMdd");
        return yyyyMMdd.print(System.currentTimeMillis());
    }

    public static String getCurrentDateF() throws Exception {
        DateTimeFormatter yyyyMMdd = DateTimeFormat.forPattern("yyyy-MM-dd");
        return yyyyMMdd.print(System.currentTimeMillis());
    }

    public static String getCurrentTime() throws Exception {
        DateTimeFormatter yyyyMMdd = DateTimeFormat.fullDateTime();
        return yyyyMMdd.print(System.currentTimeMillis());
    }

    public static String getDateTimeFormat() throws Exception {
        return getDateTime(System.currentTimeMillis(), "yyyyMMddHHmmss");
    }

    public static String getDateTime(long time) throws Exception {
        return getDateTime(time, "yyyy-MM-dd HH:mm:ss");
    }

    public static String getDateTime(long time, String format) throws Exception {
        if (time == 0) return "-";
        DateTimeFormatter yyyyMMdd = DateTimeFormat.forPattern(format);
        return yyyyMMdd.print(time);
    }

    public static String getYesterdayDate() throws Exception {
        return plusDays(getCurrentDate(), -1);
    }

    public static String getTime() {
        try {
            return getDateTime(System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long getTime(String time, String format) {
        DateFormat dateformat = new SimpleDateFormat(format);
        try {
            return dateformat.parse(time).getTime();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    public static String getBeforeDay(int beforeDate) {
        Calendar week = Calendar.getInstance();
        week.add(Calendar.DATE, -beforeDate);
        return new SimpleDateFormat("yyyy-MM-dd").format(week.getTime());
    }

    public static String getBeforeMonth(int beforeDate) {
        Calendar week = Calendar.getInstance();
        week.add(Calendar.MONTH, -beforeDate);
        return new SimpleDateFormat("yyyy-MM-dd").format(week.getTime());
    }

    public static String getBeforeYear(int beforeDate) {
        Calendar week = Calendar.getInstance();
        week.add(Calendar.YEAR, -beforeDate);
        return new SimpleDateFormat("yyyy-MM-dd").format(week.getTime());
    }

    public static String getYearFirstDay(String date) throws Exception {
        DateTimeFormatter yyyyMMdd = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTimeFormatter MM = DateTimeFormat.forPattern("yyyy-");
        return MM.print(DateTime.parse(date, yyyyMMdd)) + "01-01";
    }

    public static String getMonthFirstDay(String date) throws Exception {
        DateTimeFormatter yyyyMMdd = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTimeFormatter MM = DateTimeFormat.forPattern("yyyy-MM-");
        return MM.print(DateTime.parse(date, yyyyMMdd)) + "01";
    }

    public static long getFullTime(String date) throws Exception {
        DateTimeFormatter yyyyMMdd = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        return DateTime.parse(date, yyyyMMdd).getMillis();
    }

    public static long getTime(String date) throws Exception {
        DateTimeFormatter yyyyMMdd = DateTimeFormat.forPattern("yyyyMMdd");
        return DateTime.parse(date.replaceAll("-", ""), yyyyMMdd).getMillis();
    }

    public static String getYear(String date) throws Exception {
        DateTimeFormatter yyyyMMdd = DateTimeFormat.forPattern("yyyyMMdd");
        DateTimeFormatter yyyy = DateTimeFormat.forPattern("yyyy");
        return yyyy.print(DateTime.parse(date.replaceAll("-", ""), yyyyMMdd));
    }

    public static String getMonth(String date) throws Exception {
        DateTimeFormatter yyyyMMdd = DateTimeFormat.forPattern("yyyyMMdd");
        DateTimeFormatter MM = DateTimeFormat.forPattern("MM");
        return MM.print(DateTime.parse(date.replaceAll("-", ""), yyyyMMdd));
    }

    public static String getDay(String date) throws Exception {
        DateTimeFormatter yyyyMMdd = DateTimeFormat.forPattern("yyyyMMdd");
        DateTimeFormatter dd = DateTimeFormat.forPattern("dd");
        return dd.print(DateTime.parse(date.replaceAll("-", ""), yyyyMMdd));
    }

    public static int diffOfDate(String begin, String end) {
        DateTimeFormatter yyyyMMdd = DateTimeFormat.forPattern("yyyyMMdd");
        DateTime sdt = DateTime.parse(begin.replaceAll("-", ""), yyyyMMdd);
        DateTime edt = DateTime.parse(end.replaceAll("-", ""), yyyyMMdd);
        return Days.daysBetween(sdt, edt).getDays();
    }

    public static int diffOfMonth(String begin, String end) {
        DateTimeFormatter yyyyMM = DateTimeFormat.forPattern("yyyyMM");
        DateTime sdt = DateTime.parse(begin.replaceAll("-", "").substring(0, 6), yyyyMM);
        DateTime edt = DateTime.parse(end.replaceAll("-", "").substring(0, 6), yyyyMM);
        return Months.monthsBetween(sdt, edt).getMonths();
    }

    public static String plusMonth(String orgDate, int plusMonth) {
        DateTimeFormatter yyyyMMdd = DateTimeFormat.forPattern("yyyyMMdd");
        DateTime date = DateTime.parse(orgDate.replaceAll("-", ""), yyyyMMdd);
        return yyyyMMdd.print(date.plusMonths(plusMonth));
    }

    public static String plusWeek(String orgDate, int plusWeek) {
        DateTimeFormatter yyyyMMdd = DateTimeFormat.forPattern("yyyyMMdd");
        DateTime date = DateTime.parse(orgDate.replaceAll("-", ""), yyyyMMdd);
        return yyyyMMdd.print(date.plusWeeks(plusWeek));
    }

    public static String plusDays(String orgDate, int plusDay) {
        DateTimeFormatter yyyyMMdd = DateTimeFormat.forPattern("yyyyMMdd");
        DateTime date = DateTime.parse(orgDate.replaceAll("-", ""), yyyyMMdd);
        return yyyyMMdd.print(date.plusDays(plusDay));
    }

    public static String plusDaysF(String orgDate, int plusDay) {
        DateTimeFormatter yyyyMMdd = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime date = DateTime.parse(orgDate, yyyyMMdd);
        return yyyyMMdd.print(date.plusDays(plusDay));
    }

    public static String plusHour(String orgDate, int plusHour) throws Exception {
        return plusHour(orgDate, plusHour, "yyyyMMddHH");
    }

    public static String plusHour(String orgDate, int plusHour, String format) throws Exception {
        DateTimeFormatter yyyyMMddHH = DateTimeFormat.forPattern(format);
        DateTime date = DateTime.parse(orgDate.replaceAll("-", "").replaceAll(" ", ""), yyyyMMddHH);
        return yyyyMMddHH.print(date.plusHours(plusHour));
    }

    public static String plusHourFormat(String orgDate, int plusHour) throws Exception {
        DateTimeFormatter mmddhh = DateTimeFormat.forPattern("MM/dd HH");
        DateTimeFormatter yyyyMMddHH = DateTimeFormat.forPattern("yyyyMMddHH");
        DateTime date = DateTime.parse(orgDate.replaceAll("-", "").replaceAll(" ", ""), yyyyMMddHH);
        return mmddhh.print(date.plusHours(plusHour));
    }

    public static String plusMinute(String dt, int plusMinute) throws Exception {
        DateTimeFormatter yyyyMMddHHmmss = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime date = DateTime.parse(dt, yyyyMMddHHmmss);
        return yyyyMMddHHmmss.print(date.plusMinutes(plusMinute));
    }

    public static String sha1(String file) {
        String result = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(file));
            result = DigestUtils.sha1Hex(fis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fis);
        }
        return result;
    }

    public static String sha512(String path) {
        String result = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(path));
            result = DigestUtils.sha512Hex(fis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fis);
        }
        return result;
    }

    /**
     * Hash md5sum
     *
     * @param filePath
     * @return
     * @throws Exception
     */
    public static String md5sum(String filePath) throws Exception {
        FileInputStream fis = null;
        StringBuffer sb = new StringBuffer();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(filePath);

            byte[] dataBytes = new byte[1024];
            int nread = 0;
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }
            byte[] mdbytes = md.digest();
            for (int i = 0; i < mdbytes.length; i++) {
                sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fis);
        }
        return sb.toString();
    }

    public static byte[] digest(final String algorithm, final InputStream is) throws IOException, NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance(algorithm);
        final byte[] buf = new byte[4096];
        int n = 0;
        while ((n = is.read(buf)) != -1) {
            digest.update(buf, 0, n);
        }
        return digest.digest();
    }

    public static byte[] digest(final String algorithm, final byte[]... inputs) throws IOException, NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance(algorithm);
        for (final byte[] input : inputs) {
            digest.update(input);
        }
        return digest.digest();
    }

    public static byte[] digest(final String algorithm, final String... inputs) throws NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance(algorithm);
        for (final String input : inputs) {
            digest.update(input.getBytes());
        }
        return digest.digest();
    }

    public static byte[] toHexToBytes(String s) {
        if ((s == null) || (s.length() == 0)) {
            return null;
        }
        int size = (int) Math.ceil(s.length() / 2.0D);
        String hex = StringUtils.leftPad(s, size * 2, "0");
        byte[] b = new byte[size];
        for (int i = 0; i < b.length; i++) {
            b[i] = ((byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16));
        }
        return b;
    }

    public static String toHexString(final long n, final int length) {
        return String.format("%0" + length + "x", n);
    }

    public static String toHexString(final byte[] b) {
        if (b == null) {
            return null;
        }

        final char[] hexChars = new char[b.length * 2];
        int v;
        for (int j = 0; j < b.length; j++) {
            v = b[j] & 0xFF;
            hexChars[j * 2] = HEXARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEXARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static void sleep(final long millis) {
        try {
            Thread.sleep(millis);
        } catch (final InterruptedException e) {
        }
    }

    public static <T> Collection<T> add(final Collection<T> collection, final Collection<T> item) {
        if (item != null) {
            collection.addAll(item);
        }
        return collection;
    }

    public static <T> Collection<T> add(final Collection<T> collection, final T item) {
        if (item != null) {
            collection.add(item);
        }
        return collection;
    }

    public static String join(List<String> collection, String separator) {
        if (collection == null) return EMPTY;
        StringBuffer _sb = new StringBuffer();
        for (int i = 0; i < collection.size(); i++) {
            if ((i + 1) >= collection.size()) _sb.append(collection.get(i));
            else _sb.append(collection.get(i)).append(separator);
        }
        return _sb.toString();
    }

    public static String join(String [] collection, String separator) {
        if (collection == null) return EMPTY;
        StringBuffer _sb = new StringBuffer();
        for (int i = 0; i < collection.length; i++) {
            if ((i + 1) >= collection.length) _sb.append(collection[i]);
            else _sb.append(collection[i]).append(separator);
        }
        return _sb.toString();
    }

    public static String decodeText(final byte[] b, final int offset) {
        if (b != null && b.length >= offset + 2) {
            return toString(b, offset + 2, toShort(b, offset), UTF8);
        }
        return null;
    }

    public static String toString(final byte[] b) {
        return toString(b, UTF8);
    }

    public static String toString(final byte[] b, final String charset) {
        if (b == null) {
            return null;
        }
        return toString(b, 0, b.length, charset);
    }

    public static String toString(final byte[] b, final int offset, final String charset) {
        return b != null ? toString(b, offset, b.length - offset, charset) : null;
    }

    public static String toString(final byte[] b, final int offset, final int len, final String charset) {
        try {
            if (b != null && b.length >= offset + len) {
                if (isEquals(charset.toLowerCase(), "cp-850")) {
                    return new String(b, offset, len, "cp850");
                } else {
                    return new String(b, offset, len, charset);
                }
            }
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int toShort(final byte[] bytes, final int offset) {
        return toShort(bytes, offset, SIZEOF_SHORT);
    }

    public static int toShort(final byte[] bytes, final int offset, final int length) {
        if (length != SIZEOF_SHORT || offset + length > bytes.length) {
            throw new RuntimeException();
        }
        int n = 0;
        n ^= bytes[offset] & 0xFF;
        n <<= 8;
        n ^= bytes[offset + 1] & 0xFF;
        return n;
    }
}
