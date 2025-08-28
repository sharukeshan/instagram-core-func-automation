package base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.testng.annotations.*;
import utils.ConfigReader;
import utils.ExtentReportManager;
import utils.ScreenshotUtils;
import utils.JiraIntegration;
import com.aventstack.extentreports.ExtentTest;

import java.time.Duration;

public class BaseTest {
    
    protected static WebDriver driver;
    protected static ExtentTest test;
    
    @BeforeSuite
    public void setUpSuite() {
        ExtentReportManager.initializeReport();
        System.out.println("=== Instagram Automation Test Suite Started ===");
    }
    
    @BeforeMethod
    public void setUp() {
        initializeDriver();
        driver.manage().timeouts().implicitlyWait(
            Duration.ofSeconds(Integer.parseInt(ConfigReader.getProperty("implicit.wait")))
        );
        driver.manage().window().maximize();
        driver.get(ConfigReader.getProperty("base.url"));
        System.out.println("Browser launched and navigated to Instagram");
    }
    
    private void initializeDriver() {
        String browser = ConfigReader.getProperty("browser").toLowerCase();
        
        switch (browser) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--disable-notifications");
                chromeOptions.addArguments("--disable-popup-blocking");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--no-sandbox");
                
                if (ConfigReader.getProperty("headless").equals("true")) {
                    chromeOptions.addArguments("--headless");
                }
                driver = new ChromeDriver(chromeOptions);
                break;
                
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;
                
            case "edge":
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver();
                break;
                
            default:
                throw new IllegalArgumentException("Browser not supported: " + browser);
        }
    }
    
    @AfterMethod
    public void tearDown(org.testng.ITestResult result) {
        if (result.getStatus() == org.testng.ITestResult.FAILURE) {
            // Take screenshot on failure
            if (ConfigReader.getProperty("take.screenshot.on.failure").equals("true")) {
                String screenshotPath = ScreenshotUtils.takeScreenshot(driver, result.getMethod().getMethodName());
                test.addScreenCaptureFromPath(screenshotPath);
                System.out.println("Screenshot captured: " + screenshotPath);
            }
            
            // Create JIRA issue on failure
            if (ConfigReader.getProperty("jira.create.issue.on.failure").equals("true")) {
                JiraIntegration.createIssue(
                    result.getMethod().getMethodName(), 
                    result.getThrowable().getMessage(),
                    result.getThrowable().toString()
                );
            }
        }
        
        if (driver != null) {
            driver.quit();
            System.out.println("Browser closed");
        }
    }
    
    @AfterSuite
    public void tearDownSuite() {
        ExtentReportManager.flushReport();
        System.out.println("=== Instagram Automation Test Suite Completed ===");
    }
    
    // Utility method to get driver instance
    public static WebDriver getDriver() {
        return driver;
    }
}