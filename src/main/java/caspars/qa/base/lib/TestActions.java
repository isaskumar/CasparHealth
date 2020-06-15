package caspars.qa.base.lib;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestContext;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.ios.IOSDriver;

public class TestActions extends Base {
	private static int counter = 0;
	protected AndroidElement aEL = null;
	protected String aFilePath;
	// #Drives -----
	protected AndroidDriver<AndroidElement> androidD = null;
	protected AppiumDriver<MobileElement> appiumD = null;
	protected String aScreenshotPath;
	protected ITestContext c;
	protected IOSDriver<MobileElement> iosD = null;
	protected MobileElement mEL = null;
	protected Properties p;
	protected int timeoutDefault = 30;
	protected Transactions transactions;
	// # Elements -----
	protected WebDriverWait wait;
	// #Drives -----
	// # Elements -----
	protected WebElement we = null;
	protected WebDriver webD = null;

	public TestActions(AndroidDriver<AndroidElement> androidD, ITestContext c, String propFile) throws IOException {
		super();
		this.androidD = androidD;
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

	public TestActions(AppiumDriver<MobileElement> appiumD, ITestContext c, String propFile) throws IOException {
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

	public TestActions(IOSDriver<MobileElement> iosD, ITestContext c, String propFile) throws IOException {
		super();
		this.iosD = iosD;
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
	public TestActions(WebDriver webD, ITestContext c, String propFile) throws IOException {
		super();
		this.webD = webD;
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


	public boolean checkIfContains(String external, String internal) {
		if (external.toLowerCase().contains(internal.toLowerCase())) {
			Logs.printCHK(true, "Text \"" + internal + "\" found");
			return true;
		} else {
			Logs.printCHK(false, "Expected \"" + internal + "\" but was not there! ");
			addVerificationFailure(new Throwable("Expected \"" + internal + "\" but was not there! "));
			return false;
		}
	}


	public void clearAndSetText(String el, String text) {
		String xpath = p.getProperty(el);
		try {
			WebElement we = getWEBObjectByXPath(xpath);
			we.clear();
			we.sendKeys(text);
			Logs.printACT("clearText", "Cleared text in element: " + el + " (" + xpath + ")" + "and set to: " + text + ".");
		} catch (Exception e) {
			fail("Clear text: " + el + " (" + xpath + ")", e);
		}
	}


	public void clearText(String el) {
		String xpath = p.getProperty(el);
		try {
			WebElement we = getWEBObjectByXPath(xpath);
			we.clear();
			Logs.printACT("ClearText", "Cleared text in element: " + el + " (" + xpath + ")");
		} catch (Exception e) {
			fail("Clear text: " + el + " (" + xpath + ")", e);
		}
	}


	public void click_WEBElement(String el) {
		String xpath = p.getProperty(el);
		try {
			WebElement wel = getWEBObjectByXPath(xpath);
			try {
				wel.click();
			} catch (WebDriverException e) {
				String msg = e.getMessage();
				if (msg.contains("Element is not clickable")) {
					Point p = wel.getLocation();
					scrollTo(0, p.getY() - 100);
					sleep(2000);
					Logs.printACT("CLICK", el + " (" + xpath + ")  is NOT clickable -> scroll the page");
					wel.click();
				} else {
					throw e;
				}
			}
			Logs.printACT("CLICK", el + " (" + xpath + ")");
		} catch (Exception e) {
			fail("CLICK " + el + " (xpath: " + xpath + ")", e);
		}
	}


	public void clickNoWait(String el) {
		String xpath = p.getProperty(el);
		try {
			WebElement wel = getWEBObjectByXPath(xpath);
			webD.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
			try {
				wel.click();
			} catch (TimeoutException e) {}
			webD.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
			Logs.printACT("CLICK", xpath);
		} catch (Exception e) {
			fail("CLICK " + el + "(xpath: " + xpath + "): " + e.getMessage());
		}
	}


	public void clickTheFirstVisibleOneWEBElement(String el) {
		String xpath = p.getProperty(el);
		try {
			List<WebElement> WE = webD.findElements(By.xpath(xpath));
			for (WebElement elm : WE) {
				we = wait.until(ExpectedConditions.visibilityOf(we));
				if (elm.isDisplayed() && elm.isEnabled()) {
					Point p = elm.getLocation();
					scrollTo(p.getX(), p.getY());
					Utilities.highlightElement(webD, elm);
					elm.click();
					Logs.debug("click FIRST Visible Element " + el);
					break;
				}
			}
		} catch (Exception e) {
			Logs.debug("Error during action on click FIRST Visible element: " + el);
		}
	}


	
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
	
	public WebElement findElement(String el) {
		String xpath = p.getProperty(el);
		try {
			we = getWEBObjectByXPath(xpath);
			Logs.printACT("ELEMENT FOUND: ", xpath);
		} catch (Exception e) {
			fail("FIND element " + xpath);
		}
		return we;
	}


	public List<WebElement> findElements(String el) {
		String xpath = p.getProperty(el);
		List<WebElement> l = null;
		try {
			getWEBObjectByXPath(xpath);
			l = webD.findElements(By.xpath(xpath));
			Logs.printACT("ELEMENTS FOUND: ", xpath);
		} catch (Exception e) {
			fail("FIND elements " + xpath);
		}
		return l;
	}


	public WebElement findOnlyVisibleElement(String el) {
		String xpath = p.getProperty(el);
		try {
			List<WebElement> WE = webD.findElements(By.xpath(xpath));
			for (WebElement WEfield : WE) {
				if (WEfield.isDisplayed()) {
					Point p = WEfield.getLocation();
					scrollTo(0, p.getY() - 100);
					sleep(1000);
					Utilities.highlightElement(webD, WEfield);
					we = WEfield;
					Logs.debug("Scroll into view " + el);
					Logs.printACT("ELEMENT FOUND: ", xpath);
				}
			}
		} catch (Exception e) {
			fail("!!! FAIL to find element " + xpath);
		}
		return we;
	}


	public AndroidDriver<AndroidElement> getAndriodDriver() {
		return androidD;
	}


	public AppiumDriver<MobileElement> getAppiumDriver() {
		return appiumD;
	}


	public Object getAttribute(String nomeAttribute) {
		return c.getAttribute(nomeAttribute);
	}


	// Context methods
	public Set<String> getContextHandles() {
		Set<String> ch = null;
		ch = appiumD.getContextHandles();
		if (ch == null) {
			fail("GET CONTEXT HANDLES");
		} else {
			Logs.print("GOT CONTEXT HANDLES");
		}
		return ch;
	}


	private String getCounter() {
		counter++;
		if (counter < 10) {
			return "0" + counter;
		} else {
			return String.valueOf(counter);
		}
	}


	public IOSDriver<MobileElement> getiOSDriver() {
		return iosD;
	}


	public float getNumber(String elem) {
		String xpath = p.getProperty(elem);
		WebElement we = getWEBObjectByXPath(xpath);
		String s = we.getText();
		s = s.replaceAll("€", "").trim();
		s = s.replaceAll("-", "");
		s = s.replaceAll("\\.", "");
		s = s.replaceAll(",", ".");
		float sn = Float.valueOf(s);
		return sn;
	}


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


	public String getText(String elem) {
		String xpath = p.getProperty(elem);
		String s = null;
		try {
			WebElement we = getWEBObjectByXPath(xpath);
			s = we.getText();
			Logs.printACT("getText", "Read " + s + " from element <" + elem + "> (" + xpath + ")");
			return s;
		} catch (Exception e) {
			fail("getText  <" + elem + "> (" + xpath + ")", e);
		}
		return s;
	}


	// ### -----DRIVERs
	public WebDriver getWEBDriver() {
		return webD;
	}


	// ####-------------- GET OBJECT ---------------------------
	private WebElement getWEBObjectByXPath(String path) {
		// WebElement we = null;
		try {
			we = webD.findElement(By.xpath(path));
			Utilities.highlightElement(webD, we);
		} catch (TimeoutException e) {
			fail("LOCATE THE OBJECT FAILED (getWEBObjectByXPath): " + path);
		}
		return we;
	}


	public void hideKeyboard() {
		try {
			appiumD.hideKeyboard();
			Logs.print("HIDE KEYBOARD");
		} catch (WebDriverException e) {
			Logs.printWarning("Exception during  HIDE KEYBOARD attempt");
		}
	}


	// Other methods
	public void mouseOver(String el) {
		String xpath = p.getProperty(el);
		try {
			WebElement wel = getWEBObjectByXPath(xpath);
			Actions action = new Actions(webD);
			action.moveToElement(wel).build().perform();
			Logs.printACT("OnMouseOver", xpath);
		} catch (Exception e) {
			fail("OnMouseOver" + el + "(xpath: " + xpath + "): " + e.getMessage());
		}
	}


	public void mouseOverVisibleElement(String el) {
		String xpath = p.getProperty(el);
		try {
			List<WebElement> wel = webD.findElements(By.xpath(xpath));
			Actions action = new Actions(webD);
			for (WebElement elm : wel) {
				if (elm.isDisplayed() && elm.isEnabled()) {
					Point p = elm.getLocation();
					scrollTo(0, p.getY() - 100);
					Utilities.highlightElement(webD, elm);
					Logs.debug("Mouse Over: " + el);
					action.moveToElement(elm).build().perform();
					getScreenshot("Mouse Over Visible Element");
					break;
				}
			}
			Logs.printACT("Mouse Over Visible Element: ", xpath);
		} catch (Exception e) {
			fail("Mouse Over Visible element: " + el + "(xpath: " + xpath + "): " + e.getMessage());
		}
	}


	public void mouseOverVisibleElementAndClick(String el) {
		String xpath = p.getProperty(el);
		try {
			List<WebElement> wel = webD.findElements(By.xpath(xpath));
			Actions action = new Actions(webD);
			for (WebElement elm : wel) {
				if (elm.isDisplayed() && elm.isEnabled()) {
					Point p = elm.getLocation();
					scrollTo(0, p.getY() - 100);
					Utilities.highlightElement(webD, elm);
					Logs.debug("Mouse Over: " + el);
					action.moveToElement(elm).click().build().perform();
					break;
				}
			}
			Logs.printACT("Mouse Over Visible Element and Click: ", xpath);
		} catch (Exception e) {
			fail("Mouse Over Visible element and Click: " + el + "(xpath: " + xpath + "): " + e.getMessage());
		}
	}


	public void navigateBack() {
		try {
			webD.navigate().back();
			Logs.printACT("CLICK", "Back Button");
		} catch (Exception e) {
			fail("FAILED to click on Back Button");
		}
	}


	public void openStartPage_IOS(String URL) {
		try {
			iosD.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
			iosD.get(URL);
			Logs.printACT("IOS Emultor Browser->", "Open page: " + URL);
		} catch (Exception e) {
			fail("IOS Emultor Browser-> FAILED to Open Page: " + URL);
		}
	}


	public void openStartPage_MOBILEbrowser(String URL) {
		try {
			androidD.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
			androidD.get(URL);
			Logs.printACT("Mobile Browser->", "Open page: " + URL);
		} catch (Exception e) {
			fail("Mobile Browser-> FAILED to Open Page: " + URL);
		}
	}


	public void openStartPage_WEB(String URL) {
		try {
			webD.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
			webD.get(URL);
			Logs.printACT("OPEN", "Open page: " + URL);
		} catch (Exception e) {
			fail("FAILED to Open Page: " + URL);
		}
	}


	public void print_from_webview(String tableName) {
		String xpath = p.getProperty(tableName);
		int r = 0;
		WebElement tab = getWEBObjectByXPath(xpath);
		List<WebElement> rows = tab.findElements(By.xpath(".//tr"));
		for (WebElement row : rows) {
			System.out.println("row " + (r) + ": ");
			Logs.print("row " + (r) + ": ");
			r++;
			List<WebElement> cols = row.findElements(By.xpath(".//*"));
			int c = 0;
			for (WebElement col : cols) {
				String colText = col.getText();
				System.out.println("col " + (c) + ": " + colText);
				Logs.print("col " + (c) + ": " + colText);
				c++;
			}
		}
	}


	public void printConfiguration() {
		String platform = getAttribute("PLATFORM_NAME").toString();
		String platformVersion = getAttribute("PLATFORM_VERSION").toString();
		String deviceName = (String) getAttribute("DEVICE_NAME");
		String browserName = getAttribute("BROWSER_NAME").toString();
		String browserVersion = (String) getAttribute("BROWSER_VERSION");
		switch (platform) {
		case CONSTANTS.IOS:
			Logs.print(" PLATFORM: " + platform + " " + platformVersion + " - DEVICE: " + deviceName + " - BROWSER: " + browserName);
			break;
		case CONSTANTS.ANDROID_WebBrowser:
			Logs.print(" PLATFORM: " + platform + " " + platformVersion + " - DEVICE: " + deviceName + " - BROWSER: " + browserName);
			break;
		default:
			Logs.print(" PLATFORM: " + platform + " Version:" + platformVersion + " - BROWSER: " + browserName + " Version: " + browserVersion);
			break;
		}
	}


	public void printFrameContent(String html, String textAlt) {
		String fileName = "FRAME_" + getCounter() + ".html";
		String filePath = aFilePath + fileName;
		saveToFile(filePath, html);
		Logs.printFrame(fileName, textAlt);
	}


	// ### -----DRIVERs
	public void printTransaction() {
		transactions.printTransactions();
	}


	public void provaPos(String elem) {
		String xpath = p.getProperty(elem);
		while (true) {
			WebElement we = getWEBObjectByXPath(xpath);
			org.openqa.selenium.Dimension dim = webD.manage().window().getSize();
			Logs.debug("window size -> H:" + dim.height + " - W:" + dim.width);
			Point pos = we.getLocation();
			Logs.debug("getLocation -> X:" + pos.getX() + " - Y:" + pos.getY());
			scrollTo(0, pos.getY());
			scrollTo(0, pos.getY() - 50);
			scrollTo(0, pos.getY() + 50);
			scrollTo(0, pos.getY() - 100);
			scrollTo(0, pos.getY() + 100);
			// Rectangle r= we.getRect();
			// Log.debug("getRect -> X:"+ r.getX()+ " - Y:"+r.getY());
		}
	}


	public void saveToFile(String filePath, String content) {
		try (PrintWriter out = new PrintWriter(filePath)) {
			out.println(content);
		} catch (FileNotFoundException e) {
			Logs.printWarning("UNABLE TO SAVE FILE " + filePath + ": " + e.getMessage());
		}
	}


	private File screenshot(String name) {
		System.out.println("Taking screenshot...");
		File scrFile = null;
		if (webD != null)
			scrFile = ((TakesScreenshot) webD).getScreenshotAs(OutputType.FILE);
		else if (appiumD != null)
			scrFile = ((TakesScreenshot) appiumD).getScreenshotAs(OutputType.FILE);
		else if (androidD != null)
			scrFile = ((TakesScreenshot) androidD).getScreenshotAs(OutputType.FILE);
		else if (iosD != null)
			scrFile = ((TakesScreenshot) iosD).getScreenshotAs(OutputType.FILE);
		else
			Logs.printFail("Error on process of Screenshot....!!");
		try {
			File testScreenshot = new File(name);
			FileUtils.copyFile(scrFile, testScreenshot);
			Logs.print("Screenshot stored to " + aScreenshotPath);
			// System.out.println("Screenshot stored to " + aScreenshotPath);
			// Reporter.logs("Screenshot stored to " + aScreenshotPath);
			return testScreenshot;
		} catch (IOException e) {
			Logs.printWarning("UNABLE TO TAKE SCREENSHOT: " + e.getMessage());
		}
		return null;
	}


	public void scrollDown(int scrollSize) {
		JavascriptExecutor jse = (JavascriptExecutor) webD;
		jse.executeScript("window.scrollBy(0," + scrollSize + ")", "");
	}


	public void scrollIntoView(String el) {
		String xpath = p.getProperty(el);
		try {
			WebElement wel = getWEBObjectByXPath(xpath);
			Point p = wel.getLocation();
			scrollTo(0, p.getY() - 100);
			Logs.debug("Scroll into view " + el);
		} catch (Exception e) {
			Logs.debug("Error during scroll into view " + el);
		}
	}


	public void scrollIntoViewAndClick(String el) {
		String xpath = p.getProperty(el);
		try {
			WebElement wel = getWEBObjectByXPath(xpath);
			Point p = wel.getLocation();
			scrollTo(0, p.getY() - 100);
			sleep(2000);
			wel.click();
			Logs.debug("Scroll into view " + el);
			Logs.debug("Click on " + el);
		} catch (Exception e) {
			Logs.debug("Error during action on element: " + el);
		}
	}


	public void scrollTo(int x, int y) {
		JavascriptExecutor jse = (JavascriptExecutor) webD;
		jse.executeScript("window.scrollTo(" + x + "," + y + ")", "");
	}


	public void selectClear(String el) {
		String xpath = p.getProperty(el);
		try {
			WebElement we = getWEBObjectByXPath(xpath);
			Select select = new Select(we);
			select.deselectAll();
			// select.selectByVisibleText(text);
			// Log.printACT("SELECT", "Select " + text + " in element: " + el +
			// " ("+xpath+")");
		} catch (Exception e) {
			// fail("Select " + text + " in element: " + el + " ("+xpath+")",e);
		}
	}


	public void selectMobileOption(String el, String text) {
		String xpath = p.getProperty(el);
		try {
			WebElement we = getWEBObjectByXPath(xpath);
			Select select = new Select(we);
			// select.deselectAll();
			select.selectByVisibleText(text);
			Logs.printACT("SELECT", "Select " + text + " in element: " + el + " (" + xpath + ")");
		} catch (Exception e) {
			fail("Select " + text + " in element: " + el + " (" + xpath + ")", e);
		}
	}


	public void selectOption(String el, String text) {
		String xpath = p.getProperty(el);
		try {
			WebElement we = getWEBObjectByXPath(xpath);
			Select select = new Select(we);
			// select.deselectAll();
			select.selectByVisibleText(text);
			Logs.printACT("SELECT", "Select " + text + " in element: " + el + " (" + xpath + ")");
		} catch (Exception e) {
			fail("Select " + text + " in element: " + el + " (" + xpath + ")", e);
		}
	}


	public void setText(String el, String text) {
		String xpath = p.getProperty(el);
		try {
			WebElement we = getWEBObjectByXPath(xpath);
			// we.sendKeys(Keys.chord(Keys.CONTROL, "a"), text);
			we.sendKeys(text);
			Logs.printACT("setText", "Set " + text + " in element: " + el + " (" + xpath + ")");
		} catch (Exception e) {
			fail("setText: " + el + " (" + xpath + ")", e);
		}
	}


	public void setTextOnlyVisibleOneWEBElement(String el, String text) {
		String xpath = p.getProperty(el);
		try {
			List<WebElement> WEList = webD.findElements(By.xpath(xpath));
			for (WebElement we : WEList) {
				if (we.isDisplayed() && we.isEnabled()) {
					Point p = we.getLocation();
					scrollTo(0, p.getY() - 100);
					Utilities.highlightElement(webD, we);
					we.clear();
					we.sendKeys(text);
					Logs.debug("Scroll into view " + el);
					Logs.debug("setText Set " + text + " in element: " + el + " (" + xpath + ")");
				}
			}
			Logs.printACT("setText", "Set " + text + " in element: " + el + " (" + xpath + ")");
		} catch (Exception e) {
			fail("setText: " + el + " (" + xpath + ")", e);
		}
	}


	public void startTransaction(String name) {
		transactions.start(name);
	}


	public boolean storeVisibility(String el, int timeout) {
		String xpath = p.getProperty(el);
		boolean ret = false;
		try {
			wait = new WebDriverWait(webD, timeout);
			we = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
			Utilities.highlightElement(webD, we);
			sleep(500);
			Logs.printACT("STORE VISIBILITY", el + " (" + xpath + ")" + " is visible");
			ret = true;
		} catch (TimeoutException e) {
			Logs.printACT("STORE VISIBILITY", el + " (" + xpath + ")" + " is NOT visible");
			ret = false;
		}
		return ret;
	}


	public void switchContext(String context) {
		try {
			appiumD.context(context);
			Logs.printACT("SWITCHED TO: ", context);
		} catch (Exception e) {
			fail("SWITCH TO " + context);
		}
	}


	public String switchToOpenedPage() {
		Set<String> pageIDs = webD.getWindowHandles();
		Iterator<String> page_iter = pageIDs.iterator();
		String mainPage = page_iter.next();
		Logs.print("Main Page is:" + mainPage + "");
		String openedPage = page_iter.next();
		Logs.print("Opened Page is:" + openedPage + "");
		return webD.switchTo().window(openedPage).toString();
	}


	public void tap(String el) { // TODO: not completed
		String xpath = p.getProperty(el);
		try {
			// WebElement wel = getWEBObjectByXPath(xpath);
			// AppiumDriver ad = (AppiumDriver) d;
			// ad..tap(1, wel, 1);
			Logs.printACT("TAP", el + " (" + xpath + ")");
		} catch (Exception e) {
			fail("TAP " + el + " (xpath: " + xpath + ")", e);
		}
	}


	public boolean verifyElementAttribute(String elem, String attribute, String compareTo) {
		String xpath = p.getProperty(elem);
		boolean ret = false;
		try {
			WebElement we = getWEBObjectByXPath(xpath);
			String s = we.getAttribute(attribute);
			if (s.equals(compareTo)) {
				Logs.printCHK(true, "Text \"" + compareTo + "\" found in element \"" + elem + "\"");
				ret = true;
			} else {
				Logs.printCHK(false, "Expected \"" + compareTo + "\" but was \"" + s + "\" in element \"" + elem + "\"");
				addVerificationFailure(new Throwable("Expected \"" + compareTo + "\" but was \"" + s + "\""));
				ret = false;
			}
		} catch (Exception e) {
			fail("checkElementAttribute <" + elem + "> (" + xpath + ")", e);
		}
		return ret;
	}


	public boolean verifyExistance(String el, boolean existance) {
		String xpath = p.getProperty(el);
		boolean ret = false;
		boolean found = false;
		try {
			webD.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			we = webD.findElement(By.xpath(xpath));
			Utilities.highlightElement(webD, we);
			found = true;
		} catch (NotFoundException e) {
			found = false;
		} catch (Exception e) {
			addVerificationFailure(e);
			fail("Error during verify Existance of element " + el + "(xpath = " + xpath + "): " + e.toString());
		} finally {
			webD.manage().timeouts().implicitlyWait(timeoutDefault, TimeUnit.SECONDS);
		}
		if (existance) {
			if (found) {
				Logs.printCHK(true, "Element " + el + "(xpath = " + xpath + ") exists");
				ret = true;
			} else {
				Logs.printCHK(false, "Element " + el + "(xpath = " + xpath + ") DOESN'T exist");
				addVerificationFailure(new Throwable("Element " + el + "(xpath = " + xpath + ") DOESN'T exist"));
				ret = false;
			}
		} else {
			if (found) {
				Logs.printCHK(false, "Element " + el + "(xpath = " + xpath + ") exists");
				addVerificationFailure(new Throwable("Element " + el + "(xpath = " + xpath + ") exist"));
				ret = false;
			} else {
				Logs.printCHK(true, "Element " + el + "(xpath = " + xpath + ") DOESN'T exist");
				ret = true;
			}
		}
		return ret;
	}


	public boolean verifyText(String elem, String compareTo) {
		String xpath = p.getProperty(elem);
		boolean ret = false;
		try {
			WebElement we = getWEBObjectByXPath(xpath);
			String s = we.getText();
			if (s.equals(compareTo)) {
				Logs.printCHK(true, "Text \"" + compareTo + "\" found in element \"" + elem + "\" (" + xpath + ")");
				ret = true;
			} else {
				Logs.printCHK(false, "Expected \"" + compareTo + "\" but was \"" + s + "\" in element \"" + elem + "\"");
				addVerificationFailure(new Throwable("Expected \"" + compareTo + "\" but was \"" + s + "\""));
				ret = false;
			}
		} catch (Exception e) {
			fail("checkElementValue <" + elem + "> (" + xpath + ")", e);
		}
		return ret;
	}


	public boolean verifyVisibility(String el, boolean visibility) {
		String xpath = p.getProperty(el);
		boolean ret = false;
		boolean visible = false;
		try {
			wait = new WebDriverWait(webD, 0);
			we = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
			Utilities.highlightElement(webD, we);
			// sleep(500);
			visible = true;
		} catch (TimeoutException e) {
			visible = false;
		} catch (Exception e) {
			addVerificationFailure(e);
			fail("Error during verify Visibility of element " + el + "(xpath = " + xpath + ")", e);
		}
		if (visibility) {
			if (visible) {
				Logs.printCHK(true, "Element " + el + "(xpath = " + xpath + ") is visible");
				ret = true;
			} else {
				Logs.printCHK(false, "Element " + el + "(xpath = " + xpath + ") is NOT visible");
				addVerificationFailure(new Throwable("Element " + el + "(xpath = " + xpath + ") is NOT visible"));
				ret = false;
			}
		} else {
			if (visible) {
				Logs.printCHK(false, "Element " + el + "(xpath = " + xpath + ") is visible");
				addVerificationFailure(new Throwable("Element " + el + "(xpath = " + xpath + ") is visible"));
				ret = false;
			} else {
				Logs.printCHK(true, "Element " + el + "(xpath = " + xpath + ") is NOT visible");
				ret = true;
			}
		}
		return ret;
	}


	public boolean verifyVisibilityWithEndOFExecutionWEBElement(String el, boolean visibility) {
		String xpath = p.getProperty(el);
		boolean ret = false;
		boolean visible = false;
		Exception stopExecution = new Exception();
		try {
			wait = new WebDriverWait(webD, 3);
			we = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
			Utilities.highlightElement(webD, we);
			visible = true;
			if (visibility == true) {
				if (visible) {
					Logs.printCHK(true, "Element " + el + " with the xpath = " + xpath + " is visible");
					ret = true;
				} else {
					Logs.printCHK(false, "Element " + el + "(xpath = " + xpath + ") is NOT visible");
					addVerificationFailure(new Throwable("Element " + el + "(xpath = " + xpath + ") is NOT visible"));
					ret = false;
					throw stopExecution;
				}
			} else if (visibility == false) {
				if (visible) {
					Logs.printCHK(false, "Element " + el + "(xpath = " + xpath + ") is visible");
					addVerificationFailure(new Throwable("Element " + el + "(xpath = " + xpath + ") is visible"));
					ret = false;
					throw stopExecution;
				} else {
					Logs.printCHK(true, "Element " + el + "(xpath = " + xpath + ") is NOT visible");
					ret = true;
				}
			}
		} catch (TimeoutException e) {
			visible = false;
		} catch (Exception e) {
			addVerificationFailure(e);
			fail("Error during verify Visibility of element " + el + "(xpath = " + xpath + ")", e);
		}
		return ret;
	}


	// ####-------------- GET OBJECT FINISH ---------------------------
	public WebElement waitForWEBElement(String el, int timeout) {
		String xpath = p.getProperty(el);
		// WebElement we = null;
		try {
			wait = new WebDriverWait(webD, timeout);
			// wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[class=\"mapIcon\"]")));
			we = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
			Utilities.highlightElement(webD, we);
			Logs.printACT("WAITFORELEMENT", el + " (" + xpath + ")");
		} catch (TimeoutException e) {
			fail("WAITFORELEMENT: " + el + " (" + xpath + ")");
		}
		return we;
	}


	public WebElement waitForWEBElementIsClickable(String el, int timeout) {
		String xpath = p.getProperty(el);
		// WebElement we = null;
		try {
			wait = new WebDriverWait(webD, timeout);
			webD.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			we = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
			Utilities.highlightElement(webD, we);
			Logs.printACT("WAITFORCLICKABLEELEMENT", el + " (" + xpath + ")");
		} catch (TimeoutException e) {
			fail("WAITFORCLICKABLEELEMENT: " + el + " (" + xpath + ")");
		}
		return we;
	}


	public WebElement waitForWEBElementVisibility(String el, int timeout) {
		String xpath = p.getProperty(el);
		try {
			wait = new WebDriverWait(webD, timeout);
			we = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
			Utilities.highlightElement(webD, we);
			Logs.printACT("WAITFORELEMENT", el + " (" + xpath + ")");
		} catch (TimeoutException e) {
			fail("WAITFORELEMENT: " + el + " (" + xpath + ")");
		}
		return we;
	}
}