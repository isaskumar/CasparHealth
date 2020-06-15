package caspars.qa.base.lib;

import java.io.InputStream;

import org.apache.commons.lang3.SystemUtils;

public class CONSTANTS {
	public static final String ANDROID_NATIVE = "Android";
	public static final String ANDROID_WebBrowser = "Android_WebBrowser";
	public static final String CHROME = "chrome";
	public static final char DATA_SEPARATOR = 94;
	public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String EDGE = "edge";
	public static final char ESCAPE_CHAR = 126;
	public static final String FIREFOX = "firefox";
	public static final String HIGHLIGHT_SCRIPT;
	public static final String IEXPLORE = "internet explorer";
	public static final String IOS = "iOS - Emulator";
	public static final String IOS_NATIVE = "iOS";
	public static final String JAVA_HOME = SystemUtils.JAVA_HOME;
	public static final String KOBITON_ENVIRONMENT = "KOBITON";
	public static final String LOCAL_ENVIRONMENT = "LOCAL";
	public static final String MAC = "MAC";
	public static final int MAXRETRYCOUNT = 1;
	public static final String NATIVE_APP = "NATIVE";
	public static final String OPERA = "opera";
	public static final String OS_ARCH = (SystemUtils.OS_ARCH == null) ? "UNKNOWN" : SystemUtils.OS_ARCH.toUpperCase();
	public static final String OS_NAME = (SystemUtils.OS_NAME == null) ? "UNKNOWN" : SystemUtils.OS_NAME.toUpperCase();
	public static final String OS_VERSION = (SystemUtils.OS_VERSION == null) ? "UNKNOWN" : SystemUtils.OS_VERSION.toUpperCase();
	public static final String OS_INFO = "OS Name : " + OS_NAME + " - OS Version : " + OS_VERSION + " - OS Arch: " + OS_ARCH;
	public static final char PLACEHOLDER_BEGIN = 123;
	public static final char PLACEHOLDER_END = 125;
	public static final String PROJECTID = "projectID";
	public static final String REMOTE_ENVIRONMENT = "REMOTE";
	public static final String LOCAL_APP = "LOCAL_APP";
	public static final String SAFARI = "safari";
	public static final String SAUCE_ENVIRONMENT = "SAUCELABS";
	public static final String SCR_MODE_BROWSER = "BROWSER";
	public static final String SCR_MODE_DESKTOP = "DESKTOP";
	public static final String TEMP_DIR = SystemUtils.JAVA_IO_TMPDIR;
	public static final String TESTSETID = "testSetID";
	public static final String UDID = "udid";
	public static final String WIN = "WIN";
	public static final String WINDOWS = "Windows";
	static {
		String s = null;
		try {
			InputStream is = CONSTANTS.class.getResourceAsStream("/highlight.js");
			s = is.toString();
		} catch (Exception e) {
			Logs.debug("Error reading file highlight.js", e);
			s = null;
		}
		HIGHLIGHT_SCRIPT = s;
	}
}