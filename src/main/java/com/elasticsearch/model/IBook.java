package com.elasticsearch.model;

public interface IBook {

	/**
	 * Get Author's name
	 */
	public String getAuthor();

	/**
	 * Get Book Id
	 */
	public String getId();

	/**
	 * Get Book ISBN
	 */
	public String getIsbn();

	/**
	 * Get Book Price
	 */
	public float getPrice();

	/**
	 * Get Book title
	 */
	public String getTitle();

	/**
	 * Set Author's name
	 */
	public void setAuthor(String author);

	/**
	 * Set Book Id
	 */
	public void setId(String id);

	/**
	 * Get Book ISBN
	 */
	public void setIsbn(String isbn);

	/**
	 * Set Book price
	 */
	public void setPrice(float price);

	/**
	 * Set Book Title
	 */
	public void setTitle(String title);

}
