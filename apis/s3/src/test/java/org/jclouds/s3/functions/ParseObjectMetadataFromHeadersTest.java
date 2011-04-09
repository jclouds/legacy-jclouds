/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.s3.functions;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.util.Date;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.s3.blobstore.functions.BlobToObjectMetadata;
import org.jclouds.s3.domain.MutableObjectMetadata;
import org.jclouds.s3.domain.ObjectMetadata.StorageClass;
import org.jclouds.s3.domain.internal.MutableObjectMetadataImpl;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.internal.MutableBlobMetadataImpl;
import org.jclouds.blobstore.functions.ParseSystemAndUserMetadataFromHeaders;
import org.jclouds.crypto.CryptoStreams;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payloads;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;

/**
 * @author Adrian Cole
 */
@Test(testName = "s3.ParseObjectMetadataFromHeadersTest")
public class ParseObjectMetadataFromHeadersTest {

   @Test
   void testNormal() throws Exception {
      HttpResponse http = new HttpResponse(400, "boa", Payloads.newStringPayload(""), ImmutableMultimap.of(
            HttpHeaders.CACHE_CONTROL, "cacheControl"));
      http.getPayload().getContentMetadata().setContentLength(1025l);
      http.getPayload().getContentMetadata().setContentDisposition("contentDisposition");
      http.getPayload().getContentMetadata().setContentEncoding("encoding");

      ParseObjectMetadataFromHeaders parser = new ParseObjectMetadataFromHeaders(blobParser(http, "\"abcd\""),
            blobToObjectMetadata, "x-amz-meta-");
      MutableObjectMetadata response = parser.apply(http);
      assertEquals(response, expects);
   }

   @Test
   void testAmzEtag() throws Exception {

      HttpResponse http = new HttpResponse(400, "boa", Payloads.newStringPayload(""), ImmutableMultimap.of(
            HttpHeaders.CACHE_CONTROL, "cacheControl", "x-amz-meta-object-eTag", "\"abcd\""));

      http.getPayload().getContentMetadata().setContentLength(1025l);
      http.getPayload().getContentMetadata().setContentDisposition("contentDisposition");
      http.getPayload().getContentMetadata().setContentEncoding("encoding");
      ParseObjectMetadataFromHeaders parser = new ParseObjectMetadataFromHeaders(blobParser(http, null),
            blobToObjectMetadata, "x-amz-meta-");
      MutableObjectMetadata response = parser.apply(http);
      assertEquals(response, expects);
   }

   Date now = new Date();
   Map<String, String> userMetadata = ImmutableMap.of("foo", "bar");
   private MutableObjectMetadataImpl expects;
   BlobToObjectMetadata blobToObjectMetadata;

   private ParseSystemAndUserMetadataFromHeaders blobParser(HttpResponse response, String etag) {
      ParseSystemAndUserMetadataFromHeaders parser = createMock(ParseSystemAndUserMetadataFromHeaders.class);
      MutableBlobMetadata md = new MutableBlobMetadataImpl();
      md.getContentMetadata().setContentType("type");
      md.setETag(etag);
      md.setName("key");
      md.setLastModified(now);
      md.getContentMetadata().setContentLength(1025l);
      md.getContentMetadata().setContentDisposition("contentDisposition");
      md.getContentMetadata().setContentEncoding("encoding");
      md.getContentMetadata().setContentMD5(CryptoStreams.hex("abcd"));
      md.setUserMetadata(userMetadata);
      expect(parser.apply(response)).andReturn(md);
      replay(parser);
      return parser;
   }

   @BeforeTest
   void setUp() {
      blobToObjectMetadata = new BlobToObjectMetadata();
      expects = new MutableObjectMetadataImpl();
      expects.setCacheControl("cacheControl");
      expects.getContentMetadata().setContentDisposition("contentDisposition");
      expects.getContentMetadata().setContentEncoding("encoding");
      expects.getContentMetadata().setContentMD5(CryptoStreams.hex("abcd"));
      expects.getContentMetadata().setContentType("type");
      expects.getContentMetadata().setContentLength(1025l);
      expects.setETag("\"abcd\"");
      expects.setKey("key");
      expects.setLastModified(now);
      expects.setOwner(null);
      expects.setStorageClass(StorageClass.STANDARD);
      expects.setUserMetadata(userMetadata);
   }
}
