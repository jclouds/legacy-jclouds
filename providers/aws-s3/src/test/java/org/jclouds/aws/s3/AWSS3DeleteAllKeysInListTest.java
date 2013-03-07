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
package org.jclouds.aws.s3;

import static org.easymock.EasyMock.createMock;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.easymock.EasyMock;
import org.jclouds.aws.s3.blobstore.AWSS3AsyncBlobStore;
import org.jclouds.aws.s3.blobstore.strategy.AWSS3DeleteAllKeysInList;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.domain.internal.PageSetImpl;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.rest.RestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

@Test(singleThreaded = true, testName = "AWSS3DeleteAllKeysInListTest")
public class AWSS3DeleteAllKeysInListTest {
	   private static final String containerName = "container";
	   AWSS3AsyncBlobStore connection = null;
	   BackoffLimitedRetryHandler retryHandler = null;
	   ListeningExecutorService userExecutor =
			   MoreExecutors.listeningDecorator(
					   Executors.newFixedThreadPool(2));
	   ListenableFuture<PageSet<? extends StorageMetadata>> listFuture = null;
	   StorageMetadata storageMetadata = null;
	   ListContainerOptions recOptions =
			   ListContainerOptions.Builder.recursive();
	   BlobStoreContext blobStoreContext = null;
	   RestContext<AWSS3Client, AWSS3AsyncClient> restContext = null;
	   AWSS3AsyncClient asyncClient = null;
	   Set<String> keys = null;
	   ListenableFuture<?> immediateFuture = Futures.immediateFuture(null);
	   Long maxTime = Long.MAX_VALUE;
       AWSS3DeleteAllKeysInList deleter = null;
       List<StorageMetadata> mdList = null;

	   @SuppressWarnings("unchecked")
	   @BeforeMethod
	   protected void setUp() throws IOException {
		   connection = createMock(AWSS3AsyncBlobStore.class);
		   retryHandler = createMock(BackoffLimitedRetryHandler.class);
		   listFuture = EasyMock.createNiceMock(ListenableFuture.class);
		   storageMetadata = createMock(StorageMetadata.class);
		   blobStoreContext = createMock(BlobStoreContext.class);
		   restContext = createMock(RestContext.class);
		   asyncClient = createMock(AWSS3AsyncClient.class);
           deleter = new AWSS3DeleteAllKeysInList(userExecutor, connection,
        		   retryHandler);
	   }

	   @AfterMethod
	   protected void resetTest() {
		   EasyMock.reset(connection, retryHandler, listFuture, storageMetadata,
				   blobStoreContext, restContext, asyncClient);
	   }

	   /**
	    * Private method used record the behavior when deleting a set of keys.
	    *
	    * @param keys
	    */
	   private void deleteKeys(Set<String> keys)
	   {
		   EasyMock.<BlobStoreContext> expect(connection.getContext()).andReturn(blobStoreContext);
		   EasyMock.<RestContext<AWSS3Client, AWSS3AsyncClient>>
		       expect(blobStoreContext.unwrap(AWSS3ApiMetadata.CONTEXT_TOKEN)).andReturn(restContext);
		   EasyMock.<AWSS3AsyncClient> expect(restContext.getAsyncApi()).andReturn(asyncClient);
		   EasyMock.<ListenableFuture<?>>
               expect(asyncClient.deleteObjects(containerName, keys)).andReturn(immediateFuture);
	   }

	   /**
	    * Tests the deletion of a single blob.
	    *
	    * @throws InterruptedException
	    * @throws ExecutionException
	    * @throws TimeoutException
	    */
	   @Test
	   public void testDeleterSingleBlob() throws InterruptedException,
	   		ExecutionException, TimeoutException {
		   ImmutableSet.Builder<String> builder = ImmutableSet.builder();
		   PageSet<? extends StorageMetadata> listing = new PageSetImpl<StorageMetadata>(
						   ImmutableList.of(storageMetadata), null);
		   EasyMock.<ListenableFuture<PageSet<? extends StorageMetadata>>>
	   			expect(connection.list(containerName, recOptions)).andReturn(listFuture);
		   EasyMock.<PageSet<? extends StorageMetadata>>
		   		expect(listFuture.get(maxTime, TimeUnit.MILLISECONDS)).andReturn(listing);
		   EasyMock.<StorageType> expect(storageMetadata.getType()).andReturn(StorageType.BLOB);
		   EasyMock.<String> expect(storageMetadata.getName()).andReturn("ABCD");
		   builder.add("ABCD");
		   keys = builder.build();
		   deleteKeys(keys);

		   EasyMock.replay(connection);
		   EasyMock.replay(retryHandler);
		   EasyMock.replay(listFuture);
		   EasyMock.replay(storageMetadata);
		   EasyMock.replay(blobStoreContext);
		   EasyMock.replay(restContext);
		   EasyMock.replay(asyncClient);

		   deleter.execute(containerName);
	   }

