package com.elasticsearch.service;

import java.util.List;

import com.elasticsearch.model.*;

public interface IBookService {

	/**
	 * @param book
	 *        Adds book record into ES
	 */
	public IResultData addBookRecord(IBook book);

	/**
	 * Deletes all book records of Index: bookdata and Type: books
	 */
	public void deleteAll();

	/**
	 * @param id
	 *        Deleted book record from ES
	 */
	public IResultData deleteBookRecord(String id);

	/**
	 * @param id
	 *        Retrieves a book record from ES by ID
	 */
	public IBook getBookRecord(String id);

	/**
	 * Retrieves all book records from ES
	 */
	public List<IBook> getBookRecords();

	/**
	 * @param book
	 *        Updates a book record in ES
	 */
	public IResultData updateBookRecord(IBook book);

}
