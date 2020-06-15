package caspars.qa.base.lib;

import java.awt.MouseInfo;
import java.awt.Robot;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Coordinates;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.ITestContext;

import io.appium.java_client.android.AndroidElement;

//import io.appium.java_client.android.AndroidElement;
public class TestActionsWEB extends Base {
	private static int counter = 0;
	protected Actions actions;
	protected String aFilePath;
	protected String aScreenshotPath;
	// #Drives -----
	// # Elements -----
	// protected WebElement we = null;
	// # Elements -----
	// WebDriverWait wait = null;
	protected ITestContext c;
	protected Properties p;
	protected int timeoutDefault = 30;
	protected Transactions transactions;
	// #Drives -----
	protected WebDriver webD = null;

	/// ## -------- constructors----------##//
	public TestActionsWEB(WebDriver webD, ITestContext c, String propFile) throws IOException {
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
		actions = new Actions(webD);
	}


	public String CheckAvailablePuntoVenditaAndClick(String el, String checkEl) { // ENRICO
		webD.manage().timeouts().pageLoadTimeout(timeoutDefault, TimeUnit.SECONDS);
		String elLocation;
		try {
			elLocation = p.getProperty(el + SplitURL(getCurrentURL()));
		} catch (Exception e) {
			elLocation = p.getProperty(el);
		}
		if (elLocation == null) {
			elLocation = p.getProperty(el);
		}

		String checkElLocation;
		try {
			checkElLocation = p.getProperty(el + SplitURL(getCurrentURL()));
		} catch (Exception e) {
			checkElLocation = p.getProperty(el);
		}
		if (checkElLocation == null) {
			checkElLocation = p.getProperty(el);
		}
		WebElement we = null;
		try {
			List<WebElement> WE = webD.findElements(By.xpath(elLocation));
			for (WebElement WElocated : WE) {
				while (ExpectedConditions.elementToBeClickable(WElocated) != null) {
					if (!WElocated.isDisplayed() && !WElocated.isEnabled()) {
						Point p = WElocated.getLocation();
						scrollTo(p.getY(), p.getX());
						// scrollTo2(p.getX(), p.getY());
						// scrollActionIOS(p.getX(), p.getY());
						break;
					} else if (WElocated.isDisplayed() && WElocated.isEnabled()) {
						// Point p = WElocated.getLocation();
						// scrollActionIOS(p.getX(), p.getY());
						Utilities.highlightElement(webD, WElocated);
						WElocated.click();
						sleep(4000);
						we = WElocated;
						break;
					} else {
						Point p = WElocated.getLocation();
						// scrollTo(p.getY(), p.getX());
						scrollTo2(p.getX(), p.getY());
						// scrollActionIOS(p.getX(), p.getY());
						// break;
					}
				}
				try {
					if (webD.findElement(By.xpath(checkElLocation)).isDisplayed()) {
						we = WElocated;
						break;
					}
				} catch (Exception e) {
					if (!e.getMessage().isEmpty())
						continue;
				}
			}
		} catch (Exception e) {
			fail("!!! FAIL to find element: " + el + " -->" + elLocation);
		}
		return we.getText();
	}


	public boolean checkIfContains(String external, String internal) {
		if (external.toLowerCase().contains(internal.toLowerCase())) {
			Logs.printCHK(true, "Text \"" + internal + "\" found in " + external + " ");
			return true;
		} else {
			Logs.printCHK(false, "Expected \"" + internal + "\" is not found in \"" + external + "\" ");
			addVerificationFailure(new Throwable("Expected \"" + internal + "\" but was not there! "));
			return false;
		}
	}


	public boolean checkIFContainsV2(String ElementPropertiesString, String internal) {
		String external;

		try {
			external = webD.findElement(By.xpath(p.getProperty(ElementPropertiesString + SplitURL(getCurrentURL()))))
					.getText();
		} catch (Exception e) {
			external = webD.findElement(By.xpath(p.getProperty(ElementPropertiesString))).getText();
		}
		if (external == null) {
			external = webD.findElement(By.xpath(p.getProperty(ElementPropertiesString))).getText();
		}
		if (external.toLowerCase().contains(internal.toLowerCase())) {
			Logs.printCHK(true, "Text \"" + internal + "\" found in " + external + " ");
			return true;
		} else {
			Logs.printCHK(false, "Expected \"" + internal + "\" is not found in \"" + external + "\" ");
			addVerificationFailure(new Throwable("Expected \"" + internal + "\" but was not there! "));
			return false;
		}
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
		webD.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
		String xpath = (String) getProperty(el);
		try {
			WebElement we = getObjectByXPath(xpath, el);
			we.clear();
			we.sendKeys(text);
			Logs.printACT("clearText",
					"Cleared text in element: " + el + " (" + xpath + ")" + "and set to: " + text + ".");
		} catch (Exception e) {
			fail("Clear text: " + el + " (" + xpath + ")", e);
		}
	}



	public void clearAndSetTextAndProceed(String el, String text) {
		webD.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
		String xpath = (String) getProperty(el);
		try {
			WebElement we = getObjectByXPath(xpath, el);
			we.clear();
			we.sendKeys(text);
			Logs.printACT("clearText",
					"Cleared text in element: " + el + " (" + xpath + ")" + "and set to: " + text + ".");
		} catch (Exception e) {
			//fail("Clear text: " + el + " (" + xpath + ")", e);
			Logs.printWarning("Script timeout error, please verify the code.");
		}
	}


