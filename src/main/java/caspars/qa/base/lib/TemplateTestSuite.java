package caspars.qa.base.lib;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.remote.MobileCapabilityType;


public class TemplateTestSuite extends Base {
	protected static ThreadLocal<AppiumDriver<MobileElement> > appdr = new ThreadLocal<AppiumDriver<MobileElement> >();
	protected static ThreadLocal<WebDriver> dr = new ThreadLocal<WebDriver>();
	protected static ThreadLocal<IOSDriver<IOSElement> > iosdr = new ThreadLocal<IOSDriver<IOSElement> >();
	protected static ThreadLocal<AndroidDriver<AndroidElement> > Mdr = new ThreadLocal<AndroidDriver<AndroidElement> >();
	// #Drives -----
	protected AndroidDriver<AndroidElement> androidD;
	protected AppiumDriver<MobileElement> appiumD;
	protected ITestContext c;
	protected Map<String, String> envvar;
	protected IOSDriver<IOSElement> iosD;
	protected Process p;
	protected int projectID;
	public String startURL;
	public String startURLs;
	// #Drives -----
	protected TestActions testActions;
	protected TestActionsAPPIUM TestActionsAPPIUM;
	protected TestActionsWEB testActionsWEB;
	protected int testSetID;
	protected WebDriver webD;

	WebDriver driverAndroid;


	// protected DynamicSpiraConfiguration targetValue; //SPIRA
	public TemplateTestSuite() {
		// cap = new MutableCapabilities();
	}

	// Runs BEFORE ALL test and Runs only one..
	@BeforeTest(alwaysRun = true)
	@Parameters({
		"executionEnvironment", "platform", "device", "startUrl", "startUrls", "testSetID", "projectID", "udid"
	})


	public void before(@Optional String executionEnvironment, @Optional String platform, @Optional String device, @Optional String startUrl, ITestContext c, @Optional String startUrls, @Optional String testSetID, @Optional String projectID, @Optional String udid) {
		this.c = c;
		envvar = System.getenv();
		if (executionEnvironment == null) {
			executionEnvironment = envvar.get("EXECUTION_ENVIRONMENT"); // .toString();
		}
		if (platform == null) {
			platform = envvar.get("PLATFORM");
		}
		if (device == null) {
			device = envvar.get("DEVICE");
		}
		if (startUrl == null) {
			startUrl = envvar.get("START_URL");
		}
		startURL = startUrl;
		if (startUrls == null) {
			startUrls = envvar.get("START_URLs");
		}
		startURLs = startUrls;
		if (udid == null) {
			udid = envvar.get("UDID");
		}
	}


	// --------
	// Runs AFTER any single test
	@AfterMethod(alwaysRun = true)


	public void close() {
		if (getDriverWEB() != null) {
			getDriverWEB().quit();
		} else if (getAndroidDriver() != null)
			getAndroidDriver().quit();
		else if (getAppiumdDriver() != null)
			getAppiumdDriver().quit();
		else if (getIOSDriver() != null) {
			//RPINO - spostato in afterTest -	getIOSDriver().quit();
			System.out.println("\r\n" + "RPINO-Excluding closure for IOS");
		}
		else
			fail("Error during Driver initialize Can not Close Driver, Driver not supported !!!");
	}


	//RPINO -added afterTest method to close the connection at the end of all tests
	@AfterTest


	public void closeTest() {
		System.out.println("RPINO-Entrato in closeTest");
		if (getIOSDriver() != null) {
			System.out.println("RPINO-Chiudo la connessione");
			getIOSDriver().quit();
		}
	}
	//RPINO


	public AndroidDriver<AndroidElement> getAndroidDriver() {
		return Mdr.get();
	}


	public AppiumDriver<MobileElement> getAppiumdDriver() {
		return appdr.get();
	}


	public WebDriver getDriverWEB() {
		return dr.get();
	}


	public IOSDriver<IOSElement> getIOSDriver() {
		return iosdr.get();
	}


	public void postset() {
		checkVerificationErrors();
		Logs.close();
	}


	@AfterSuite


	public void postsuite() {
		// generate allure env variables
		try {
			String e = "Browser=" + System.getenv("BROWSER") + "\n" + "Platform=" + System.getenv("PLATFORM");
			Path path = Paths.get("allure-results/environment.properties");
			Files.write(path, e.getBytes());
		} catch (IOException e) {}
	}


	public void preset() {
		Logs.start();
		if (getDriverWEB() != null)
			testActionsWEB.printConfiguration();
		else if (getAppiumdDriver() != null)
			TestActionsAPPIUM.printConfiguration();
		else
			System.out.println();
	}

	// ##----- ANDROID
	public void setAndroidDriver(AndroidDriver<AndroidElement> driver) {
		Mdr.set(driver);
	}

	// ##--------APPIUM
	public void setAppiumDriver(AppiumDriver<MobileElement> driver) {
		appdr.set(driver);
	}


	// ##-------- IOS
	public void setIOSDriver(IOSDriver<IOSElement> driver) {
		iosdr.set(driver);
	}


