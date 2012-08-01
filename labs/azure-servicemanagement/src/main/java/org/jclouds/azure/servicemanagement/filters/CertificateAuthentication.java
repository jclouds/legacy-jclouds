package org.jclouds.azure.servicemanagement.filters;

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;

public class CertificateAuthentication implements HttpRequestFilter{

	@Override
	public HttpRequest filter(HttpRequest request) throws HttpException {
		
		return request;
	}

}
