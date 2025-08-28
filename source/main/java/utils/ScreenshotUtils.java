package utils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotUtils {
    
    public static String takeScreenshot(WebDriver driver, String testName) {
        try {
            // Create screenshots directory if it doesn't exist
            File screenshotDir = new File(ConfigReader.getScreenshotPath());
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }
            
            // Take screenshot
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
            
            // Generate filename with timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String fileName = testName + "_" + timestamp + ".png";
            File destFile = new File(screenshotDir, fileName);
            
            // Copy screenshot to destination
            FileUtils.copyFile(sourceFile, destFile);
            
            System.out.println("Screenshot saved: " + destFile.getAbsolutePath());
            return destFile.getAbsolutePath();
            
        } catch (IOException e) {
            System.err.println("Failed to take screenshot: " + e.getMessage());
            return null;
        }
    }
    
    public static String takeScreenshotBase64(WebDriver driver) {
        try {
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            return takesScreenshot.getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            System.err.println("Failed to take base64 screenshot: " + e.getMessage());
            return null;
        }
    }
}