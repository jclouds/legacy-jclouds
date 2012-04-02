package org.jclouds.openstack.nova.v1_1;

import org.jclouds.apis.ApiType;
import org.jclouds.apis.BaseApiMetadataTest;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "NovaApiMetadataTest")
public class NovaApiMetadataTest extends BaseApiMetadataTest {

   public NovaApiMetadataTest() {
      super(new NovaApiMetadata(), ApiType.COMPUTE);
   }
}
