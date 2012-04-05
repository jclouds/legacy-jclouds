package org.jclouds.loadbalancer;

import org.jclouds.apis.ApiMetadata;

import com.google.common.annotations.Beta;

/**
 * 
 * @author Adrian Cole
 * @since 1.5
 */
@Beta
public interface LoadBalancerServiceApiMetadata<S, A, C extends LoadBalancerServiceContext<S, A>, M extends LoadBalancerServiceApiMetadata<S, A, C, M>>
      extends ApiMetadata<S, A, C, M> {

   public static interface Builder<S, A, C extends LoadBalancerServiceContext<S, A>, M extends LoadBalancerServiceApiMetadata<S, A, C, M>>
         extends ApiMetadata.Builder<S, A, C, M> {
   }

}