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
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.getLast;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Maps.uniqueIndex;
import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_IDENTITY;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULT_NETWORK;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_TIMEOUT_TASK_COMPLETED;

import java.net.URI;
import java.util.Map;
import java.util.SortedMap;
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
import org.jclouds.rest.AsyncClientFactory;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.rest.suppliers.RetryOnTimeOutButNotOnAuthorizationExceptionSupplier;
import org.jclouds.vcloud.VCloudToken;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.endpoints.Catalog;
import org.jclouds.vcloud.endpoints.Network;
import org.jclouds.vcloud.endpoints.Org;
import org.jclouds.vcloud.handlers.ParseVCloudErrorFromHttpResponse;
import org.jclouds.vcloud.internal.VCloudLoginAsyncClient;
import org.jclouds.vcloud.internal.VCloudVersionsAsyncClient;
import org.jclouds.vcloud.internal.VCloudLoginAsyncClient.VCloudSession;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.inject.Injector;
import com.google.inject.Provides;

/**
 * Configures the VCloud authentication service connection, including logging
 * and http transport.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public abstract class CommonVCloudRestClientModule<S, A> extends RestClientModule<S, A> {

   public CommonVCloudRestClientModule(Class<S> syncClientType, Class<A> asyncClientType) {
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
   protected abstract Predicate<URI> successTester(Injector injector,
         @Named(PROPERTY_VCLOUD_TIMEOUT_TASK_COMPLETED) long completed);

   @VCloudToken
   @Provides
   String provideVCloudToken(Supplier<VCloudSession> cache) {
      return checkNotNull(cache.get().getVCloudToken(), "No token present in session");
   }

   @Provides
   @Org
   @Singleton
   protected URI provideOrg(@Org Iterable<NamedResource> orgs) {
      return getLast(orgs).getId();
   }

   @Provides
   @Org
   @Singleton
   protected String provideOrgName(@Org Iterable<NamedResource> orgs) {
      return getLast(orgs).getName();
   }

   @Provides
   @Org
   @Singleton
   protected Supplier<Map<String, NamedResource>> provideVDCtoORG(@Named(PROPERTY_SESSION_INTERVAL) long seconds,
         final OrgNameToOrgSupplier supplier) {
      return new RetryOnTimeOutButNotOnAuthorizationExceptionSupplier<Map<String, NamedResource>>(authException,
            seconds, new Supplier<Map<String, NamedResource>>() {
               @Override
               public Map<String, NamedResource> get() {
                  return supplier.get();
               }
            });
   }

   @Provides
   @Singleton
   protected Supplier<Map<URI, ? extends org.jclouds.vcloud.domain.VDC>> provideURIToVDC(
         @Named(PROPERTY_SESSION_INTERVAL) long seconds, final URItoVDC supplier) {
      return new RetryOnTimeOutButNotOnAuthorizationExceptionSupplier<Map<URI, ? extends org.jclouds.vcloud.domain.VDC>>(
            authException, seconds, new Supplier<Map<URI, ? extends org.jclouds.vcloud.domain.VDC>>() {
               @Override
               public Map<URI, ? extends org.jclouds.vcloud.domain.VDC> get() {
                  return supplier.get();
               }
            });
   }

   @Singleton
   public static class URItoVDC implements Supplier<Map<URI, ? extends org.jclouds.vcloud.domain.VDC>> {
      private final Supplier<Map<String, Map<String, ? extends org.jclouds.vcloud.domain.VDC>>> orgVDCMap;

      @Inject
      URItoVDC(Supplier<Map<String, Map<String, ? extends org.jclouds.vcloud.domain.VDC>>> orgVDCMap) {
         this.orgVDCMap = orgVDCMap;
      }

      @Override
      public Map<URI, ? extends org.jclouds.vcloud.domain.VDC> get() {
         return uniqueIndex(
               concat(transform(
                     orgVDCMap.get().values(),
                     new Function<Map<String, ? extends org.jclouds.vcloud.domain.VDC>, Iterable<? extends org.jclouds.vcloud.domain.VDC>>() {

                        @Override
                        public Iterable<? extends org.jclouds.vcloud.domain.VDC> apply(
                              Map<String, ? extends org.jclouds.vcloud.domain.VDC> from) {
                           return from.values();
                        }

                     })), new Function<org.jclouds.vcloud.domain.VDC, URI>() {

                  @Override
                  public URI apply(org.jclouds.vcloud.domain.VDC from) {
                     return from.getId();
                  }

               });
      }

   }

   @Provides
   @Org
   @Singleton
   protected Iterable<NamedResource> provideOrgs(Supplier<VCloudSession> cache, @Named(PROPERTY_IDENTITY) String user) {
      VCloudSession discovery = cache.get();
      checkState(discovery.getOrgs().size() > 0, "No orgs present for user: " + user);
      return discovery.getOrgs().values();
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
                     propagate(e);
                     assert false : e;
                     return null;
                  }
               }

            });
   }

   final static Function<NamedResource, String> name = new Function<NamedResource, String>() {

      @Override
      public String apply(NamedResource from) {
         return from.getName();
      }

   };

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
   @Catalog
   @Singleton
   protected String provideCatalogName(
         Supplier<Map<String, Map<String, ? extends org.jclouds.vcloud.domain.Catalog>>> catalogs) {
      return getLast(getLast(catalogs.get().values()).keySet());
   }

   @Provides
   @Network
   @Singleton
   protected abstract URI provideDefaultNetwork(S client);

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
}
