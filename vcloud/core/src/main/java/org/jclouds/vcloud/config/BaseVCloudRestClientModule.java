/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.vcloud.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULT_NETWORK;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_ENDPOINT;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_KEY;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_SESSIONINTERVAL;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_USER;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_VERSION;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.concurrent.ExpirableSupplier;
import org.jclouds.concurrent.RetryOnTimeOutExceptionSupplier;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.logging.Logger;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rest.AsyncClientFactory;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.vcloud.VCloudAsyncClient;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.VCloudToken;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.Organization;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.endpoints.Catalog;
import org.jclouds.vcloud.endpoints.Network;
import org.jclouds.vcloud.endpoints.Org;
import org.jclouds.vcloud.endpoints.TasksList;
import org.jclouds.vcloud.endpoints.VCloud;
import org.jclouds.vcloud.endpoints.VCloudApi;
import org.jclouds.vcloud.endpoints.VCloudLogin;
import org.jclouds.vcloud.endpoints.VDC;
import org.jclouds.vcloud.endpoints.internal.CatalogItemRoot;
import org.jclouds.vcloud.endpoints.internal.VAppRoot;
import org.jclouds.vcloud.endpoints.internal.VAppTemplateRoot;
import org.jclouds.vcloud.handlers.ParseVCloudErrorFromHttpResponse;
import org.jclouds.vcloud.internal.VCloudLoginAsyncClient;
import org.jclouds.vcloud.internal.VCloudVersionsAsyncClient;
import org.jclouds.vcloud.internal.VCloudLoginAsyncClient.VCloudSession;
import org.jclouds.vcloud.predicates.TaskSuccess;
import org.jclouds.vcloud.predicates.VAppNotFound;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.inject.Provides;

