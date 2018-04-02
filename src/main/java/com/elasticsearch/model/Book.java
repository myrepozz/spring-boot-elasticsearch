package com.elasticsearch.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Java Object Model for Book Record**/

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Book implements IBook {
	
	public static String generateId(){
		return UUID.randomUUID().toString().split("-")[0];
	}
	
	public static IBook of(String isbn, String title, String author, float price){
		return new Book(isbn, title, author, price);
	}
	
	public Book () {}

	public Book(String isbn, String title, String author, float price) {
		
		this.id = generateId();
		this.isbn = isbn;
		this.title = title;
		this.author = author;
		this.price = price;
		
	}

	private String id;
	private String isbn;
	private String title;
	private String author;
	private float price;

	@Override
	public String getAuthor() {
		return author;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getIsbn() {
		return isbn;
	}

	@Override
	public float getPrice() {
		return price;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setAuthor(String author) {
		this.author = author;
	}

	@Override
	public void setId(String id) {
		this.id = id;

	}

	@Override
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	@Override
	public void setPrice(float price) {
		this.price = price;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString(){
		return "ID: " + this.getId() +  " ISBN: "+ getIsbn() +" Title: " + this.getTitle() + " Author: " + this.getAuthor()+ " Price: "+ this.getPrice();
	}
}
