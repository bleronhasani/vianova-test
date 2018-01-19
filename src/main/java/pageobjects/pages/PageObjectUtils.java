package pageobjects.pages;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.WebDriverFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PageObjectUtils {
    /**
     * Function awaits for the spinner container to finish loading,
     * Usualy means the page is loaded
     */
    public static void spinnerWait() {
        WebDriver driver = WebDriverFactory.getInstance().getWebDriver();
        Long startTime = System.currentTimeMillis();
        try {
            WebElement webElement = driver.findElement(By.className("spinner-container"));
            while (System.currentTimeMillis() - startTime < 20 * 5000 && webElement.isDisplayed()) {
            }
        } catch (StaleElementReferenceException ignored) {

        } catch (NoSuchElementException r) {
            System.out.println("\nSpinner is not visible - Page loaded\n");
        }
    }

    /**
     * Method used to scroll to the top of the current page
     *
     * @throws InterruptedException used for Thread.sleep (pending removal)
     */
    public static void scrollToTop() throws InterruptedException {
        WebDriver driver = WebDriverFactory.getInstance().getWebDriver();
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0)");
        Thread.sleep(1000);
    }

    /**
     * Method used to scroll to the bottom of the current page
     *
     * @param driver Used for passing the current state driver
     * @throws InterruptedException used for Thread.sleep (pending removal)
     */
    public static void scrollToBottom(WebDriver driver) throws InterruptedException {
        ((JavascriptExecutor) driver).executeScript(" window.scrollTo(0,document.body.scrollHeight)");
        Thread.sleep(1000);
    }

    /**
     * Method used to scroll to a view of specified element
     *
     * @param element The element to scroll to.
     * @throws InterruptedException used for Thread.sleep (pending removal)
     */
    public static void scrollToElement(WebElement element) throws InterruptedException {
        WebDriver driver = WebDriverFactory.getInstance().getWebDriver();
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        Thread.sleep(500);
    }

    /**
     * Method used to scroll to a view of specified element and return the element
     *
     * @param driver  Used for passing the current state driver
     * @param element The element to scroll to.
     * @throws InterruptedException used for Thread.sleep (pending removal)
     */
    public static WebElement scrollToElementGet(WebDriver driver, WebElement element) throws InterruptedException {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        Thread.sleep(500);
        return element;
    }

    /**
     * The method can determine whether the list is in a table or not.
     *
     * @param element Element with the list inside
     * @param keys    Keys for filtering
     * @return Return list of filtered items
     */
    public static List<WebElement> getFilteredListByKey(WebElement element, List<String> keys) {
        List<WebElement> result = new ArrayList<>();
        List<WebElement> itemsList;
        String t = element.getAttribute("innerHTML").contains("<tr") ? "tr" : "li";
        itemsList = element.findElements(By.tagName(t));
        result.addAll(itemsList.stream().filter(item -> keys.stream().allMatch(key -> item.getText().toLowerCase().contains(key.toLowerCase()))).collect(Collectors.toList()));
        return result;
    }

    /**
     * Overloads the previous method with only one string as a key
     */
    public static List<WebElement> getFilteredListByKey(WebElement element, String key) {
        return getFilteredListByKey(element, Collections.singletonList(key));
    }

    /**
     * @param element      Receive an element which includes a list
     * @param fullItemName The specific item to look for
     * @return Return the required element if found, otherwise fail.
     */
    public static WebElement getSpecificItemFromListElement(WebElement element, String fullItemName) throws InterruptedException {
        Thread.sleep(4000);
        spinnerWait();
        scrollToTop();
        String t = element.getAttribute("innerHTML").contains("<tr") ? "tr" : "li";
        List<WebElement> itemsList = element.findElements(By.tagName(t));
        for (WebElement item : itemsList) {
            if (item.getText().toLowerCase().contains(fullItemName.toLowerCase())) {
                return item;
            }
        }
        Assert.assertTrue("The item " + fullItemName.toLowerCase() + " was not found in the list", false);
        return null;
    }

    /**
     * @param element      Receive an element which includes a list
     * @param fullItemName The specific item to look for
     * @return Return the required element if found, otherwise fail.
     */
    private static int getIndexOfSpecificItemFromListElement(WebElement element, String fullItemName) {
        String t = element.getAttribute("innerHTML").contains("<tr") ? "tr" : "li";
        List<WebElement> itemsList = element.findElements(By.tagName(t));
        for (int i = 0; i < itemsList.size(); i++) {
            if (itemsList.get(i).getText().toLowerCase().contains(fullItemName.toLowerCase())) {
                return i;
            }
        }
        Assert.assertTrue("The item " + fullItemName.toLowerCase() + " was not found in the list", false);
        return 0;
    }

    /**
     * @param element      Receive an element which includes a list
     * @param fullItemName The specific item to look for
     * @return Return the required element if found, otherwise fail.
     */
    public static WebElement getNextSpecificItemFromListElement(WebElement element, String fullItemName) {
        String t = element.getAttribute("innerHTML").contains("<tr") ? "tr" : "li";
        List<WebElement> itemsList = element.findElements(By.tagName(t));
        return itemsList.get(PageObjectUtils.getIndexOfSpecificItemFromListElement(element, fullItemName) + 1);
    }

    public static boolean isAlertPresent(WebDriver driver) {
        try {
            driver.switchTo().alert();
            return true;
        }   // try
        catch (NoAlertPresentException Ex) {
            return false;
        }
    }

    public static By getElementByLocator(LocatorType locatorType, String id) {
        switch (locatorType) {
            case CLASSNAME:
                return By.className(id);
            case CSS:
                return By.cssSelector(id);
            case ID:
                return By.id(id);
            case LINK:
                return By.linkText(id);
            case NAME:
                return By.name(id);
            case TAGNAME:
                return By.tagName(id);
            case XPATH:
                return By.xpath(id);
        }
        return null;
    }

    static WebElement getElementFromPairs(List<Pair<LocatorType, String>> locatorPairs) throws InterruptedException {
        WebDriver driver = WebDriverFactory.getInstance().getWebDriver();
        WebDriverWait wait = new WebDriverWait(driver, 20);
        WebElement element = null;
        spinnerWait();
        scrollToTop();
        for (Pair<LocatorType, String> loc : locatorPairs) {
            if (element == null)
                element = driver.findElement(getElementByLocator(loc.getKey(), loc.getValue()));
            else element = element.findElement(getElementByLocator(loc.getKey(), loc.getValue()));
        }
        scrollToElement(element);
        return element;
    }

    public static WebElement locateElement(WebDriver webDriver, LocatorType type, String ref) {
        switch (type) {
            case ID:
                return webDriver.findElement(By.id(ref));
            case CLASSNAME:
                return webDriver.findElement(By.className(ref));
            case XPATH:
                return webDriver.findElement(By.xpath(ref));
            case CSS:
                return webDriver.findElement(By.cssSelector(ref));
            case LINK:
                return webDriver.findElement(By.linkText(ref));
            case NAME:
                return webDriver.findElement(By.name(ref));
            case TAGNAME:
                return webDriver.findElement(By.tagName(ref));
        }
        return null;
    }

    public static WebElement getElementFromPairs(LocatorType locType, String id) throws InterruptedException {
        List<Pair<LocatorType, String>> locatorPairs = new ArrayList<>();
        locatorPairs.add(Pair.of(locType, id));
        return getElementFromPairs(locatorPairs);
    }


    public enum LocatorType {
        CLASSNAME, CSS, ID, LINK, NAME, TAGNAME, XPATH;
    }
}
