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

package org.jclouds.azureblob.blobstore.strategy;

import org.jclouds.azureblob.AzureBlobClient;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.internal.BlobImpl;
import org.jclouds.blobstore.domain.internal.MutableBlobMetadataImpl;
import org.jclouds.io.MutableContentMetadata;
import org.jclouds.io.Payload;
import org.jclouds.io.PayloadSlicer;
import org.jclouds.io.payloads.BaseMutableContentMetadata;
import org.jclouds.io.payloads.StringPayload;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

import java.util.List;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;

@Test(groups = "unit", testName = "AzureBlobBlockUploadStrategyTest")
public class AzureBlobBlockUploadStrategyTest {

   public void testExecute() throws Exception {
      String container = "test-container";
      String blobName = "test-blob";
      long oneMB = 1048576L;
      AzureBlobClient client = createMock(AzureBlobClient.class);
      PayloadSlicer slicer = createMock(PayloadSlicer.class);
      MutableBlobMetadata metadata = new MutableBlobMetadataImpl();
      MutableContentMetadata contentMetadata = new BaseMutableContentMetadata();
      contentMetadata.setContentLength(MultipartUploadStrategy.MAX_BLOCK_SIZE * 3 + oneMB);
      metadata.setName(blobName);
      metadata.setContentMetadata(contentMetadata);
      Blob blob = new BlobImpl(metadata);
      Payload payload = new StringPayload("ABCD");
      payload.setContentMetadata(contentMetadata);
      blob.setPayload(payload);

      expect(slicer.slice(payload, 0, MultipartUploadStrategy.MAX_BLOCK_SIZE)).andReturn(payload);
      expect(slicer.slice(payload, MultipartUploadStrategy.MAX_BLOCK_SIZE, MultipartUploadStrategy.MAX_BLOCK_SIZE)).andReturn(payload);
      expect(slicer.slice(payload, MultipartUploadStrategy.MAX_BLOCK_SIZE * 2, MultipartUploadStrategy.MAX_BLOCK_SIZE)).andReturn(payload);
      expect(slicer.slice(payload, MultipartUploadStrategy.MAX_BLOCK_SIZE * 3, oneMB)).andReturn(payload);
      client.putBlock(eq(container), eq(blobName), anyObject(String.class), eq(payload));
      expectLastCall().times(4);
      expect(client.putBlockList(eq(container), eq(blobName), anyObject(List.class))).andReturn("Fake ETAG");

      AzureBlobBlockUploadStrategy strat = new AzureBlobBlockUploadStrategy(client, slicer);
      replay(slicer,client);
      String etag = strat.execute(container, blob);
      assertEquals(etag, "Fake ETAG");
   }
}
