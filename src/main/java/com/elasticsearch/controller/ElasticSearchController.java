package com.elasticsearch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.elasticsearch.model.*;
import com.elasticsearch.service.IBookService;

/**
 * Controller uses thymeleaf templates for view rendering and binds data to the view through model attributes
 * @author Farhan Haq
 */

@Controller
public class ElasticSearchController {

	private IBookService bookService;

	@Autowired
	public ElasticSearchController(IBookService bookService) {
		this.bookService = bookService;
	}

	// Returns Template for adding a new book record
	@RequestMapping(value = "/addBook", method = RequestMethod.GET)
	public String addBookView(Model model) {
		model.addAttribute("book", new Book());
		return "addBook";
	}

	// Renders view for delete status
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public String deleteBook(Model model, @ModelAttribute Book book) {
		
		String id = book.getId();
		Pair<Boolean, String> result = bookService.deleteBookRecord(id);
		
		model.addAttribute("error", !result.getFirst());
		model.addAttribute("message", result.getSecond());
		
		return "status";
	}
	// Renders view for add status
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String insertBook(Model model, @ModelAttribute Book book) throws Exception {
		
		Pair<Boolean, String> result = bookService.addBookRecord(book);
		
		model.addAttribute("error", !result.getFirst());
		model.addAttribute("message", result.getSecond());
		
		return "status";
	}
	// Renders main view for showing all book records
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String show() {
		return "books";
	}

	/*@RequestMapping(value = "/status", method = RequestMethod.GET)
	public String status() {
		return "status";
	}*/

	// Renders view for update status
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String updateBook(Model model, @ModelAttribute Book book) {
		
		Pair<Boolean, String> result = bookService.updateBookRecord(book);
		
		model.addAttribute("error", !result.getFirst());
		model.addAttribute("message", result.getSecond());
		return "status";
	}

	// Renders view for updating a book record
	@RequestMapping(value = "/updateBook", method = RequestMethod.GET)
	public String updateBookView(Model model, @RequestParam("id") String id) {
		
		IBook book = bookService.getBookRecord(id);
		model.addAttribute("book", book);
		return "updateBook";
	}
}
