package caspars.qa.base.testcases;

import org.testng.annotations.Test;

import caspars.qa.base.common.pages.SmartMobileAccess;
import caspars.qa.base.lib.TemplateTestSuite;

public class TestCasesAndroid extends TemplateTestSuite{
	
	
	@Test(enabled = true, threadPoolSize = 1, description = "")
	public void BTC_001_Android_Mobie_SmartMobile_Check_the_functionality_of_knowledge() {
		preset();
		try {
			SmartMobileAccess smartMobile = new SmartMobileAccess(TestActionsAPPIUM);
			smartMobile.MTC_001_Login_Caspars("YLQ7089", "Welcome2020");
			smartMobile.MTC_002_Verify_the_homepage();
			smartMobile.MTC_003_Verify_the_My_therapy_week_menu();
			smartMobile.MTC_004_Verify_the_functionality_of_knowledge();
			smartMobile.MTC_005_Logout();
		} catch (Exception e) {
			testActionsWEB.fail(e.toString());
		} finally {
			postset();
		}
	}
}
