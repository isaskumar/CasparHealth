package caspars.qa.base.common.pages;
import caspars.qa.base.lib.Base;
import caspars.qa.base.lib.Logs;
import caspars.qa.base.lib.TestActionsAPPIUM;

public class SmartMobileAccess extends Base { 

	public TestActionsAPPIUM t;

	public SmartMobileAccess(TestActionsAPPIUM t) {
		this.t = t;
	}


	public void MTC_001_Login_Caspars(String username, String password) {
		Logs.step("START: MTC_001_Login_Caspars");
		t.storeVisibility("smartmobile.home.signin.button", 5);
		t.verifyVisibility("smartmobile.home.signin.button", true);
		t.verifyVisibility("smartmobile.home.welcome.msg.page1", true);
		t.verifyVisibility("smartmobile.home.welcome.next", true);
		t.click("smartmobile.home.welcome.next");
		t.verifyVisibility("smartmobile.home.welcome.day.msg", true);
		t.storeVisibility("smartmobile.home.welcome.night.msg", 5);
		t.verifyVisibility("smartmobile.home.welcome.night.msg", true);
		t.verifyVisibility("smartmobile.home.welcome.next", true);
		t.click("smartmobile.home.welcome.next");
		t.verifyVisibility("smartmobile.home.welcome.msg.page3", true);
		t.verifyVisibility("smartmobile.home.start", true);
		t.click("smartmobile.home.start");
		t.storeVisibility("smartmobile.login.title", 5);
		t.verifyVisibility("smartmobile.login.title", true);
		t.verifyVisibility("smartmobile.login.brand.logo", true);
		t.verifyVisibility("smartmobile.login.message", true);
		t.verifyVisibility("smartmobile.login.email.txt", true);
		t.clearAndSetText("smartmobile.login.email.txt", username);
		t.verifyVisibility("smartmobile.login.pass.txt", true);
		t.clearAndSetText("smartmobile.login.pass.txt", password);
		t.verifyVisibility("smartmobile.login.pass.visibility", true);
		t.verifyVisibility("smartmobile.login.signin.btn", true);
		t.click("smartmobile.login.signin.btn");
		Logs.print("MTC_001_Login_Caspars: FINISHED");
		t.getScreenshot("MTC_001_Login_Caspars");
	}

	public void MTC_002_Verify_the_homepage() {
		Logs.step("START: MTC_002_Verify_the_homepage");
		if(t.storeVisibility("smartmobile.welcome.clinic.not.now.btn", 5)) {
			t.verifyVisibility("smartmobile.welcome.clinic.not.now.btn", true);
			t.click("smartmobile.welcome.clinic.not.now.btn");
		}
		t.storeVisibility("homepage.mytraining", 10);
		t.verifyVisibility("homepage.mytraining", true);
		t.verifyVisibility("homepage.total.exercise.count", true);
		t.verifyVisibility("homepage.my.training.go", true);
		t.verifyVisibility("homepage.knowledge.icon", true);
		t.verifyVisibility("homepage.knowledge.title", true);
		t.verifyVisibility("homepage.knowledge.count", true);
		t.verifyVisibility("homepage.knowledge.go.to.arrow", true);
		t.verifyVisibility("homepage.wellbeing.title", true);
		t.verifyVisibility("homepage.wellbeing.icon", true);
		t.verifyVisibility("homepage.wellbeing.count", true);
		t.verifyVisibility("homepage.steps.icon", true);
		t.verifyVisibility("homepage.steps.title", true);
		t.verifyVisibility("homepage.steps.progress", true);
		t.verifyVisibility("homepage.steps.goals", true);
		t.verifyVisibility("homepage.steps.sync.wearable", true);
		t.verifyVisibility("homepage.steps.activity.motivation", true);
		t.verifyVisibility("homepage.steps.add.steps", true);
		t.verifyVisibility("homepage.menu.navigation", true);
		t.verifyVisibility("homepage.menu.activity", true);
		t.verifyVisibility("homepage.menu.activity.icon", true);
		t.verifyVisibility("homepage.menu.therapy", true);
		t.verifyVisibility("homepage.menu.therapy.icon", true);
		t.verifyVisibility("homepage.menu.chat", true);
		t.verifyVisibility("homepage.menu.chat.icon", true);
		t.verifyVisibility("homepage.menu.more", true);
		t.verifyVisibility("homepage.menu.more.icon", true);
		Logs.print("MTC_002_Verify_the_homepage: FINISHED");
		t.getScreenshot("MTC_002_Verify_the_homepage");
	}

