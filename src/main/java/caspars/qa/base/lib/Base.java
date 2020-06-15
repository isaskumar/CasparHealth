package caspars.qa.base.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;

public class Base {
	private static Map<ITestResult, List<Throwable> > verificationFailuresMap = new HashMap<ITestResult, List<Throwable> >();

	protected static void addVerificationFailure(Throwable e) {
		List<Throwable> verificationFailures = getVerificationFailures();
		verificationFailuresMap.put(Reporter.getCurrentTestResult(), verificationFailures);
		verificationFailures.add(e);
	}

	public static void checkVerificationErrors() {
		ITestResult result = Reporter.getCurrentTestResult();
		List<Throwable> verificationFailures = getVerificationFailures();
		// if there are verification failures...
		if (verificationFailures.size() > 0) {
			// set the test to failed
			result.setStatus(ITestResult.FAILURE);
			result.setThrowable(new Throwable("Verification errors"));
		}
	}

	public static List<Throwable> getVerificationFailures() {
		List<Throwable> verificationFailures = verificationFailuresMap.get(Reporter.getCurrentTestResult());
		return verificationFailures == null ? new ArrayList<Throwable>() : verificationFailures;
	}

	public void fail(String line) {
		Logs.printFail(line);
		getScreenshot("Failure_Screenshot");
		Assert.fail(line);
	}

	public void fail(String line, Exception e) {
		Logs.printFail(line + " Exception: " + e.getMessage());
		getScreenshot("Failure_Screenshot");
		Assert.fail(line + " Exception: " + e.getMessage());
	}

	public void failMobile(String line) {
		Logs.printFail(line);
		getScreenshotMobile("Failure_Screenshot_Mobile");
		Assert.fail(line);
	}

	public void failMobile(String line, Exception e) {
		Logs.printFail(line + " Exception: " + e.getMessage());
		getScreenshotMobile("Failure_Screenshot_Mobile");
		Assert.fail(line + " Exception: " + e.getMessage());
	}

	public void getScreenshot(String name) {}

	public void getScreenshotMobile(String name) {}

	public void sleep(int millisec) {
		try {
			Thread.sleep(millisec);
		} catch (Exception e) {
			Logs.printError("Error during Sleep process....");
		}
	}
}