	public String clearString(String MyText) {
		try {
			String s = MyText.toLowerCase();
			s = s.replaceAll("\\W", "");
			s = s.replaceAll("\\s", "");
			//			s = s.replaceAll("-", "");
			//			s = s.replaceAll(",", "");
			s = s.replaceAll("modifica", "");
			return s;
		} catch (Exception e) {
			fail("<clearString> ERROR--> MyText: " + MyText + " " + e);
			return MyText;
		}
	}

	public void clickAndProceed(String el) {
		webD.manage().timeouts().pageLoadTimeout(timeoutDefault, TimeUnit.SECONDS);
		// String xpath = p.getProperty(el)+ SplitURL(getCurrentURL());

		String xpath = (String) getProperty(el);
		WebElement we = null;
		try {
			//		we = webD.findElement(ByXPath.xpath(xpath));
			we = findElementAll(el);
			try {
				we.click();

			} catch (WebDriverException e) {
				String msg = e.getMessage();
				Logs.printError(msg);
				if (msg.contains("Cannot click")) {
					Point p = we.getLocation();
					scrollTo2(p.getY(), p.getX());
					sleep(250);
					Logs.printACT("CLICK", el + " (" + xpath + ")  is NOT clickable -> scroll the page");
					// BANNER
					if (storeVisibility("banner.CONSEGNA.GRATUITA", 5)) {
						scrollDown(100);
						webD.switchTo().defaultContent();
						Logs.printACT("Further Scroll", "CONSEGNA.GRATUITA Banner detected scroll the page");
					}
					we.click();
					sleep(500);
				} else {
					Logs.printWarning("Something was wrong, please verify the code.");
				}
			}
			Logs.printACT("CLICK", el + " (" + xpath + ")");
		} catch (Exception e) {
			Logs.printWarning("Script timeout error, please verify the code.");
		}
	}


	/**
	 * TODO: aggiungere click da JavaScript per gli elementi ostici da cliccare, con relativi controlli
	 * 
	 * @see CommonsOakleyWEB#TC_40_01_006_Store_Zip_Filters(int storeTypeN, int productN)
	 */
	public void click(String el) {
		webD.manage().timeouts().pageLoadTimeout(timeoutDefault, TimeUnit.SECONDS);

		String xpath = (String) getProperty(el);
		WebElement we = null;
		try {

			we = findElementAll(el);
			try {
				if ((webD.toString()).contains("MAC")) {
					we.sendKeys(Keys.ENTER);
				} else {
					we.click();
				}

			} catch (WebDriverException e) {
				String msg = e.getMessage();
				Logs.printError(msg);
				if (msg.contains("Cannot click")) {
					Point p = we.getLocation();
					scrollTo2(p.getY(), p.getX());
					sleep(250);
					Logs.printACT("CLICK", el + " (" + xpath + ")  is NOT clickable -> scroll the page");
					// BANNER
					//					if (storeVisibility("banner.CONSEGNA.GRATUITA", 5)) {
					//						scrollDown(100);
					//						webD.switchTo().defaultContent();
					//						Logs.printACT("Further Scroll", "CONSEGNA.GRATUITA Banner detected scroll the page");
					//					}
					we.click();
					sleep(500);
				} else {
					// fail("ERROR --> CLICK :" + el + "(xpath = " + xpath + "): " + e.toString());
					fail("ERROR --> CLICK :" + el + "(path = " + xpath + "): " + e.toString());
					throw e;
				}
			}
			// Logs.printACT("CLICK", el + " (" + xpath + ")");
			Logs.printACT("CLICK", el + " (" + xpath + ")");
		} catch (Exception e) {
			// fail("ERROR -> CLICK " + el + " (xpath: " + xpath + ")", e);
			fail("ERROR -> CLICK " + el + " (path: " + xpath + ")", e);
		}
	}


	public void ClickBfAf(String el) {
		webD.manage().timeouts().pageLoadTimeout(timeoutDefault, TimeUnit.SECONDS);

		String xpath = (String) getProperty(el);
		WebElement sel = null;
		try {
			sel = webD.findElement(By.xpath(xpath));
			try {
				actions.moveToElement(sel, -120, 0).click().build().perform();

				Logs.printACT("ClickBfAf ", xpath + " effettuato correttamente");
			} catch (WebDriverException e) {
				fail("ERROR -> ClickBfAf " + xpath + " non cliccabile", e);
			}
		} catch (Exception e) {
			fail("ERROR -> " + xpath + " elemento non trovato", e);
		}
	}


	public void clickCountryMAC(String el) {
		webD.manage().timeouts().pageLoadTimeout(timeoutDefault, TimeUnit.SECONDS);

		String xpath = (String) getProperty(el);
		WebElement sel = null;
		try {
			sel = webD.findElement(By.xpath(xpath));
			try {
				actions.moveToElement(sel, 100, 60).click().build().perform();
				Logs.printACT("ClickOnTop ", xpath + " effettuato correttamente");
			} catch (WebDriverException e) {
				fail("ERROR -> ClickOnTop " + xpath + " non cliccabile", e);
			}
		} catch (Exception e) {
			fail("ERROR -> " + xpath + " elemento non trovato", e);
		}
	}


