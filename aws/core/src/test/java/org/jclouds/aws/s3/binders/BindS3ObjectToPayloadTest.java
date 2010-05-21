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
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import org.jclouds.aws.s3.blobstore.functions.ObjectToBlob;
import org.jclouds.aws.s3.domain.MutableObjectMetadata;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.blobstore.binders.BindBlobToPayloadAndUserMetadataToHeadersWithPrefix;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code BindS3ObjectToPayload}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "s3.BindS3ObjectToPayloadTest")
public class BindS3ObjectToPayloadTest {
   @Test
   public void test5GBIsOk() {

      BindBlobToPayloadAndUserMetadataToHeadersWithPrefix blobBinder = createMock(BindBlobToPayloadAndUserMetadataToHeadersWithPrefix.class);
      ObjectToBlob object2Blob = createMock(ObjectToBlob.class);
      HttpRequest request = createMock(HttpRequest.class);
      S3Object s3Object = createMock(S3Object.class);
      Blob blob = createMock(Blob.class);
      MutableObjectMetadata md = createNiceMock(MutableObjectMetadata.class);

      expect(s3Object.getContentLength()).andReturn(5368709120l).atLeastOnce();
      expect(object2Blob.apply(s3Object)).andReturn(blob);
      blobBinder.bindToRequest(request, blob);
      expect(s3Object.getMetadata()).andReturn(md).atLeastOnce();

      replay(blobBinder);
      replay(object2Blob);
      replay(request);
      replay(s3Object);
      replay(blob);
      replay(md);

      BindS3ObjectToPayload bindS3ObjectToPayload = new BindS3ObjectToPayload(object2Blob,
               blobBinder);

      bindS3ObjectToPayload.bindToRequest(request, s3Object);

      verify(blobBinder);
      verify(object2Blob);
      verify(request);
      verify(s3Object);
      verify(blob);
      verify(md);

   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testOver5GBIsBad() {

      BindBlobToPayloadAndUserMetadataToHeadersWithPrefix blobBinder = createMock(BindBlobToPayloadAndUserMetadataToHeadersWithPrefix.class);
      ObjectToBlob object2Blob = createMock(ObjectToBlob.class);
      HttpRequest request = createMock(HttpRequest.class);
      S3Object s3Object = createMock(S3Object.class);
      Blob blob = createMock(Blob.class);
      MutableObjectMetadata md = createNiceMock(MutableObjectMetadata.class);

      expect(s3Object.getContentLength()).andReturn(5368709121l).atLeastOnce();
      expect(object2Blob.apply(s3Object)).andReturn(blob);
      blobBinder.bindToRequest(request, blob);
      expect(s3Object.getMetadata()).andReturn(md).atLeastOnce();

      replay(blobBinder);
      replay(object2Blob);
      replay(request);
      replay(s3Object);
      replay(blob);
      replay(md);

      BindS3ObjectToPayload bindS3ObjectToPayload = new BindS3ObjectToPayload(object2Blob,
               blobBinder);

      bindS3ObjectToPayload.bindToRequest(request, s3Object);

      verify(blobBinder);
      verify(object2Blob);
      verify(request);
      verify(s3Object);
      verify(blob);
      verify(md);

   }
}
