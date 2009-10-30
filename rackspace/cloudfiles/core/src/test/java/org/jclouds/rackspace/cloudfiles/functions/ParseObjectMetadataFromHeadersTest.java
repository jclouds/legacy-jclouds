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
package org.jclouds.rackspace.cloudfiles.functions;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertNotNull;

import java.net.URI;

import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.rackspace.cloudfiles.domain.MutableObjectInfoWithMetadata;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Jsr330;

/**
 * Tests behavior of {@code ParseContainerListFromJsonResponse}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudfiles.ParseObjectMetadataFromHeadersTest")
public class ParseObjectMetadataFromHeadersTest {
   Injector i = Guice.createInjector(new ParserModule(), new AbstractModule() {

      @Override
      protected void configure() {
         bindConstant().annotatedWith(
                  Jsr330.named(BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX)).to("sdf");
      }

   });

   public void testEtagCaseIssue() {
      ParseObjectMetadataFromHeaders parser = i.getInstance(ParseObjectMetadataFromHeaders.class);
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getEndpoint()).andReturn(URI.create("http://localhost/test")).atLeastOnce();
      replay(request);
      parser.setContext(request);
      HttpResponse response = new HttpResponse();
      response.getHeaders().put("Content-Type", "text/plain");
      response.getHeaders().put("Last-Modified", "Fri, 12 Jun 2007 13:40:18 GMT");
      response.getHeaders().put("Content-Length", "0");

      response.getHeaders().put("Etag", "feb1");
      MutableObjectInfoWithMetadata md = parser.apply(response);
      assertNotNull(md.getHash());
   }
}
