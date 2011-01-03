package org.jclouds.elb.loadbalancer.config;

import org.jclouds.elb.domain.LoadBalancer;
import org.jclouds.elb.loadbalancer.functions.LoadBalancerToLoadBalancerMetadata;
import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;

import com.google.common.base.Function;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
public class ELBLoadBalancerServiceDependenciesModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(new TypeLiteral<Function<LoadBalancer, LoadBalancerMetadata>>() {
      }).to(LoadBalancerToLoadBalancerMetadata.class);
   }

}
