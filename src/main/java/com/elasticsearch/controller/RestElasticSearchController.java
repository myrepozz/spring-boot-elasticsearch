package com.elasticsearch.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.elasticsearch.model.IBook;
import com.elasticsearch.service.IBookService;

@RestController
public class RestElasticSearchController {

	@Autowired
	private IBookService bookService;

	@GetMapping(value = "/get/all")
	public List<IBook> getBooks() {
		List<IBook> books = bookService.getBookRecords();

		return books;
	}
	
	@GetMapping(value = "/get")
	public IBook getBook(@RequestParam("id") String id) {
		IBook book = bookService.getBookRecord(id);
		return book;
	}
	
}
