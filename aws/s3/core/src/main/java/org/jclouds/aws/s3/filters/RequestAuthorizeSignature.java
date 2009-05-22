/**
 *
 * Copyright (C) 2009 Adrian Cole <adrian@jclouds.org>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.aws.s3.filters;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.jclouds.aws.s3.reference.S3Constants;
import org.jclouds.aws.s3.util.DateService;
import org.jclouds.aws.s3.util.S3Utils;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpHeaders;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Signs the S3 request. This will update timestamps at most once per second.
 * 
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AmazonS3/latest/RESTAuthentication.html"
 *      />
 * @author Adrian Cole
 * 
 */
public class RequestAuthorizeSignature implements HttpRequestFilter {
    private static final String[] firstHeadersToSign = new String[] {
	    HttpHeaders.CONTENT_MD5, HttpHeaders.CONTENT_TYPE, HttpHeaders.DATE };

    private final String accessKey;
    private final String secretKey;
    private final DateService dateService;

    public static final long BILLION = 1000000000;
    private final AtomicReference<String> timeStamp;
    private final AtomicLong trigger = new AtomicLong(System.nanoTime() + 1
	    * BILLION);

    /**
     * Start the time update service. Amazon clocks need to be within 900
     * seconds of the request time. This method updates the clock every second.
     * This is not performed per-request, as creation of the date object is a
     * slow, synchronized command.
     */
    synchronized void updateIfTimeOut() {

	if (trigger.get() - System.nanoTime() <= 0) {
	    timeStamp.set(createNewStamp());
	    trigger.set(System.nanoTime() + 1 * BILLION);
	}

    }

    // this is a hotspot when submitted concurrently, so be lazy.
    // amazon is ok with up to 15 minutes off their time, so let's
    // be as lazy as possible.
    String createNewStamp() {
	return dateService.rfc822DateFormat();
    }

    public String timestampAsHeaderString() {
	updateIfTimeOut();
	return timeStamp.get();
    }

    @Inject
    public RequestAuthorizeSignature(
	    @Named(S3Constants.PROPERTY_AWS_ACCESSKEYID) String accessKey,
	    @Named(S3Constants.PROPERTY_AWS_SECRETACCESSKEY) String secretKey,
	    DateService dateService) {
	this.accessKey = accessKey;
	this.secretKey = secretKey;
	this.dateService = dateService;
	timeStamp = new AtomicReference<String>(createNewStamp());
    }

    public void filter(HttpRequest request) throws HttpException {
	// re-sign the request
	removeOldHeaders(request);

	addDateHeader(request);

	String toSign = createStringToSign(request);

	addAuthHeader(request, toSign);
    }

    public static String createStringToSign(HttpRequest request) {
	StringBuilder buffer = new StringBuilder();
	appendMethod(request, buffer);
	appendHttpHeaders(request, buffer);
	appendAmzHeaders(request, buffer);
	appendBucketName(request, buffer);
	appendUriPath(request, buffer);
	return buffer.toString();
    }

    private void removeOldHeaders(HttpRequest request) {
	request.getHeaders().removeAll(S3Constants.AUTHORIZATION);
	request.getHeaders().removeAll(HttpHeaders.CONTENT_TYPE);
	request.getHeaders().removeAll(HttpHeaders.DATE);
    }

    private void addAuthHeader(HttpRequest request, String toSign)
	    throws HttpException {
	String signature;
	try {
	    signature = S3Utils.hmacSha1Base64(toSign, secretKey.getBytes());
	} catch (Exception e) {
	    throw new HttpException("error signing request", e);
	}
	request.getHeaders().put(S3Constants.AUTHORIZATION,
		"AWS " + accessKey + ":" + signature);
    }

    private static void appendMethod(HttpRequest request, StringBuilder toSign) {
	toSign.append(request.getMethod()).append("\n");
    }

    private void addDateHeader(HttpRequest request) {
	request.getHeaders().put(HttpHeaders.DATE, timestampAsHeaderString());
    }

    private static void appendAmzHeaders(HttpRequest request,
	    StringBuilder toSign) {
	Set<String> headers = new TreeSet<String>(request.getHeaders().keySet());
	for (String header : headers) {
	    if (header.startsWith("x-amz-")) {
		toSign.append(header).append(":");
		for (String value : request.getHeaders().get(header))
		    toSign.append(value.replaceAll("\r?\n", "")).append(",");
		toSign.deleteCharAt(toSign.lastIndexOf(","));
		toSign.append("\n");
	    }
	}
    }

    private static void appendHttpHeaders(HttpRequest request,
	    StringBuilder toSign) {
	for (String header : firstHeadersToSign)
	    toSign.append(valueOrEmpty(request.getHeaders().get(header)))
		    .append("\n");
    }

    private static void appendBucketName(HttpRequest request,
	    StringBuilder toSign) {
	String hostHeader = request.getHeaders().get(HttpHeaders.HOST)
		.iterator().next();
	if (hostHeader.endsWith(".s3.amazonaws.com"))
	    toSign.append("/").append(
		    hostHeader.substring(0, hostHeader.length() - 17));
    }

    private static void appendUriPath(HttpRequest request, StringBuilder toSign) {
	int queryIndex = request.getUri().indexOf('?');
	if (queryIndex >= 0)
	    toSign.append(request.getUri().substring(0, queryIndex));
	else
	    toSign.append(request.getUri());
    }

    private static String valueOrEmpty(Collection<String> collection) {
	return (collection != null && collection.size() >= 1) ? collection
		.iterator().next() : "";
    }
}