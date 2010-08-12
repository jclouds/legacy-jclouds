/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_IDENTITY;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULT_NETWORK;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_TIMEOUT_TASK_COMPLETED;

import java.net.URI;
import java.util.Map;
import java.util.SortedMap;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.AsyncClientFactory;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.rest.suppliers.RetryOnTimeOutButNotOnAuthorizationExceptionSupplier;
import org.jclouds.vcloud.VCloudAsyncClient;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.VCloudToken;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.Organization;
import org.jclouds.vcloud.endpoints.Catalog;
import org.jclouds.vcloud.endpoints.Network;
import org.jclouds.vcloud.endpoints.Org;
import org.jclouds.vcloud.endpoints.TasksList;
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

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Configures the VCloud authentication service connection, including logging
 * and http transport.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public abstract class BaseVCloudRestClientModule<S extends VCloudClient, A extends VCloudAsyncClient> extends
      RestClientModule<S, A> {

   public BaseVCloudRestClientModule(Class<S> syncClientType, Class<A> asyncClientType) {
      super(syncClientType, asyncClientType);
   }

   @Override
   protected void configure() {
      requestInjection(this);
      bind(new TypeLiteral<Supplier<Map<String, NamedResource>>>() {
      }).annotatedWith(Org.class).to(new TypeLiteral<OrgNameToOrgSupplier>() {
      });
      super.configure();
   }

   @Resource
   protected Logger logger = Logger.NULL;

   @Provides
   @Singleton
   protected Predicate<String> successTester(TaskSuccess success,
         @Named(PROPERTY_VCLOUD_TIMEOUT_TASK_COMPLETED) long completed) {
      return new RetryablePredicate<String>(success, completed);
   }

   @VCloudToken
   @Provides
   String provideVCloudToken(Supplier<VCloudSession> cache) {
      return checkNotNull(cache.get().getVCloudToken(), "No token present in session");
   }

   @Provides
   @Org
   @Singleton
   protected URI provideOrg(@Org Iterable<NamedResource> orgs) {
      return Iterables.getLast(orgs).getLocation();
   }

   @Provides
   @Org
   @Singleton
   protected String provideOrgName(@Org Iterable<NamedResource> orgs) {
      return Iterables.getLast(orgs).getName();
   }

   @Provides
   @Singleton
   @VDC
   protected Supplier<Map<String, String>> provideVDCtoORG(@Named(PROPERTY_SESSION_INTERVAL) long seconds,
         final Supplier<Map<String, Organization>> orgToVDCSupplier) {
      return new RetryOnTimeOutButNotOnAuthorizationExceptionSupplier<Map<String, String>>(authException, seconds,
            new Supplier<Map<String, String>>() {
               @Override
               public Map<String, String> get() {
                  Map<String, String> returnVal = Maps.newLinkedHashMap();
                  for (Entry<String, Organization> orgr : orgToVDCSupplier.get().entrySet()) {
                     for (String vdc : orgr.getValue().getVDCs().keySet()) {
                        returnVal.put(vdc, orgr.getKey());
                     }
                  }
                  return returnVal;
               }
            });

   }

   @Provides
   @Org
   @Singleton
   protected Iterable<NamedResource> provideOrgs(Supplier<VCloudSession> cache, @Named(PROPERTY_IDENTITY) String user) {
      VCloudSession discovery = cache.get();
      checkState(discovery.getOrgs().size() > 0, "No orgs present for user: " + user);
      return discovery.getOrgs().values();
   }

   @Provides
   @VCloudApi
   @Singleton
   URI provideVCloudApi(@VCloudLogin URI vcloudUri) {
      return URI.create(vcloudUri.toASCIIString().replace("/login", ""));
   }

   protected AtomicReference<AuthorizationException> authException = new AtomicReference<AuthorizationException>();

   @Provides
   @Singleton
   protected Supplier<VCloudSession> provideVCloudTokenCache(@Named(PROPERTY_SESSION_INTERVAL) long seconds,
         final VCloudLoginAsyncClient login) {
      return new RetryOnTimeOutButNotOnAuthorizationExceptionSupplier<VCloudSession>(authException, seconds,
            new Supplier<VCloudSession>() {

               @Override
               public VCloudSession get() {
                  try {
                     return login.login().get(10, TimeUnit.SECONDS);
                  } catch (Exception e) {
                     Throwables.propagate(e);
                     assert false : e;
                     return null;
                  }
               }

            });
   }

   @Provides
   @Singleton
   protected Supplier<Map<String, Organization>> provideOrgMapCache(@Named(PROPERTY_SESSION_INTERVAL) long seconds,
         final OrganizationMapSupplier supplier) {
      return new RetryOnTimeOutButNotOnAuthorizationExceptionSupplier<Map<String, Organization>>(authException,
            seconds, new Supplier<Map<String, Organization>>() {
               @Override
               public Map<String, Organization> get() {
                  return supplier.get();
               }

            });
   }

   @Singleton
   public static class OrganizationMapSupplier implements Supplier<Map<String, Organization>> {
      protected final Supplier<VCloudSession> sessionSupplier;
      private final VCloudClient client;

      @Inject
      protected OrganizationMapSupplier(Supplier<VCloudSession> sessionSupplier, VCloudClient client) {
         this.sessionSupplier = sessionSupplier;
         this.client = client;
      }

      @Override
      public Map<String, Organization> get() {
         Map<String, Organization> returnVal = Maps.newLinkedHashMap();
         for (String orgName : sessionSupplier.get().getOrgs().keySet()) {
            returnVal.put(orgName, client.getOrganizationNamed(orgName));
         }
         return returnVal;
      }

   }

   @Provides
   @Singleton
   @org.jclouds.vcloud.endpoints.VCloudLogin
   protected URI provideAuthenticationURI(VCloudVersionsAsyncClient versionService,
         @Named(PROPERTY_API_VERSION) String version) throws InterruptedException, ExecutionException, TimeoutException {
      SortedMap<String, URI> versions = versionService.getSupportedVersions().get(180, TimeUnit.SECONDS);
      checkState(versions.size() > 0, "No versions present");
      checkState(versions.containsKey(version), "version " + version + " not present in: " + versions);
      return versions.get(version);
   }

   @Singleton
   private static class OrgNameToOrgSupplier implements Supplier<Map<String, NamedResource>> {
      private final Supplier<VCloudSession> sessionSupplier;

      @SuppressWarnings("unused")
      @Inject
      OrgNameToOrgSupplier(Supplier<VCloudSession> sessionSupplier) {
         this.sessionSupplier = sessionSupplier;
      }

      @Override
      public Map<String, NamedResource> get() {
         return sessionSupplier.get().getOrgs();
      }

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
   protected Organization provideOrganization(VCloudClient discovery) throws ExecutionException, TimeoutException,
         InterruptedException {
      if (authException.get() != null)
         throw authException.get();
      try {
         return discovery.getDefaultOrganization();
      } catch (AuthorizationException e) {
         authException.set(e);
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
   protected URI provideCatalog(Organization org, @Named(PROPERTY_IDENTITY) String user) {
      checkState(org.getCatalogs().size() > 0, "No catalogs present in org: " + org.getName());
      return Iterables.get(org.getCatalogs().values(), 0).getLocation();
   }

   @Provides
   @Network
   @Singleton
   protected URI provideDefaultNetwork(VCloudClient client) throws InterruptedException, ExecutionException,
         TimeoutException {
      if (authException.get() != null)
         throw authException.get();
      try {
         org.jclouds.vcloud.domain.VDC vDC = client.getDefaultVDC();
         Map<String, NamedResource> networks = vDC.getAvailableNetworks();
         checkState(networks.size() > 0, "No networks present in vDC: " + vDC.getName());
         return Iterables.get(networks.values(), 0).getLocation();
      } catch (AuthorizationException e) {
         authException.set(e);
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
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(ParseVCloudErrorFromHttpResponse.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(ParseVCloudErrorFromHttpResponse.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(ParseVCloudErrorFromHttpResponse.class);
   }

   @Provides
   @TasksList
   @Singleton
   protected URI provideDefaultTasksList(Organization org) {
      checkState(org.getTasksLists().size() > 0, "No tasks lists present in org: " + org.getName());
      return Iterables.get(org.getTasksLists().values(), 0).getLocation();
   }
}
