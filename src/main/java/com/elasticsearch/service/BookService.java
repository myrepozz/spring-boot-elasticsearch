package com.elasticsearch.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.elasticsearch.model.Book;
import com.elasticsearch.model.IBook;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Farhan Haq
 * This class provides Elastic Search CRUD Operations for Book Records
 */

@Service
public class BookService implements IBookService {

	public final String INDEX = "bookdata";
	public final String TYPE = "books";

	private ObjectMapper objectMapper;
	private Map<Status, String> statusMap;
	private RestHighLevelClient restHighLevelClient;

	private static final Logger logger = LoggerFactory.getLogger(BookService.class);

	private enum Status {
		EMPTY, EXISTS, ADD_ERROR, ADD_SUCCESS, DELETE_ERROR, DELETE_SUCCESS, UPDATE_ERROR, UPDATE_SUCCESS
	}


	public BookService(ObjectMapper objectMapper, RestHighLevelClient restHighLevelClient) {
		this.objectMapper = objectMapper;
		this.restHighLevelClient = restHighLevelClient;
		statusMap = new EnumMap<Status, String>(Status.class);
		populateStatusMap();
	}

	// Creates and stores Book Record data into Elastic Search
	@Override
	public Pair<Boolean, String> addBookRecord(IBook book) {

		Optional<String> emptyField = getEmptyField(book);
		if (emptyField.isPresent())
			return Pair.of(Boolean.FALSE, getStatusMessage(Status.EMPTY, emptyField.get()));


		if (bookRecordExists(book))
			return Pair.of(Boolean.FALSE, getStatusMessage(Status.EXISTS));

		if(book.getId()==null)
			book.setId(Book.generateId());

		Map<?, ?> dataMap = objectMapper.convertValue(book, Map.class);
		IndexRequest indexRequest = new IndexRequest(INDEX, TYPE, book.getId()).source(dataMap);
		IndexResponse response = null;
		try {
			response = restHighLevelClient.index(indexRequest);
		} catch (ElasticsearchException e) {
			logger.error(e.getDetailedMessage());
		} catch (java.io.IOException ex) {
			logger.error(ex.getLocalizedMessage());
		}

		return response!=null && response.getResult() == Result.CREATED ? Pair.of(Boolean.TRUE, getStatusMessage(Status.ADD_SUCCESS)) :  Pair.of(Boolean.FALSE, getStatusMessage(Status.ADD_ERROR)) ;
	}

	// Deletes Book Record data from Elastic Search
	@Override
	public Pair<Boolean, String> deleteBookRecord(String id) {
		DeleteRequest deleteRequest = new DeleteRequest(INDEX, TYPE, id);
		DeleteResponse deleteResponse = null;
		try {
			deleteResponse = restHighLevelClient.delete(deleteRequest);
		} catch (java.io.IOException e) {
			logger.error(e.getLocalizedMessage());
		}

		return deleteResponse!=null && deleteResponse.getResult() == Result.DELETED ? Pair.of(Boolean.TRUE, getStatusMessage(Status.DELETE_SUCCESS, id)) : Pair.of(Boolean.FALSE, getStatusMessage(Status.DELETE_ERROR, id));

	}

	// Gets all records as Book Objects 
	@Override
	public List<IBook> getBookRecords() {

		SearchRequest searchRequest = new SearchRequest(INDEX);
		searchRequest.types(TYPE);
		SearchResponse searchResponse = null;

		try {
			searchResponse = restHighLevelClient.search(searchRequest);
		} catch (IOException e) {
			logger.error(e.getMessage());
			return new ArrayList<>();
		}

		return searchResponse == null ? new ArrayList<>()
				: Arrays.asList(searchResponse.getHits().getHits()).stream().map(searchHit -> objectMapper.convertValue(searchHit.getSourceAsMap(), Book.class))
				.collect(Collectors.toList());
	}

	// Gets a specific Book Record with an ID
	@Override
	public IBook getBookRecord(String id) {

		GetRequest getRequest = new GetRequest(INDEX, TYPE, id);
		IBook book = null;
		try {
			GetResponse getResponse = restHighLevelClient.get(getRequest);
			Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
			book = objectMapper.convertValue(sourceAsMap, Book.class);
		} catch (java.io.IOException e) {
			logger.error(e.getLocalizedMessage());
		}

		return book;
	}

