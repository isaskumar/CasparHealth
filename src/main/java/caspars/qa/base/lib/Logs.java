package caspars.qa.base.lib;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.TestListenerAdapter;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class Logs extends TestListenerAdapter{
	private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	public static final String relativeFilesPath = "Files/";
	public static final String relativeGraphicPath = "Performances/";
	public static final String relativeScreenshotPath = "Screenshots/";
	private static ThreadLocal<Integer> stepNum = new ThreadLocal<Integer>();
	private static DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	
	public ExtentHtmlReporter htmlReporter;
	public ExtentReports extent;
	public ExtentTest test;
	
	
	public void onStart(ITestContext testContext) {
		htmlReporter = new ExtentHtmlReporter("C:/Users/semmalai/eclipse-workspace/CasperHealthMobile/Reports/report.html");
		htmlReporter.config().setDocumentTitle("CASPAR-HEALTH Android Test Automation Result");
		htmlReporter.config().setReportName("Android Test Result");
		htmlReporter.config().setTheme(Theme.DARK);
		
		extent = new ExtentReports();
		extent.attachReporter(htmlReporter);
		extent.setSystemInfo("Host Name", "localhost");
		extent.setSystemInfo("Environment", "QA");
		extent.setSystemInfo("user", "Sasi");
	}
	
	public void onTestSuccess(ITestResult result) {
		test = extent.createTest(result.getName());
		test.log(Status.PASS, "Test Case Passed"+result.getName());
	}
	
	public void onTestFailure(ITestResult result) {
		test = extent.createTest(result.getName());
		test.log(Status.FAIL, "Test Case Failed "+ result.getName());
		test.log(Status.FAIL, "Test Case Failed "+ result.getThrowable());
	}
	
	public void onTestSkipped(ITestResult result) {
		test = extent.createTest(result.getName());
		test.log(Status.SKIP, "Test Case Skipped"+ result.getName());
	}
	
	public void onFinish(ITestContext testContext) {
		extent.flush();
	}

	public static void close() {
		String testId = Reporter.getCurrentTestResult().getName();
		String d = timeFormat.format(new Date());
		System.out.println(d + " | ---- | End " + testId);
		Reporter.log("<tr><td colspan = \"3\"  class=\"headerTest\"> End " + testId + " [" + dateFormat.format(new Date()) + " " + timeFormat.format(new Date()) + "]" + "</tr>");
		Reporter.log("</table>");
	}


	public static void debug() {
		ITestResult result = Reporter.getCurrentTestResult();
		System.out.println(result.getAttribute("description"));
	}


	public static void debug(String line) {
		String d = timeFormat.format(new Date());
		System.out.println(d + " | DBG  | " + line);
	}


	public static void debug(String string, Exception e) {
		String d = timeFormat.format(new Date());
		System.out.println(d + " | DBG  | " + string + " - Eccezione: " + e.getMessage());
	}


	/**
	 * Print a test message in the log
	 *
	 * @param line
	 */
	public static void print(String line) {
		String d = timeFormat.format(new Date());
		System.out.println(d + " | INFO | " + line);
		Reporter.log("<tr><td class=\"info\">INFO</td><td class=\"description\">" + line + "</td><td class=\"time\">" + d + "</td></tr>");
	}


	/**
	 * 
	Log the ACT action and the text message
	 *
	 * @param action
	 * @param line
	 */
	public static void printACT(String action, String line) {
		String d = timeFormat.format(new Date());
		System.out.println(d + " | ACT  | " + action + " > " + line);
		Reporter.log("<tr><td class=\"action\">" + action + "</td><td class=\"description\">" + line + "</td><td class=\"time\">" + d + "</td></tr>");
	}


	public static void printCHK(boolean ok, String line) {
		String d = timeFormat.format(new Date());
		if (ok) {
			Reporter.log("<tr><td class=\"ok\">OK</td><td class=\"description\">" + line + " </td><td class=\"time\">" + d + "</td></tr>");
			System.out.println(d + " | CHK  | OK > " + line);
		} else {
			Reporter.log("<tr><td class=\"fail\">KO</td><td class=\"description\">" + line + " </td><td class=\"time\">" + d + "</td></tr>");
			System.out.println(d + " | CHK  | KO > " + line);
		}
	}


	protected static void printCompareImage(String testName, String filenameActual, String filenameExpected, boolean res) {
		String d = timeFormat.format(new Date());
		String relativePathActual = relativeScreenshotPath + filenameActual;
		String relativePathExpected = relativeScreenshotPath + filenameExpected;
		String htmlLine = "<div style=\"cursor:pointer;\" onclick=\"javascript:ImageDiffImage('" + relativePathActual + "','" + relativePathExpected + "','" + testName + "');\">" + "<div style=\"border:0px solid; float:left; padding: 20px 5px 0px 0px;\">Check Image<br><b>" + testName + "</b></div>" + "<div style=\"border:0px solid; float:left; padding: 5px 5px 5px 5px;\">Actual Image:" + "<div style=\"border:0px solid; float:bottom\" >" + "<img src=\"file:" + relativePathActual
				+ "\" style=\"height:60px;\" border=0 />" + "</div>" + "</div>" + "<div style=\"border:0px solid; float:left; padding: 5px 5px 5px 5px;\">Expected Image:" + "<div style=\"border:0px solid; float:bottom; \" >" + "<img src=\"file:" + relativePathExpected + "\" style=\"height:60px;\" border=0 />" + "</div>" + "</div>" + "</div>";
		String line = "Check Image \"" + testName + "\"";
		if (res) {
			Reporter.log("<tr><td class=\"ok\">OK</td><td class=\"description\">" + htmlLine + " </td><td class=\"time\">" + d + "</td></tr>");
			System.out.println(d + " | CHK  | OK > " + line);
		} else {
			Reporter.log("<tr><td class=\"fail\">KO</td><td class=\"description\">" + htmlLine + " </td><td class=\"time\">" + d + "</td></tr>");
			System.out.println(d + " | CHK  | KO > " + line);
		}
	}


	protected static void printError(String line) {
		String d = timeFormat.format(new Date());
		System.out.println(d + " | ERROR| " + line);
		Reporter.log("<tr><td class=\"fail\">ERROR</td><td class=\"description\">" + line + "</td><td class=\"time\">" + d + "</td></tr>");
	}


	public static void printFail(String line) {
		String d = timeFormat.format(new Date());
		System.out.println(d + " | FAIL | > " + line);
		Reporter.log("<tr><td class=\"fail\">FAIL</td><td class=\"description\">" + line + "</td><td class=\"time\">" + d + "</td></tr>");
		//throw new Error();

	}


	public static void printFrame(String frameFileName, String textAlt) {
		String d = timeFormat.format(new Date());
		System.out.println(d + " |TESTO | " + textAlt);
		String relativePath = relativeFilesPath + frameFileName;
		Reporter.log("<tr><td colspan = \"3\" class=\"description\"><iFrame  style=\"border:none;\" height=\"300\" width=\"100%\" src=\"" + relativePath + "\"></iFrame></td></tr>");
	}


	protected static void printGetImage(String fileName) {
		String d = timeFormat.format(new Date());
		System.out.println(d + " | INFO | Image \"" + fileName + "\" saved");
		String relativePath = relativeScreenshotPath + fileName;
		String s = "<div style=\"border:0px solid; float:left;padding: 10px 10px 0px 0px\">Image<br><b>" + relativePath + "</b></div><div style=\"cursor:pointer\" onclick=\"javascript:ImagePreview('" + relativePath + "','" + relativePath + "');\"><img src=\"file:" + relativePath + "\" style=\"height:100px;\" border=0></div>";
		Reporter.log("<tr><td class=\"info\">INFO</td><td class=\"description\">" + s + "</td><td class=\"time\">" + d + "</td></tr>");
	}


	public static void printPerformances(String testName, String fileName) {
		String d = timeFormat.format(new Date());
		System.out.println(d + " | INFO | Performances graphic \"" + testName + "\" saved in " + fileName);
		String relativePath = relativeGraphicPath + fileName;
		String s = "<div style=\"border:0px solid; float:left;padding: 10px 10px 0px 0px\">Performances graphic<br><b>" + testName + "</b></div><div style=\"cursor:pointer\" onclick=\"javascript:ImagePreview('" + relativePath + "','" + testName + "');\"><img src=\"file:" + relativePath + "\" style=\"height:100px;\" border=0></div>";
		Reporter.log("<tr><td class=\"info\">INFO</td><td class=\"description\">" + s + "</td><td class=\"time\">" + d + "</td></tr>");
	}


	protected static void printScreenshot(String testName, String fileName) {
		printScreenshot(testName, fileName, null);
	}


	protected static void printScreenshot(String testName, String fileName, String path) {
		String d = timeFormat.format(new Date());
		String screenshotPath;
		System.out.println(d + " | INFO | Screenshot \"" + testName + "\" saved in " + fileName);
		String envValue = System.getenv("JENKINS_HOME");

		if (envValue != null) {
			screenshotPath = "..\\html\\Screenshots\\" + fileName;
		} else if(path == null) {
			screenshotPath = relativeScreenshotPath + fileName;
		} else {
			screenshotPath = "file://" + path + fileName;
		}
		String s = "<div style=\"border:0px solid; float:left;padding: 10px 10px 0px 0px\">Screenshot<br><b>" + testName + "</b></div><div style=\"cursor:pointer\" onclick=\"javascript:ImagePreview('" + screenshotPath + "','" + testName + "');\"><img src=\"" + screenshotPath + "\" style=\"height:100px;\" border=0></div>";
		Reporter.log("<tr><td class=\"info\">INFO</td><td class=\"description\">" + s + "</td><td class=\"time\">" + d + "</td></tr>");
	}


	public static void printStartTransaction() {
		String d = timeFormat.format(new Date());
		Reporter.log("<tr><td class=\"info\">TRANSACTION LIST</td><td class=\"time\">Elapsed time (mSec)</td><td class=\"time\">" + d + "</td></tr>");
	}


	public static void printTransaction(String name, long elapsed) {
		String d = timeFormat.format(new Date());
		System.out.println(d + " | INFO | " + name + " > " + elapsed);
		Reporter.log("<tr><td class=\"info\">" + name + "</td><td class=\"time\">" + elapsed + "</td><td class=\"time\">" + d + "</td></tr>");
	}


	public static void printWarning(String line) {
		String d = timeFormat.format(new Date());
		System.out.println(d + " | WARN | " + line);
		Reporter.log("<tr><td class=\"warning\">WARN</td><td class=\"description\">" + line + "</td><td class=\"time\">" + d + "</td></tr>");
	}


	public static void start() {
		String testName = Reporter.getCurrentTestResult().getName();
		String compressedDate = (new SimpleDateFormat("ddMMyyyyHHmmss")).format(new Date());
		String completeDate = (new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")).format(new Date());
		String testId = testName.replace(' ', '_') + "_" + compressedDate;
		String d = timeFormat.format(new Date());
		System.out.println(d + " | ---- | Start " + testId + " (date: " + completeDate + ")");
		Reporter.log("<a href=\"javascript:toggleElement('" + testId + "', 'block')\" title=\"Click to view test log\"><b>" + testName + "</b></a>");
		Reporter.log("<table class=\"stepsTable\" id=\"" + testId + "\">");
		Reporter.log("<tr><td colspan = \"3\"  class=\"headerTest\">Start " + testId + " [" + completeDate + "]" + "</tr>");
		stepNum.set(0);
	}


	public static void step(String msg) {
		String d = timeFormat.format(new Date());
		Integer sn = stepNum.get() + 1;
		stepNum.set(sn);
		System.out.println(d + " |STEP " + sn + "| " + msg);
		Reporter.log("<tr><td colspan = \"2\" class=\"step\">STEP " + sn + ": " + msg + "</td><td class=\"time\">" + d + "</td></tr>");
	}
}