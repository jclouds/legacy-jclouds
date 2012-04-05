package org.jclouds.blobstore;

import org.jclouds.apis.ApiMetadata;

import com.google.common.annotations.Beta;

/**
 * 
 * @author Adrian Cole
 * @since 1.5
 */
@Beta
public interface BlobStoreApiMetadata<S, A, C extends BlobStoreContext<S, A>, M extends BlobStoreApiMetadata<S, A, C, M>>
      extends ApiMetadata<S, A, C, M> {

   public static interface Builder<S, A, C extends BlobStoreContext<S, A>, M extends BlobStoreApiMetadata<S, A, C, M>>
         extends ApiMetadata.Builder<S, A, C, M> {
   }

}