package org.jclouds.greenqloud.storage;

import org.jclouds.providers.BaseProviderMetadataTest;
import org.jclouds.providers.ProviderMetadata;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "GreenQloudStorageProviderTest")
public class GreenQloudStorageProviderTest extends BaseProviderMetadataTest {

   public GreenQloudStorageProviderTest() {
      super(new GreenQloudStorageProviderMetadata(), ProviderMetadata.BLOBSTORE_TYPE);
   }

}