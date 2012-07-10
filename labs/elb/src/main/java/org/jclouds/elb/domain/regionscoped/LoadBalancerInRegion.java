package org.jclouds.elb.domain.regionscoped;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.elb.domain.LoadBalancer;

/**
 * @author Adrian Cole
 */
public class LoadBalancerInRegion extends RegionAndName {
   protected final LoadBalancer loadBalancer;

   public LoadBalancerInRegion(LoadBalancer loadBalancer, String regionId) {
      super(regionId, checkNotNull(loadBalancer, "loadBalancer").getName());
      this.loadBalancer = loadBalancer;
   }

   public LoadBalancer getLoadBalancer() {
      return loadBalancer;
   }

   // superclass hashCode/equals are good enough, and help us use RegionAndId and LoadBalancerInRegion
   // interchangeably as Map keys

   @Override
   public String toString() {
      return "[loadBalancer=" + loadBalancer + ", regionId=" + regionId + "]";
   }

}