	public void ClickMinus30pxl(String el) {
		webD.manage().timeouts().pageLoadTimeout(timeoutDefault, TimeUnit.SECONDS);

		String xpath = (String) getProperty(el);
		WebElement sel = null;
		try {
			sel = webD.findElement(By.xpath(xpath));
			try {
				actions.moveToElement(sel, 5, -32).click().build().perform();
				Logs.printACT("ClickOnTop ", xpath + " effettuato correttamente");
			} catch (WebDriverException e) {
				fail("ERROR -> ClickOnTop " + xpath + " non cliccabile", e);
			}
		} catch (Exception e) {
			fail("ERROR -> " + xpath + " elemento non trovato", e);
		}
	}


	public void clickOnlyVisibleOne(String el) {
		webD.manage().timeouts().pageLoadTimeout(timeoutDefault, TimeUnit.SECONDS);
		String xpath = (String) getProperty(el);

		try {
			List<WebElement> WE = webD.findElements(By.xpath(xpath));
			for (WebElement button : WE) {
				if (button.isDisplayed() && button.isEnabled()) {
					// Point p = button.getLocation();
					// scrollTo(0, p.getY() - 100);
					Utilities.highlightElement(webD, button);
					sleep(1000);
					button.click();
					Logs.debug("click Only Visible Element " + el);
					break;
				}
			}
		} catch (Exception e) {
			Logs.debug("Error during action on click Only Visible element: " + el);
		}
	}


	//	public WebElement waitForText(String el, String txt, int timeout) {
	//		webD.manage().timeouts().pageLoadTimeout(timeoutDefault, TimeUnit.SECONDS);
	//		String xpath = p.getProperty(el);
	//
	//		try {
	//			WebDriverWait wait = new WebDriverWait(webD, timeout);
	//			WebElement we = getObjectByXPath(xpath, el);
	//			wait.until(ExpectedConditions.textToBePresentInElement(we, txt));
	//			Logs.printACT("waitForText", "TEXT: "+txt+" ELEMENT:"+el + " XPATH:" + xpath + "");
	//		} catch (TimeoutException e) {
	//			fail("waitForText: " + el + " (" + xpath + ")");
	//			addVerificationFailure(e);
	//		}
	//		return we;
	//	}
	public void clickOnTop(String el) {
		webD.manage().timeouts().pageLoadTimeout(timeoutDefault, TimeUnit.SECONDS);

		String xpath = (String) getProperty(el);
		WebElement sel = null;
		try {
			sel = webD.findElement(By.xpath(xpath));
			try {
				actions.moveToElement(sel, 1, 1).click().build().perform();
				Logs.printACT("ClickOnTop ", xpath + " effettuato correttamente");
			} catch (WebDriverException e) {
				fail("ERROR -> ClickOnTop " + xpath + " non cliccabile", e);
			}
		} catch (Exception e) {
			fail("ERROR -> " + xpath + " elemento non trovato", e);
		}
	}

	public void clickOnMac(String el) {
		webD.manage().timeouts().pageLoadTimeout(timeoutDefault, TimeUnit.SECONDS);

		String xpath = (String) getProperty(el);
		WebElement we = null;
		try {
			//		we = webD.findElement(ByXPath.xpath(xpath));
			we = findElementAll(el);
			try {
				we.click();
				Logs.print(el + " -> cliccato correttamente");
			} catch (WebDriverException e) {
				fail("ERROR -> ClickOnMac " + xpath + " non cliccabile", e);
			}
		} catch (Exception e) {
			fail("ERROR -> " + xpath + " elemento non trovato", e);
		}
	}

	public void openUrlSauceLabs(String url) {
		try {
			webD.navigate().to(url);
			Logs.print(url + " opened correctly!");
		} catch (WebDriverException e) {
			fail("It's not possible to open " + url, e);
		}
	}

	public void clickOnAlmostTheTop(String el) {
		webD.manage().timeouts().pageLoadTimeout(timeoutDefault, TimeUnit.SECONDS);

		String xpath = (String) getProperty(el);
		WebElement sel = null;
		try {
			sel = webD.findElement(By.xpath(xpath));
			try {
				actions.moveToElement(sel, -2, -2).click().build().perform();
				Logs.printACT("ClickOnTop ", xpath + " effettuato correttamente");
			} catch (WebDriverException e) {
				fail("ERROR -> ClickOnTop " + xpath + " non cliccabile", e);
			}
		} catch (Exception e) {
			fail("ERROR -> " + xpath + " elemento non trovato", e);
		}
	}


