package org.jclouds.openstack.swift;

import org.jclouds.blobstore.internal.BaseBlobStoreApiMetadataTest;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "SwiftApiMetadataTest")
public class SwiftApiMetadataTest extends BaseBlobStoreApiMetadataTest {

   public SwiftApiMetadataTest() {
      super(new SwiftApiMetadata());
   }
}
