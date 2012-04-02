package org.jclouds.openstack.swift;

import org.jclouds.apis.ApiType;
import org.jclouds.apis.BaseApiMetadataTest;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "SwiftApiMetadataTest")
public class SwiftApiMetadataTest extends BaseApiMetadataTest {

   public SwiftApiMetadataTest() {
      super(new SwiftApiMetadata(), ApiType.BLOBSTORE);
   }
}
