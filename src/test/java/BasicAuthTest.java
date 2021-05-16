import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.network.Network;
import org.openqa.selenium.devtools.network.model.Headers;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.testng.Assert.assertEquals;

/**
 * Project Name    : basic-authentication-handling-selenium-demo
 * Developer       : Osanda Deshan
 * Version         : 1.0.0
 * Date            : 5/16/2021
 * Time            : 2:04 PM
 * Description     :
 **/

public class BasicAuthTest {

    private ChromeDriver driver;

    @BeforeMethod
    public void setup() {
        // Setup Chrome driver
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(30, SECONDS);

        // Authentication username & password
        String username = "admin";
        String password = "admin";

        // Get the devtools from the running driver and create a session
        DevTools devTools = driver.getDevTools();
        devTools.createSession();

        // Enable the Network domain of devtools
        devTools.send(Network.enable(Optional.of(100000), Optional.of(100000), Optional.of(100000)));
        String auth = username + ":" + password;

        // Encoding the username and password using Base64 (java.util)
        String encodeToString = Base64.getEncoder().encodeToString(auth.getBytes());

        // Pass the network header -> Authorization : Basic <encoded String>
        Map<String, Object> headers = new HashMap<>();
        headers.put("Authorization", "Basic " + encodeToString);
        devTools.send(Network.setExtraHTTPHeaders(new Headers(headers)));

        // Load the application url
        driver.get("https://the-internet.herokuapp.com/basic_auth");
    }

    @Test
    public void testValidLogin() {
        String successFullyLoggedInText = driver.findElementByXPath("//p").getText();
        assertEquals(successFullyLoggedInText, "Congratulations! You must have the proper credentials.");
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }
}
