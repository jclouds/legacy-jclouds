package org.jclouds.openstack.cinder.v1.domain;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

@Test(groups = "unit", testName = "VolumeTest")
public class VolumeTest {
   public void testVolumeForId() {
      Volume volume = Volume.forId("60761c60-0f56-4499-b522-ff13e120af10");
      
      assertEquals(volume.getId(), "60761c60-0f56-4499-b522-ff13e120af10");
      assertEquals(volume.getStatus(), Volume.Status.UNRECOGNIZED);
      assertEquals(volume.getZone(), "nova");
   }
}
