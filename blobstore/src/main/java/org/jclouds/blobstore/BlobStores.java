package org.jclouds.blobstore;

import java.util.Iterator;

import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.options.ListContainerOptions;

import com.google.common.annotations.Beta;
import com.google.common.collect.AbstractIterator;

public class BlobStores {

   /**
    * A variant of BlobStore.list(String, ListContainerOptions) that
    * produces an Iterable over the entire set of results, not just one
    * page, making multiple calls to BlobStore.list as needed.
    */
   @Beta
   public static Iterable<StorageMetadata> listAll(final BlobStore blobStore, final String container,
            final ListContainerOptions options) {

      return new Iterable<StorageMetadata>() {
         public Iterator<StorageMetadata> iterator() {
            return new AbstractIterator<StorageMetadata>() {
               private Iterator<? extends StorageMetadata> iterator;
               private String marker;

               public StorageMetadata computeNext() {
                  while (true) {
                     if (iterator == null) {
                        ListContainerOptions nextOptions = marker == null ? options : options.clone().afterMarker(marker);
                        PageSet<? extends StorageMetadata> list = blobStore.list(container, nextOptions);
                        iterator = list.iterator();
                        marker = list.getNextMarker();
                     }
                     if (iterator.hasNext()) {
                        return iterator.next();
                     }
                     if (marker == null) {
                        return endOfData();
                     }
                     iterator = null;
                  }
               }
            };
         }
      };
   }

}
