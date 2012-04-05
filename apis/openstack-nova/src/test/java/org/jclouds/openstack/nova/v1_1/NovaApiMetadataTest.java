package org.jclouds.openstack.nova.v1_1;

import org.jclouds.compute.internal.BaseComputeServiceApiMetadataTest;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "NovaApiMetadataTest")
public class NovaApiMetadataTest extends BaseComputeServiceApiMetadataTest {

   public NovaApiMetadataTest() {
      super(new NovaApiMetadata());
   }
}
