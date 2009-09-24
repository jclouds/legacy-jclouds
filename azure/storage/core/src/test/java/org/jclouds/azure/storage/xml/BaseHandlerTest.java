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
package org.jclouds.azure.storage.xml;

import org.jclouds.azure.storage.xml.config.AzureStorageParserModule;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.util.DateService;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class BaseHandlerTest {

   protected AzureStorageParserFactory parserFactory = null;
   protected DateService dateService = null;

   private Injector injector;

   @BeforeTest
   protected void setUpInjector() {
      injector = Guice.createInjector(new AzureStorageParserModule(), new ParserModule());
      parserFactory = injector.getInstance(AzureStorageParserFactory.class);
      dateService = injector.getInstance(DateService.class);
      assert parserFactory != null;
   }

   @AfterTest
   protected void tearDownInjector() {
      parserFactory = null;
      dateService = null;
      injector = null;
   }

}