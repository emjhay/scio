package com.scio.model;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class ServiceHandler {

	static String response = null;
	public final static int GET = 1;
	public final static int POST = 2;
	public final static int PUT = 3;
	public final static int DELETE = 4;

	public ServiceHandler() {

	}

	/**
	 * Making service call
	 * 
	 * @url - url to make request
	 * @method - http request method
	 * */
	public String makeServiceCall(String url, int method) {
		return this.makeServiceCall(url, method, null);
	}

	/**
	 * Making service call
	 * 
	 * @url - url to make request
	 * @method - http request method
	 * @params - http request params
	 * */
	public String makeServiceCall(String url, int method, StringEntity se) {
		try {
			// http client
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpEntity httpEntity = null;
			HttpResponse httpResponse = null;

			// Checking http request method type
			if (method == POST) {
				HttpPost httpPost = new HttpPost(url);
				// adding post params
				if (se != null) {
					httpPost.setEntity(se);
				}

				httpResponse = httpClient.execute(httpPost);

			} else if (method == GET) {
				// appending params to url
				// if (params != null) {
				// String paramString = URLEncodedUtils
				// .format(params, "utf-8");
				// url += "?" + paramString;
				// }
				HttpGet httpGet = new HttpGet(url);

				httpResponse = httpClient.execute(httpGet);

			}else if(method == PUT){
				
				HttpPut httpPut = new HttpPut(url);
				
				
				if (se != null) {
					httpPut.setEntity(se);
				}
				
				httpResponse = httpClient.execute(httpPut);
				
			}else if(method == DELETE){
				HttpDelete httpDelete = new HttpDelete(url);
				httpResponse = httpClient.execute(httpDelete);
			}
			
			httpEntity = httpResponse.getEntity();
			
			response = EntityUtils.toString(httpEntity);
			

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return response;

	}

	public String deleteByID(String url) {
		
		String responseCode = "";

		try {

			URL urls = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) urls.openConnection();
			connection.setRequestMethod("DELETE");
			connection.getResponseCode();
			
			responseCode = connection.getResponseMessage();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responseCode;
	}

}
