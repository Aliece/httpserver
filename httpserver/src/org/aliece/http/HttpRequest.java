package org.aliece.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;
import java.util.HashMap;

public class HttpRequest {
	private String rawUrl;
	private HashMap<String, String> headerValues = new HashMap<String, String>();
	private ServletContext servletContext;

	public HttpRequest(InputStream is, ServletContext ctx) throws IOException {
		parseHeader(new InputStreamReader(is, "UTF-8"));
		servletContext = ctx;
	}

	public void parseHeader(Reader reader) throws IOException {
		BufferedReader br = new BufferedReader(reader);
		String line = br.readLine();
		String[] tokens = line.split(" ");
		rawUrl = tokens[1];
		while ((line = br.readLine()) != null && !line.equals("")) {
			tokens = line.split(": ");
			headerValues.put(tokens[0].toLowerCase(), tokens[1].toLowerCase());
		}
	}

	public String getRequestedResource() {
		return (rawUrl.indexOf('?') > 0) ? rawUrl.split("?")[0] : rawUrl;
	}

	public String getHeader(String key) {
		return (String) headerValues.get(key);
	}

	public boolean closeAfterResponse() {
		return getHeader("connection").charAt(0) == 'c';
	}

	public int getContentLength() {
		String lengthStr = (String) headerValues.get("content-length");
		return (lengthStr != null) ? Integer.parseInt(lengthStr) : 0;
	}

	@SuppressWarnings("deprecation")
	public long getModifiedSince() {
		String tmp = (String) headerValues.get("if-modified-since");
		return (tmp == null) ? 0 : Date.parse(tmp);
	}

	public ServletContext getServletContext() {
		return servletContext;
	}
}
