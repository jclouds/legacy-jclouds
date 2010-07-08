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
package org.jclouds.rackspace.cloudfiles.binders;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import org.jclouds.blobstore.binders.BindUserMetadataToHeadersWithPrefix;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.Payload;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.ObjectToBlob;
import org.jclouds.rackspace.cloudfiles.domain.CFObject;
import org.jclouds.rackspace.cloudfiles.domain.MutableObjectInfoWithMetadata;
import org.testng.annotations.Test;

import com.google.common.collect.Multimap;

/**
 * Tests behavior of {@code BindCFObjectToPayload}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudfiles.BindCFObjectToPayloadTest")
public class BindCFObjectToPayloadTest {
   @Test
   public void testPassWithMinimumDetailsAndPayload5GB() {

      BindUserMetadataToHeadersWithPrefix mdBinder = createMock(BindUserMetadataToHeadersWithPrefix.class);
      ObjectToBlob object2Blob = createMock(ObjectToBlob.class);
      HttpRequest request = createMock(HttpRequest.class);
      CFObject object = createMock(CFObject.class);
      Payload payload = createMock(Payload.class);
      Blob blob = createMock(Blob.class);
      MutableObjectInfoWithMetadata md = createMock(MutableObjectInfoWithMetadata.class);

      expect(object.getPayload()).andReturn(payload).atLeastOnce();
      expect(payload.getContentLength()).andReturn(5368709120l).atLeastOnce();
      expect(object2Blob.apply(object)).andReturn(blob);
      mdBinder.bindToRequest(request, blob);

      replay(payload);
      replay(mdBinder);
      replay(object2Blob);
      replay(request);
      replay(object);
      replay(blob);
      replay(md);

      BindCFObjectToPayload binder = new BindCFObjectToPayload(object2Blob, mdBinder);

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
   public void testChunkedBind() {

      BindUserMetadataToHeadersWithPrefix mdBinder = createMock(BindUserMetadataToHeadersWithPrefix.class);
      ObjectToBlob object2Blob = createMock(ObjectToBlob.class);
      HttpRequest request = createMock(HttpRequest.class);
      CFObject object = createMock(CFObject.class);
      Payload payload = createMock(Payload.class);
      Blob blob = createMock(Blob.class);
      MutableObjectInfoWithMetadata md = createMock(MutableObjectInfoWithMetadata.class);
      Multimap<String, String> headers = createMock(Multimap.class);

      expect(object.getPayload()).andReturn(payload).atLeastOnce();
      expect(payload.getContentLength()).andReturn(null).atLeastOnce();
      expect(object2Blob.apply(object)).andReturn(blob);
      mdBinder.bindToRequest(request, blob);
      expect(request.getHeaders()).andReturn(headers).atLeastOnce();
      expect(headers.put("Transfer-Encoding", "chunked")).andReturn(true);

      replay(headers);
      replay(payload);
      replay(mdBinder);
      replay(object2Blob);
      replay(request);
      replay(object);
      replay(blob);
      replay(md);

      BindCFObjectToPayload binder = new BindCFObjectToPayload(object2Blob, mdBinder);

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
      CFObject object = createMock(CFObject.class);
      Payload payload = createMock(Payload.class);
      Blob blob = createMock(Blob.class);
      MutableObjectInfoWithMetadata md = createMock(MutableObjectInfoWithMetadata.class);

      expect(object.getPayload()).andReturn(payload).atLeastOnce();
      expect(payload.getContentLength()).andReturn(5368709121l).atLeastOnce();
      expect(object2Blob.apply(object)).andReturn(blob);
      mdBinder.bindToRequest(request, blob);
      expect(object.getInfo()).andReturn(md).atLeastOnce();

      replay(payload);
      replay(mdBinder);
      replay(object2Blob);
      replay(request);
      replay(object);
      replay(blob);
      replay(md);

      BindCFObjectToPayload bindCFObjectToPayload = new BindCFObjectToPayload(object2Blob, mdBinder);

      bindCFObjectToPayload.bindToRequest(request, object);

      verify(payload);
      verify(mdBinder);
      verify(object2Blob);
      verify(request);
      verify(object);
      verify(blob);
      verify(md);

   }
}
