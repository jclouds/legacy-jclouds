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
package org.jclouds.aws.s3.binders;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import org.jclouds.aws.s3.blobstore.functions.ObjectToBlob;
import org.jclouds.aws.s3.domain.MutableObjectMetadata;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.blobstore.binders.BindUserMetadataToHeadersWithPrefix;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.http.HttpRequest;
import org.jclouds.io.Payload;
import org.testng.annotations.Test;

import com.google.common.collect.Multimap;

/**
 * Tests behavior of {@code BindS3ObjectToPayload}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "s3.BindS3ObjectToPayloadTest")
public class BindS3ObjectToPayloadTest {
   @Test
   public void testPassWithMinimumDetailsAndPayload5GB() {

      BindUserMetadataToHeadersWithPrefix mdBinder = createMock(BindUserMetadataToHeadersWithPrefix.class);
      ObjectToBlob object2Blob = createMock(ObjectToBlob.class);
      HttpRequest request = createMock(HttpRequest.class);
      S3Object object = createMock(S3Object.class);
      Payload payload = createMock(Payload.class);
      Blob blob = createMock(Blob.class);
      MutableObjectMetadata md = createMock(MutableObjectMetadata.class);

      expect(object.getPayload()).andReturn(payload).atLeastOnce();
      expect(payload.getContentLength()).andReturn(5368709120l).atLeastOnce();
      expect(object2Blob.apply(object)).andReturn(blob);
      mdBinder.bindToRequest(request, blob);
      expect(object.getMetadata()).andReturn(md).atLeastOnce();
      expect(md.getCacheControl()).andReturn(null).atLeastOnce();
      expect(md.getContentDisposition()).andReturn(null).atLeastOnce();
      expect(md.getContentEncoding()).andReturn(null).atLeastOnce();

      replay(payload);
      replay(mdBinder);
      replay(object2Blob);
      replay(request);
      replay(object);
      replay(blob);
      replay(md);

      BindS3ObjectToPayload binder = new BindS3ObjectToPayload(object2Blob, mdBinder);

      binder.bindToRequest(request, object);

      verify(payload);
      verify(mdBinder);
      verify(object2Blob);
      verify(request);
      verify(object);
      verify(blob);
      verify(md);

   }

   @SuppressWarnings("unchecked")
   @Test
   public void testExtendedPropertiesBind() {

      BindUserMetadataToHeadersWithPrefix mdBinder = createMock(BindUserMetadataToHeadersWithPrefix.class);
      ObjectToBlob object2Blob = createMock(ObjectToBlob.class);
      HttpRequest request = createMock(HttpRequest.class);
      S3Object object = createMock(S3Object.class);
      Payload payload = createMock(Payload.class);
      Blob blob = createMock(Blob.class);
      MutableObjectMetadata md = createMock(MutableObjectMetadata.class);
      Multimap<String, String> headers = createMock(Multimap.class);

      expect(object.getPayload()).andReturn(payload).atLeastOnce();
      expect(payload.getContentLength()).andReturn(5368709120l).atLeastOnce();
      expect(object2Blob.apply(object)).andReturn(blob);
      mdBinder.bindToRequest(request, blob);
      expect(object.getMetadata()).andReturn(md).atLeastOnce();
      expect(request.getHeaders()).andReturn(headers).atLeastOnce();
      expect(md.getCacheControl()).andReturn("no-cache").atLeastOnce();
      expect(headers.put("Cache-Control", "no-cache")).andReturn(true);

      expect(md.getContentDisposition()).andReturn("attachment; filename=\"fname.ext\"")
               .atLeastOnce();
      expect(headers.put("Content-Disposition", "attachment; filename=\"fname.ext\"")).andReturn(true);

      expect(md.getContentEncoding()).andReturn("gzip").atLeastOnce();
      expect(headers.put("Content-Encoding", "gzip")).andReturn(true);

      replay(headers);
      replay(payload);
      replay(mdBinder);
      replay(object2Blob);
      replay(request);
      replay(object);
      replay(blob);
      replay(md);

      BindS3ObjectToPayload binder = new BindS3ObjectToPayload(object2Blob, mdBinder);

      binder.bindToRequest(request, object);

      verify(headers);
      verify(payload);
      verify(mdBinder);
      verify(object2Blob);
      verify(request);
      verify(object);
      verify(blob);
      verify(md);

   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testOver5GBIsBad() {

      BindUserMetadataToHeadersWithPrefix mdBinder = createMock(BindUserMetadataToHeadersWithPrefix.class);
      ObjectToBlob object2Blob = createMock(ObjectToBlob.class);
      HttpRequest request = createMock(HttpRequest.class);
      S3Object object = createMock(S3Object.class);
      Payload payload = createMock(Payload.class);
      Blob blob = createMock(Blob.class);
      MutableObjectMetadata md = createMock(MutableObjectMetadata.class);

      expect(object.getPayload()).andReturn(payload).atLeastOnce();
      expect(payload.getContentLength()).andReturn(5368709121l).atLeastOnce();
      expect(object2Blob.apply(object)).andReturn(blob);
      mdBinder.bindToRequest(request, blob);
      expect(object.getMetadata()).andReturn(md).atLeastOnce();
      expect(md.getCacheControl()).andReturn(null).atLeastOnce();
      expect(md.getContentDisposition()).andReturn(null).atLeastOnce();
      expect(md.getContentEncoding()).andReturn(null).atLeastOnce();

      replay(payload);
      replay(mdBinder);
      replay(object2Blob);
      replay(request);
      replay(object);
      replay(blob);
      replay(md);

      BindS3ObjectToPayload bindS3ObjectToPayload = new BindS3ObjectToPayload(object2Blob, mdBinder);

      bindS3ObjectToPayload.bindToRequest(request, object);

      verify(payload);
      verify(mdBinder);
      verify(object2Blob);
      verify(request);
      verify(object);
      verify(blob);
      verify(md);

   }
}
