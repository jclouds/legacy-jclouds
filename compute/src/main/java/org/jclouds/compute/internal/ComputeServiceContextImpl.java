package org.jclouds.compute.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.lifecycle.Closer;
import org.jclouds.rest.internal.RestContextImpl;

/**
 * @author Adrian Cole
 */
public class ComputeServiceContextImpl<A, S> extends RestContextImpl<A, S> implements
         ComputeServiceContext<A, S> {
   private final ComputeService computeService;

   public ComputeServiceContextImpl(Closer closer, ComputeService computeService, A asyncApi,
            S syncApi, URI endPoint, String account) {
      super(closer, asyncApi, syncApi, endPoint, account);
      this.computeService = checkNotNull(computeService, "computeService");
   }

   public ComputeService getComputeService() {
      return computeService;
   }

}
