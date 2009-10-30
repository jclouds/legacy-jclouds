package org.jclouds.rackspace.cloudfiles.config;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jclouds.blobstore.functions.CalculateSize;
import org.jclouds.blobstore.functions.GenerateMD5;
import org.jclouds.blobstore.functions.GenerateMD5Result;
import org.jclouds.rackspace.cloudfiles.domain.CFObject;
import org.jclouds.rackspace.cloudfiles.domain.MutableObjectInfoWithMetadata;
import org.jclouds.rackspace.cloudfiles.domain.internal.CFObjectImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;

/**
 * Configures the domain object mappings needed for all CF implementations
 * 
 * @author Adrian Cole
 */
public class CFObjectModule extends AbstractModule {


   /**
    * explicit factories are created here as it has been shown that Assisted Inject is extremely
    * inefficient. http://code.google.com/p/google-guice/issues/detail?id=435
    */
   @Override
   protected void configure() {
      bind(CFObject.Factory.class).to(CFObjectFactory.class).in(Scopes.SINGLETON);
   }

   private static class CFObjectFactory implements CFObject.Factory {
      @Inject
      GenerateMD5Result generateMD5Result;
      @Inject
      GenerateMD5 generateMD5;
      @Inject
      CalculateSize calculateSize;
      @Inject
      Provider<MutableObjectInfoWithMetadata> metadataProvider;

      public CFObject create(MutableObjectInfoWithMetadata metadata) {
         return new CFObjectImpl(generateMD5Result, generateMD5, calculateSize,
                  metadata != null ? metadata : metadataProvider.get());
      }
   }

   @Provides
   CFObject provideCFObject(CFObject.Factory factory) {
      return factory.create(null);
   }

}