package org.jclouds.blobstore.internal;

import java.util.Set;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.ApiType;
import org.jclouds.apis.Apis;
import org.jclouds.apis.internal.BaseApiMetadataTest;
import org.jclouds.blobstore.BlobStoreApiMetadata;
import org.jclouds.blobstore.BlobStoreContext;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public abstract class BaseBlobStoreApiMetadataTest extends BaseApiMetadataTest {

   @SuppressWarnings("rawtypes")
   public BaseBlobStoreApiMetadataTest(BlobStoreApiMetadata toTest) {
     super(toTest, ApiType.BLOBSTORE);
   }

   @Test
   public void testContextAssignableFromBlobStoreContext() {
      Set<ApiMetadata<?, ?, ?, ?>> all = ImmutableSet.copyOf(Apis.contextAssignableFrom(TypeToken.of(BlobStoreContext.class)));
      assert all.contains(toTest) : String.format("%s not found in %s", toTest, all);
   }

}