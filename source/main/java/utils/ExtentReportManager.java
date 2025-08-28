package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExtentReportManager {
    
    private static ExtentReports extent;
    private static ExtentTest test;
    private static String reportPath;
    
    public static void initializeReport() {
        if (extent == null) {
            // Create reports directory if it doesn't exist
            File reportsDir = new File(ConfigReader.getReportPath());
            if (!reportsDir.exists()) {
                reportsDir.mkdirs();
            }
            
            // Generate report filename with timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            reportPath = ConfigReader.getReportPath() + "Instagram_Test_Report_" + timestamp + ".html";
            
            // Initialize ExtentSparkReporter
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            
            // Configure the reporter
            sparkReporter.config().setDocumentTitle(ConfigReader.getProperty("report.title"));
            sparkReporter.config().setReportName(ConfigReader.getProperty("report.name"));
            sparkReporter.config().setTheme(Theme.STANDARD);
            sparkReporter.config().setTimeStampFormat("EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'");
            
            // Initialize ExtentReports
            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
            
            // Add system information
            extent.setSystemInfo("Operating System", System.getProperty("os.name"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("Browser", ConfigReader.getBrowser());
            extent.setSystemInfo("Environment", ConfigReader.getProperty("environment"));
            extent.setSystemInfo("Base URL", ConfigReader.getBaseUrl());
            
            System.out.println("ExtentReport initialized: " + reportPath);
        }
    }
    
    public static ExtentTest createTest(String testName, String description) {
        test = extent.createTest(testName, description);
        return test;
    }
    
    public static ExtentTest createTest(String testName) {
        test = extent.createTest(testName);
        return test;
    }
    
    public static ExtentTest getTest() {
        return test;
    }
    
    public static void flushReport() {
        if (extent != null) {
            extent.flush();
            System.out.println("ExtentReport flushed successfully");
            System.out.println("Report location: " + reportPath);
        }
    }
    
    public static ExtentReports getExtentReports() {
        return extent;
    }
}