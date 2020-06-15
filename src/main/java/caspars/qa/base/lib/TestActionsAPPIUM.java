package caspars.qa.base.lib;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestContext;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidElement;

public class TestActionsAPPIUM extends Base {
	private static int counter = 0;
	protected String aFilePath;
	// #Drives -----
	protected AppiumDriver<MobileElement> appiumD = null;
	protected String aScreenshotPath;
	// # Elements -----
	protected ITestContext c;
	// #Drives -----
	// # Elements -----
	protected MobileElement mobileEL = null;
	protected Properties p;
	protected int timeoutDefault = 30;
	protected Transactions transactions;

	/// ## -------- constructors----------##//
	public TestActionsAPPIUM(AppiumDriver<MobileElement> appiumD, ITestContext c, String propFile) throws IOException {
		super();
		this.appiumD = appiumD;
		this.c = c;
		transactions = new Transactions();
		String outDirStr = c.getOutputDirectory();
		createDir(outDirStr);
		aScreenshotPath = c.getOutputDirectory() + "/../html/" + Logs.relativeScreenshotPath;
		createDir(aScreenshotPath);
		aFilePath = c.getOutputDirectory() + "/../html/" + Logs.relativeFilesPath;
		createDir(aFilePath);
		p = new Properties();
		p.load(this.getClass().getResourceAsStream(propFile));
	}


	/// ## -------- constructors----------##//
	private void createDir(String DirPath) {
		File outDir = new File(DirPath);
		File dir = outDir.getParentFile();
		if (!dir.exists()) {
			Logs.debug(dir.getAbsolutePath() + " does not exist -> create directory...");
			dir.mkdirs();
		}
		if (!outDir.exists()) {
			Logs.debug(DirPath + " does not exist -> create directory...");
			outDir.mkdir();
		}
	}


	public void endTransaction(String name) {
		transactions.end(name);
	}


	// ### -----GET DRIVER
	public AppiumDriver<MobileElement> getAppiumDriver() {
		return appiumD;
	}


	public Object getAttribute(String nomeAttribute) {
		return c.getAttribute(nomeAttribute);
	}


	private String getCounter() {
		counter++;
		if (counter < 10) {
			return "0" + counter;
		} else {
			return String.valueOf(counter);
		}
	}





	// ####-------------- GET OBJECT ---------------------------
	private MobileElement getObjectByXPath(String path, String el) {
		MobileElement me = null;
		try {
			me = appiumD.findElement(By.xpath(path));
			Utilities.highlightElement(appiumD, me);
		} catch (TimeoutException e) {
			failMobile("LOCATE THE OBJECT FAILED (getObjectByXPath): " + el + "->" + path);
		}
		return me;
	}

	public String getCurrentURL() {
		appiumD.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
		String currentURL = null;
		try {
			currentURL = appiumD.getCurrentUrl();
			// Logs.printACT("Get Current URL of page:", currentURL);
		} catch (Exception e) {
			failMobile("FAIL -> getCurrentURL of page", e);
		}
		return currentURL;
	}


	// ####-------------- GET OBJECT FINISH ---------------------------
	// ####-------------- TEST ACTIONS START---------------------------

	public String SplitURL(String url) {

		Pattern pattern = Pattern.compile("(https?://)([^:^/]*)(:\\d*)?(.*)?");
		Matcher matcher = pattern.matcher(url);
		matcher.find();

		String uri = matcher.group(4);
		String uri2 = matcher.group(2);

		uri = uri.replace("/", "");
		uri = uri.substring(0, 5);
		// uri2=uri2.substring(4,10);

		if (uri2.contains("oakley")) {
			return ("." + uri);

		} else {
			return "";

		}
	}

	public Object getProperty(String key) {
		String val = null;
		try {
			val = p.getProperty(key);
		} catch (Exception e) {
			val = p.getProperty(key);
		}
		if (val == null) {
			val = p.getProperty(key);
		}
		if (val == null) {
			Logs.printFail("ERROR --> key: " + key + " - value: " + val);
		}
		return val;
	}

	public List<MobileElement> listofElements(String locate) {
		return appiumD.findElements(By.className(locate));
	}

	public MobileElement findElementAll(String el) throws Exception {
		String locate;
		try {
			locate = p.getProperty(el + SplitURL(getCurrentURL()));
		} catch (Exception e) {
			locate = p.getProperty(el);
		}
		if (locate == null) {
			locate = p.getProperty(el);
		}

		try {
			return appiumD.findElement(By.cssSelector(locate));
		} catch (WebDriverException e) {}
		try {
			return appiumD.findElement(By.xpath(locate));
		} catch (WebDriverException e) {}
		try {
			return (MobileElement) appiumD.findElements(By.xpath(locate));
		} catch (WebDriverException e) {}
		try {
			return appiumD.findElement(By.className(locate));
		} catch (WebDriverException e) {}
		try {
			return appiumD.findElement(By.id(locate));
		} catch (WebDriverException e) {}
		try {
			return appiumD.findElement(By.linkText(locate));
		} catch (WebDriverException e) {}
		try {
			return appiumD.findElement(By.tagName(locate));
		} catch (WebDriverException e) {}
		try {
			return appiumD.findElement(By.partialLinkText(locate));
		} catch (WebDriverException e) {}
		throw new Exception("Element Cannot Be Located");
	}



