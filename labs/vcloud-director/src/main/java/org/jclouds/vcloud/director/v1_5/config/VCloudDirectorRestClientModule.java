/**
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.director.v1_5.config;

import static com.google.common.base.Throwables.propagate;
import static org.jclouds.rest.config.BinderUtils.bindClientAndAsyncClient;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.jclouds.Constants;
import org.jclouds.concurrent.RetryOnTimeOutExceptionFunction;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.location.Provider;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorAsyncClient;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorClient;
import org.jclouds.vcloud.director.v1_5.annotations.Login;
import org.jclouds.vcloud.director.v1_5.domain.Session;
import org.jclouds.vcloud.director.v1_5.domain.SessionWithToken;
import org.jclouds.vcloud.director.v1_5.features.AdminCatalogAsyncClient;
import org.jclouds.vcloud.director.v1_5.features.AdminCatalogClient;
import org.jclouds.vcloud.director.v1_5.features.AdminOrgAsyncClient;
import org.jclouds.vcloud.director.v1_5.features.AdminOrgClient;
import org.jclouds.vcloud.director.v1_5.features.AdminVdcAsyncClient;
import org.jclouds.vcloud.director.v1_5.features.AdminVdcClient;
import org.jclouds.vcloud.director.v1_5.features.CatalogAsyncClient;
import org.jclouds.vcloud.director.v1_5.features.CatalogClient;
import org.jclouds.vcloud.director.v1_5.features.GroupAsyncClient;
import org.jclouds.vcloud.director.v1_5.features.GroupClient;
import org.jclouds.vcloud.director.v1_5.features.MediaAsyncClient;
import org.jclouds.vcloud.director.v1_5.features.MediaClient;
import org.jclouds.vcloud.director.v1_5.features.MetadataAsyncClient;
import org.jclouds.vcloud.director.v1_5.features.MetadataClient;
import org.jclouds.vcloud.director.v1_5.features.NetworkAsyncClient;
import org.jclouds.vcloud.director.v1_5.features.NetworkClient;
import org.jclouds.vcloud.director.v1_5.features.OrgAsyncClient;
import org.jclouds.vcloud.director.v1_5.features.OrgClient;
import org.jclouds.vcloud.director.v1_5.features.QueryAsyncClient;
import org.jclouds.vcloud.director.v1_5.features.QueryClient;
import org.jclouds.vcloud.director.v1_5.features.TaskAsyncClient;
import org.jclouds.vcloud.director.v1_5.features.TaskClient;
import org.jclouds.vcloud.director.v1_5.features.UploadAsyncClient;
import org.jclouds.vcloud.director.v1_5.features.UploadClient;
import org.jclouds.vcloud.director.v1_5.features.UserAsyncClient;
import org.jclouds.vcloud.director.v1_5.features.UserClient;
import org.jclouds.vcloud.director.v1_5.features.VAppAsyncClient;
import org.jclouds.vcloud.director.v1_5.features.VAppClient;
import org.jclouds.vcloud.director.v1_5.features.VAppTemplateAsyncClient;
import org.jclouds.vcloud.director.v1_5.features.VAppTemplateClient;
import org.jclouds.vcloud.director.v1_5.features.VdcAsyncClient;
import org.jclouds.vcloud.director.v1_5.features.VdcClient;
import org.jclouds.vcloud.director.v1_5.functions.LoginUserInOrgWithPassword;
import org.jclouds.vcloud.director.v1_5.handlers.InvalidateSessionAndRetryOn401AndLogoutOnClose;
import org.jclouds.vcloud.director.v1_5.handlers.VCloudDirectorErrorHandler;
import org.jclouds.vcloud.director.v1_5.login.SessionAsyncClient;
import org.jclouds.vcloud.director.v1_5.login.SessionClient;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * Configures the VCloudDirector connection.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class VCloudDirectorRestClientModule extends RestClientModule<VCloudDirectorClient, VCloudDirectorAsyncClient> {

   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>>builder()
            .put(AdminCatalogClient.class, AdminCatalogAsyncClient.class)
            .put(AdminOrgClient.class, AdminOrgAsyncClient.class)
            .put(AdminVdcClient.class, AdminVdcAsyncClient.class)
            .put(CatalogClient.class, CatalogAsyncClient.class)
            .put(NetworkClient.class, NetworkAsyncClient.class)
            .put(OrgClient.class, OrgAsyncClient.class)
            .put(QueryClient.class, QueryAsyncClient.class)
            .put(MediaClient.class, MediaAsyncClient.class)
            .put(TaskClient.class, TaskAsyncClient.class)
            .put(VdcClient.class, VdcAsyncClient.class)
            .put(VAppClient.class, VAppAsyncClient.class)
            .put(VAppTemplateClient.class, VAppTemplateAsyncClient.class)
            .put(UploadClient.class, UploadAsyncClient.class)
            .put(MetadataClient.Readable.class, MetadataAsyncClient.Readable.class)
            .put(MetadataClient.Writeable.class, MetadataAsyncClient.Writable.class)
            .put(GroupClient.class, GroupAsyncClient.class)
            .put(UserClient.class, UserAsyncClient.class)
            .put(AdminNetworkClient.class, AdminNetworkAsyncClient.class)
            .build();

   public VCloudDirectorRestClientModule() {
      super(VCloudDirectorClient.class, VCloudDirectorAsyncClient.class, DELEGATE_MAP);
   }

   @Override
   protected void configure() {
      // Bind clients that are used directly in Functions, Predicates and other circumstances
      bindClientAndAsyncClient(binder(), SessionClient.class, SessionAsyncClient.class);
      bindClientAndAsyncClient(binder(), OrgClient.class, OrgAsyncClient.class);
      bindClientAndAsyncClient(binder(), TaskClient.class, TaskAsyncClient.class);
      bindClientAndAsyncClient(binder(), VAppClient.class, VAppAsyncClient.class);

      bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(InvalidateSessionAndRetryOn401AndLogoutOnClose.class);

      super.configure();
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(VCloudDirectorErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(VCloudDirectorErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(VCloudDirectorErrorHandler.class);
   }

   @Provides
   @Login
   protected Supplier<URI> loginUrl(@Provider Supplier<URI> provider) {
      // TODO: technically, we should implement version client, but this will work
      return Suppliers.compose(new Function<URI, URI>() {

         @Override
         public URI apply(URI arg0) {
            return URI.create(arg0.toASCIIString() + "/sessions");
         }

      }, provider);
   }

   @Provides
   protected Supplier<Session> currentSession(Supplier<SessionWithToken> in) {
      return Suppliers.compose(new Function<SessionWithToken, Session>() {

         @Override
         public Session apply(SessionWithToken arg0) {
            return arg0.getSession();
         }

      }, in);

   }

   @Provides
   @Singleton
   @org.jclouds.vcloud.director.v1_5.annotations.Session
   protected Supplier<String> sessionToken(Supplier<SessionWithToken> in) {
      return Suppliers.compose(new Function<SessionWithToken, String>() {

         @Override
         public String apply(SessionWithToken arg0) {
            return arg0.getToken();
         }

      }, in);

   }

   @Provides
   @Singleton
   protected Function<Credentials, SessionWithToken> makeSureFilterRetriesOnTimeout(
            LoginUserInOrgWithPassword loginWithPasswordCredentials) {
      // we should retry on timeout exception logging in.
      return new RetryOnTimeOutExceptionFunction<Credentials, SessionWithToken>(loginWithPasswordCredentials);
   }

   @Provides
   @Singleton
   public LoadingCache<Credentials, SessionWithToken> provideSessionWithTokenCache(
            Function<Credentials, SessionWithToken> getSessionWithToken,
            @Named(Constants.PROPERTY_SESSION_INTERVAL) int seconds) {
      return CacheBuilder.newBuilder().expireAfterWrite(seconds, TimeUnit.SECONDS).build(
               CacheLoader.from(getSessionWithToken));
   }

   // Temporary conversion of a cache to a supplier until there is a single-element cache
   // http://code.google.com/p/guava-libraries/issues/detail?id=872
   @Provides
   @Singleton
   protected Supplier<SessionWithToken> provideSessionWithTokenSupplier(
            final LoadingCache<Credentials, SessionWithToken> cache, @Provider final Credentials creds) {
      return new Supplier<SessionWithToken>() {
         @Override
         public SessionWithToken get() {
            try {
               return cache.get(creds);
            } catch (ExecutionException e) {
               throw propagate(e.getCause());
            }
         }
      };
   }
}
