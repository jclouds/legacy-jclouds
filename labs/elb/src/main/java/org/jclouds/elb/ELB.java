package org.jclouds.elb;

import org.jclouds.collect.PaginatedIterable;
import org.jclouds.collect.PaginatedIterables;
import org.jclouds.elb.domain.LoadBalancer;
import org.jclouds.elb.features.LoadBalancerApi;
import org.jclouds.elb.options.ListLoadBalancersOptions;

import com.google.common.base.Function;

/**
 * Utilities for using ELB.
 * 
 * @author Adrian Cole
 */
public class ELB {

   /**
    * List loadBalancers based on the criteria in the {@link ListLoadBalancersOptions} passed in.
    * 
    * @param loadBalancerApi
    *           the {@link LoadBalancerApi} to use for the request
    * @param options
    *           the {@link ListLoadBalancersOptions} describing the ListLoadBalancers request
    * 
    * @return iterable of loadBalancers fitting the criteria
    */
   public static Iterable<LoadBalancer> listLoadBalancers(final LoadBalancerApi loadBalancerApi, final ListLoadBalancersOptions options) {
      return PaginatedIterables.lazyContinue(loadBalancerApi.list(options), new Function<Object, PaginatedIterable<LoadBalancer>>() {

         @Override
         public PaginatedIterable<LoadBalancer> apply(Object input) {
            return loadBalancerApi.list(options.clone().afterMarker(input));
         }

         @Override
         public String toString() {
            return "listLoadBalancers(" + options + ")";
         }
      });
   }
   
   public static Iterable<LoadBalancer> listLoadBalancers(LoadBalancerApi loadBalancerApi) {
      return listLoadBalancers(loadBalancerApi, new ListLoadBalancersOptions());
   }
   
}
