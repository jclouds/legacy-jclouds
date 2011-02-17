/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.blobstore.functions;

import static org.testng.Assert.assertEquals;

import javax.inject.Provider;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.internal.MutableBlobMetadataImpl;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payloads;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * @author Adrian Cole
 */
@Test(sequential = true)
public class ParseSystemAndUserMetadataFromHeadersTest {

   private ParseSystemAndUserMetadataFromHeaders parser;
   private Provider<MutableBlobMetadata> blobMetadataProvider = new Provider<MutableBlobMetadata>() {

      public MutableBlobMetadata get() {
         return new MutableBlobMetadataImpl();
      }

   };

   @BeforeTest
   void setUp() {
      parser = new ParseSystemAndUserMetadataFromHeaders(blobMetadataProvider, new SimpleDateFormatDateService(),
               "prefix");
      parser.setName("key");
   }

   @Test
   public void testApplySetsName() {
      HttpResponse from = new HttpResponse(200, "ok", Payloads.newStringPayload(""), ImmutableMultimap.of(
               HttpHeaders.LAST_MODIFIED, "Wed, 09 Sep 2009 19:50:23 GMT"));
      from.getPayload().getContentMetadata().setContentType(MediaType.APPLICATION_JSON);
      from.getPayload().getContentMetadata().setContentLength(100l);
      BlobMetadata metadata = parser.apply(from);
      assertEquals(metadata.getName(), "key");
   }

   @Test
   public void testNoContentOn204IsOk() {
      HttpResponse from = new HttpResponse(204, "ok", Payloads.newStringPayload(""), ImmutableMultimap.of(
               HttpHeaders.LAST_MODIFIED, "Wed, 09 Sep 2009 19:50:23 GMT"));
      parser.apply(from);
   }

   @Test
   public void testSetLastModified() {
      HttpResponse from = new HttpResponse(200, "ok", Payloads.newStringPayload(""), ImmutableMultimap.of(
               HttpHeaders.LAST_MODIFIED, "Wed, 09 Sep 2009 19:50:23 GMT"));
      MutableBlobMetadata metadata = blobMetadataProvider.get();
      parser.parseLastModifiedOrThrowException(from, metadata);
      assertEquals(metadata.getLastModified(), new SimpleDateFormatDateService()
               .rfc822DateParse("Wed, 09 Sep 2009 19:50:23 GMT"));
   }


   @Test
   public void testSetLastModifiedIso8601() {
      HttpResponse from = new HttpResponse(200, "ok", Payloads.newStringPayload(""), ImmutableMultimap.of(
               HttpHeaders.LAST_MODIFIED, "2011-01-28T17:35:08.000+0000"));
      MutableBlobMetadata metadata = blobMetadataProvider.get();
      parser.parseLastModifiedOrThrowException(from, metadata);
      assertEquals(metadata.getLastModified(), new SimpleDateFormatDateService()
               .iso8601DateParse("2011-01-28T17:35:08.000Z"));
   }

   
   @Test(expectedExceptions = HttpException.class)
   public void testSetLastModifiedException() {
      HttpResponse from = new HttpResponse(200, "ok", Payloads.newStringPayload(""));
      MutableBlobMetadata metadata = blobMetadataProvider.get();
      parser.parseLastModifiedOrThrowException(from, metadata);
   }

   @Test
   public void testAddETagTo() {
      HttpResponse from = new HttpResponse(200, "ok", Payloads.newStringPayload(""), ImmutableMultimap.of(
               HttpHeaders.ETAG, "0xfeb"));
      MutableBlobMetadata metadata = blobMetadataProvider.get();
      parser.addETagTo(from, metadata);
      assertEquals(metadata.getETag(), "0xfeb");
   }

   @Test
   public void testAddUserMetadataTo() {
      HttpResponse from = new HttpResponse(200, "ok", Payloads.newStringPayload(""), ImmutableMultimap.of("prefix"
               + "key", "value"));
      MutableBlobMetadata metadata = blobMetadataProvider.get();
      parser.addUserMetadataTo(from, metadata);
      assertEquals(metadata.getUserMetadata().get("key"), "value");
   }
}
