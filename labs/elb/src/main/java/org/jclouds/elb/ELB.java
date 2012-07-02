package org.jclouds.elb;

import org.jclouds.collect.PaginatedSet;
import org.jclouds.collect.PaginatedSets;
import org.jclouds.elb.domain.LoadBalancer;
import org.jclouds.elb.features.LoadBalancerClient;
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
    * @param loadBalancerClient
    *           the {@link LoadBalancerClient} to use for the request
    * @param options
    *           the {@link ListLoadBalancersOptions} describing the ListLoadBalancers request
    * 
    * @return iterable of loadBalancers fitting the criteria
    */
   public static Iterable<LoadBalancer> listLoadBalancers(final LoadBalancerClient loadBalancerClient, final ListLoadBalancersOptions options) {
      return PaginatedSets.lazyContinue(loadBalancerClient.list(options), new Function<String, PaginatedSet<LoadBalancer>>() {

         @Override
         public PaginatedSet<LoadBalancer> apply(String input) {
            return loadBalancerClient.list(options.clone().marker(input));
         }

         @Override
         public String toString() {
            return "listLoadBalancers(" + options + ")";
         }
      });
   }
   
   public static Iterable<LoadBalancer> listLoadBalancers(LoadBalancerClient loadBalancerClient) {
      return listLoadBalancers(loadBalancerClient, new ListLoadBalancersOptions());
   }
   
}