	   /**
	    * Tests the deletion of blobs returned by two PageSets.
	    *
	    * @throws InterruptedException
	    * @throws ExecutionException
	    * @throws TimeoutException
	    */
	   @SuppressWarnings("unchecked")
	   @Test
	   public void testDeleterTwoPages() throws InterruptedException,
	   		ExecutionException, TimeoutException {
		   ImmutableSet.Builder<String> builder = ImmutableSet.builder();
		   String marker = "page2";
		   PageSet<? extends StorageMetadata> listing =
				   new PageSetImpl<StorageMetadata>(ImmutableList.of(storageMetadata), marker);

		   EasyMock.<ListenableFuture<PageSet<? extends StorageMetadata>>>
	   			expect(connection.list(containerName, recOptions)).andReturn(listFuture).once();
		   EasyMock.<PageSet<? extends StorageMetadata>>
		   		expect(listFuture.get(maxTime, TimeUnit.MILLISECONDS)).andReturn(listing);
		   EasyMock.<StorageType> expect(storageMetadata.getType()).andReturn(StorageType.BLOB);
		   EasyMock.<String> expect(storageMetadata.getName()).andReturn("ABCD");
		   builder.add("ABCD");
		   keys = builder.build();
		   deleteKeys(keys);

		   // All for "page2"
		   ImmutableSet.Builder<String> builder2 = ImmutableSet.builder();
		   StorageMetadata storageMetadata2 = createMock(StorageMetadata.class);
		   ListContainerOptions recOptions2 =
				   ListContainerOptions.Builder.afterMarker(marker).recursive();
		   PageSet<? extends StorageMetadata> listing2 =
				   new PageSetImpl<StorageMetadata>(ImmutableList.of(storageMetadata2), null);
		   ListenableFuture<PageSet<? extends StorageMetadata>> listFuture2 =
				   EasyMock.createNiceMock(ListenableFuture.class);

		   EasyMock.<ListenableFuture<PageSet<? extends StorageMetadata>>>
	  			expect(connection.list(containerName, recOptions2)).andReturn(listFuture2).once();
		   EasyMock.<PageSet<? extends StorageMetadata>>
		   		expect(listFuture2.get(maxTime, TimeUnit.MILLISECONDS)).andReturn(listing2);
		   EasyMock.<StorageType> expect(storageMetadata2.getType()).andReturn(StorageType.BLOB);
		   EasyMock.<String> expect(storageMetadata2.getName()).andReturn("ABCD2");
		   builder2.add("ABCD2");
		   Set<String> keys2 = builder2.build();
		   deleteKeys(keys2);

		   EasyMock.replay(connection);
		   EasyMock.replay(retryHandler);
		   EasyMock.replay(listFuture);
		   EasyMock.replay(storageMetadata);
		   EasyMock.replay(blobStoreContext);
		   EasyMock.replay(restContext);
		   EasyMock.replay(asyncClient);
		   EasyMock.replay(listFuture2);
		   EasyMock.replay(storageMetadata2);

		   deleter.execute(containerName);
	   }

	   /**
	    * Tests the deletion of > 1000 blobs. There are two delete requests
	    * generated in this case. One for blobs 0-999 and another for 1000+.
	    *
	    * @throws InterruptedException
	    * @throws ExecutionException
	    * @throws TimeoutException
	    */
	   @Test
	   public void testDeleterThousandsOfBlob() throws InterruptedException,
	   		ExecutionException, TimeoutException {
		   ImmutableSet.Builder<String> builder1 = ImmutableSet.builder();
		   ImmutableSet.Builder<String> builder2 = ImmutableSet.builder();
		   ImmutableSet.Builder<String> builder = builder1;

		   // Some number > 1000.
           int TOTAL_KEYS = 1005;
           mdList = Lists.newArrayListWithCapacity(TOTAL_KEYS);
           for (int i = 0; i < TOTAL_KEYS; i++) {
        	   mdList.add(createMock(StorageMetadata.class));
           }

		   PageSet<? extends StorageMetadata> listing =
				   new PageSetImpl<StorageMetadata>(mdList, null);
		   EasyMock.<ListenableFuture<PageSet<? extends StorageMetadata>>>
	   			expect(connection.list(containerName, recOptions))
	   				.andReturn(listFuture);
		   EasyMock.<PageSet<? extends StorageMetadata>>
		   		expect(listFuture.get(maxTime, TimeUnit.MILLISECONDS))
		   			.andReturn(listing);

		   int i = 0;
		   int maxMultiDeleteKeys = 1000;
		   for (StorageMetadata md : listing) {
			   String keyName = "ABCD_" + i;
			   EasyMock.<StorageType> expect(md.getType())
		   			.andReturn(StorageType.BLOB);
			   EasyMock.<String> expect(md.getName()).andReturn(keyName);

   		       builder.add(keyName);
			   i++;

			   if (i % maxMultiDeleteKeys == 0) {
				   keys = builder.build();
				   deleteKeys(keys);

		       	   builder = builder2;
			   }
		   }

		   keys = builder.build();
		   if (!keys.isEmpty()) {
			   deleteKeys(keys);
		   }

		   EasyMock.replay(connection);
		   EasyMock.replay(retryHandler);
		   EasyMock.replay(listFuture);
		   EasyMock.replay(storageMetadata);
		   EasyMock.replay(blobStoreContext);
		   EasyMock.replay(restContext);
		   EasyMock.replay(asyncClient);
		   for (i = 0; i < TOTAL_KEYS; i++) {
			   EasyMock.replay(mdList.get(i));
		   }

		   deleter.execute(containerName);
	   }
}
