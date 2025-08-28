package tests;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.HomePage;
import utils.ConfigReader;
import utils.ExtentReportManager;

public class LoginTest extends BaseTest {
    
    @Test(priority = 1, description = "Verify successful login with valid credentials")
    public void testValidLogin() {
        test = ExtentReportManager.createTest("Valid Login Test", 
               "Test login functionality with valid username and password");
        
        try {
            test.log(Status.INFO, "Starting login test with valid credentials");
            
            // Initialize Login Page
            LoginPage loginPage = new LoginPage(driver);
            test.log(Status.INFO, "Login page initialized");
            
            // Verify login page is displayed
            Assert.assertTrue(loginPage.isLoginPageDisplayed(), "Login page should be displayed");
            test.log(Status.PASS, "Login page is displayed successfully");
            
            // Perform login
            String username = ConfigReader.getTestUsername();
            String password = ConfigReader.getTestPassword();
            
            test.log(Status.INFO, "Entering credentials for user: " + username);
            HomePage homePage = loginPage.performLogin(username, password);
            
            // Verify successful login by checking if home page is displayed
            Thread.sleep(5000); // Wait for page to load
            Assert.assertTrue(homePage.isHomePageDisplayed(), "Home page should be displayed after login");
            
            test.log(Status.PASS, "Login successful - Home page displayed");
            System.out.println("✓ Valid login test passed");
            
        } catch (Exception e) {
            test.log(Status.FAIL, "Login test failed: " + e.getMessage());
            System.err.println("✗ Valid login test failed: " + e.getMessage());
            Assert.fail("Test failed: " + e.getMessage());
        }
    }
    
    @Test(priority = 2, description = "Verify login fails with invalid credentials")
    public void testInvalidLogin() {
        test = ExtentReportManager.createTest("Invalid Login Test", 
               "Test login functionality with invalid username and password");
        
        try {
            test.log(Status.INFO, "Starting login test with invalid credentials");
            
            // Initialize Login Page
            LoginPage loginPage = new LoginPage(driver);
            test.log(Status.INFO, "Login page initialized");
            
            // Verify login page is displayed
            Assert.assertTrue(loginPage.isLoginPageDisplayed(), "Login page should be displayed");
            test.log(Status.PASS, "Login page is displayed successfully");
            
            // Attempt login with invalid credentials
            String invalidUsername = ConfigReader.getProperty("invalid.username");
            String invalidPassword = ConfigReader.getProperty("invalid.password");
            
            test.log(Status.INFO, "Attempting login with invalid credentials: " + invalidUsername);
            loginPage.performLogin(invalidUsername, invalidPassword);
            
            // Wait for error message
            Thread.sleep(3000);
            
            // Verify error message is displayed
            Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed for invalid login");
            
            String errorMessage = loginPage.getErrorMessage();
            test.log(Status.PASS, "Error message displayed: " + errorMessage);
            System.out.println("✓ Invalid login test passed - Error message: " + errorMessage);
            
        } catch (Exception e) {
            test.log(Status.FAIL, "Invalid login test failed: " + e.getMessage());
            System.err.println("✗ Invalid login test failed: " + e.getMessage());
            Assert.fail("Test failed: " + e.getMessage());
        }
    }
    
    @Test(priority = 3, description = "Verify login with empty credentials")
    public void testEmptyCredentialsLogin() {
        test = ExtentReportManager.createTest("Empty Credentials Login Test", 
               "Test login functionality with empty username and password");
        
        try {
            test.log(Status.INFO, "Starting login test with empty credentials");
            
            // Initialize Login Page
            LoginPage loginPage = new LoginPage(driver);
            test.log(Status.INFO, "Login page initialized");
            
            // Verify login page is displayed
            Assert.assertTrue(loginPage.isLoginPageDisplayed(), "Login page should be displayed");
            test.log(Status.PASS, "Login page is displayed successfully");
            
            // Attempt login with empty credentials
            test.log(Status.INFO, "Attempting login with empty credentials");
            loginPage.performLogin("", "");
            
            // Wait for validation
            Thread.sleep(2000);
            
            // Verify we're still on login page (login should not succeed)
            Assert.assertTrue(loginPage.isLoginPageDisplayed(), "Should remain on login page with empty credentials");
            test.log(Status.PASS, "Login correctly prevented with empty credentials");
            System.out.println("✓ Empty credentials login test passed");
            
        } catch (Exception e) {
            test.log(Status.FAIL, "Empty credentials login test failed: " + e.getMessage());
            System.err.println("✗ Empty credentials login test failed: " + e.getMessage());
            Assert.fail("Test failed: " + e.getMessage());
        }
    }
    
    @Test(priority = 4, description = "Verify login page elements are present")
    public void testLoginPageElements() {
        test = ExtentReportManager.createTest("Login Page Elements Test", 
               "Verify all required elements are present on login page");