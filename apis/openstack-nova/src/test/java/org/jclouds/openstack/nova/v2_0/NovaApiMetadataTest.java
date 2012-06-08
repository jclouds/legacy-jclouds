package org.jclouds.openstack.nova.v2_0;

import org.jclouds.compute.internal.BaseComputeServiceApiMetadataTest;
import org.jclouds.openstack.nova.v2_0.NovaApiMetadata;
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