	// Runs BEFORE any test
	@BeforeMethod(alwaysRun = true)
	@Parameters({
		"executionEnvironment", "platform", "browser", "device", "version", "serverUrl", "propFile", "testSetID", "projectID", "udid", "appPath", "apkName"
	})


	public void setup(@Optional String executionEnvironment, @Optional String platform, @Optional String browser, @Optional String device, @Optional String version, @Optional String serverUrl, @Optional String propFile, @Optional String testSetID, @Optional String projectID, @Optional String udid, Method method, @Optional String appPath, @Optional String apkName) throws IOException {
		String testName = method.getName();
		String compressedDate = (new SimpleDateFormat("ddMMyyyyHHmmss")).format(new Date());
		String testId = testName.replace(' ', '_') + "_" + compressedDate;
		MutableCapabilities cap = null;
		Capabilities caps = null;
		envvar = System.getenv();
		if (platform == null) {
			platform = envvar.get("PLATFORM"); // the Operating System
		}
		if (browser == null) {
			browser = envvar.get("BROWSER"); // the Browser
		}
		// String startURL = envvar.get("START_URL"); // the Start Url
		if (executionEnvironment == null) {
			executionEnvironment = envvar.get("EXECUTION_ENVIRONMENT");
		}
		if (device == null) {
			device = envvar.get("DEVICE");
		}
		if (version == null) {
			version = envvar.get("VERSION");
		}
		if (serverUrl == null) {
			serverUrl = envvar.get("SERVER_URL");
		}
		if (propFile == null) {
			propFile = envvar.get("PROP_FILE");
		}
		if (udid == null) {
			udid = envvar.get("UDID");
		}
		if (udid == null) {
			udid = envvar.get("UDID");
		}
		if (appPath == null) {
			appPath = envvar.get("APP_PATH");
		}
		if (apkName == null) {
			apkName = envvar.get("APK_NAME");
		}
	
		switch (executionEnvironment) {
		case CONSTANTS.KOBITON_ENVIRONMENT:
			break;
		case CONSTANTS.SAUCE_ENVIRONMENT:
			switch (browser) {
			case CONSTANTS.FIREFOX:

				break;
			case CONSTANTS.CHROME:
				
				break;
			case CONSTANTS.SAFARI:
				
				break;
			case CONSTANTS.IEXPLORE:
				
				break;
			default:
				fail("browser " + browser + " not supported");
				break;
			}
			caps = ((RemoteWebDriver) webD).getCapabilities();
			c.setAttribute("PLATFORM_NAME", caps.getCapability("platformName"));
			c.setAttribute("PLATFORM_VERSION", caps.getVersion());
			c.setAttribute("BROWSER_NAME", caps.getBrowserName());
			c.setAttribute("BROWSER_VERSION", caps.getVersion());
			break;
		case CONSTANTS.LOCAL_ENVIRONMENT:
			switch (platform) {
			case CONSTANTS.WINDOWS:
				switch (browser) {
				case CONSTANTS.FIREFOX: 
					break;
				case CONSTANTS.CHROME: 
					break;
				case CONSTANTS.IEXPLORE: 
					break;
				case CONSTANTS.OPERA:
					break;
				case CONSTANTS.EDGE:
					break;
				default:
					fail("browser " + browser + " not supported");
					break;
				}
				break;

			}
			caps = ((RemoteWebDriver) webD).getCapabilities();
			c.setAttribute("PLATFORM_NAME", CONSTANTS.OS_NAME);
			c.setAttribute("PLATFORM_VERSION", CONSTANTS.OS_VERSION);
			c.setAttribute("BROWSER_NAME", caps.getBrowserName());
			c.setAttribute("BROWSER_VERSION", caps.getVersion());
			break;
		case CONSTANTS.LOCAL_APP:
			cap = new MutableCapabilities();


			File app = new File(appPath, apkName);

			cap.setCapability("app", app.getAbsolutePath());
			cap.setCapability("deviceName", device);
			cap.setCapability("platformName","Android");
			//LOCAL
			appiumD = new AppiumDriver<MobileElement>(new URL("http://127.0.0.1:4723/wd/hub"), cap);

			caps = appiumD.getCapabilities();
			c.setAttribute("PLATFORM_NAME", cap.getCapability("platformName"));
			c.setAttribute("PLATFORM_VERSION", caps.getCapability("platformVersion"));
			c.setAttribute("BROWSER_NAME", caps.getBrowserName());
			c.setAttribute("DEVICE_NAME", caps.getCapability("deviceName"));
			break;

		case CONSTANTS.REMOTE_ENVIRONMENT:
			switch (platform) {
			case CONSTANTS.NATIVE_APP:
				cap = new MutableCapabilities();
				File appRemote = new File(appPath, apkName);
				//com.casparhealth.android.patient.splash.SplashActivity
				//cap.setCapability("app", appRemote.getAbsolutePath());
				cap.setCapability("deviceName", device);
				cap.setCapability("platformName","Android");
				cap.setCapability("uiautomator2ServerInstallTimeout", 10000);
				/*cap.setCapability("noReset", true);
				cap.setCapability("fullReset", false);*/
				cap.setCapability("appPackage", "com.casparhealth.android.patient");
				cap.setCapability("appActivity", "com.casparhealth.android.patient.splash.SplashActivity");
				// SERVER
				appiumD = new AppiumDriver<MobileElement>(new URL(serverUrl + "/wd/hub"), cap);
				caps = appiumD.getCapabilities();
				c.setAttribute("PLATFORM_NAME", cap.getCapability("platformName"));
				c.setAttribute("PLATFORM_VERSION", caps.getCapability("platformVersion"));
				c.setAttribute("BROWSER_NAME", caps.getBrowserName());
				c.setAttribute("DEVICE_NAME", caps.getCapability("deviceName"));
				break;
			case CONSTANTS.IOS_NATIVE:
				cap = new MutableCapabilities();

				cap.setCapability((MobileCapabilityType.AUTOMATION_NAME), "XCUITest");   //Mandatory for IOS
				cap.setCapability(MobileCapabilityType.PLATFORM_NAME, "iOS");
				cap.setCapability(MobileCapabilityType.DEVICE_NAME, device);
				cap.setCapability(MobileCapabilityType.UDID, udid);
				cap.setCapability(MobileCapabilityType.APP, appPath + apkName);
				//cap.setCapability(MobileCapabilityType.APPLICATION_NAME, appPath + apkName);

				//LOCAL

				// SERVER
				iosD = new IOSDriver<IOSElement>(new URL(serverUrl + "/wd/hub"), cap);
				caps = ((RemoteWebDriver) iosD).getCapabilities();
				c.setAttribute("DEVICE_NAME", caps.getCapability("deviceName"));
				break;
			case CONSTANTS.ANDROID_WebBrowser: // ANDROID
				break;
			case CONSTANTS.IOS: // SIMULATOR - EMULATOR
				
				break;
			case CONSTANTS.MAC: // REMOTE
				switch (browser) {
				case CONSTANTS.SAFARI:
					break;
				case CONSTANTS.FIREFOX:
					break;
				case CONSTANTS.CHROME:
					break;
				case CONSTANTS.OPERA:
					break;
				default:
					fail("browser " + browser + " not supported");
					break;
				}
				// ### Execution Env. infomation ### //
				caps = ((RemoteWebDriver) webD).getCapabilities();
				c.setAttribute("PLATFORM_NAME", cap.getPlatform());
				c.setAttribute("PLATFORM_VERSION", cap.getVersion());
				c.setAttribute("BROWSER_NAME", caps.getBrowserName());
				c.setAttribute("BROWSER_VERSION", caps.getVersion());
				break;
			case CONSTANTS.WINDOWS: // REMOTE 
				switch (browser) {
				case CONSTANTS.FIREFOX:
					break;
				case CONSTANTS.CHROME:
					break;
				case CONSTANTS.IEXPLORE:
					break;
				case CONSTANTS.EDGE:
					break;
				default:
					fail("browser " + browser + " not supported");
					break;
				}
				caps = ((RemoteWebDriver) webD).getCapabilities();
				c.setAttribute("PLATFORM_NAME", caps.getPlatform());
				c.setAttribute("PLATFORM_VERSION", caps.getVersion());
				c.setAttribute("BROWSER_NAME", caps.getBrowserName());
				c.setAttribute("BROWSER_VERSION", caps.getVersion());
				break;
			default:
				fail("platform " + platform + " not supported");
				break;
			}
			break;
		default:
			fail("ExecutionEnvironment " + executionEnvironment + " not supported");
			break;
		}
		if (webD != null)
			setWebDriver(webD);
		else if (androidD != null)
			setAndroidDriver(androidD);
		else if (appiumD != null)
			setAppiumDriver(appiumD);
		else if (iosD != null)
			setIOSDriver(iosD);
		else
			fail("Error during setDriver operation.....!");
		c.setAttribute("ENVIRONMENT", executionEnvironment);
		if (webD != null)
			testActionsWEB = new TestActionsWEB(getDriverWEB(), c, propFile);
		else if (androidD != null) {
			// TestActionsAndroid = new TestActionsAndroid(getDriverWEB(),
			// getAndroidDriver(), c, propFile);
			//TestActionsAndroid = new TestActionsAndroid(getAndroidDriver(), c, propFile);
		} else if (appiumD != null) {
			TestActionsAPPIUM = new TestActionsAPPIUM(getAppiumdDriver(), c, propFile);
		} else if (iosD != null) {
			// TestActionsIOS = new TestActionsIOS(getDriverWEB(), getIOSDriver(), c,
			// propFile);
			//TestActionsIOS = new TestActionsIOS(getIOSDriver(), c, propFile);
			System.out.println("RPINO-Eseguo pulizia cookies");
			getIOSDriver().manage().deleteAllCookies();
			//RPINO - Fine
		} else {
			fail("Driver is not initialized, please control your Driver used for the suite");
		}
	}

	// ## ---WEB
	public void setWebDriver(WebDriver driver) {
		dr.set(driver);
	}
}