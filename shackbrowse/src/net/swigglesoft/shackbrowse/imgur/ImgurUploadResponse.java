package net.swigglesoft.shackbrowse.imgur;

import org.json.JSONObject;

public class ImgurUploadResponse
{
	public boolean success = false;
	public Exception exception = null;
	public JSONObject response = null;
	public String errorMessage = null;

	ImgurUploadResponse(boolean success, JSONObject response, String errorMessage, Exception exception) {
		this.exception = exception;
		this.success = success;
		this.errorMessage = errorMessage;
		this.response = response;
	}
}
