package org.jclouds.elb.loadbalancer.strategy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.aws.util.AWSUtils.parseHandle;

import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.elb.ELBClient;
import org.jclouds.elb.domain.LoadBalancer;
import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;
import org.jclouds.loadbalancer.strategy.GetLoadBalancerMetadataStrategy;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ELBGetLoadBalancerMetadataStrategy implements GetLoadBalancerMetadataStrategy {

   private final ELBClient client;
   private final Function<LoadBalancer, LoadBalancerMetadata> converter;

   @Inject
   protected ELBGetLoadBalancerMetadataStrategy(ELBClient client, Function<LoadBalancer, LoadBalancerMetadata> converter) {
      this.client = checkNotNull(client, "client");
      this.converter = checkNotNull(converter, "converter");
   }

   @Override
   public LoadBalancerMetadata getLoadBalancer(String id) {
      String[] parts = parseHandle(id);
      String region = parts[0];
      String name = parts[1];
      try {
         return converter.apply(getOnlyElement(client.describeLoadBalancersInRegion(region, name)));
      } catch (NoSuchElementException e) {
         return null;
      }
   }

}