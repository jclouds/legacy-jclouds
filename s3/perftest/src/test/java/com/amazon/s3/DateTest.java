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
package com.amazon.s3;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jclouds.aws.PerformanceTest;
import org.jclouds.aws.s3.DateService;
import org.joda.time.DateTime;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;

@Test(sequential = true, timeOut = 2 * 60 * 1000, testName = "s3.DateTest", groups = "performance")
public class DateTest extends PerformanceTest {
    Injector i = Guice.createInjector();

    DateService utils = i.getInstance(DateService.class);
    SimpleDateFormat dateParser;


    public DateTest() {
        this.dateParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        this.dateParser.setTimeZone(new SimpleTimeZone(0, "GMT"));
    }

    Date amazonDateFromString(String toParse) throws ParseException {
        return this.dateParser.parse(toParse);
    }

    private static String toParse = "2009-03-12T02:00:07.000Z";

    @Test
    public void testParseDateSameAsAmazon() throws ParseException, ExecutionException, InterruptedException {
        Date java = dateParser.parse(toParse);
        DateTime joda = utils.dateTimeFromXMLFormat(toParse);
        assert java.equals(joda.toDate());
    }


    @Test
    public void testTimeStampDateSameAsAmazon() throws ExecutionException, InterruptedException {
        String java = AWSAuthConnection.httpDate();
        String joda = utils.timestampAsHeaderString();
        assert java.equals(joda);
    }

    @Test
    void testTimeStampSerialResponseTime() throws ExecutionException, InterruptedException {
        for (int i = 0; i < LOOP_COUNT; i++)
            utils.timestampAsHeaderString();
    }

    @Test
    void testAmazonTimeStampSerialResponseTime() {
        for (int i = 0; i < LOOP_COUNT; i++)
            AWSAuthConnection.httpDate();
    }

    @Test
    void testTimeStampParallelResponseTime() throws InterruptedException, ExecutionException {
        CompletionService<Boolean> completer = new ExecutorCompletionService<Boolean>(exec);
        for (int i = 0; i < LOOP_COUNT; i++)
            completer.submit(new Callable<Boolean>() {
                public Boolean call() throws ExecutionException, InterruptedException {
                    utils.timestampAsHeaderString();
                    return true;
                }
            });
        for (int i = 0; i < LOOP_COUNT; i++) assert completer.take().get();
    }

    @Test
    void testAmazonTimeStampParallelResponseTime() throws InterruptedException, ExecutionException {
        CompletionService<Boolean> completer = new ExecutorCompletionService<Boolean>(exec);
        for (int i = 0; i < LOOP_COUNT; i++)
            completer.submit(new Callable<Boolean>() {
                public Boolean call() {
                    AWSAuthConnection.httpDate();
                    return true;
                }
            });
        for (int i = 0; i < LOOP_COUNT; i++) assert completer.take().get();
    }


    @Test
    void testParseDateSerialResponseTime() throws ExecutionException, InterruptedException {
        for (int i = 0; i < LOOP_COUNT; i++)
            utils.dateTimeFromXMLFormat(toParse);
    }

    @Test
    void testAmazonParseDateSerialResponseTime() {
        for (int i = 0; i < LOOP_COUNT; i++)
            AWSAuthConnection.httpDate();
    }

    @Test
    void testParseDateParallelResponseTime() throws InterruptedException, ExecutionException {
        CompletionService<Boolean> completer = new ExecutorCompletionService<Boolean>(exec);

        for (int i = 0; i < LOOP_COUNT; i++)
            completer.submit(new Callable<Boolean>() {
                public Boolean call() throws ExecutionException, InterruptedException {
                    utils.dateTimeFromXMLFormat(toParse);
                    return true;
                }
            });
        for (int i = 0; i < LOOP_COUNT; i++) assert completer.take().get();
    }

    @Test
    void testAmazonParseDateParallelResponseTime() throws InterruptedException, ExecutionException {
        CompletionService<Boolean> completer = new ExecutorCompletionService<Boolean>(exec);

        for (int i = 0; i < LOOP_COUNT; i++)
            completer.submit(new Callable<Boolean>() {
                public Boolean call() {
                    AWSAuthConnection.httpDate();
                    return true;
                }
            });
        for (int i = 0; i < LOOP_COUNT; i++) assert completer.take().get();
    }

}