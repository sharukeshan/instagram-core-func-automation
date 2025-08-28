package utils;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JiraIntegration {
    
    private static final String JIRA_URL = ConfigReader.getProperty("jira.url");
    private static final String JIRA_USERNAME = ConfigReader.getProperty("jira.username");
    private static final String JIRA_API_TOKEN = ConfigReader.getProperty("jira.api.token");
    private static final String PROJECT_KEY = ConfigReader.getProperty("jira.project.key");
    private static final String ISSUE_TYPE = ConfigReader.getProperty("jira.issue.type");
    
    public static void createIssue(String testName, String errorMessage, String stackTrace) {
        try {
            // Create JIRA issue JSON
            JSONObject issueJson = createIssueJson(testName, errorMessage, stackTrace);
            
            // Send POST request to JIRA
            String response = sendPostRequest(issueJson.toString());
            
            if (response != null) {
                JSONObject responseJson = new JSONObject(response);
                String issueKey = responseJson.getString("key");
                System.out.println("JIRA Issue created successfully: " + issueKey);
                System.out.println("JIRA URL: " + JIRA_URL + "/browse/" + issueKey);
            }
            
        } catch (Exception e) {
            System.err.println("Failed to create JIRA issue: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static JSONObject createIssueJson(String testName, String errorMessage, String stackTrace) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        JSONObject fields = new JSONObject();
        fields.put("summary", "Test Failure: " + testName + " - " + timestamp);
        
        // Description with detailed information
        StringBuilder description = new StringBuilder();
        description.append("*Test Case:* ").append(testName).append("\n\n");
        description.append("*Execution Time:* ").append(timestamp).append("\n\n");
        description.append("*Environment:* ").append(ConfigReader.getProperty("environment")).append("\n\n");
        description.append("*Browser:* ").append(ConfigReader.getBrowser()).append("\n\n");
        description.append("*Base URL:* ").append(ConfigReader.getBaseUrl()).append("\n\n");
        description.append("*Error Message:*\n").append(errorMessage).append("\n\n");
        description.append("*Stack Trace:*\n{code}\n").append(stackTrace).append("\n{code}\n\n");
        description.append("*Steps to Reproduce:*\n");
        description.append("1. Run the automated test: ").append(testName).append("\n");
        description.append("2. Observe the failure\n\n");
        description.append("*Priority:* Medium\n");
        description.append("*Component:* Instagram Web Application\n");
        
        fields.put("description", description.toString());
        
        // Project
        JSONObject project = new JSONObject();
        project.put("key", PROJECT_KEY);
        fields.put("project", project);
        
        // Issue Type
        JSONObject issueType = new JSONObject();
        issueType.put("name", ISSUE_TYPE);
        fields.put("issuetype", issueType);
        
        // Priority
        JSONObject priority = new JSONObject();
        priority.put("name", "Medium");
        fields.put("priority", priority);
        
        // Labels
        fields.put("labels", new String[]{"automation", "instagram", "selenium"});
        
        JSONObject issue = new JSONObject();
        issue.put("fields", fields);
        
        return issue;
    }
    
    private static String sendPostRequest(String jsonPayload) {
        try {
            URL url = new URL(JIRA_URL + "/rest/api/2/issue/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            // Set request method and headers
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            
            // Set authentication header
            String auth = JIRA_USERNAME + ":" + JIRA_API_TOKEN;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
            connection.setRequestProperty("Authorization", "Basic " + encodedAuth);
            
            connection.setDoOutput(true);
            
            // Write JSON payload
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            // Read response
            int responseCode = connection.getResponseCode();
            if (responseCode == 201) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    return response.toString();
                }
            } else {
                System.err.println("JIRA API Error: " + responseCode);
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        System.err.println(responseLine);
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error sending request to JIRA: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Method to test JIRA connectivity
    public static boolean testConnection() {
        try {
            URL url = new URL(JIRA_URL + "/rest/api/2/myself");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            
            String auth = JIRA_USERNAME + ":" + JIRA_API_TOKEN;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
            connection.setRequestProperty("Authorization", "Basic " + encodedAuth);
            
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                System.out.println("JIRA connection successful");
                return true;
            } else {
                System.err.println("JIRA connection failed: " + responseCode);
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("JIRA connection test failed: " + e.getMessage());
            return false;
        }
    }
}