package com.book.management.book;

import com.book.management.book.controller.BookControllerTest;
import com.book.management.book.service.impl.BookServiceImplTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Test Suite to run all tests for the Book Service.
 * 
 * Run this class to execute all controller and service tests.
 * 
 * @author Aditya Srivastava
 */
@Suite
@SuiteDisplayName("Book Service - All Tests")
@SelectClasses({
    BookControllerTest.class,
    BookServiceImplTest.class
})
public class BookServiceTestSuite {
    // This class serves as the entry point for running all tests.
    // JUnit Platform will discover and run all tests from the selected classes.
}
