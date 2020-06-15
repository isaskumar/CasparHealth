package caspars.qa.base.lib;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;

public class Utilities {
	private static ThreadLocal<String> _OLD_BORDER = new ThreadLocal<String>();
	private static ThreadLocal<WebElement> _OLD_ElEMENT = new ThreadLocal<WebElement>();
	private static ThreadLocal<IOSElement> _OLD_ElEMENT_IOS = new ThreadLocal<IOSElement>();


	private static String get_OLD_BORDER() {
		// return _OLD_BORDER;
		return _OLD_BORDER.get();
	}


	private static WebElement get_OLD_ElEMENT() {
		// return _OLD_ElEMENT;
		return _OLD_ElEMENT.get();
	}


	private static IOSElement get_OLD_ElEMENT_IOS() {
		// return _OLD_ElEMENT;
		return _OLD_ElEMENT_IOS.get();
	}
	// Add for Native App tests
	public static Point getCenter(WebElement element) {
		Dimension size = element.getSize();
		int halfWidth = size.getWidth() / 2;
		int halfHeight = size.getHeight() / 2;
		Point loc = element.getLocation();
		int posX = loc.getX() + halfWidth;
		int posY = loc.getY() + halfHeight;
		Point point = new Point(posX, posY);
		return point;
	}


	public static void highlightElement(WebDriver d, WebElement we) {
		unHighlightElement(d, get_OLD_ElEMENT(), get_OLD_BORDER());
		if (CONSTANTS.HIGHLIGHT_SCRIPT != null)
			if (d instanceof JavascriptExecutor) {
				// Log.debug("Calling Highlight...");
				try {
					String border = (String) ((JavascriptExecutor) d).executeScript(new StringBuilder().append(CONSTANTS.HIGHLIGHT_SCRIPT).append("return highlight(arguments[0]);").toString(), new Object[] { we });
					// Log.debug(new StringBuilder().append("BORDER
					// VALUE::").append(border).toString());
					set_OLD_ElEMENT(we, border);
				} catch (Exception e) {
					set_OLD_ElEMENT(null, null);
					// Log.debug("Exception highlighting element", e);
				}
			} else
				Logs.debug("There is a problem reading Highlight script. Skipping highlighting for now...");
	}


	/// ANDROID
	public static void highlightElementAPPIUM(AppiumDriver<?> d, MobileElement we) {
		unHighlightElement(d, get_OLD_ElEMENT(), get_OLD_BORDER());
		if (CONSTANTS.HIGHLIGHT_SCRIPT != null)
			if (d instanceof JavascriptExecutor) {
				// Log.debug("Calling Highlight...");
				try {
					String border = (String) ((JavascriptExecutor) d).executeScript(new StringBuilder().append(CONSTANTS.HIGHLIGHT_SCRIPT).append("return highlight(arguments[0]);").toString(), new Object[] { we });
					// Log.debug(new StringBuilder().append("BORDER
					// VALUE::").append(border).toString());
					set_OLD_ElEMENT(we, border);
				} catch (Exception e) {
					set_OLD_ElEMENT(null, null);
					// Log.debug("Exception highlighting element", e);
				}
			} else
				Logs.debug("There is a problem reading Highlight script. Skipping highlighting for now...");
	}


	//// IOS
	public static void highlightElementIOS(IOSDriver<IOSElement> d, IOSElement we) {
		unHighlightElementIOS(d, get_OLD_ElEMENT_IOS(), get_OLD_BORDER());
		if (CONSTANTS.HIGHLIGHT_SCRIPT != null)
			if (d instanceof JavascriptExecutor) {
				// Logs.debug("Calling Highlight...");
				try {
					String border = (String) ((JavascriptExecutor) d).executeScript(new StringBuilder().append(CONSTANTS.HIGHLIGHT_SCRIPT).append("return highlight(arguments[0]);").toString(), new Object[] { we });
					// Log.debug(new StringBuilder().append("BORDER
					// VALUE::").append(border).toString());
					set_OLD_ElEMENT_IOS(we, border);
				} catch (Exception e) {
					set_OLD_ElEMENT_IOS(null, null);
					// Log.debug("Exception highlighting element", e);
				}
			} else
				Logs.debug("There is a problem reading Highlight script. Skipping highlighting for now...");
	}


	private static void set_OLD_ElEMENT(WebElement ele, String border) {
		if (ele != null) {
			// _OLD_ElEMENT = ele;
			// _OLD_BORDER = border;
			_OLD_ElEMENT.set(ele);
			_OLD_BORDER.set(border);
		}
	}


	private static void set_OLD_ElEMENT_IOS(IOSElement ele, String border) {
		if (ele != null) {
			// _OLD_ElEMENT = ele;
			// _OLD_BORDER = border;
			_OLD_ElEMENT_IOS.set(ele);
			_OLD_BORDER.set(border);
		}
	}


	private static void unHighlightElement(WebDriver d, WebElement oldElement, String oldBorder) {
		if ((oldElement != null))
			try {
				// Log.debug(new StringBuilder().append("old element
				// found").toString());
				((JavascriptExecutor) d).executeScript(new StringBuilder().append(CONSTANTS.HIGHLIGHT_SCRIPT).append(" unHighlight(arguments[0], arguments[1]);").toString(), new Object[] { oldElement, oldBorder });
				// Log.debug("old element unhighlighted");
			} catch (Exception e) {
				Logs.debug("Unable to unhighlight old element", e);
			} finally {
				set_OLD_ElEMENT(null, null);
			}
		else
			set_OLD_ElEMENT(null, null);
	}


	private static void unHighlightElementIOS(IOSDriver<IOSElement> d, IOSElement oldElement, String oldBorder) {
		if ((oldElement != null)) {
			try {
				// Log.debug(new StringBuilder().append("old element
				// found").toString());
				((JavascriptExecutor) d).executeScript(new StringBuilder().append(CONSTANTS.HIGHLIGHT_SCRIPT).append(" unHighlight(arguments[0], arguments[1]);").toString(), new Object[] { oldElement, oldBorder });
				// Log.debug("old element unhighlighted");
			} catch (Exception e) {
				// Logs.debug("Unable to unhighlight old element", e);
				Logs.printWarning("Unable to unhighlight old element........!");
			} finally {
				set_OLD_ElEMENT_IOS(null, null);
			}
		} else
			set_OLD_ElEMENT_IOS(null, null);
	}
}