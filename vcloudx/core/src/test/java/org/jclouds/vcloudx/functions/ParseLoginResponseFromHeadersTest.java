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
package org.jclouds.vcloudx.functions;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.vcloudx.VCloudXLogin.VCloudXSession;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
/**
 * @author Adrian Cole
 */
public class ParseLoginResponseFromHeadersTest extends BaseHandlerTest {

   private ParseLoginResponseFromHeaders parser;

   @BeforeTest
   void setUp() {
      parser = injector.getInstance(ParseLoginResponseFromHeaders.class);
   }

   @Test
   public void testApply() {
      HttpResponse response = new HttpResponse();
      response.setMessage("OK");
      response.setStatusCode(200);
      response.setContent(getClass().getResourceAsStream("/orglist.xml"));
      response.getHeaders().put(HttpHeaders.SET_COOKIE,
               "vcloud-token=9er4d061-4bff-48fa-84b1-5da7166764d2; path=/");
      response.getHeaders().put(HttpHeaders.CONTENT_LENGTH, "307");
      response.getHeaders().put(HttpHeaders.CONTENT_TYPE,
               "Content-Type: application/xml; charset=utf-8");
      VCloudXSession reply = parser.apply(response);
      assertEquals(reply.getVCloudToken(), "9er4d061-4bff-48fa-84b1-5da7166764d2");
      assertEquals(reply.getOrg(), URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/org/48"));

   }

}
