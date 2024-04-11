package com.library.steps;

import com.library.pages.BasePage;
import com.library.pages.BookPage;
import com.library.pages.DashBoardPage;
import com.library.pages.LoginPage;
import com.library.utility.BrowserUtil;
import com.library.utility.ConfigurationReader;
import com.library.utility.DB_Util;
import com.library.utility.Driver;
import io.cucumber.java.en.*;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.eo.Se;
import org.junit.Assert;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.Map;

public class UserStepDefs {
    String actualUserCount;

    @Given("Establish the database connection")
    public void establish_the_database_connection() {

        // Create DB connection
        // DB_Util.createConnection();
        System.out.println("-----------------------------------------");
        System.out.println("------    DB CONN IS DONE BY HOOK -------");
        System.out.println("-----------------------------------------");


    }

    @When("Execute query to get all IDs from users")
    public void execute_query_to_get_all_i_ds_from_users() {
        String query = "select count(id) from users";
        DB_Util.runQuery(query);

        actualUserCount = DB_Util.getFirstRowFirstColumn();
        System.out.println("actualUserCount = " + actualUserCount);
    }

    @Then("verify all users has unique ID")
    public void verify_all_users_has_unique_id() {
        String query = "select  count(distinct id) from users";
        DB_Util.runQuery(query);

        String expectedUserCount = DB_Util.getFirstRowFirstColumn();
        System.out.println("expectedUserCount = " + expectedUserCount);

        Assert.assertEquals(expectedUserCount, actualUserCount);


        // Close DB Conn
        // DB_Util.destroy();

        System.out.println("-----------------------------------------");
        System.out.println("------    DB CONN IS CLOSED BY HOOK -------");
        System.out.println("-----------------------------------------");


    }


    @When("Execute query to get all columns")
    public void execute_query_to_get_all_columns() {

        DB_Util.runQuery("select * from users");


    }

    @Then("verify the below columns are listed in result")
    public void verify_the_below_columns_are_listed_in_result(List<String> expectedColumnNames) {

        List<String> actualColumnNames = DB_Util.getAllColumnNamesAsList();

        Assert.assertEquals(expectedColumnNames, actualColumnNames);

    }

    LoginPage loginPage;
    DashBoardPage dashBoardPage;
    String globalBorrowedBooksNumberUI;

    @Given("the {string} on the home page")
    public void the_on_the_home_page(String userType) {

        loginPage = new LoginPage();
        loginPage.login(userType);

    }

    @When("the librarian gets borrowed books number")
    public void the_librarian_gets_borrowed_books_number() {

        dashBoardPage = new DashBoardPage();
        BrowserUtil.waitFor(5);
        String borrowedBooksNumberUI = dashBoardPage.borrowedBooksNumber.getText();
        globalBorrowedBooksNumberUI = borrowedBooksNumberUI;

    }

    @Then("borrowed books number information must match with DB")
    public void borrowed_books_number_information_must_match_with_db() {

        DB_Util.runQuery("select count(*) from book_borrow where is_returned =0");
        String borrowedBooksNumberDB = DB_Util.getFirstRowFirstColumn();


        Assert.assertEquals(globalBorrowedBooksNumberUI, borrowedBooksNumberDB);

    }






    BasePage basePage;
    List<String> categoriesFromUi;
    BookPage bookPage = new BookPage();


    @When("the user navigates to {string} page")
    public void the_user_navigates_to_page(String module) {

        basePage = new BookPage();
        basePage.navigateModule(module);

    }

    @When("the user clicks book categories")
    public void the_user_clicks_book_categories() {


        Select select = new Select(((BookPage) basePage).mainCategoryElement);

        List<WebElement> options = select.getOptions();
        categoriesFromUi = BrowserUtil.getElementsText(options);

        categoriesFromUi.remove("ALL");
        System.out.println("categoriesFromUi = " + categoriesFromUi);


    }

    @Then("verify book categories must match book_categories table from db")
    public void verify_book_categories_must_match_book_categories_table_from_db() {

        DB_Util.runQuery("select name from book_categories");

        List<String> categoriesFromDB = DB_Util.getColumnDataAsList("name");

        Assert.assertEquals( categoriesFromUi, categoriesFromDB);
        System.out.println("categoriesFromDB = " + categoriesFromDB);
    }





    String searchedBook;

    @When("the user searches for {string} book")
    public void the_user_searches_for_book(String userSearchSomething) {

searchedBook = userSearchSomething;
bookPage.search.sendKeys(searchedBook);
BrowserUtil.waitFor(1);



    }


    @When("the user clicks edit book button")
    public void the_user_clicks_edit_book_button() {
        bookPage.editBook(searchedBook).click();
        BrowserUtil.waitFor(1);

    }

    @Then("book information must match the Database")
    public void book_information_must_match_the_database() {

        String UIbookName = bookPage.bookName.getAttribute("value");
        System.out.println("UIbookName = " + UIbookName);
        String UIauthorName = bookPage.author.getAttribute("value");
        String UI_ISBN = bookPage.isbn.getAttribute("value");
        String UIdescription = bookPage.description.getAttribute("value");
        String UI_year = bookPage.year.getAttribute("value");


        Select categoryDropDown = new Select(bookPage.categoryDropdown);
        String UI_category = categoryDropDown.getFirstSelectedOption().getText();

// 2.Get data from DB

        String query = "select b.name as bookName, b.year, b.author, b.description, b.isbn, bc.name as categoryName from books b inner join book_categories bc on b.book_category_id = bc.id where b.name='Clean Code'";
        DB_Util.runQuery(query);
        Map<String, String> DB_info_rowMap = DB_Util.getRowMap(1);
        System.out.println("DB_info_rowMap = " + DB_info_rowMap);

        // 3. Compare 2 data
        Assert.assertEquals(UIbookName,DB_info_rowMap.get("bookName"));
        Assert.assertEquals(UI_category,DB_info_rowMap.get("categoryName"));
        Assert.assertEquals(UIdescription,DB_info_rowMap.get("description"));


    }






    @When("I execute query to find most popular book genre")
    public void i_execute_query_to_find_most_popular_book_genre() {
        DB_Util.runQuery("");
    }
    @Then("verify {string} is the most popular book genre.")
    public void verify_is_the_most_popular_book_genre(String string) {

    }







}
