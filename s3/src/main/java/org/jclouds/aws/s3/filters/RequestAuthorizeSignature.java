/**
 *
 * Copyright (C) 2009 Adrian Cole <adriancole@jclouds.org>
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.jclouds.aws.s3.DateService;
import org.jclouds.aws.s3.S3Constants;
import org.jclouds.aws.s3.S3Utils;
import org.jclouds.http.HttpConstants;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.joda.time.DateTime;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class RequestAuthorizeSignature implements HttpRequestFilter {
    private static final String[] firstHeadersToSign = new String[] {
	    "Content-MD5", "Content-Type", "Date" };

    private final String accessKey;
    private final String secretKey;
    private final DateService dateService;
    private Map<String, DateTime> dateCache = new ConcurrentHashMap<String, DateTime>();

    public static final long BILLION = 1000000000;
    private final AtomicReference<String> timeStamp;
    private AtomicLong trigger = new AtomicLong(System.nanoTime() + 1 * BILLION);

    /**
     * Start the time update service. Amazon clocks need to be within 900
     * seconds of the request time. This method updates the clock every second.
     * This is not performed per-request, as creation of the date object is a
     * slow, synchronized command.
     */
    synchronized void updateIfTimeOut() {
	if (trigger.get() - System.nanoTime() <= 0) {
	    timeStamp.set(createNewStamp());
	}
	trigger.set(System.nanoTime() + 1 * BILLION);
    }

    // this is a hotspot when submitted concurrently, so be lazy.
    // amazon is ok with up to 15 minutes off their time, so let's
    // be as lazy as possible.
    String createNewStamp() {
	return dateService.timestampAsHeaderString();
    }

    public String timestampAsHeaderString() {
	updateIfTimeOut();
	return timeStamp.get();
    }

    public DateTime dateTimeFromXMLFormat(String toParse) {
	DateTime time = dateCache.get(toParse);
	if (time == null) {
	    time = dateService.dateTimeFromXMLFormat(toParse);
	    dateCache.put(toParse, time);
	}
	return time;
    }

    public DateTime dateTimeFromHeaderFormat(String toParse) {
	DateTime time = dateCache.get(toParse);
	if (time == null) {
	    time = dateService.dateTimeFromHeaderFormat(toParse);
	    dateCache.put(toParse, time);
	}
	return time;
    }

    @Inject
    public RequestAuthorizeSignature(
	    @Named("jclouds.aws.accesskeyid") String accessKey,
	    @Named("jclouds.aws.secretaccesskey") String secretKey,
	    DateService dateService) {
	this.accessKey = accessKey;
	this.secretKey = secretKey;
	this.dateService = dateService;
	timeStamp = new AtomicReference<String>(createNewStamp());
    }

    public void filter(HttpRequest request) throws HttpException {
	// re-sign the request
	request.getHeaders().removeAll(S3Constants.AUTH);
	request.getHeaders().removeAll(HttpConstants.CONTENT_TYPE);

	StringBuilder toSign = new StringBuilder();
	if (request.getContent() != null && request.getContentType() != null) {
	    request.getHeaders().put(HttpConstants.CONTENT_TYPE,
		    request.getContentType());
	}
	toSign.append(request.getMethod()).append("\n");
	request.getHeaders().put(HttpConstants.DATE,
		dateService.timestampAsHeaderString());
	for (String header : firstHeadersToSign)
	    toSign.append(
		    valueOrEmpty(request.getHeaders().get(header))).append("\n");
	for (String header : request.getHeaders().keySet()) {
	    if (header.startsWith("x-amz-")) {
		toSign.append(header).append(":");
		for (String value : request.getHeaders().get(header))
		    toSign.append(value.replaceAll("\r?\n", " ")).append(",");
		toSign.deleteCharAt(toSign.lastIndexOf(","));
		toSign.append("\n");
	    }
	}
	// to do amazon headers
	String hostHeader = request.getHeaders().get(HttpConstants.HOST)
		.iterator().next();
	if (hostHeader.endsWith(".s3.amazonaws.com"))
	    toSign.append("/").append(
		    hostHeader.substring(0, hostHeader.length() - 17));
	toSign.append(request.getUri());
	String signature;
	try {
	    signature = S3Utils.digest(toSign.toString(), secretKey.getBytes());
	} catch (Exception e) {
	    throw new HttpException("error signing request", e);
	}
	request.getHeaders().put("Authorization",
		"AWS " + accessKey + ":" + signature);
    }

    private String valueOrEmpty(Collection<String> collection) {
	return (collection != null && collection.size() >= 1) ? collection
		.iterator().next() : "";
    }
}