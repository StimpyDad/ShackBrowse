package net.swigglesoft.shackbrowse.imgur;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


public class ImgurTools
{
	private static final String TAG = ImgurTools.class.getSimpleName();
	private static final String UPLOAD_URL = "https://api.imgur.com/3/imge";

	public static class RefreshAccessTokenTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			String accessToken = ImgurAuthorization.getInstance().requestNewAccessToken();
			if (!TextUtils.isEmpty(accessToken)) {
				Log.i(TAG, "Got new access token");
			}
			else {
				Log.i(TAG, "Could not get new access token");
			}
			return accessToken;
		}
	}

	protected static JSONObject onInput(InputStream in) throws Exception {
		StringBuilder sb = new StringBuilder();
		Scanner scanner = new Scanner(in);
		while (scanner.hasNext()) {
			sb.append(scanner.next());
		}

		JSONObject root = new JSONObject(sb.toString());
		String id = root.getJSONObject("data").getString("id");
		String deletehash = root.getJSONObject("data").getString("deletehash");

		Log.e(TAG, sb.toString());
		Log.i(TAG, "new imgur url: http://imgur.com/" + id + " (delete hash: " + deletehash + ")");
		return root;
	}

	private static int copy(InputStream input, OutputStreamWriter output) throws IOException
	{
		byte[] imageBytes = IOUtils.toByteArray(input);
		String encodedImageData = Base64.encodeToString(imageBytes, Base64.DEFAULT);
		String encodedData = URLEncoder.encode(encodedImageData, "UTF-8");
		output.write(encodedData);
		return encodedData.length();
	}

	public static ImgurUploadResponse uploadImageToImgur (InputStream imageIn)
	{
		HttpURLConnection conn = null;
		InputStream responseIn = null;
		ImgurUploadResponse responseObject = null;

		try {
			conn = (HttpURLConnection) new URL(UPLOAD_URL).openConnection();
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			String imageStartData = URLEncoder.encode("image", "UTF-8") + "=";
			ImgurAuthorization.getInstance().addToHttpURLConnection(conn);

			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(imageStartData);
			copy(imageIn, wr);
			wr.flush();
			wr.close();

			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				responseIn = conn.getInputStream();
				responseObject = new ImgurUploadResponse(true, onInput(responseIn), null, null);
			}
			else {
				String responseCode = String.valueOf(conn.getResponseCode());
				Log.i(TAG, "responseCode=" + responseCode);
				responseIn = conn.getErrorStream();
				StringBuilder sb = new StringBuilder();
				Scanner scanner = new Scanner(responseIn);
				while (scanner.hasNext()) {
					sb.append(scanner.next());
				}
				String errorMessage = "uploadImageToImgur error responseCode: " + responseCode + ",response: " + sb.toString();
				Log.i(TAG, errorMessage);
				FirebaseCrashlytics.getInstance().log(errorMessage);
				responseObject = new ImgurUploadResponse(false, null, errorMessage, null);
			}
		} catch (Exception ex) {
			Log.e(TAG, "Error during POST", ex);
			FirebaseCrashlytics.getInstance().recordException(ex);
			responseObject = new ImgurUploadResponse(false, null, "Error during POST: " + ex.getMessage(), ex);
		} finally {
			try {
				responseIn.close();
			} catch (Exception ignore) {}
			try {
				conn.disconnect();
			} catch (Exception ignore) {}
			try {
				imageIn.close();
			} catch (Exception ignore) {}
		}
		return responseObject;
	}
}
