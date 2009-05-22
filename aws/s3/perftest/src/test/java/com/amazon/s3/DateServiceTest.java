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
package com.amazon.s3;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;

import org.jclouds.aws.PerformanceTest;
import org.jclouds.aws.s3.util.DateService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/* 
 * TODO: Scrap any non-DateService references (eg Joda & Amazon) if/when
 * we confirm that using the Java primitives is better/faster.
 */

/**
 * Compares performance of date operations
 * 
 * @author Adrian Cole
 * @author James Murty
 */
@Test(sequential = true, timeOut = 2 * 60 * 1000, testName = "s3.DateTest")
public class DateServiceTest extends PerformanceTest {
    Injector i = Guice.createInjector();

    DateService dateService = i.getInstance(DateService.class);

    private TestData[] testData;
    class TestData {
        public final String iso8601DateString;
        public final String rfc822DateString;
        public final Date date;
    	
        TestData(String iso8601, String rfc822, Date date) {
        	this.iso8601DateString = iso8601;
        	this.rfc822DateString = rfc822;
        	this.date = date;
        }
    }
        
    private long startTime;
    

    public DateServiceTest() {
        // Constant time test values, each TestData item must contain matching times!
    	testData = new TestData[] {
    		new TestData("2009-03-12T02:00:07.000Z", "Thu, 12 Mar 2009 02:00:07 GMT", new Date(1236823207000l)),    	
    		new TestData("2009-03-14T04:00:07.000Z", "Sat, 14 Mar 2009 04:00:07 GMT", new Date(1237003207000l)),
    		new TestData("2009-03-16T06:00:07.000Z", "Mon, 16 Mar 2009 06:00:07 GMT", new Date(1237183207000l)),    	
    		new TestData("2009-03-18T08:00:07.000Z", "Wed, 18 Mar 2009 08:00:07 GMT", new Date(1237363207000l)),    	
    		new TestData("2009-03-20T10:00:07.000Z", "Fri, 20 Mar 2009 10:00:07 GMT", new Date(1237543207000l))    	
    	};
    }
    
    
    // Joda items for performance comparisons
    private DateTimeFormatter headerDateFormat = DateTimeFormat
    	.forPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'");

    private DateTime jodaParseIso8601(String toParse) {
    	return new DateTime(toParse);
    }

    private DateTime jodaParseRfc822(String toParse) {
    	return headerDateFormat.parseDateTime(toParse);
    }

    private String timestampAsHeaderString() {
    	return toHeaderString(new DateTime());
    }

    private String toHeaderString(DateTime date) {
    	return headerDateFormat.print(date.withZone(DateTimeZone.UTC));
    }

    
    private void startClock() {
    	startTime = System.currentTimeMillis();
    }
    
    private void printElapsedClockTime(String testName) {
    	System.out.println(testName + " took " + 
			(System.currentTimeMillis() - startTime) + "ms for "
			+ LOOP_COUNT+ " loops");    	
    }
    
    @Test
    public void testIso8601DateParse() throws ExecutionException, 
    	InterruptedException 
    {
    	DateTime dsDate = dateService.iso8601DateParse(testData[0].iso8601DateString);
        assert testData[0].date.equals(dsDate.toDate());
    }

    @Test
    public void testRfc822DateParse() throws ExecutionException, 
    	InterruptedException 
    {
    	DateTime dsDate = dateService.rfc822DateParse(testData[0].rfc822DateString);
        assert testData[0].date.equals(dsDate.toDate());
    }

    @Test
    public void testIso8601DateFormat() throws ExecutionException, 
    	InterruptedException 
    {
    	String dsString = dateService.iso8601DateFormat(testData[0].date);
        assert testData[0].iso8601DateString.equals(dsString);
    }

    @Test
    public void testRfc822DateFormat() throws ExecutionException, 
    	InterruptedException 
    {    	
    	String dsString = dateService.rfc822DateFormat(testData[0].date);
        assert testData[0].rfc822DateString.equals(dsString);
    }

    @Test
    void testIso8601DateFormatResponseTime() throws ExecutionException,
        InterruptedException {
    for (int i = 0; i < LOOP_COUNT; i++)
    	dateService.iso8601DateFormat();
    }

    @Test
    void testRfc822DateFormatResponseTime() throws ExecutionException,
        InterruptedException {
    for (int i = 0; i < LOOP_COUNT; i++)
    	dateService.rfc822DateFormat();
    }
    
