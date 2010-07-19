package org.jclouds.azure.storage.blob.binders;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import org.jclouds.azure.storage.blob.blobstore.functions.AzureBlobToBlob;
import org.jclouds.azure.storage.blob.domain.AzureBlob;
import org.jclouds.azure.storage.blob.domain.BlobType;
import org.jclouds.azure.storage.blob.domain.MutableBlobProperties;
import org.jclouds.blobstore.binders.BindUserMetadataToHeadersWithPrefix;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.http.HttpRequest;
import org.jclouds.io.Payload;
import org.testng.annotations.Test;

import com.google.common.collect.Multimap;

/**
 * Tests behavior of {@code BindAzureBlobToPayload}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "azure.BindAzureBlobToPayloadTest")
public class BindAzureBlobToPayloadTest {
   @SuppressWarnings("unchecked")
   @Test
   public void testPassBlockWithMinimumDetailsAndPayload64MB() {

      BindUserMetadataToHeadersWithPrefix mdBinder = createMock(BindUserMetadataToHeadersWithPrefix.class);
      AzureBlobToBlob object2Blob = createMock(AzureBlobToBlob.class);
      HttpRequest request = createMock(HttpRequest.class);
      AzureBlob object = createMock(AzureBlob.class);
      Payload payload = createMock(Payload.class);
      Blob blob = createMock(Blob.class);
      MutableBlobProperties md = createMock(MutableBlobProperties.class);
      Multimap<String, String> headers = createMock(Multimap.class);

      expect(object.getPayload()).andReturn(payload).atLeastOnce();
      expect(payload.getContentLength()).andReturn(1024l).atLeastOnce();
      expect(object2Blob.apply(object)).andReturn(blob);
      mdBinder.bindToRequest(request, blob);
      expect(object.getProperties()).andReturn(md).atLeastOnce();
      expect(md.getType()).andReturn(BlobType.BLOCK_BLOB).atLeastOnce();
      expect(request.getHeaders()).andReturn(headers).atLeastOnce();
      expect(headers.put("x-ms-blob-type", "BlockBlob")).andReturn(true);

      expect(md.getContentLanguage()).andReturn(null).atLeastOnce();
      expect(md.getContentEncoding()).andReturn(null).atLeastOnce();

      replay(headers);
      replay(payload);
      replay(mdBinder);
      replay(object2Blob);
      replay(request);
      replay(object);
      replay(blob);
      replay(md);

      BindAzureBlobToPayload binder = new BindAzureBlobToPayload(object2Blob, mdBinder);

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

   @SuppressWarnings("unchecked")
   @Test
   public void testBlockExtendedPropertiesBind() {

      BindUserMetadataToHeadersWithPrefix mdBinder = createMock(BindUserMetadataToHeadersWithPrefix.class);
      AzureBlobToBlob object2Blob = createMock(AzureBlobToBlob.class);
      HttpRequest request = createMock(HttpRequest.class);
      AzureBlob object = createMock(AzureBlob.class);
      Payload payload = createMock(Payload.class);
      Blob blob = createMock(Blob.class);
      MutableBlobProperties md = createMock(MutableBlobProperties.class);
      Multimap<String, String> headers = createMock(Multimap.class);

      expect(object.getPayload()).andReturn(payload).atLeastOnce();
      expect(payload.getContentLength()).andReturn(1024l).atLeastOnce();
      expect(object2Blob.apply(object)).andReturn(blob);
      mdBinder.bindToRequest(request, blob);
      expect(object.getProperties()).andReturn(md).atLeastOnce();
      expect(md.getType()).andReturn(BlobType.BLOCK_BLOB).atLeastOnce();
      expect(request.getHeaders()).andReturn(headers).atLeastOnce();
      expect(headers.put("x-ms-blob-type", "BlockBlob")).andReturn(true);

      expect(md.getContentLanguage()).andReturn("en").atLeastOnce();
      expect(headers.put("Content-Language", "en")).andReturn(true);

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

      BindAzureBlobToPayload binder = new BindAzureBlobToPayload(object2Blob, mdBinder);

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

   @SuppressWarnings("unchecked")
   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testBlockOver64MBIsBad() {

      BindUserMetadataToHeadersWithPrefix mdBinder = createMock(BindUserMetadataToHeadersWithPrefix.class);
      AzureBlobToBlob object2Blob = createMock(AzureBlobToBlob.class);
      HttpRequest request = createMock(HttpRequest.class);
      AzureBlob object = createMock(AzureBlob.class);
      Payload payload = createMock(Payload.class);
      Blob blob = createMock(Blob.class);
      MutableBlobProperties md = createMock(MutableBlobProperties.class);
      Multimap<String, String> headers = createMock(Multimap.class);

      expect(object.getPayload()).andReturn(payload).atLeastOnce();
      expect(payload.getContentLength()).andReturn(5368709121l).atLeastOnce();
      expect(object2Blob.apply(object)).andReturn(blob);
      mdBinder.bindToRequest(request, blob);
      expect(object.getProperties()).andReturn(md).atLeastOnce();
      expect(md.getType()).andReturn(BlobType.BLOCK_BLOB).atLeastOnce();
      expect(request.getHeaders()).andReturn(headers).atLeastOnce();
      expect(headers.put("x-ms-blob-type", "BlockBlob")).andReturn(true);

      expect(md.getContentLanguage()).andReturn(null).atLeastOnce();
      expect(md.getContentEncoding()).andReturn(null).atLeastOnce();

      replay(headers);
      replay(payload);
      replay(mdBinder);
      replay(object2Blob);
      replay(request);
      replay(object);
      replay(blob);
      replay(md);

      BindAzureBlobToPayload bindAzureBlobToPayload = new BindAzureBlobToPayload(object2Blob,
               mdBinder);

      bindAzureBlobToPayload.bindToRequest(request, object);

      verify(headers);
      verify(payload);
      verify(mdBinder);
      verify(object2Blob);
      verify(request);
      verify(object);
      verify(blob);
      verify(md);

   }
}
