/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.aws.s3.xml;

import org.jclouds.aws.s3.xml.config.S3ParserModule;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "s3.S3ParserFactoryTest")
public class S3ParserFactoryTest {

   Injector injector = null;
   S3ParserFactory parserFactory = null;

   @BeforeTest
   void setUpInjector() {
      injector = Guice.createInjector(new S3ParserModule());
      parserFactory = injector.getInstance(S3ParserFactory.class);
   }

   @AfterTest
   void tearDownInjector() {
      parserFactory = null;
      injector = null;
   }

   @Test
   void testCreateListBucketsParser() {
      assert parserFactory.createListBucketsParser() != null;
   }

   @Test
   void testCreateListBucketParser() {
      assert parserFactory.createListBucketParser() != null;
   }

   @Test
   void testCreateCopyObjectParser() {
      assert parserFactory.createCopyObjectParser() != null;
   }

   @Test
   void testCreateErrorParser() {
      assert parserFactory.createErrorParser() != null;
   }

}