/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.aws.s3.blobstore.strategy.internal;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.fail;

import java.util.SortedMap;
import java.util.concurrent.TimeoutException;

import org.jclouds.aws.s3.AWSS3ApiMetadata;
import org.jclouds.aws.s3.AWSS3AsyncClient;
import org.jclouds.aws.s3.AWSS3Client;
import org.jclouds.aws.s3.blobstore.AWSS3BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.io.MutableContentMetadata;
import org.jclouds.io.Payload;
import org.jclouds.io.PayloadSlicer;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;
import org.jclouds.s3.domain.ObjectMetadata;
import org.jclouds.s3.domain.ObjectMetadataBuilder;
import org.jclouds.util.Throwables2;
import org.testng.annotations.Test;

import com.google.common.collect.Maps;

/**
 * Tests behavior of {@code SequentialMultipartUploadStrategy}
 * 
 * @author Tibor Kiss
 */
@Test(groups = "unit")
public class SequentialMultipartUploadStrategyTest {
   
   @SuppressWarnings("unchecked")
   @Test
   public void testWithTwoParts() {
      AWSS3BlobStore ablobStore = createMock(AWSS3BlobStore.class);      
      PayloadSlicer slicer = createMock(PayloadSlicer.class);
      String container = "container";
      String key = "mpu-test";
      Blob blob = createMock(Blob.class);
      MutableBlobMetadata blobMeta = createMock(MutableBlobMetadata.class);
      Payload payload = createMock(Payload.class);
      MutableContentMetadata contentMeta = createMock(MutableContentMetadata.class);
      BlobStoreContext context = createMock(BlobStoreContext.class);
      RestContext<AWSS3Client, AWSS3AsyncClient> psc = createMock(RestContextImpl.class);
      AWSS3Client client = createMock(AWSS3Client.class);
      ObjectMetadata ometa = createMock(ObjectMetadata.class);
      String uploadId = "uploadId";
      long chunkSize = MultipartUploadSlicingAlgorithm.DEFAULT_PART_SIZE;
      long remaining = 100L;
      SortedMap<Integer, String> etags = Maps.newTreeMap();
      etags.put(Integer.valueOf(1), "eTag1");
      etags.put(Integer.valueOf(2), "eTag2");
      
      expect(blob.getMetadata()).andReturn(blobMeta).atLeastOnce();
      expect(blobMeta.getName()).andReturn(key).atLeastOnce();
      expect(blob.getPayload()).andReturn(payload).atLeastOnce();
      expect(payload.getContentMetadata()).andReturn(contentMeta).atLeastOnce();
      expect(contentMeta.getContentLength()).andReturn(Long.valueOf(chunkSize + remaining));
      expect(ablobStore.getContext()).andReturn(context).atLeastOnce();
      expect(context.unwrap(AWSS3ApiMetadata.CONTEXT_TOKEN)).andReturn(psc).atLeastOnce();
      expect(psc.getApi()).andReturn(client).atLeastOnce();
      expect(client.initiateMultipartUpload(container, new ObjectMetadataBuilder().key(key).build())).andReturn("uploadId").atLeastOnce();
      expect(slicer.slice(payload, 0, chunkSize)).andReturn(payload).atLeastOnce();
      expect(client.uploadPart(container, key, 1, uploadId, payload)).andReturn("eTag1").atLeastOnce();
      expect(slicer.slice(payload, chunkSize, remaining)).andReturn(payload).atLeastOnce();
      expect(client.uploadPart(container, key, 2, uploadId, payload)).andReturn("eTag2").atLeastOnce();      
      expect(client.completeMultipartUpload(container, key, uploadId, etags)).andReturn("eTag").atLeastOnce();

      replay(ablobStore);
      replay(slicer);
      replay(blob);
      replay(blobMeta);
      replay(payload);
      replay(contentMeta);
      replay(context);
      replay(psc);
      replay(client);
      replay(ometa);

      SequentialMultipartUploadStrategy strategy = new SequentialMultipartUploadStrategy(ablobStore, slicer);
      strategy.execute(container, blob, PutOptions.NONE);

      verify(ablobStore);
      verify(slicer);
      verify(blob);
      verify(blobMeta);
      verify(payload);
      verify(contentMeta);
      verify(context);
      verify(psc);
      verify(client);
      verify(ometa);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testWithTimeout() {
      AWSS3BlobStore ablobStore = createMock(AWSS3BlobStore.class);      
      PayloadSlicer slicer = createMock(PayloadSlicer.class);
      String container = "container";
      String key = "mpu-test";
      Blob blob = createMock(Blob.class);
      MutableBlobMetadata blobMeta = createMock(MutableBlobMetadata.class);
      Payload payload = createMock(Payload.class);
      MutableContentMetadata contentMeta = createMock(MutableContentMetadata.class);
      BlobStoreContext context = createMock(BlobStoreContext.class);
      RestContext<AWSS3Client, AWSS3AsyncClient> psc = createMock(RestContextImpl.class);
      AWSS3Client client = createMock(AWSS3Client.class);
      ObjectMetadata ometa = createMock(ObjectMetadata.class);
      String uploadId = "uploadId";
      long chunkSize = MultipartUploadSlicingAlgorithm.DEFAULT_PART_SIZE;
      long remaining = 100L;
      SortedMap<Integer, String> etags = Maps.newTreeMap();
      etags.put(Integer.valueOf(1), "eTag1");
      etags.put(Integer.valueOf(2), "eTag2");
      
      expect(blob.getMetadata()).andReturn(blobMeta).atLeastOnce();
      expect(blobMeta.getName()).andReturn(key).atLeastOnce();
      expect(blob.getPayload()).andReturn(payload).atLeastOnce();
      expect(payload.getContentMetadata()).andReturn(contentMeta).atLeastOnce();
      expect(contentMeta.getContentLength()).andReturn(Long.valueOf(chunkSize + remaining));
      expect(ablobStore.getContext()).andReturn(context).atLeastOnce();
      expect(context.unwrap(AWSS3ApiMetadata.CONTEXT_TOKEN)).andReturn(psc).atLeastOnce();
      expect(psc.getApi()).andReturn(client).atLeastOnce();
      expect(client.initiateMultipartUpload(container, new ObjectMetadataBuilder().key(key).build())).andReturn("uploadId").atLeastOnce();
      expect(slicer.slice(payload, 0, chunkSize)).andReturn(payload).atLeastOnce();
      expect(client.uploadPart(container, key, 1, uploadId, payload)).andReturn("eTag1").atLeastOnce();
      expect(slicer.slice(payload, chunkSize, remaining)).andReturn(payload).atLeastOnce();
      expect(client.uploadPart(container, key, 2, uploadId, payload)).andThrow(new RuntimeException(new TimeoutException()));
      client.abortMultipartUpload(container, key, uploadId);
      expectLastCall().atLeastOnce();

      replay(ablobStore);
      replay(slicer);
      replay(blob);
      replay(blobMeta);
      replay(payload);
      replay(contentMeta);
      replay(context);
      replay(psc);
      replay(client);
      replay(ometa);

      SequentialMultipartUploadStrategy strategy = new SequentialMultipartUploadStrategy(ablobStore, slicer);
      try {
         strategy.execute(container, blob, PutOptions.NONE);
         fail("Should throw RuntimeException with TimeoutException cause!");
      } catch (RuntimeException rtex) {
         TimeoutException timeout = Throwables2.getFirstThrowableOfType(rtex, TimeoutException.class);
         if (timeout == null) {
            throw rtex;
         }
      }

      verify(ablobStore);
      verify(slicer);
      verify(blob);
      verify(blobMeta);
      verify(payload);
      verify(contentMeta);
      verify(context);
      verify(psc);
      verify(client);
      verify(ometa);
   }
}
