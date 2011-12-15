package org.jclouds.blobstore;

import static org.easymock.classextension.EasyMock.createMock;
import static org.testng.Assert.assertEquals;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.easymock.classextension.EasyMock;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.internal.PageSetImpl;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

public class BlobStoresTest {

   private final String containerName = "mycontainer";

   @Test(expectedExceptions={ContainerNotFoundException.class})
   public void testListAllForUnknownContainerFromTransientBlobStore() throws Exception {
      ListContainerOptions options = ListContainerOptions.NONE;
      BlobStoreContext context = new BlobStoreContextFactory().createContext("transient", "dummyid", "dummykey");
      try {
         BlobStore blobStore = context.getBlobStore();
   
         // Arguably it would be best to throw the exception as soon as listAll is called; but because
         // the iterator is lazy we don't the exception until we first call iterator().next() or hasNext().
         Iterable<StorageMetadata> iterable = BlobStores.listAll(blobStore, "wrongcontainer", options);
         iterable.iterator().hasNext();
      } finally {
         context.close();
      }
   }
   
   @Test
   public void testListAllFromTransientBlobStore() throws Exception {
      final int NUM_BLOBS = 31;
      ListContainerOptions options = ListContainerOptions.Builder.maxResults(10);
      BlobStoreContext context = new BlobStoreContextFactory().createContext("transient", "dummyid", "dummykey");
      BlobStore blobStore = null;
      try {
         blobStore = context.getBlobStore();
         blobStore.createContainerInLocation(null, containerName);   
         Set<String> expectedNames = new HashSet<String>();
         for (int i = 0; i < NUM_BLOBS; i++) {
            String blobName = "myname"+i;
            blobStore.putBlob(containerName, blobStore.blobBuilder(blobName).payload("payload"+i).build());
            expectedNames.add(blobName);
         }
         
         Iterable<StorageMetadata> iterable = BlobStores.listAll(blobStore, containerName, options);
         Iterable<String> iterableNames = Iterables.transform(iterable, new Function<StorageMetadata,String>() {
            @Override public String apply(StorageMetadata input) {
               return input.getName();
            }});
         
         // Note that blob.getMetadata being put does not equal blob metadata being retrieved 
         // because uri is null in one and populated in the other.
         // Therefore we just compare names to ensure the iterator worked.
         assertEquals(ImmutableSet.copyOf(iterableNames), expectedNames);
      } finally {
         if (blobStore != null) blobStore.deleteContainer(containerName);
         context.close();
      }
   }
   
   @Test
   public void testListAllWhenOnePage() throws Exception {
      BlobStore blobStore = createMock(BlobStore.class);
      ListContainerOptions options = ListContainerOptions.NONE;
      StorageMetadata v1 = createMock(StorageMetadata.class);
      PageSet<StorageMetadata> pageSet = new PageSetImpl<StorageMetadata>(Collections.singletonList(v1), null);
      
      EasyMock.<PageSet<? extends StorageMetadata>>expect(blobStore.list(containerName, options)).andReturn(pageSet).once();
      EasyMock.replay(blobStore);
      
      Iterable<StorageMetadata> iterable = BlobStores.listAll(blobStore, containerName, options);
      assertEquals(ImmutableList.copyOf(iterable), ImmutableList.of(v1));
   }
   
   @Test
   public void testListAllWhenTwoPages() throws Exception {
      BlobStore blobStore = createMock(BlobStore.class);
      ListContainerOptions options = ListContainerOptions.NONE;
      ListContainerOptions options2 = ListContainerOptions.Builder.afterMarker("marker1");
      StorageMetadata v1 = createMock(StorageMetadata.class);
      StorageMetadata v2 = createMock(StorageMetadata.class);
      PageSet<StorageMetadata> pageSet = new PageSetImpl<StorageMetadata>(Collections.singletonList(v1), "marker1");
      PageSet<StorageMetadata> pageSet2 = new PageSetImpl<StorageMetadata>(Collections.singletonList(v2), null);
      
      EasyMock.<PageSet<? extends StorageMetadata>>expect(blobStore.list(containerName, options)).andReturn(pageSet).once();
      EasyMock.<PageSet<? extends StorageMetadata>>expect(blobStore.list(containerName, options2)).andReturn(pageSet2).once();
      EasyMock.replay(blobStore);
      
      Iterable<StorageMetadata> iterable = BlobStores.listAll(blobStore, containerName, options);
      assertEquals(ImmutableList.copyOf(iterable), ImmutableList.of(v1, v2));
   }
}