	public void click(String el) {
		String path = "";
		try {
			path = p.getProperty(el);
		} catch (Exception e) {
			Logs.printFail("It's not possible to find the element..");
		}


		MobileElement me = null;
		try {
			me = findElementAll(el);
			try {
				me.click();
			} catch (WebDriverException e) {
				String msg = e.getMessage();
				Logs.printError(msg);
				if (msg.contains("not clickable")) {

					Logs.printACT("CLICK", el + " (" + path + ")  is NOT clickable -> scroll the page");
					me.click();
					sleep(500);
				} else {

					failMobile("ERROR --> CLICK :" + el + "(path = " + path + "): " + e.toString());
					throw e;
				}
			}
			Logs.printACT("CLICK", el + " (" + path + ")");
		} catch (Exception e) {
			failMobile("ERROR -> CLICK " + el + " (path: " + path + ")", e);
		}
	}

	public void scrollDown(int scrollSize) {
		JavascriptExecutor jse = appiumD;
		jse.executeScript("window.scrollBy(0," + scrollSize + ")", "");
	}

	public boolean checkIfContains(String external, String internal) {
		boolean result = false;
		try {
			if (external.toLowerCase().contains(internal.toLowerCase())) {
				Logs.printCHK(true, "Text \"" + internal + "\" found in " + external + " ");
				// return true;
				result = true;
			} else {
				Logs.printCHK(false, "Expected \"" + internal + "\" is not found in \"" + external + "\" ");
				addVerificationFailure(new Throwable("Expected \"" + internal + "\" but was not there! "));
				// return false;
				result = false;
			}
		} catch (Exception e) {
			Logs.printCHK(false, "Expected \"" + internal + "\" is not found in \"" + external + "\" ");
			addVerificationFailure(new Throwable("Expected \"" + internal + "\" but was not there! "));
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	public boolean checkIfNOTContains(String external, String internal) {
		if (external.toLowerCase().contains(internal.toLowerCase())) {
			Logs.printCHK(false, "Text \"" + internal + "\" found in " + external + " ");
			addVerificationFailure(new Throwable("NOT-Expected \"" + internal + "\" but found..!"));
			return false;
		} else {
			Logs.printCHK(true, "Expected \"" + internal + "\" is not found in \"" + external + "\" ");
			return true;
		}
	}

	public void clearAndSetText(String el, String text) {
		String xpath = null;
		try {
			try {
				xpath = p.getProperty(el + SplitURL(getCurrentURL()));
			} catch (Exception e) {
				xpath = p.getProperty(el);
			}
			if (xpath == null) {
				xpath = p.getProperty(el);
			}

			MobileElement we = getObjectByXPath(xpath, el);
			we.clear();
			we.sendKeys(text);
			Logs.printACT("clearText", "Cleared text in element: " + el + " (" + xpath + ")" + "and set to: " + text + ".");
		} catch (Exception e) {
			failMobile("Clear text: " + el + " (" + xpath + ")", e);  // TO DO: Necessario per insertSku();
		}
	}

	public void setText(String el, String text) {
		appiumD.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
		String xpath = "";
		try {
			xpath = p.getProperty(el);
		} catch (Exception e) {
			Logs.printWarning("Element is not selectable!");
		}
		if (xpath == null) {
			xpath = p.getProperty(el);
		}

		try {

			getObjectByXPath(xpath, el).sendKeys(text);
			Logs.printACT("setText", "Set " + text + " in element: " + el + " (" + xpath + ")");
		} catch (Exception e) {
			failMobile("setText: " + el + " (" + xpath + ")", e);
		}
	}

	public boolean storeVisibility(String el, int timeout) {
		String xpath = "";
		try {
			xpath = p.getProperty(el);
		} catch (Exception e) {
			Logs.printFail("Element is not selectable!");
		}

		boolean ret = false;
		try {
			WebDriverWait wait = new WebDriverWait(appiumD, timeout);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
			Utilities.highlightElement(appiumD, mobileEL);
			Logs.printACT("STORE VISIBILITY", el + " (" + xpath + ")" + " is visible");
			ret = true;
		} catch (TimeoutException e) {
			Logs.printACT("STORE VISIBILITY", el + " (" + xpath + ")" + " is NOT visible");
			ret = false;
		}
		return ret;
	}

	public boolean verifyVisibility(String el, boolean visibility, boolean enableTestVerification) {
		String xpath = (String) getProperty(el);
		WebElement we = null;
		boolean ret = false;
		boolean visible = false;
		boolean fail = false;
		StringBuilder failMessage = new StringBuilder();
		try {
			we = getObjectByXPath(xpath, el);
			if (we.isDisplayed() && we.isEnabled()) {
				Utilities.highlightElement(appiumD, we);
				visible = true;
			}
		} catch (TimeoutException e) {
		} catch (Exception e) {
			if (enableTestVerification) {
				addVerificationFailure(e);
				fail("Error during verify Visibility of element " + el + "(xpath = " + xpath + ")", e);
			} else {
				fail = true;
				failMessage.append(e.toString());
			}
		}
		if (visibility) {
			if (visible) {
				if (enableTestVerification) {
					Logs.printCHK(true, "Element " + el + "(xpath = " + xpath + ") is visible");
				} else {
					Logs.printACT("CHECK VISIBILITY", "Element " + el + "(xpath = " + xpath + ") is visible");
				}
				ret = true;
			} else {
				if (enableTestVerification) {
					Logs.printCHK(false, "Element " + el + "(xpath = " + xpath + ") is NOT visible");
					addVerificationFailure(new Throwable("Element " + el + "(xpath = " + xpath + ") is NOT visible"));
				} else if (fail) {
					Logs.printACT("CHECK VISIBILITY", "Error during verify Visibility of element " + el + "(xpath = "
							+ xpath + "): " + failMessage.toString());
				} else {
					Logs.printACT("CHECK VISIBILITY", "Element " + el + "(xpath = " + xpath + ") is NOT visible");
				}
				ret = false;
			}
		} else {
			if (visible) {
				if (enableTestVerification) {
					Logs.printCHK(false, "Element " + el + "(xpath = " + xpath + ") is visible");
					addVerificationFailure(new Throwable("Element " + el + "(xpath = " + xpath + ") is visible"));
				} else {
					Logs.printACT("CHECK VISIBILITY", "Element " + el + "(xpath = " + xpath + ") is visible");
				}
				ret = false;
			} else {
				if (enableTestVerification) {
					Logs.printCHK(true, "Element " + el + "(xpath = " + xpath + ") is NOT visible");
				} else if (fail) {
					Logs.printACT("CHECK VISIBILITY", "Error during verify Visibility of element " + el + "(xpath = "
							+ xpath + "): " + failMessage.toString());
				} else {
					Logs.printACT("CHECK VISIBILITY", "Element " + el + "(xpath = " + xpath + ") is NOT visible");
				}
				ret = true;
			}
		}
		return ret;
	}

	public boolean verifyVisibility(String el, boolean visibility) {
		return this.verifyVisibility(el, visibility, true);
	}

	// ####-------------- TEST ACTIONS END ---------------------------
	// Screenshot methods
	@Override
	public void getScreenshot(String screenshotName) {
		String fullScreenshotName = getCounter() + "_" + screenshotName.trim() + ".gif";
		String name = aScreenshotPath + fullScreenshotName;
		File screen = screenshot(name);
		if (screen != null) {
			Logs.printScreenshot(screenshotName, fullScreenshotName);
		}
	}


	public String getTestName() {
		return c.getName();
	}


	public void openStartPage(String URL) {
		try {
			appiumD.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
			appiumD.get(URL);
			Logs.printACT("OPEN", "Open page: " + URL);
		} catch (Exception e) {
			fail("FAILED to Open Page: " + URL);
		}
	}


	public void printConfiguration() {
		String platform = getAttribute("PLATFORM_NAME").toString();
		String platformVersion = getAttribute("PLATFORM_VERSION").toString();
		String deviceName = (String) getAttribute("DEVICE_NAME");
		// String browserName = getAttribute("BROWSER_NAME").toString();
		// String browserVersion = (String) getAttribute("BROWSER_VERSION");
		switch (platform) {
		case CONSTANTS.IOS:
			Logs.print(" PLATFORM: " + platform + " " + platformVersion + " - DEVICE: " + deviceName + " - BROWSER: " + "");
			break;
		case CONSTANTS.IOS_NATIVE:
			Logs.print(" PLATFORM: " + platform + " " + platformVersion + " - DEVICE: " + deviceName + " - BROWSER: " + "");
			break;
		default:
			Logs.print(" PLATFORM: " + platform + " Version:" + platformVersion + " - BROWSER: " + "" + " Version: " + "");
			break;
		}
	}


	// ### -----GET DRIVER
	public void printTransaction() {
		transactions.printTransactions();
	}


	private File screenshot(String name) {
		System.out.println("Taking screenshot...");
		File scrFile = null;
		if (appiumD != null)
			scrFile = ((TakesScreenshot) appiumD).getScreenshotAs(OutputType.FILE);
		try {
			File testScreenshot = new File(name);
			FileUtils.copyFile(scrFile, testScreenshot);
			Logs.print("Screenshot stored to " + aScreenshotPath);
			return testScreenshot;
		} catch (IOException e) {
			Logs.printWarning("UNABLE TO TAKE SCREENSHOT: " + e.getMessage());
		}
		return null;
	}


	public void startTransaction(String name) {
		transactions.start(name);
	}
}