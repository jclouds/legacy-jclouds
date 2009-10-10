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
package org.jclouds.mezeo.pcs2.decorators;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.http.HttpRequest;
import org.jclouds.mezeo.pcs2.domain.PCSFile;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code AddFileInfoAsXmlEntity}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "pcs2.AddFileInfoAsXmlEntityTest")
public class AddFileInfoAsXmlEntityTest {

   public void test() {
      AddFileInfoAsXmlEntity binder = new AddFileInfoAsXmlEntity();
      HttpRequest request = new HttpRequest("GET", URI.create("http://localhost"));
      PCSFile file = new PCSFile("foo");
      binder.decorateRequest(request, file);
      assertEquals(
               request.getEntity(),
               "<file><name>foo</name><mime_type>application/octet-stream</mime_type><public>false</public></file>");
      assertEquals(
               request.getFirstHeaderOrNull(HttpHeaders.CONTENT_LENGTH),
               "<file><name>foo</name><mime_type>application/octet-stream</mime_type><public>false</public></file>"
                        .getBytes().length
                        + "");
      assertEquals(request.getFirstHeaderOrNull(HttpHeaders.CONTENT_TYPE),
               "application/vnd.csp.file-info+xml");

   }

   public void testCompound() {
      AddFileInfoAsXmlEntity binder = new AddFileInfoAsXmlEntity();
      HttpRequest request = new HttpRequest("GET", URI.create("http://localhost"));
      PCSFile file = new PCSFile("subdir/foo");
      binder.decorateRequest(request, file);
      assertEquals(
               request.getEntity(),
               "<file><name>foo</name><mime_type>application/octet-stream</mime_type><public>false</public></file>");
      assertEquals(
               request.getFirstHeaderOrNull(HttpHeaders.CONTENT_LENGTH),
               "<file><name>foo</name><mime_type>application/octet-stream</mime_type><public>false</public></file>"
                        .getBytes().length
                        + "");
      assertEquals(request.getFirstHeaderOrNull(HttpHeaders.CONTENT_TYPE),
               "application/vnd.csp.file-info+xml");

   }
}
