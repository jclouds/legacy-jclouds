package org.jclouds.aws.ec2.compute.domain;

import org.jclouds.aws.domain.Region;

public class PortsRegionTag extends RegionTag {
   private final int[] ports;

   public PortsRegionTag(Region region, String tag, int[] ports) {
      super(region, tag);
      this.ports = ports;
   }

   // intentionally not overriding equals or hash-code so that we can search only by region/tag in a
   // map

   public int[] getPorts() {
      return ports;
   }

}