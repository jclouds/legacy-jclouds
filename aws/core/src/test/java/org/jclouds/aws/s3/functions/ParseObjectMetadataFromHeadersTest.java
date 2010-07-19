/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.aws.s3.functions;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.util.Date;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.aws.s3.blobstore.functions.BlobToObjectMetadata;
import org.jclouds.aws.s3.domain.MutableObjectMetadata;
import org.jclouds.aws.s3.domain.ObjectMetadata.StorageClass;
import org.jclouds.aws.s3.domain.internal.MutableObjectMetadataImpl;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.internal.MutableBlobMetadataImpl;
import org.jclouds.blobstore.functions.ParseSystemAndUserMetadataFromHeaders;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.encryption.internal.JCEEncryptionService;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payloads;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
@Test(testName = "s3.ParseObjectMetadataFromHeadersTest")
public class ParseObjectMetadataFromHeadersTest {
   private static final EncryptionService encryptionService = new JCEEncryptionService();

   @Test
   void testNormal() throws Exception {
      HttpResponse http = new HttpResponse(400, "boa", Payloads.newStringPayload(""));
      http.getPayload().setContentLength(1025l);

      http.getHeaders().put(HttpHeaders.CACHE_CONTROL, "cacheControl");
      http.getHeaders().put("Content-Disposition", "contentDisposition");
      http.getHeaders().put(HttpHeaders.CONTENT_ENCODING, "encoding");
      ParseObjectMetadataFromHeaders parser = new ParseObjectMetadataFromHeaders(blobParser(http,
               "\"abc\""), blobToObjectMetadata, encryptionService, "x-amz-meta-");
      MutableObjectMetadata response = parser.apply(http);
      assertEquals(response, expects);
   }

   @Test
   void testAmzEtag() throws Exception {

      HttpResponse http = new HttpResponse(400, "boa", Payloads.newStringPayload(""));
      http.getPayload().setContentLength(1025l);

      http.getHeaders().put(HttpHeaders.CACHE_CONTROL, "cacheControl");
      http.getHeaders().put("Content-Disposition", "contentDisposition");
      http.getHeaders().put(HttpHeaders.CONTENT_ENCODING, "encoding");
      http.getHeaders().put("x-amz-meta-object-eTag", "\"abc\"");
      ParseObjectMetadataFromHeaders parser = new ParseObjectMetadataFromHeaders(blobParser(http,
               null), blobToObjectMetadata, encryptionService, "x-amz-meta-");
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
      md.setContentType("type");
      md.setETag(etag);
      md.setName("key");
      md.setLastModified(now);
      md.setSize(1025l);
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
      expects.setContentDisposition("contentDisposition");
      expects.setContentEncoding("encoding");
      expects.setContentMD5(encryptionService.fromHex("abc"));
      expects.setContentType("type");
      expects.setETag("\"abc\"");
      expects.setKey("key");
      expects.setLastModified(now);
      expects.setOwner(null);
      expects.setSize(1025l);
      expects.setStorageClass(StorageClass.STANDARD);
      expects.setUserMetadata(userMetadata);
   }
}