    @Test
    void testFormatIso8601DateInParallel() throws InterruptedException,
        ExecutionException {
    CompletionService<Boolean> completer = new ExecutorCompletionService<Boolean>(
        exec);
    startClock();    
    for (int i = 0; i < LOOP_COUNT; i++) {
    	final TestData myData = testData[i % testData.length];
        completer.submit(new Callable<Boolean>() {
        public Boolean call() throws ExecutionException,
            InterruptedException 
        {
    		String dsString = dateService.iso8601DateFormat(myData.date);
    		/*
    		 *  Comment-in the assert below to test thread safety.
    		 *  Comment it out to test performance
    		 */
    		assert myData.iso8601DateString.equals(dsString);
    		return true;
        }
        });
    }
    for (int i = 0; i < LOOP_COUNT; i++)
        assert completer.take().get();
    printElapsedClockTime("testFormatIso8601DateInParallel");
    }

    @Test
    void testFormatAmazonDateInParallel() throws InterruptedException,
        ExecutionException {
    CompletionService<Boolean> completer = new ExecutorCompletionService<Boolean>(
        exec);
    startClock();    
    for (int i = 0; i < LOOP_COUNT; i++)
        completer.submit(new Callable<Boolean>() {
        public Boolean call() {
            AWSAuthConnection.httpDate();
            return true;
        }
        });
    for (int i = 0; i < LOOP_COUNT; i++)
        assert completer.take().get();
    printElapsedClockTime("testFormatAmazonDateInParallel");
    }

    @Test
    void testFormatJodaDateInParallel() throws InterruptedException,
        ExecutionException {
    CompletionService<Boolean> completer = new ExecutorCompletionService<Boolean>(
        exec);
    startClock();    
    for (int i = 0; i < LOOP_COUNT; i++) {
    	final TestData myData = testData[i % testData.length];
        completer.submit(new Callable<Boolean>() {
        public Boolean call() {
        	String jodaString = toHeaderString(new DateTime(myData.date));
    		assert myData.rfc822DateString.equals(jodaString);
            return true;
        }
        });
    }
    for (int i = 0; i < LOOP_COUNT; i++)
        assert completer.take().get();
    printElapsedClockTime("testFormatJodaDateInParallel");
    }

    @Test
    void testIso8601ParseDateSerialResponseTime() throws ExecutionException,
        InterruptedException {
    for (int i = 0; i < LOOP_COUNT; i++)
    	dateService.iso8601DateParse(testData[0].iso8601DateString);
    }

    @Test
    void testAmazonParseDateSerialResponseTime() {
    for (int i = 0; i < LOOP_COUNT; i++)
        AWSAuthConnection.httpDate();
    }

    @Test
    void testParseIso8601DateInParallel() throws InterruptedException,
        ExecutionException {
    CompletionService<Boolean> completer = new ExecutorCompletionService<Boolean>(
        exec);
    startClock();
    for (int i = 0; i < LOOP_COUNT; i++) {
    	final TestData myData = testData[i % testData.length];
        completer.submit(new Callable<Boolean>() {
        public Boolean call() throws ExecutionException,
            InterruptedException {
        	DateTime dsDate = dateService.iso8601DateParse(myData.iso8601DateString);
            assert myData.date.equals(dsDate.toDate());                	
			return true;
        }
        });
    }
    for (int i = 0; i < LOOP_COUNT; i++)
        assert completer.take().get();
    printElapsedClockTime("testParseIso8601DateInParallel");
    }

    @Test
    void testParseAmazonDateInParallel() throws InterruptedException,
        ExecutionException {
    CompletionService<Boolean> completer = new ExecutorCompletionService<Boolean>(
        exec);
    startClock();
    for (int i = 0; i < LOOP_COUNT; i++)
        completer.submit(new Callable<Boolean>() {
        public Boolean call() {
            AWSAuthConnection.httpDate();
            return true;
        }
        });
    for (int i = 0; i < LOOP_COUNT; i++)
        assert completer.take().get();
    printElapsedClockTime("testParseAmazonDateInParallel");
    }

    @Test
    void testParseJodaDateInParallel() throws InterruptedException,
        ExecutionException {
    CompletionService<Boolean> completer = new ExecutorCompletionService<Boolean>(
        exec);
    startClock();
    for (int i = 0; i < LOOP_COUNT; i++) {
    	final TestData myData = testData[i % testData.length];
        completer.submit(new Callable<Boolean>() {
        public Boolean call() {
        	Date jodaDate = jodaParseIso8601(myData.iso8601DateString).toDate();            
            assert myData.date.equals(jodaDate);
            return true;
        }
        });
    }
    for (int i = 0; i < LOOP_COUNT; i++)
        assert completer.take().get();
    printElapsedClockTime("testParseJodaDateInParallel");
    }

}