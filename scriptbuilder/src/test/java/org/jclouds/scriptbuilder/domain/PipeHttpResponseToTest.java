/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.scriptbuilder.domain;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class PipeHttpResponseToTest {
   PipeHttpResponseTo bash = new PipeHttpResponseTo(Statements.interpret("bash"), "GET", URI
            .create("https://adriancolehappy.s3.amazonaws.com/java/install"), ImmutableMultimap.of("Host",
            "adriancolehappy.s3.amazonaws.com", "Date", "Sun, 12 Sep 2010 08:25:19 GMT", "Authorization",
            "AWS 0ASHDJAS82:JASHFDA="));

   PipeHttpResponseToBash bash2 = new PipeHttpResponseToBash("GET", URI
            .create("https://adriancolehappy.s3.amazonaws.com/java/install"), ImmutableMultimap.of("Host",
            "adriancolehappy.s3.amazonaws.com", "Date", "Sun, 12 Sep 2010 08:25:19 GMT", "Authorization",
            "AWS 0ASHDJAS82:JASHFDA="));

   public void testPipeHttpResponseToBashUNIX() {
      assertEquals(
               bash.render(OsFamily.UNIX),
               "curl -q -s -S -L --connect-timeout 10 --max-time 600 --retry 20 -X GET -H \"Host: adriancolehappy.s3.amazonaws.com\" -H \"Date: Sun, 12 Sep 2010 08:25:19 GMT\" -H \"Authorization: AWS 0ASHDJAS82:JASHFDA=\" https://adriancolehappy.s3.amazonaws.com/java/install |(bash)\n");
      assertEquals(bash2.render(OsFamily.UNIX), bash.render(OsFamily.UNIX));
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testPipeHttpResponseToBashWINDOWS() {
      bash.render(OsFamily.WINDOWS);
   }

   PipeHttpResponseTo untar = new PipeHttpResponseTo(Statements
            .interpret("{md} {root}stage{fs} &&{cd} {root}stage{fs} &&tar -xpzf -"), "GET", URI
            .create("https://adriancolehappy.s3.amazonaws.com/java/install"), ImmutableMultimap.of("Host",
            "adriancolehappy.s3.amazonaws.com", "Date", "Sun, 12 Sep 2010 08:25:19 GMT", "Authorization",
            "AWS 0ASHDJAS82:JASHFDA="));

   PipeHttpResponseTo untar2 = new PipeHttpResponseToTarxpzfIntoDirectory("GET", URI
            .create("https://adriancolehappy.s3.amazonaws.com/java/install"), ImmutableMultimap.of("Host",
            "adriancolehappy.s3.amazonaws.com", "Date", "Sun, 12 Sep 2010 08:25:19 GMT", "Authorization",
            "AWS 0ASHDJAS82:JASHFDA="), "{root}stage{fs}");

   public void testPipeHttpResponseToUntarUNIX() {
      assertEquals(
               untar.render(OsFamily.UNIX),
               "curl -q -s -S -L --connect-timeout 10 --max-time 600 --retry 20 -X GET -H \"Host: adriancolehappy.s3.amazonaws.com\" -H \"Date: Sun, 12 Sep 2010 08:25:19 GMT\" -H \"Authorization: AWS 0ASHDJAS82:JASHFDA=\" https://adriancolehappy.s3.amazonaws.com/java/install |(mkdir -p /stage/ &&cd /stage/ &&tar -xpzf -)\n");
      assertEquals(untar.render(OsFamily.UNIX), untar2.render(OsFamily.UNIX));

   }

}