	// Updates the contents of a particular Book Record 
	@Override
	public Pair<Boolean, String> updateBookRecord(IBook book) {

		Optional<String> emptyField = getEmptyField(book);
		if (emptyField.isPresent())
			return Pair.of(Boolean.FALSE, getStatusMessage(Status.EMPTY, emptyField.get()));

		UpdateRequest updateRequest = new UpdateRequest(INDEX, TYPE, book.getId()).fetchSource(true);
		UpdateResponse updateResponse = null;
		try {
			String bookJson = objectMapper.writeValueAsString(book);
			updateRequest.doc(bookJson, XContentType.JSON);
			updateResponse = restHighLevelClient.update(updateRequest);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage());
		} catch (java.io.IOException e) {
			logger.error(e.getMessage());
		}

		return updateResponse!=null && updateResponse.getResult() == Result.UPDATED ? Pair.of(Boolean.TRUE, getStatusMessage(Status.UPDATE_SUCCESS, book.getId())): Pair.of(Boolean.FALSE, getStatusMessage(Status.UPDATE_ERROR, book.getId())) ;
	}
	
	@Override
	public void deleteAll() {
		getBookRecords().forEach( record -> deleteBookRecord(record.getId()));
	}

	// Checks if the new book record matches with the existing book records
	private boolean bookRecordExists (IBook book) {

		SearchRequest searchRequest = new SearchRequest();

		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.boolQuery()
				.should(QueryBuilders.termQuery("isbn", book.getIsbn()))
				.should(QueryBuilders.matchQuery("title", book.getTitle()))
				.minimumShouldMatch("1")

				);
		searchRequest.source(sourceBuilder);
		SearchResponse searchResponse = null;
		int num_of_records=0;

		try {
			searchResponse = restHighLevelClient.search(searchRequest);
			num_of_records = searchResponse.getHits().getHits().length;
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

		return num_of_records > 0;
	}

	// Initializes and stores all status messages in map for quick lookup
	private void populateStatusMap(){

		statusMap.put(Status.EXISTS, "Book's ISBN and/or Title matches with the existing record." );
		statusMap.put(Status.ADD_SUCCESS, "Book record has been successfully added.");
		statusMap.put(Status.ADD_ERROR, "Error occurred while adding book record." );
		statusMap.put(Status.DELETE_SUCCESS, "Book record with ID: %s has been successfully deleted." );
		statusMap.put(Status.DELETE_ERROR, "Error deleting book record with ID: %s" );
		statusMap.put(Status.UPDATE_SUCCESS, "Book record with ID: %s has been successfully updated." );
		statusMap.put(Status.UPDATE_ERROR, "Error updating book record with ID: %s" );
		statusMap.put(Status.EMPTY, " %s field cannot be empty." );	

	}

	// Loads data into Elastic search on application startup 
	@PostConstruct
	private void loadData () {

		addBookRecord(Book.of("9781501151774", "The Woman in Cabin 10", "Ruth Ware", 41));
		addBookRecord(Book.of("9780385514231", "Origin", "Dan Brown", 35));
		addBookRecord(Book.of("9780735253308", "The Handmaid's Tale", "Margaret Atwood", 36));
		addBookRecord(Book.of("9781101967683", "The Whistler", "John Grisham", 55));
		addBookRecord(Book.of("9780062654199", "The Alice Network", "Kate Quinn", 60));
		addBookRecord(Book.of("9780385689632", "Into the Water", "Paula Hawkins", 47));
		addBookRecord(Book.of("9780385543026", "Camino Island", "John Grisham", 35));
		addBookRecord(Book.of("9780525954972", "A Column of Fire", "Ken Follett", 43));
		addBookRecord(Book.of("9781250080400", "The Nightingale", "Kristin Hannah", 39));

		logger.info("Books data has been loaded into Elastic Search Engine");

	}

	// Creates status message for web templates
	private String getStatusMessage(Status status, String... strings){
		return strings.length > 0 ? String.format(statusMap.get(status), strings[0]) :statusMap.get(status);	
	}
	
	private Optional<String> getEmptyField (IBook book) {
		String field = null;

		if (book.getIsbn().isEmpty())
			field= "ISBN";
		else if (book.getTitle().isEmpty()){
			field = "Title";
		}
		else if (book.getAuthor().isEmpty()){
			field = "Author";
		}

		return Optional.ofNullable(field);
	}

}
