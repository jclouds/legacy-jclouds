package org.jclouds.cloudstack.internal;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.cloudstack.CloudStackAsyncClient;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.CloudStackContext;
import org.jclouds.cloudstack.CloudStackDomainAsyncClient;
import org.jclouds.cloudstack.CloudStackDomainClient;
import org.jclouds.cloudstack.CloudStackGlobalAsyncClient;
import org.jclouds.cloudstack.CloudStackGlobalClient;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.Utils;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.domain.Credentials;
import org.jclouds.rest.RestContext;

/**
 * @author Adrian Cole
 */
@Singleton
public class CloudStackContextImpl extends ComputeServiceContextImpl<CloudStackClient, CloudStackAsyncClient> implements
      CloudStackContext {
   private final RestContext<CloudStackDomainClient, CloudStackDomainAsyncClient> domainContext;
   private final RestContext<CloudStackGlobalClient, CloudStackGlobalAsyncClient> globalContext;

   @Inject
   public CloudStackContextImpl(ComputeService computeService, Map<String, Credentials> credentialStore, Utils utils,
         @SuppressWarnings("rawtypes") RestContext providerSpecificContext,
         RestContext<CloudStackDomainClient, CloudStackDomainAsyncClient> domainContext,
         RestContext<CloudStackGlobalClient, CloudStackGlobalAsyncClient> globalContext) {
      super(computeService, credentialStore, utils, providerSpecificContext);
      this.domainContext = domainContext;
      this.globalContext = globalContext;
   }

   @Override
   public RestContext<CloudStackDomainClient, CloudStackDomainAsyncClient> getDomainContext() {
      return domainContext;
   }

   @Override
   public RestContext<CloudStackGlobalClient, CloudStackGlobalAsyncClient> getGlobalContext() {
      return globalContext;
   }
}
