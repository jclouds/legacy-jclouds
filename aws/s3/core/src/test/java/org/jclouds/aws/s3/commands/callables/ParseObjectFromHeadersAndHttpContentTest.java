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
package org.jclouds.aws.s3.commands.callables;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.reset;
import static org.testng.Assert.assertEquals;

import org.apache.commons.io.IOUtils;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.domain.S3Object.Metadata;
import org.jclouds.aws.s3.domain.acl.CannedAccessPolicy;
import org.jclouds.aws.s3.reference.S3Headers;
import org.jclouds.aws.util.DateService;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpHeaders;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "s3.ParseObjectFromHeadersAndHttpContentTest")
public class ParseObjectFromHeadersAndHttpContentTest {

   @Test(expectedExceptions = IllegalStateException.class)
   public void testCall() throws HttpException {
      ParseMetadataFromHeaders metadataParser = createMock(ParseMetadataFromHeaders.class);
      ParseObjectFromHeadersAndHttpContent callable = new ParseObjectFromHeadersAndHttpContent(
               metadataParser);
      HttpResponse response = createMock(HttpResponse.class);
      expect(response.getStatusCode()).andReturn(409).atLeastOnce();
      expect(response.getContent()).andReturn(null);
      replay(response);
      callable.setResponse(response);
      callable.call();
   }

   @Test
   public void testParseContentLengthWhenContentRangeSet() throws HttpException {
      ParseMetadataFromHeaders metadataParser = createMock(ParseMetadataFromHeaders.class);
      ParseObjectFromHeadersAndHttpContent callable = new ParseObjectFromHeadersAndHttpContent(
               metadataParser);
      HttpResponse response = createMock(HttpResponse.class);
      metadataParser.setResponse(response);
      Metadata meta = createMock(Metadata.class);
      expect(metadataParser.call()).andReturn(meta);
      expect(meta.getSize()).andReturn(-1l);
      meta.setSize(-1l);
      expect(response.getFirstHeaderOrNull(HttpHeaders.CONTENT_LENGTH)).andReturn("10485760")
               .atLeastOnce();
      expect(response.getFirstHeaderOrNull(HttpHeaders.CONTENT_RANGE)).andReturn(
               "0-10485759/20232760").atLeastOnce();
      meta.setSize(20232760l);
      expect(meta.getSize()).andReturn(20232760l);

      expect(response.getStatusCode()).andReturn(200).atLeastOnce();
      expect(response.getContent()).andReturn(IOUtils.toInputStream("test"));
      replay(response);
      replay(metadataParser);
      replay(meta);

      callable.setResponse(response);
      S3Object object = callable.call();
      assertEquals(object.getContentLength(), 10485760);
      assertEquals(object.getMetadata().getSize(), 20232760);
      assertEquals(object.getContentRange(), "0-10485759/20232760");

   }
   
   @Test
   public void testParseObjectFromHeadersWithCannedAccessPolicy() throws HttpException {
      DateService dateService = new DateService();
      ParseMetadataFromHeaders metadataParser = new ParseMetadataFromHeaders(dateService);
      metadataParser.setKey("object-key");
      ParseObjectFromHeadersAndHttpContent callable = new ParseObjectFromHeadersAndHttpContent(
            metadataParser);

      HttpResponse response = createNiceMock(HttpResponse.class);
      callable.setResponse(response);

      S3Object object;
      
      // Test with no policy
      buildDefaultResponseMock(response);
      expect(response.getFirstHeaderOrNull(S3Headers.CANNED_ACL)).andReturn(null);
      replay(response);            
      object = callable.call();
      assertEquals(object.getMetadata().getCannedAccessPolicy(), null);
      
      // Test setting the "private" policy
      buildDefaultResponseMock(response);
      expect(response.getFirstHeaderOrNull(S3Headers.CANNED_ACL)).andReturn(
            CannedAccessPolicy.PRIVATE.toString());
      replay(response);            
      object = callable.call();
      assertEquals(object.getMetadata().getCannedAccessPolicy(), CannedAccessPolicy.PRIVATE);

      // Test setting the "authenticated read" policy
      buildDefaultResponseMock(response);
      expect(response.getFirstHeaderOrNull(S3Headers.CANNED_ACL)).andReturn(
            CannedAccessPolicy.AUTHENTICATED_READ.toString());
      replay(response);            
      object = callable.call();
      assertEquals(object.getMetadata().getCannedAccessPolicy(), 
            CannedAccessPolicy.AUTHENTICATED_READ);
   }
   
   private void buildDefaultResponseMock(HttpResponse response) {
      reset(response);
      
      // Headers required for the parse classes to work, but we don't care about this data.
      String data = "test data";
      expect(response.getStatusCode()).andReturn(200).atLeastOnce();
      Multimap<String, String> emptyHeaders = HashMultimap.create();
      expect(response.getHeaders()).andReturn(emptyHeaders).atLeastOnce();
      expect(response.getContent()).andReturn(IOUtils.toInputStream(data)).atLeastOnce();
      expect(response.getFirstHeaderOrNull(HttpHeaders.LAST_MODIFIED))
         .andReturn(new DateService().rfc822DateFormat()).atLeastOnce();
      expect(response.getFirstHeaderOrNull(HttpHeaders.CONTENT_TYPE))
         .andReturn("text/plain").atLeastOnce();
      expect(response.getFirstHeaderOrNull(HttpHeaders.CONTENT_LENGTH))
         .andReturn("" + data.length()).atLeastOnce();            
   }

}
