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
package org.jclouds.rackspace.cloudservers.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.UnknownHostException;

import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.rackspace.cloudservers.domain.Image;
import org.jclouds.rackspace.cloudservers.domain.ImageStatus;
import org.jclouds.util.DateService;
import org.joda.time.DateTime;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseImageFromGsonResponse}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudservers.ParseImageFromGsonResponseTest")
public class ParseImageFromGsonResponseTest {

   Injector i = Guice.createInjector(new ParserModule());
   DateService dateService = new DateService();

   public void testApplyInputStreamDetails() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/test_get_image_details.json");

      ParseImageFromGsonResponse parser = new ParseImageFromGsonResponse(i.getInstance(Gson.class));
      Image response = parser.apply(is);
      assertEquals(response.getId(), 2);
      assertEquals(response.getName(), "CentOS 5.2");
      assertEquals(response.getCreated(), new DateTime("2010-08-10T12:00:00Z"));
      assertEquals(response.getProgress(), new Integer(80));
      assertEquals(response.getServerId(), new Integer(12));
      assertEquals(response.getStatus(), ImageStatus.SAVING);
      assertEquals(response.getUpdated(), new DateTime("2010-10-10T12:00:00Z"));
   }

}
