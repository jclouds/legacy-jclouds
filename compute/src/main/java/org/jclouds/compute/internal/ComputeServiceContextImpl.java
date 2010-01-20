package org.jclouds.compute.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.rest.RestContext;

/**
 * @author Adrian Cole
 */
public class ComputeServiceContextImpl<X, Y> implements ComputeServiceContext {
   private final ComputeService computeService;
   private final RestContext<X, Y> providerSpecificContext;

   @Inject
   public ComputeServiceContextImpl(ComputeService computeService,
            RestContext<X, Y> providerSpecificContext) {
      this.computeService = checkNotNull(computeService, "computeService");
      this.providerSpecificContext = providerSpecificContext;
   }

   public ComputeService getComputeService() {
      return computeService;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <A, S> RestContext<A, S> getProviderSpecificContext() {
      return (RestContext<A, S>) providerSpecificContext;
   }

   @Override
   public void close() {
      providerSpecificContext.close();
   }
}
