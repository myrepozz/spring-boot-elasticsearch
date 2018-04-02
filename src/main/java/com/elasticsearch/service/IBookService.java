package com.elasticsearch.service;

import java.util.List;

import org.springframework.data.util.Pair;

import com.elasticsearch.model.IBook;

public interface IBookService {
	
	/** 
	 * @param book
	 * Adds book record into ES 
	 */
	public Pair<Boolean, String> addBookRecord(IBook book);

	/**
	 * @param id
	 * Deleted book record from ES
	 */
	public Pair<Boolean, String> deleteBookRecord(String id);

	/**
	 * @param id
	 * Retrieves a book record from ES by ID 
	 */
	public IBook getBookRecord(String id);

	/**
	 * Retrieves all book records from ES
	 */
	public List<IBook> getBookRecords();

	/**
	 * @param book
	 * Updates a book record in ES 
	 */
	public Pair<Boolean, String> updateBookRecord(IBook book);
	
	/**
	 * Deletes all book records of Index: bookdata and Type: books
	 */
	public void deleteAll();
	

}
