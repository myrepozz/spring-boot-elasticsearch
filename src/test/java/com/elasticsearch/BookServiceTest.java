package com.elasticsearch;

import static org.junit.Assert.assertEquals;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.test.context.junit4.SpringRunner;

import com.elasticsearch.ElasticsearchDemoApplication;
import com.elasticsearch.model.Book;
import com.elasticsearch.model.IBook;
import com.elasticsearch.service.IBookService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ElasticsearchDemoApplication.class)

/**
 * Note: While executing CRUD operations in test cases, the updated states of the book records are not being retrieved instantaneously
 * through Rest High Level Client
 * That's why a delay has been added between transactions to get the correct and updated states
 */


public class BookServiceTest {
	
	@Autowired
	IBookService bookService;
	
	@Before
    public void emptyData() {
		bookService.deleteAll();		
    }
	
	@Test
    public void testaddBookRecord() {
		
			delay(1000);
			IBook book = Book.of("12345789", "testTitle1", "testAuthor1", 100);
			//Add first time to test if the record gets added successfully
			bookService.addBookRecord(book);
			delay(1000);
			IBook testBook = bookService.getBookRecord(book.getId());
			assertEquals(testBook.getIsbn(), book.getIsbn());
			assertEquals(testBook.getTitle(), book.getTitle());
		    assertEquals(testBook.getAuthor(), book.getAuthor());
		    // Next time it should not add as the record already exists  
		    Pair<Boolean, String> result =  bookService.addBookRecord(testBook);
		    assertEquals(result.getFirst(), false);
		     
		    bookService.deleteAll();
	}
		
		@Test
	    public void testgetBookRecords() {
			
			delay(1000);
			// Create and add three book records
			IBook book1 = Book.of("12345787", "testTitle1", "testAuthor1", 100);
			IBook book2 = Book.of("12345788", "testTitle2", "testAuthor2", 100);
			IBook book3 = Book.of("12345789", "testTitle3", "testAuthor3", 100);
			
			bookService.addBookRecord(book1);
			bookService.addBookRecord(book2);
			bookService.addBookRecord(book3);
			
			delay(2000);
			// Check if all three records have been stored and retrieved from the ES
			List<IBook> books = bookService.getBookRecords();
			assertEquals(books.size(), 3);
			
			bookService.deleteAll();
		}

		@Test
	    public void testUpdateBookRecord() {
			
			delay(1000);
			// Create and add a book record
			IBook book = Book.of("12345755", "testTitle4", "testAuthor4", 100);
			bookService.addBookRecord(book);
			
			delay(1000);
			// Update the book record
			book.setIsbn(book.getIsbn() + "00");
			book.setTitle(book.getTitle() + " updated");
			book.setAuthor(book.getAuthor() + "updated");
			
			bookService.updateBookRecord(book);
			
			delay(1000);
			// Retrieve the book record to check if it has been updated
			IBook testBook = bookService.getBookRecord(book.getId());
			
			 assertEquals(testBook.getIsbn(), book.getIsbn());
			 assertEquals(testBook.getTitle(), book.getTitle());
		     assertEquals(testBook.getAuthor(), book.getAuthor());	
		     
		     bookService.deleteBookRecord(testBook.getId());
		}
		
		@Test
	    public void testDeleteBookRecord() {
			
			delay(1000);
			// Create and add a book record
			IBook book = Book.of("12345755", "testTitle4", "testAuthor4", 100);
			bookService.addBookRecord(book);
			delay(1000);
			assertEquals(bookService.getBookRecords().size(), 1);
			// Delete the book record
		    bookService.deleteBookRecord(book.getId());
		    delay(1000);
		    // Check if the book record has been deleted
		    assertEquals(bookService.getBookRecords().size(), 0);
		}
			
		
		private void delay(int milliseconds){
			try {
				Thread.sleep(milliseconds);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	


