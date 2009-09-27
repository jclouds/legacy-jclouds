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
package org.jclouds.blobstore.functions;

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Collections;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.functions.ParseContentTypeFromHeaders.BlobMetadataFactory;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.util.DateService;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

public class ParseBlobMetadataFromHeadersTest {

   private ParseSystemAndUserMetadataFromHeaders<BlobMetadata> parser;

   @BeforeTest
   void setUp() {
      parser = new ParseSystemAndUserMetadataFromHeaders<BlobMetadata>(new DateService(), "prefix",
               new BlobMetadataFactory<BlobMetadata>() {
                  public BlobMetadata create(String key) {
                     return new BlobMetadata(key);
                  }
               });
      parser
               .setContext(new HttpRequest("GET", URI.create("http://localhost/key")),
                        new Object[] {});
   }

   @Test
   public void testAddAllHeadersTo() {
      Multimap<String, String> allHeaders = ImmutableMultimap.of("key", "value");
      HttpResponse from = new HttpResponse();
      from.setHeaders(allHeaders);
      BlobMetadata metadata = new BlobMetadata("test");
      parser.addAllHeadersTo(from, metadata);
      assertEquals(metadata.getAllHeaders().get("key"), Collections.singletonList("value"));
   }

   @Test
   public void testApplySetsKey() {
      HttpResponse from = new HttpResponse();
      from.getHeaders().put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
      from.getHeaders().put(HttpHeaders.LAST_MODIFIED, "Wed, 09 Sep 2009 19:50:23 GMT");
      from.getHeaders().put(HttpHeaders.CONTENT_LENGTH, "100");
      BlobMetadata metadata = parser.apply(from);
      assertEquals(metadata.getKey(), "key");
   }

   @Test
   public void testSetContentLength() {
      HttpResponse from = new HttpResponse();
      from.getHeaders().put(HttpHeaders.CONTENT_LENGTH, "100");
      BlobMetadata metadata = new BlobMetadata("test");
      parser.setContentLengthOrThrowException(from, metadata);
      assertEquals(metadata.getSize(), 100);
   }

   @Test(expectedExceptions = HttpException.class)
   public void testSetContentLengthException() {
      HttpResponse from = new HttpResponse();
      BlobMetadata metadata = new BlobMetadata("test");
      parser.setContentLengthOrThrowException(from, metadata);
   }

   @Test
   public void testSetContentType() {
      HttpResponse from = new HttpResponse();
      from.getHeaders().put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
      BlobMetadata metadata = new BlobMetadata("test");
      parser.setContentTypeOrThrowException(from, metadata);
      assertEquals(metadata.getContentType(), MediaType.APPLICATION_JSON);
   }

   @Test(expectedExceptions = HttpException.class)
   public void testSetContentTypeException() {
      HttpResponse from = new HttpResponse();
      BlobMetadata metadata = new BlobMetadata("test");
      parser.setContentTypeOrThrowException(from, metadata);
   }

   @Test
   public void testSetLastModified() {
      HttpResponse from = new HttpResponse();
      from.getHeaders().put(HttpHeaders.LAST_MODIFIED, "Wed, 09 Sep 2009 19:50:23 GMT");
      BlobMetadata metadata = new BlobMetadata("test");
      parser.parseLastModifiedOrThrowException(from, metadata);
      assertEquals(metadata.getLastModified(), new DateService()
               .rfc822DateParse("Wed, 09 Sep 2009 19:50:23 GMT"));
   }

   @Test(expectedExceptions = HttpException.class)
   public void testSetLastModifiedException() {
      HttpResponse from = new HttpResponse();
      BlobMetadata metadata = new BlobMetadata("test");
      parser.parseLastModifiedOrThrowException(from, metadata);
   }

   @Test
   public void testAddETagTo() {
      HttpResponse from = new HttpResponse();
      from.getHeaders().put(HttpHeaders.ETAG, "0xfeb");
      BlobMetadata metadata = new BlobMetadata("test");
      parser.addETagTo(from, metadata);
      assertEquals(metadata.getETag(), HttpUtils.fromHexString("0xfeb"));
   }

   @Test
   public void testAddUserMetadataTo() {
      Multimap<String, String> allHeaders = ImmutableMultimap.of("prefix" + "key", "value");
      HttpResponse from = new HttpResponse();
      from.setHeaders(allHeaders);
      BlobMetadata metadata = new BlobMetadata("test");
      parser.addUserMetadataTo(from, metadata);
      assertEquals(metadata.getUserMetadata().get("key"), Collections.singletonList("value"));
   }
}
