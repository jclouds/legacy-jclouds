/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.mezeo.pcs2.binders;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.http.HttpRequest;
import org.jclouds.mezeo.pcs2.config.PCSObjectModule;
import org.jclouds.mezeo.pcs2.domain.PCSFile;
import org.testng.annotations.Test;

import com.google.inject.Guice;

/**
 * Tests behavior of {@code BindFileInfoToXmlPayload}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "pcs2.BindFileInfoToXmlPayloadTest")
public class BindFileInfoToXmlPayloadTest {
   PCSFile.Factory factory = Guice.createInjector(new PCSObjectModule()).getInstance(
            PCSFile.Factory.class);

   public void test() {
      BindFileInfoToXmlPayload binder = new BindFileInfoToXmlPayload();
      HttpRequest request = new HttpRequest("GET", URI.create("http://localhost"));
      PCSFile file = factory.create(null);
      file.getMetadata().setName("foo");
      binder.bindToRequest(request, file);
      assertEquals(
               request.getPayload().getRawContent(),
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
      BindFileInfoToXmlPayload binder = new BindFileInfoToXmlPayload();
      HttpRequest request = new HttpRequest("GET", URI.create("http://localhost"));

      PCSFile file = factory.create(null);
      file.getMetadata().setName("subdir/foo");
      binder.bindToRequest(request, file);
      assertEquals(
               request.getPayload().getRawContent(),
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