/**
 * Configures the VCloud authentication service connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public abstract class BaseVCloudRestClientModule<S extends VCloudClient, A extends VCloudAsyncClient>
         extends RestClientModule<S, A> {

   public BaseVCloudRestClientModule(Class<S> syncClientType, Class<A> asyncClientType) {
      super(syncClientType, asyncClientType);
   }

   @Override
   protected void configure() {
      requestInjection(this);
      super.configure();
   }

   @Resource
   protected Logger logger = Logger.NULL;

   @Provides
   @Singleton
   protected Predicate<IPSocket> socketTester(SocketOpen open) {
      return new RetryablePredicate<IPSocket>(open, 130, 10, TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   protected Predicate<String> successTester(TaskSuccess success) {
      return new RetryablePredicate<String>(success, 60 * 30, 10, TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   @Named("NOT_FOUND")
   protected Predicate<VApp> successTester(VAppNotFound notFound) {
      return new RetryablePredicate<VApp>(notFound, 5, 1, TimeUnit.SECONDS);
   }

   @VCloudToken
   @Provides
   String provideVCloudToken(Supplier<VCloudSession> cache) {
      return checkNotNull(cache.get().getVCloudToken(), "No token present in session");
   }

   @Provides
   @Org
   @Singleton
   protected URI provideOrg(Supplier<VCloudSession> cache, @Named(PROPERTY_VCLOUD_USER) String user) {
      VCloudSession discovery = cache.get();
      checkState(discovery.getOrgs().size() > 0, "No orgs present for user: " + user);
      return Iterables.getLast(discovery.getOrgs().values()).getLocation();
   }

   @Provides
   @VCloudApi
   @Singleton
   URI provideVCloudApi(@VCloudLogin URI vcloudUri) {
      return URI.create(vcloudUri.toASCIIString().replace("/login", ""));
   }

   private AuthorizationException authException = null;

   /**
    * borrowing concurrency code to ensure that caching takes place properly
    */
   @Provides
   @Singleton
   Supplier<VCloudSession> provideVCloudTokenCache(
            @Named(PROPERTY_VCLOUD_SESSIONINTERVAL) long seconds, final VCloudLoginAsyncClient login) {
      return new ExpirableSupplier<VCloudSession>(
               new RetryOnTimeOutExceptionSupplier<VCloudSession>(new Supplier<VCloudSession>() {
                  public VCloudSession get() {
                     try {
                        // http://code.google.com/p/google-guice/issues/detail?id=483
                        // guice doesn't remember when singleton providers throw exceptions.
                        // in this case, if describeRegions fails, it is called again for
                        // each provider method that depends on it. To short-circuit this,
                        // we remember the last exception trusting that guice is single-threaded
                        if (authException != null)
                           throw authException;
                        return login.login().get(10, TimeUnit.SECONDS);
                     } catch (AuthorizationException e) {
                        BaseVCloudRestClientModule.this.authException = e;
                        throw e;
                     } catch (Exception e) {
                        Throwables.propagate(e);
                        assert false : e;
                        return null;
                     }
                  }

               }), seconds, TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   @VCloud
   protected URI provideBaseURI(@Named(PROPERTY_VCLOUD_ENDPOINT) String endpoint) {
      return URI.create(endpoint);
   }

   @Provides
   @Singleton
   @org.jclouds.vcloud.endpoints.VCloudLogin
   protected URI provideAuthenticationURI(VCloudVersionsAsyncClient versionService,
            @Named(PROPERTY_VCLOUD_VERSION) String version) throws InterruptedException,
            ExecutionException, TimeoutException {
      SortedMap<String, URI> versions = versionService.getSupportedVersions().get(180,
               TimeUnit.SECONDS);
      checkState(versions.size() > 0, "No versions present");
      checkState(versions.containsKey(version), "version " + version + " not present in: "
               + versions);
      return versions.get(version);
   }

   @Provides
   @Singleton
   protected VCloudLoginAsyncClient provideVCloudLogin(AsyncClientFactory factory) {
      return factory.create(VCloudLoginAsyncClient.class);
   }

   @Provides
   @Singleton
   protected VCloudVersionsAsyncClient provideVCloudVersions(AsyncClientFactory factory) {
      return factory.create(VCloudVersionsAsyncClient.class);
   }

   @Provides
   @Singleton
   public BasicAuthentication provideBasicAuthentication(@Named(PROPERTY_VCLOUD_USER) String user,
            @Named(PROPERTY_VCLOUD_KEY) String key, EncryptionService encryptionService)
            throws UnsupportedEncodingException {
      return new BasicAuthentication(user, key, encryptionService);
   }

   @Provides
   @CatalogItemRoot
   @Singleton
   String provideCatalogItemRoot(@VCloudLogin URI vcloudUri) {
      return vcloudUri.toASCIIString().replace("/login", "/catalogItem");
   }

   @Provides
   @VAppRoot
   @Singleton
   String provideVAppRoot(@VCloudLogin URI vcloudUri) {
      return vcloudUri.toASCIIString().replace("/login", "/vapp");
   }

   @Provides
   @VAppTemplateRoot
   @Singleton
   String provideVAppTemplateRoot(@VCloudLogin URI vcloudUri) {
      return vcloudUri.toASCIIString().replace("/login", "/vAppTemplate");
   }

   @Provides
   @Singleton
   protected Organization provideOrganization(VCloudClient discovery) throws ExecutionException,
            TimeoutException, InterruptedException {
      if (authException != null)
         throw authException;
      try {
         return discovery.getDefaultOrganization();
      } catch (AuthorizationException e) {
         BaseVCloudRestClientModule.this.authException = e;
         throw e;
      }
   }

   @Provides
   @VDC
   @Singleton
   protected URI provideDefaultVDC(Organization org) {
      checkState(org.getVDCs().size() > 0, "No vdcs present in org: " + org.getName());
      return Iterables.get(org.getVDCs().values(), 0).getLocation();
   }

   @Provides
   @Catalog
   @Singleton
   protected URI provideCatalog(Organization org, @Named(PROPERTY_VCLOUD_USER) String user) {
      checkState(org.getCatalogs().size() > 0, "No catalogs present in org: " + org.getName());
      return Iterables.get(org.getCatalogs().values(), 0).getLocation();
   }

   @Provides
   @Network
   @Singleton
   protected URI provideDefaultNetwork(VCloudClient client) throws InterruptedException,
            ExecutionException, TimeoutException {
      if (authException != null)
         throw authException;
      try {
         org.jclouds.vcloud.domain.VDC vDC = client.getDefaultVDC();
         Map<String, NamedResource> networks = vDC.getAvailableNetworks();
         checkState(networks.size() > 0, "No networks present in vDC: " + vDC.getName());
         return Iterables.get(networks.values(), 0).getLocation();
      } catch (AuthorizationException e) {
         BaseVCloudRestClientModule.this.authException = e;
         throw e;
      }
   }

   @Provides
   @Named(PROPERTY_VCLOUD_DEFAULT_NETWORK)
   @Singleton
   String provideDefaultNetworkString(@Network URI network) {
      return network.toASCIIString();
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(
               ParseVCloudErrorFromHttpResponse.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(
               ParseVCloudErrorFromHttpResponse.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(
               ParseVCloudErrorFromHttpResponse.class);
   }

   @Provides
   @TasksList
   @Singleton
   protected URI provideDefaultTasksList(Organization org) {
      checkState(org.getTasksLists().size() > 0, "No tasks lists present in org: " + org.getName());
      return Iterables.get(org.getTasksLists().values(), 0).getLocation();
   }
}