	public void MTC_003_Verify_the_My_therapy_week_menu() {
		Logs.step("START: MTC_003_Verify_the_My_therapy_week_menu");
		t.verifyVisibility("homepage.menu.therapy", true);
		t.click("homepage.menu.therapy");
		t.storeVisibility("my.therapy.week.title", 5);
		t.verifyVisibility("my.therapy.week.title", true);
		t.verifyVisibility("my.therapy.this.week", true);
		t.verifyVisibility("my.therapy.exercise.icon", true);
		t.verifyVisibility("my.therapy.exercise", true);
		t.verifyVisibility("my.therapy.exercise.count", true);
		t.verifyVisibility("my.therapy.exercise.arrow", true);
		t.verifyVisibility("my.therapy.knowledge.icon", true);
		t.verifyVisibility("my.therapy.knowledge", true);
		t.verifyVisibility("my.therapy.knowledge.count", true);
		t.verifyVisibility("my.therapy.wellbeing.icon", true);
		t.verifyVisibility("my.therapy.wellbeing", true);
		t.verifyVisibility("my.therapy.wellbeing.count", true);
		t.verifyVisibility("my.therapy.steps", true);
		t.verifyVisibility("my.therapy.steps.progress.txt", true);
		t.verifyVisibility("my.therapy.steps.goal.txt", true);
		t.verifyVisibility("my.therapy.steps.arrow", true);
		Logs.print("MTC_003_Verify_the_My_therapy_week_menu: FINISHED");
		t.getScreenshot("MTC_003_Verify_the_My_therapy_week_menu");
	}

	public void MTC_004_Verify_the_functionality_of_knowledge() {
		Logs.step("START: MTC_004_Verify_the_functionality_of_knowledge");
		t.verifyVisibility("my.therapy.knowledge.icon", true);
		t.click("my.therapy.knowledge.icon");
		t.storeVisibility("knowledge.header.title", 5);
		t.verifyVisibility("knowledge.header.title", true);
		t.verifyVisibility("knowledge.navigate.back", true);
		t.verifyVisibility("knowledge.video.duration", true);
		t.verifyVisibility("knowledge.video.overlay", true);
		t.verifyVisibility("knowledge.video.overlay.pause", true);
		t.click("knowledge.video.duration");
		t.storeVisibility("knowledge.exercises.title", 5);
		t.verifyVisibility("knowledge.exercises.title", true);
		t.verifyVisibility("knowledge.navigate.back", true);
		t.verifyVisibility("knowledge.exercises.duration", true);
		t.verifyVisibility("knowledge.exercises.video.play", true);
		Logs.print("MTC_004_Verify_the_functionality_of_knowledge: FINISHED");
		t.getScreenshot("MTC_004_Verify_the_functionality_of_knowledge");
	}
	public void MTC_005_Logout() {
		Logs.step("START: MTC_005_Logout");
		t.verifyVisibility("homepage.menu.more", true);
		t.click("homepage.menu.more");
		t.storeVisibility("more.title", 5);
		t.verifyVisibility("more.title", true);
		t.verifyVisibility("more.logout", true);
		t.click("more.logout");
		Logs.print("MTC_005_Logout: FINISHED");
		t.getScreenshot("MTC_005_Logout");
	}

}
