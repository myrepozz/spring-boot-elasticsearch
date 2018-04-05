package com.elasticsearch.model;

public class ResultData implements IResultData {

	public static IResultData of(boolean error, String message) {
		return new ResultData(error, message);

	}

	private boolean error;

	private String message;

	public ResultData(boolean error, String message) {
		this.error = error;
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public boolean hasError() {
		return error;

	}

}