	public void clickTheFirstVisibleOne(String el) {
		webD.manage().timeouts().pageLoadTimeout(timeoutDefault, TimeUnit.SECONDS);
		String xpath = (String) getProperty(el);

		try {
			List<WebElement> WE = webD.findElements(By.xpath(xpath));
			for (WebElement elm : WE) {
				if (elm.isDisplayed() && elm.isEnabled()) {
					try {
						Utilities.highlightElement(webD, elm);
						elm.click();
						sleep(1000);
						Logs.debug("click FIRST Visible Element " + el);
						break;
					} catch (WebDriverException e) {
						String msg = e.getMessage();
						Logs.printError(msg);
						if (msg.contains("is not clickable")) {
							Point p = elm.getLocation();
							scrollTo(p.getY(), p.getX());
							Utilities.highlightElement(webD, elm);
							elm.click();
							sleep(1000);
							Logs.debug("click FIRST Visible Element " + el);
							break;
						} else {
							fail("ERROR --> <clickTheFirstVisibleOne> :" + el + "(xpath = " + xpath + "): "
									+ e.toString());
							throw e;
						}
					}
				}
			}
		} catch (Exception e) {
			Logs.debug("Error during action on <clickTheFirstVisibleOne> click FIRST Visible element: " + el);
		}
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


	public WebElement findElement(String el) {

		String xpath = (String) getProperty(el);
		WebElement we = null;
		try {
			we = getObjectByXPath(xpath, el);
			Logs.printACT("ELEMENT FOUND: ", xpath);
		} catch (Exception e) {
			fail("FIND element " + xpath);
		}
		return we;
	}


	// Find element with all locators.
	public WebElement findElementAll(String el) throws Exception {
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
			return webD.findElement(By.cssSelector(locate));
		} catch (WebDriverException e) {}
		try {
			return webD.findElement(By.xpath(locate));
		} catch (WebDriverException e) {}
		try {
			return webD.findElement(By.className(locate));
		} catch (WebDriverException e) {}
		try {
			return webD.findElement(By.id(locate));
		} catch (WebDriverException e) {}
		try {
			return webD.findElement(By.linkText(locate));
		} catch (WebDriverException e) {}
		try {
			return webD.findElement(By.tagName(locate));
		} catch (WebDriverException e) {}
		try {
			return webD.findElement(By.partialLinkText(locate));
		} catch (WebDriverException e) {}
		throw new Exception("Element Cannot Be Located");
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


	public String getCurrentURL() {
		webD.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
		String currentURL = null;
		try {
			currentURL = webD.getCurrentUrl();
			// Logs.printACT("Get Current URL of page:", currentURL);
		} catch (Exception e) {
			fail("FAIL -> getCurrentURL of page", e);
		}
		return currentURL;
	}


	public String getInputText(String el) {
		String xpath = p.getProperty(el);
		String attributeValue = null;

		try {

			WebElement we = null;
			we = webD.findElement(By.xpath(xpath));
			attributeValue = we.getAttribute("value");
			Logs.print("Get value: " + attributeValue + " from: " + xpath);
		} catch (Exception e) {
			fail("FAIL sendKeyboardAction: " + el + "(xpath: " + xpath + "): " + e.getMessage());
		}
		return attributeValue;
	}


	public float getNumber(String elem) {
		String xpath = (String) getProperty(elem);
		WebElement we = null;
		try {
			we = getObjectByXPath(xpath, elem);
			String s = we.getText();
			s = s.replaceAll("€", "").trim();
			s = s.replaceAll("-", "");
			s = s.replaceAll("%", "");
			s = s.replaceAll("\\.", "");
			s = s.replaceAll(",", ".");
			s = s.replaceAll("[a-zA-Z]", "");
			float sn = Float.valueOf(s);
			return sn;
		} catch (Exception e) {
			fail("<getNumber> ERROR--> XPATH:" + xpath + " Element:" + elem + " " + e);
			return -1;
		}
	}


	// ####-------------- GET - SET OBJECT ---------------------------
	public WebElement getObjectByXPath(String path, String el) {
		WebElement we = null;
		try {
			we = webD.findElement(By.xpath(path));
			Utilities.highlightElement(webD, we);
		} catch (TimeoutException e) {
			fail("LOCATE THE OBJECT FAILED (getObjectByXPath): " + el + "->" + path);
		}
		return we;
	}

	public void setProperty(String key, String value) {
		p.setProperty(key, value);

		String val = p.getProperty(key);
		if (val == null) {
			Logs.printFail("ERROR --> key: " + key + " - value: " + val);
		}
	}

	public Object getProperty(String key) {
		String val = null;
		try {
			val = p.getProperty(key + SplitURL(getCurrentURL()));
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


	/// TOOLS
	// Screenshot methods
	@Override
	public void getScreenshot(String screenshotName) {
		try {
			String fullScreenshotName = getCounter() + "_" + System.currentTimeMillis() + "_" + screenshotName.trim()
			+ ".gif";
			File screen = screenshot(fullScreenshotName);
			if (screen != null) {
				Logs.printScreenshot(screenshotName, fullScreenshotName, aScreenshotPath);
			}
		} catch (NoSuchWindowException e) {
			switchToDefault();
			String fullScreenshotName = getCounter() + "_" + System.currentTimeMillis() + "_" + screenshotName.trim()
			+ ".gif";
			File screen = screenshot(fullScreenshotName);
			if (screen != null) {
				Logs.printScreenshot(screenshotName, fullScreenshotName, aScreenshotPath);
			}
		}
	}


	public void getScreenshotNew(String screenshotName) {
		try {
			String fullScreenshotName = getCounter() + "_" + System.currentTimeMillis() + "_" + screenshotName.trim()
			+ ".gif";
			String name = aScreenshotPath + fullScreenshotName;
			File screen = screenshot(name);
			if (screen != null) {
				Logs.printScreenshot(screenshotName, fullScreenshotName);
			}
		} catch (NoSuchWindowException e) {
			switchToDefault();
			String fullScreenshotName = getCounter() + "_" + System.currentTimeMillis() + "_" + screenshotName.trim()
			+ ".gif";
			String name = aScreenshotPath + fullScreenshotName;
			File screen = screenshot(name);
			if (screen != null) {
				Logs.printScreenshot(screenshotName, fullScreenshotName);
			}
		}
	}


	public String getTestName() {
		return c.getName();
	}


	public String getText(String elem) {
		String xpath = (String) getProperty(elem);
		String s = null;
		try {
			s = getObjectByXPath(xpath, elem).getText();
			Logs.printACT("getText", "Read \n" + s + " from element:--> " + elem + "and XPATH:" + xpath + "");
			return s;
		} catch (Exception e) {
			fail("getText  of:<" + elem + "> which has a XPATH:(" + xpath + ") gives Error: ", e);
		}
		return s;
	}


	// ### -----GET DRIVER
	public WebDriver getWEBDriver() {
		return webD;
	}

	public void javasriptexecutor(String script) {
		JavascriptExecutor jse = (JavascriptExecutor) webD;
		jse.executeScript(script);
	}



	public void mouseOver(String el) {
		webD.manage().timeouts().pageLoadTimeout(timeoutDefault, TimeUnit.SECONDS);
		String xpath = (String) getProperty(el);
		WebElement we = null;
		try {

			we = webD.findElement(By.xpath(xpath));
			Robot robot = new Robot();
			Point p = we.getLocation();
			int X = p.getX();
			int Y = p.getY();
			robot.mouseMove(X + we.getSize().height/2,Y+(we.getSize().width));
			robot.delay(1_000); 

			//a.moveToElement(we).build().perform();
			Logs.debug("MouseOver: " + el);
		} catch (Exception e) {
			Logs.debug("Error during action on element: " + el);
		}
	}
	public void mouseOverAndPixels(String el,int Xpix,int Ypix) {
		webD.manage().timeouts().pageLoadTimeout(timeoutDefault, TimeUnit.SECONDS);
		String xpath = (String) getProperty(el);
		WebElement we = null;
		try {

			we = webD.findElement(By.xpath(xpath));
			Robot robot = new Robot();
			Point p = we.getLocation();
			int X = p.getX();
			int Y = p.getY();

			robot.mouseMove(X+Xpix,Y+Ypix);
			robot.delay(1_000); 

			//a.moveToElement(we).build().perform();
			Logs.debug("MouseOverAndPixels: " + el);
		} catch (Exception e) {
			Logs.debug("Error during action on element: " + el);
		}
	}


	public void mouseOverANDClick(String el) {
		webD.manage().timeouts().pageLoadTimeout(timeoutDefault, TimeUnit.SECONDS);
		String xpath = (String) getProperty(el);
		WebElement we = null;
		try {
			// we = getObjectByXPath(xpath,el);
			we = webD.findElement(By.xpath(xpath));
			Actions a = new Actions(webD);
			a.moveToElement(we).click().build().perform();
			Logs.debug("MouseOver and Click: " + el);
		} catch (Exception e) {
			Logs.debug("Error during action on element: " + el);
		}
	}


	public void mouseOverElementsAndClickTheElement(String el, int th) {
		String xpath = (String) getProperty(el);

		try {
			List<WebElement> wel = webD.findElements(By.xpath(xpath));
			Actions action = new Actions(webD);
			for (WebElement elm : wel) {
				if (elm.isDisplayed() && elm.isEnabled()) {
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


	public void mouseOverScrollVisibleElementAndClick(String el) {
		String xpath = (String) getProperty(el);

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


	public void mouseOverVisibleElementHoldMoveAndRelease(String el, String ToElement) {
		String xpath = (String) getProperty(el);

		try {
			List<WebElement> wel = webD.findElements(By.xpath(xpath));
			WebElement welto = webD.findElement(By.xpath(xpath));
			Actions action = new Actions(webD);
			for (WebElement elm : wel) {
				if (elm.isDisplayed() && elm.isEnabled()) {
					Utilities.highlightElement(webD, elm);
					Logs.debug("Mouse Over: " + el);
					action.moveToElement(elm).clickAndHold().moveToElement(welto).release().build().perform();
					break;
				}
			}
			Logs.printACT("Mouse Over Visible Element and Click: ", xpath);
		} catch (Exception e) {
			fail("Mouse Over Visible element and Click: " + el + "(xpath: " + xpath + "): " + e.getMessage());
		}
	}


	// ####-------------- GET OBJECT FINISH ---------------------------
	// ####-------------- Actions of WEB START ---------------------------
	public void openStartPage(String URL) {
		try {
			webD.manage().timeouts().pageLoadTimeout(50, TimeUnit.SECONDS);
			webD.get(URL);
			Logs.printACT("OPEN", "Open page: " + URL);
		} catch (Exception e) {
			fail("FAILED to Open Page: " + URL);
		}
	}


	public void printConfiguration() {
		String platform = getAttribute("PLATFORM_NAME").toString();
		//String platformVersion = getAttribute("PLATFORM_VERSION").toString();
		String deviceName = (String) getAttribute("DEVICE_NAME");
		String browserName = getAttribute("BROWSER_NAME").toString();
		//String browserVersion = (String) getAttribute("BROWSER_VERSION");
		switch (platform) {
		case CONSTANTS.IOS:
			Logs.print(" PLATFORM: " + platform + " - DEVICE: " + deviceName + " - BROWSER: "
					+ "");
			break;
		case CONSTANTS.IOS_NATIVE:
			Logs.print(" PLATFORM: " + platform + " - DEVICE: " + deviceName + " - BROWSER: "
					+ "");
			break;
		default:
			Logs.print(
					" PLATFORM: " + platform + " - BROWSER: " + browserName);
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
		if (webD != null)
			scrFile = ((TakesScreenshot) webD).getScreenshotAs(OutputType.FILE);
		try {
			File testScreenshot = new File(new StringBuilder().append(aScreenshotPath).append(name).toString());
			FileUtils.copyFile(scrFile, testScreenshot);
			Logs.print("Screenshot stored to " + aScreenshotPath + "/" + name);
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
		webD.manage().timeouts().pageLoadTimeout(timeoutDefault, TimeUnit.SECONDS);
		String xpath = (String) getProperty(el);
		WebElement we = null;
		try {
			we = getObjectByXPath(xpath, el);
			Coordinates cor = (Coordinates) we.getLocation();
			cor.inViewPort();
			Point p = we.getLocation();
			scrollTo(cor.inViewPort().x, cor.inViewPort().y);
			sleep(1000);
			// we.click();
			Logs.debug("Scroll into view: " + el + ":Xpath :" + xpath + " via Scroll Point" + p
					+ " and using cor.inViewPort :" + cor.toString());
		} catch (Exception e) {
			Logs.debug("Error during action on element: " + el);
		}
	}


	public void scrollIntoViewAndClick(String el) {
		webD.manage().timeouts().pageLoadTimeout(timeoutDefault, TimeUnit.SECONDS);
		String xpath = (String) getProperty(el);
		WebElement we = null;
		try {
			we = getObjectByXPath(xpath, el);
			Point p = we.getLocation();
			scrollTo(0, p.getY() - 120);
			sleep(2000);
			we.click();
			Logs.debug("Scroll into view and Click" + el);
		} catch (Exception e) {
			Logs.debug("Error during action on element: " + el);
		}
	}


	public void scrollTo(int x, int y) {
		JavascriptExecutor jse = (JavascriptExecutor) webD;
		jse.executeScript("window.scrollTo(" + x + "," + y + ")", "");
	}


	public void scrollTo2(int x, int y) {
		JavascriptExecutor jse = (JavascriptExecutor) webD;
		jse.executeScript("window.scrollBy(" + x / 4 + "," + y / 4 + ")");
		// jse.executeScript("arguments[0].scrollIntoView(true);",el);
	}


	public void selectOption(String el, String text) {
		webD.manage().timeouts().pageLoadTimeout(timeoutDefault, TimeUnit.SECONDS);
		WebElement we = null;
		String xpath = (String) getProperty(el);
		try {
			// we = getObjectByXPath(xpath,el);
			we = webD.findElement(By.xpath(xpath));
			Select select = new Select(we);
			// select.deselectAll();
			try {
				select.selectByVisibleText(text);
			} catch (WebDriverException e) {
				String msg = e.getMessage();
				if (msg.contains("Cannot locate element") || msg.contains("is not")) {
					select.selectByValue(text);
				} else {
					throw e;
				}
				Logs.printACT("SELECT", "Select " + text + " in element: " + el + " (" + xpath + ")");
			}
			Logs.printACT("SELECT", "Select " + text + " in element: " + el + " (" + xpath + ")");
		} catch (Exception e) {
			fail("Select " + text + " in element: " + el + " (" + xpath + ")", e);
		}
	}


	public void sendKeyboardAction(String el) { // TODO: not ready
		String xpath = (String) getProperty(el);
		WebElement we = null;
		try {
			we = webD.findElement(By.xpath(xpath));
			Actions action = new Actions(webD);
			if (we.isDisplayed() && we.isEnabled()) {
				Utilities.highlightElement(webD, we);
				action.sendKeys(Keys.ENTER).build().perform();
			}
			Logs.printACT("sendKeyboardAction: ", el + ":->" + xpath);
		} catch (Exception e) {
			fail("FAIL sendKeyboardAction: " + el + "(xpath: " + xpath + "): " + e.getMessage());
		}
	}


	public void setText(String el, String text) {
		webD.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
		String xpath = (String) getProperty(el);
		try {
			// we = getObjectByXPath(xpath);
			// we.sendKeys(text);
			getObjectByXPath(xpath, el).sendKeys(text);
			Logs.printACT("setText", "Set " + text + " in element: " + el + " (" + xpath + ")");
		} catch (Exception e) {
			fail("setText: " + el + " (" + xpath + ")", e);
		}
	}


	public void setTextOnlyVisibleOne(String el, String text) {
		webD.manage().timeouts().pageLoadTimeout(timeoutDefault, TimeUnit.SECONDS);
		String xpath = (String) getProperty(el);

		try {
			List<WebElement> WE = webD.findElements(By.xpath(xpath));
			for (WebElement we : WE) {
				if (we.isDisplayed()) {
					// Point p = we.getLocation();
					// scrollTo(0, p.getY() - 100);
					sleep(1000);
					Utilities.highlightElement(webD, we);
					we.clear();
					sleep(250);
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


	public void startTransaction(String name) {
		transactions.start(name);
	}


	public boolean storeVisibility(String el, int timeout) {
		webD.manage().timeouts().pageLoadTimeout(timeoutDefault, TimeUnit.SECONDS);
		String xpath = (String) getProperty(el);
		boolean ret = false;
		try {
			WebDriverWait wait = new WebDriverWait(webD, timeout);
			WebElement we = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
			Utilities.highlightElement(webD, we);
			Logs.printACT("STORE VISIBILITY", el + " (" + xpath + ")" + " is visible");
			ret = true;
		} catch (TimeoutException e) {
			Logs.printACT("STORE VISIBILITY", el + " (" + xpath + ")" + " is NOT visible");
			ret = false;
		}
		return ret;
	}




	public void switchToDefault() {
		try {
			webD.switchTo().defaultContent();
			Logs.printACT("switchTo() ->   defaultContent", "");
		} catch (Exception e) {
			Logs.debug("Error during action on <switchToDefault> : ");
			String msg = e.getMessage();
			if (msg.contains("no such window")) {
				Set<String> pageIDs = webD.getWindowHandles();
				Iterator<String> page_iter = pageIDs.iterator();
				String mainPage = page_iter.next();
				webD.switchTo().window(mainPage);
			}
		}
	}


	public void switchToiFrame(String el) {
		String xpath = (String) getProperty(el);
		WebElement Iframe = webD.findElement(By.xpath(xpath));
		try {
			webD.switchTo().frame(Iframe);
			Logs.printACT("switchTo() ->   iframe", xpath);
		} catch (Exception e) {
			Logs.debug("Error during action on <switchToiFrame> : " + el);
		}
	}


	public void switchToOpenedPage() {
		Set<String> pageIDs = webD.getWindowHandles();
		Iterator<String> page_iter = pageIDs.iterator();
		String mainPage = page_iter.next();
		Logs.print("MainPage() is -> " + mainPage);
		String openedPage = page_iter.next();
		webD.switchTo().window(openedPage);
		Logs.printACT("switchTo() ->   ", openedPage);
	}


	public void switchToFirstPage() {
		int i = 0;
		for (String winHandle : webD.getWindowHandles() ) {
			if(i != 1) {
				webD.switchTo().window(winHandle);
				i = 1;
			}
			Logs.printACT("switchTo() ->   ", winHandle);
		}
	}

	public void switchToSamePage() {
		for (String winHandle : webD.getWindowHandles() ) {
			webD.switchTo().window(winHandle);
			Logs.printACT("switchTo() ->   ", winHandle);
		}
	}



	public boolean verifyExistance(String el, boolean existance) {
		webD.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
		String xpath = (String) getProperty(el);
		WebElement we = null;
		boolean ret = false;
		boolean found = false;
		try {
			we = getObjectByXPath(xpath, el);
			if (we.isDisplayed())
				found = true;
		} catch (NotFoundException e) {
			found = false;
		} catch (Exception e) {
			addVerificationFailure(e);
			fail("Error during <METHOD:verifyExistance> of element " + el + "(xpath = " + xpath + "): " + e.toString());
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


	public void verifyPrivacyIsClickedSpecial(String el) {
		String css;
		try {
			css = p.getProperty(el + SplitURL(getCurrentURL()));
		} catch (Exception e) {
			css = p.getProperty(el);
		}
		if (css == null) {
			css = p.getProperty(el);
		}

		List<WebElement> WE = null;
		try {
			WE = webD.findElements(By.cssSelector(css));
			for (WebElement ISclicked : WE) {
				if (!(ISclicked.isSelected())) {
					ISclicked.click();
				}
			}
		} catch (NotFoundException e) {
			addVerificationFailure(new Throwable("Element " + el + "css = " + css + " DOESN'T exist"));
		} catch (Exception e) {
			addVerificationFailure(new Throwable("Element " + el + "css = " + css + " Gives Exception"));
			fail("Error during METHOD: verifyIsClicked of element " + el + "css = " + css + ": " + e.toString());
		}
	}


	public boolean verifyText(String elem, String compareTo) {
		String xpath = (String) getProperty(elem);
		boolean ret = false;
		WebElement we = null;
		try {
			we = getObjectByXPath(xpath, elem);
			String s = we.getText();
			if (s.equals(compareTo)) {
				Logs.printCHK(true, "Text \"" + compareTo + "\" found in element \"" + elem + "\" (" + xpath + ")");
				ret = true;
			} else {
				Logs.printCHK(false,
						"Expected \"" + compareTo + "\" but was \"" + s + "\" in element \"" + elem + "\"");
				addVerificationFailure(new Throwable("Expected \"" + compareTo + "\" but was \"" + s + "\""));
				ret = false;
			}
		} catch (Exception e) {
			fail("checkElementValue <" + elem + "> (" + xpath + ")", e);
		}
		return ret;
	}


	public void verifyVisibilitieS(String el) {
		String xpath = (String) getProperty(el);
		boolean visible = false;
		try {
			List<WebElement> WE = webD.findElements(By.xpath(xpath));
			for (WebElement we : WE) {
				if (we.isDisplayed() && we.isEnabled()) {
					visible = true;
					break;
				}
			}
		} catch (TimeoutException e) {
			visible = false;
		} catch (Exception e) {
			visible = false;
			addVerificationFailure(e);
			fail("Error during verify Visibility of element " + el + "(xpath = " + xpath + ")", e);
		}
		if (visible == false) {
			fail("verify Visibility of element " + el + "(xpath = " + xpath + ")");
		}
	}

	public boolean verifyVisibility(String el, boolean visibility) {
		return this.verifyVisibility(el, visibility, true);
	}

	public boolean verifyVisibility(String el, boolean visibility, boolean enableTestVerification) {
		String xpath = (String) getProperty(el);
		WebElement we = null;
		boolean ret = false;
		boolean visible = false;
		boolean fail = false;
		Exception ex = new Exception();
		try {
			we = getObjectByXPath(xpath, el);
			if (we.isDisplayed() && we.isEnabled()) {
				Utilities.highlightElement(webD, we);
				visible = true;
			}
		} catch (TimeoutException e) {
			ex = e;
		} catch (Exception e) {
			fail = true;
			ex = e;
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
					if (fail) {
						Logs.printCHK(false, "Error during verify Visibility of element " + el + "(xpath = " + xpath
								+ "): " + ex.toString());
						addVerificationFailure(ex);
					} else {
						Logs.printCHK(false, "Element " + el + " (xpath = " + xpath + ") is NOT visible");
						addVerificationFailure(
								new Throwable("Element " + el + " (xpath = " + xpath + ") is NOT visible"));
					}
				} else if (fail) {
					Logs.printACT("CHECK VISIBILITY", "Error during verify Visibility of element " + el + " (xpath = "
							+ xpath + "): " + ex.toString());
				} else {
					Logs.printACT("CHECK VISIBILITY", "Element " + el + " (xpath = " + xpath + ") is NOT visible");
				}
				ret = false;
			}
		} else {
			if (visible) {
				if (enableTestVerification) {
					Logs.printCHK(false, "Element " + el + "(xpath = " + xpath + ") is visible");
					addVerificationFailure(new Throwable("Element " + el + " (xpath = " + xpath + ") is visible"));
				} else {
					Logs.printACT("CHECK VISIBILITY", "Element " + el + " (xpath = " + xpath + ") is visible");
				}
				ret = false;
			} else {
				if (enableTestVerification) {
					Logs.printCHK(true, "Element " + el + " (xpath = " + xpath + ") is NOT visible");
				} else if (fail) {
					Logs.printACT("CHECK VISIBILITY", "Error during verify Visibility of element " + el + " (xpath = "
							+ xpath + "): " + ex.toString());
				} else {
					Logs.printACT("CHECK VISIBILITY", "Element " + el + " (xpath = " + xpath + ") is NOT visible");
				}
				ret = true;
			}
		}
		return ret;
	}


	public boolean verifyVisibilityWithEndExecution(String el, boolean visibility) {
		webD.manage().timeouts().pageLoadTimeout(timeoutDefault, TimeUnit.SECONDS);
		String xpath = (String) getProperty(el);
		WebElement we = null;
		boolean ret = false;
		boolean visible = false;
		Exception stopExecution = new Exception();
		try {
			WebDriverWait wait = new WebDriverWait(webD, 0);
			// we = (WebElement)
			// wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
			Utilities.highlightElement(webD, we);
			visible = true;
			if (visibility) {
				if (visible) {
					Logs.printCHK(true, "Element " + el + " with the xpath = " + xpath + " is visible");
					ret = true;
				} else {
					Logs.printCHK(false, "Element " + el + "(xpath = " + xpath + ") is NOT visible");
					addVerificationFailure(new Throwable("Element " + el + "(xpath = " + xpath + ") is NOT visible"));
					ret = false;
					throw stopExecution;
				}
			} else {
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


	/// ################################# ACTIONS
	public WebElement waitForElement(String el, int timeout) {
		webD.manage().timeouts().pageLoadTimeout(timeoutDefault, TimeUnit.SECONDS);

		String xpath = (String) getProperty(el);
		WebElement we = null;
		try {
			WebDriverWait wait = new WebDriverWait(webD, timeout);
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
			Utilities.highlightElement(webD, we);
			Logs.printACT("WAIT_FOR_ELEMENT", el + " (" + xpath + ")");
		} catch (TimeoutException e) {
			fail("WAIT_FOR_ELEMENT: " + el + " (" + xpath + ")" + e);
			addVerificationFailure(e);
		}
		return we;
	}


	public WebElement waitForElementIsClickable(String el, int timeout) {
		webD.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
		String xpath = (String) getProperty(el);
		WebElement we = null;
		try {
			// FluentWait<WebDriver> wait = new WebDriverWait(webD, timeout);
			WebDriverWait wait = new WebDriverWait(webD, timeout);
			wait.until(ExpectedConditions.elementToBeClickable(getObjectByXPath(xpath, el)));
			Logs.printACT("WAITFORCLICKABLEELEMENT", el + " (" + xpath + ")");
		} catch (TimeoutException e) {
			fail("WAITFORCLICKABLEELEMENT: " + el + " (" + xpath + ")");
		}
		return we;
	}


	// IOS special
	public WebElement waitForElementISClickable(String el, int timeout) {
		webD.manage().timeouts().pageLoadTimeout(timeoutDefault, TimeUnit.SECONDS);
		String xpath = (String) getProperty(el);
		WebElement we = null;
		try {
			WebDriverWait wait = new WebDriverWait(webD, timeout);
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
			Logs.printACT("waitForElementISClickable:", el + " (" + xpath + ")");
		} catch (TimeoutException e) {
			fail("Error on finding element via <waitForElementISClickable>: " + el + " (" + xpath + ") "
					+ e.toString());
		}
		return we;
	}


	// IOS special
	public WebElement waitForElementVisibility(String el, int timeout) {
		webD.manage().timeouts().pageLoadTimeout(timeoutDefault, TimeUnit.SECONDS);
		String xpath = (String) getProperty(el);
		WebElement we = null;
		try {
			WebDriverWait wait = new WebDriverWait(webD, timeout);
			// FluentWait<WebDriver> wait = new WebDriverWait(webD, timeout);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
			Logs.printACT("WAITFORELEMENT", el + " (" + xpath + ")");
		} catch (TimeoutException e) {
			fail("Error on finding element via <waitForElementVisibility>: " + el + " (" + xpath + ") " + e.toString());
		}
		return we;
	}


	public void scrollIntoViewNew(String el) {
		webD.manage().timeouts().pageLoadTimeout(timeoutDefault, TimeUnit.SECONDS);
		String xpath = (String) getProperty(el);
		WebElement we = null;

		try {
			we = getObjectByXPath(xpath, el);
			Actions actions = new Actions(webD);
			actions.moveToElement(we);
			actions.perform();

			Logs.debug("Scroll into view: " + el + ":Xpath :" + xpath);
		} catch (Exception e) {
			Logs.debug("Error during action on element: " + el);
		}

	}

}