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
package org.jclouds.s3.functions;

import static com.google.common.io.BaseEncoding.base16;
import static org.easymock.EasyMock.createMock;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_HEADER_TAG;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;
import static org.testng.Assert.assertEquals;

import java.util.Date;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.RequestSigner;
import org.jclouds.s3.domain.MutableObjectMetadata;
import org.jclouds.s3.domain.ObjectMetadata.StorageClass;
import org.jclouds.s3.domain.internal.MutableObjectMetadataImpl;
import org.jclouds.s3.reference.S3Headers;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.name.Names;

/**
 * @author Adrian Cole
 */
@Test(testName = "s3.ParseObjectMetadataFromHeadersTest")
public class ParseObjectMetadataFromHeadersTest {

   @Test
   void testNormalParsesETagIntoMD5AndMetadataHeaders() throws Exception {
      HttpResponse http = HttpResponse.builder().statusCode(400).message("boa").payload("")
                                      .addHeader(S3Headers.USER_METADATA_PREFIX + "foo", "bar")
                                      .addHeader(HttpHeaders.LAST_MODIFIED, lastModified)
                                      .addHeader(HttpHeaders.ETAG, "\"abcd\"")
                                      .addHeader(HttpHeaders.CACHE_CONTROL, "cacheControl").build();
      http.getPayload().getContentMetadata().setContentLength(1025l);
      http.getPayload().getContentMetadata().setContentDisposition("contentDisposition");
      http.getPayload().getContentMetadata().setContentEncoding("encoding");
      http.getPayload().getContentMetadata().setContentType(MediaType.APPLICATION_OCTET_STREAM);

      MutableObjectMetadata response = parser.apply(http);

      MutableObjectMetadataImpl expects = new MutableObjectMetadataImpl();
      expects.setCacheControl("cacheControl");
      expects.getContentMetadata().setContentDisposition("contentDisposition");
      expects.getContentMetadata().setContentEncoding("encoding");
      expects.getContentMetadata().setContentType(MediaType.APPLICATION_OCTET_STREAM);
      expects.getContentMetadata().setContentLength(1025l);
      expects.getContentMetadata().setContentMD5(base16().lowerCase().decode("abcd"));
      expects.setETag("\"abcd\"");
      expects.setKey("key");
      expects.setLastModified(now);
      expects.setOwner(null);
      expects.setStorageClass(StorageClass.STANDARD);
      expects.setUserMetadata(userMetadata);
      assertEquals(response, expects);
   }

   @Test
   void testMultipartDoesntAttemptToParseETagIntoMD5() throws Exception {
      HttpResponse http = HttpResponse.builder().statusCode(400).message("boa").payload("")
                                      .addHeader(S3Headers.USER_METADATA_PREFIX + "foo", "bar")
                                      .addHeader(HttpHeaders.LAST_MODIFIED, lastModified)
                                      .addHeader(HttpHeaders.ETAG, "\"abcd-1\"")
                                      .addHeader(HttpHeaders.CACHE_CONTROL, "cacheControl").build();
      http.getPayload().getContentMetadata().setContentLength(1025l);
      http.getPayload().getContentMetadata().setContentDisposition("contentDisposition");
      http.getPayload().getContentMetadata().setContentEncoding("encoding");
      http.getPayload().getContentMetadata().setContentType(MediaType.APPLICATION_OCTET_STREAM);

      MutableObjectMetadata response = parser.apply(http);

      MutableObjectMetadataImpl expects = new MutableObjectMetadataImpl();
      expects.setCacheControl("cacheControl");
      expects.getContentMetadata().setContentDisposition("contentDisposition");
      expects.getContentMetadata().setContentEncoding("encoding");
      expects.getContentMetadata().setContentType(MediaType.APPLICATION_OCTET_STREAM);
      expects.getContentMetadata().setContentLength(1025l);
      expects.setETag("\"abcd-1\"");
      expects.setKey("key");
      expects.setLastModified(now);
      expects.setOwner(null);
      expects.setStorageClass(StorageClass.STANDARD);
      expects.setUserMetadata(userMetadata);
      assertEquals(response, expects);
   }

   @Test
   void testAmzEtagStillParsesToMD5AndDoesntMistakeAmzEtagForUserMetadata() throws Exception {
      HttpResponse http = HttpResponse.builder().statusCode(400).message("boa").payload("")
                                      .addHeader(S3Headers.USER_METADATA_PREFIX + "foo", "bar")
                                      .addHeader(HttpHeaders.LAST_MODIFIED, lastModified)
                                      .addHeader(S3Headers.AMZ_ETAG, "\"abcd\"")
                                      .addHeader(HttpHeaders.CACHE_CONTROL, "cacheControl").build();
      
      http.getPayload().getContentMetadata().setContentLength(1025l);
      http.getPayload().getContentMetadata().setContentDisposition("contentDisposition");
      http.getPayload().getContentMetadata().setContentEncoding("encoding");
      http.getPayload().getContentMetadata().setContentType(MediaType.APPLICATION_OCTET_STREAM);

      MutableObjectMetadata response = parser.apply(http);

      MutableObjectMetadataImpl expects = new MutableObjectMetadataImpl();
      expects.setCacheControl("cacheControl");
      expects.getContentMetadata().setContentDisposition("contentDisposition");
      expects.getContentMetadata().setContentEncoding("encoding");
      expects.getContentMetadata().setContentMD5(base16().lowerCase().decode("abcd"));
      expects.getContentMetadata().setContentType(MediaType.APPLICATION_OCTET_STREAM);
      expects.getContentMetadata().setContentLength(1025l);
      expects.setETag("\"abcd\"");
      expects.setKey("key");
      expects.setLastModified(now);
      expects.setOwner(null);
      expects.setStorageClass(StorageClass.STANDARD);
      expects.setUserMetadata(userMetadata);

      assertEquals(response, expects);
   }

   String lastModified = new SimpleDateFormatDateService().rfc822DateFormat(new Date());
   // rfc isn't accurate down to nanos, so we'll parse back to ensure tests pass
   Date now = new SimpleDateFormatDateService().rfc822DateParse(lastModified);

   Map<String, String> userMetadata = ImmutableMap.of("foo", "bar");
   ParseObjectMetadataFromHeaders parser;

   @BeforeTest
   void setUp() {
      parser = Guice.createInjector(new AbstractModule() {

         @Override
         protected void configure() {
            bind(RequestSigner.class).toInstance(createMock(RequestSigner.class));
            bindConstant().annotatedWith(Names.named(PROPERTY_HEADER_TAG)).to(S3Headers.DEFAULT_AMAZON_HEADERTAG);
            bindConstant().annotatedWith(Names.named(PROPERTY_USER_METADATA_PREFIX)).to(S3Headers.USER_METADATA_PREFIX);

         }

      }).getInstance(ParseObjectMetadataFromHeaders.class).setKey("key");
   }
}
