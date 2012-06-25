package org.jclouds.nodepool.config;

import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.nodepool.internal.JsonNodeMetadataStore;
import org.jclouds.nodepool.internal.NodeMetadataStore;

import com.google.inject.BindingAnnotation;
import com.google.inject.Provides;

public class NodePoolComputServiceContextModule extends BaseComputeServiceContextModule {

   @Retention(RetentionPolicy.RUNTIME)
   @BindingAnnotation
   public @interface Internal {
   }

   @Override
   protected void configure() {
      super.configure();
      bind(NodeMetadataStore.class).annotatedWith(Internal.class).to(JsonNodeMetadataStore.class);
   }

   @Provides
   @Singleton
   public Map<String, InputStream> provideInputStreamMapFromBlobStore(BlobStoreContext in,
            @Named(NodeMetadataStore.CONTAINER) String container) {
      return in.createInputStreamMap(container);
   }

}
