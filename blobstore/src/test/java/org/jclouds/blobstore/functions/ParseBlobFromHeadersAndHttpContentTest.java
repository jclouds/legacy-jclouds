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
package org.jclouds.blobstore.functions;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Collections;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.blobstore.config.BlobStoreObjectModule;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpResponse;
import org.jclouds.util.Utils;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Guice;

/**
 * @author Adrian Cole
 */
public class ParseBlobFromHeadersAndHttpContentTest {

   @BeforeTest
   void setUp() {

      blobProvider = Guice.createInjector(new BlobStoreObjectModule()).getInstance(
               Blob.Factory.class);
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testCall() throws HttpException {
      ParseSystemAndUserMetadataFromHeaders metadataParser = createMock(ParseSystemAndUserMetadataFromHeaders.class);
      ParseBlobFromHeadersAndHttpContent callable = new ParseBlobFromHeadersAndHttpContent(
               metadataParser, blobProvider);
      HttpResponse response = createMock(HttpResponse.class);
      expect(response.getFirstHeaderOrNull("Content-Length")).andReturn("100");
      expect(response.getFirstHeaderOrNull("Content-Range")).andReturn(null);
      expect(response.getHeaders()).andReturn(ImmutableMultimap.of("Content-Length", "100"));
      expect(response.getContent()).andReturn(null);
      replay(response);
      callable.apply(response);
   }

   @Test
   public void testAddAllHeadersTo() {
      ParseSystemAndUserMetadataFromHeaders metadataParser = createMock(ParseSystemAndUserMetadataFromHeaders.class);
      ParseBlobFromHeadersAndHttpContent callable = new ParseBlobFromHeadersAndHttpContent(
               metadataParser, blobProvider);
      Multimap<String, String> allHeaders = ImmutableMultimap.of("key", "value");
      HttpResponse from = new HttpResponse();
      from.getHeaders().putAll(allHeaders);
      Blob object = blobProvider.create(null);
      callable.addAllHeadersTo(from, object);
      assertEquals(object.getAllHeaders().get("key"), Collections.singletonList("value"));
   }

   private Blob.Factory blobProvider;

   @Test(enabled = false)
   // TODO.. very complicated test.
   public void testParseContentLengthWhenContentRangeSet() throws HttpException {
      ParseSystemAndUserMetadataFromHeaders metadataParser = createMock(ParseSystemAndUserMetadataFromHeaders.class);
      ParseBlobFromHeadersAndHttpContent callable = new ParseBlobFromHeadersAndHttpContent(
               metadataParser, blobProvider);
      HttpResponse response = createMock(HttpResponse.class);
      MutableBlobMetadata meta = createMock(MutableBlobMetadata.class);
      expect(metadataParser.apply(response)).andReturn(meta);
      InputStream test = Utils.toInputStream("test");

      expect(meta.getSize()).andReturn(-1l);
      meta.setSize(-1l);
      expect(response.getFirstHeaderOrNull(HttpHeaders.CONTENT_LENGTH)).andReturn("10485760")
               .atLeastOnce();
      expect(response.getFirstHeaderOrNull("Content-Range")).andReturn("0-10485759/20232760")
               .atLeastOnce();
      expect(response.getHeaders()).andReturn(
               ImmutableMultimap.of("Content-Length", "10485760", "Content-Range",
                        "0-10485759/20232760"));
      meta.setSize(20232760);

      expect(response.getStatusCode()).andReturn(200).atLeastOnce();
      expect(response.getContent()).andReturn(test);
      expect(meta.getSize()).andReturn(20232760l);

      replay(response);
      replay(metadataParser);

      Blob object = callable.apply(response);
      assertEquals(object.getContentLength(), new Long(10485760));
      assertEquals(object.getMetadata().getSize(), new Long(20232760));
      assertEquals(object.getAllHeaders().get("Content-Range"), Collections
               .singletonList("0-10485759/20232760"));

   }

}
