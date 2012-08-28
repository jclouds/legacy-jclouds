/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
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
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.location.Provider;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.config.BinderUtils;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.rest.internal.RestContextImpl;
import org.jclouds.util.Suppliers2;
import org.jclouds.vcloud.director.v1_5.admin.VCloudDirectorAdminApi;
import org.jclouds.vcloud.director.v1_5.admin.VCloudDirectorAdminAsyncApi;
import org.jclouds.vcloud.director.v1_5.annotations.Login;
import org.jclouds.vcloud.director.v1_5.domain.Entity;
import org.jclouds.vcloud.director.v1_5.domain.Session;
import org.jclouds.vcloud.director.v1_5.domain.SessionWithToken;
import org.jclouds.vcloud.director.v1_5.features.CatalogApi;
import org.jclouds.vcloud.director.v1_5.features.CatalogAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.MediaApi;
import org.jclouds.vcloud.director.v1_5.features.MediaAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.MetadataApi;
import org.jclouds.vcloud.director.v1_5.features.MetadataAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.NetworkApi;
import org.jclouds.vcloud.director.v1_5.features.NetworkAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.OrgApi;
import org.jclouds.vcloud.director.v1_5.features.OrgAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.QueryApi;
import org.jclouds.vcloud.director.v1_5.features.QueryAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.TaskApi;
import org.jclouds.vcloud.director.v1_5.features.TaskAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.UploadApi;
import org.jclouds.vcloud.director.v1_5.features.UploadAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.VAppApi;
import org.jclouds.vcloud.director.v1_5.features.VAppAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.VAppTemplateApi;
import org.jclouds.vcloud.director.v1_5.features.VAppTemplateAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.VdcApi;
import org.jclouds.vcloud.director.v1_5.features.VdcAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.VmApi;
import org.jclouds.vcloud.director.v1_5.features.VmAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.admin.AdminCatalogApi;
import org.jclouds.vcloud.director.v1_5.features.admin.AdminCatalogAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.admin.AdminNetworkApi;
import org.jclouds.vcloud.director.v1_5.features.admin.AdminNetworkAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.admin.AdminOrgApi;
import org.jclouds.vcloud.director.v1_5.features.admin.AdminOrgAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.admin.AdminQueryApi;
import org.jclouds.vcloud.director.v1_5.features.admin.AdminQueryAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.admin.AdminVdcApi;
import org.jclouds.vcloud.director.v1_5.features.admin.AdminVdcAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.admin.GroupApi;
import org.jclouds.vcloud.director.v1_5.features.admin.GroupAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.admin.UserApi;
import org.jclouds.vcloud.director.v1_5.features.admin.UserAsyncApi;
import org.jclouds.vcloud.director.v1_5.functions.LoginUserInOrgWithPassword;
import org.jclouds.vcloud.director.v1_5.functions.href.ResolveEntity;
import org.jclouds.vcloud.director.v1_5.handlers.InvalidateSessionAndRetryOn401AndLogoutOnClose;
import org.jclouds.vcloud.director.v1_5.handlers.VCloudDirectorErrorHandler;
import org.jclouds.vcloud.director.v1_5.login.SessionApi;
import org.jclouds.vcloud.director.v1_5.login.SessionAsyncApi;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorApi;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorAsyncApi;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;

/**
 * Configures the cloudstack connection.
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class VCloudDirectorRestClientModule extends RestClientModule<VCloudDirectorApi, VCloudDirectorAsyncApi> {
   
   public static final Map<Class<?>, Class<?>> USER_DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>>builder()
         .put(CatalogApi.class, CatalogAsyncApi.class)
         .put(MediaApi.class, MediaAsyncApi.class)
         .put(MetadataApi.Readable.class, MetadataAsyncApi.Readable.class)
         .put(MetadataApi.Writeable.class, MetadataAsyncApi.Writeable.class)
         .put(NetworkApi.class, NetworkAsyncApi.class)
         .put(OrgApi.class, OrgAsyncApi.class)
         .put(QueryApi.class, QueryAsyncApi.class)
         .put(TaskApi.class, TaskAsyncApi.class)
         .put(UploadApi.class, UploadAsyncApi.class)
         .put(VAppApi.class, VAppAsyncApi.class)
         .put(VAppTemplateApi.class, VAppTemplateAsyncApi.class)
         .put(VdcApi.class, VdcAsyncApi.class)
         .put(VmApi.class, VmAsyncApi.class)
         .build();
   
   public static final Map<Class<?>, Class<?>> ADMIN_DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>>builder()
         .putAll(USER_DELEGATE_MAP)
         .put(AdminCatalogApi.class, AdminCatalogAsyncApi.class)
         .put(AdminNetworkApi.class, AdminNetworkAsyncApi.class)
         .put(AdminOrgApi.class, AdminOrgAsyncApi.class)
         .put(AdminQueryApi.class, AdminQueryAsyncApi.class)
         .put(AdminVdcApi.class, AdminVdcAsyncApi.class)
         .put(GroupApi.class, GroupAsyncApi.class)
         .put(UserApi.class, UserAsyncApi.class)
         .build();
   
   @Override
   protected void bindAsyncClient() {
      // bind the user api (default)
      super.bindAsyncClient();
      // bind the admin api
      BinderUtils.bindAsyncClient(binder(), VCloudDirectorAdminAsyncApi.class);
   }
   
   @Override
   protected void bindClient() {
      // bind the user api (default)
      super.bindClient();
      // bind the admin api
      BinderUtils.bindClient(binder(), VCloudDirectorAdminApi.class, VCloudDirectorAdminAsyncApi.class, ADMIN_DELEGATE_MAP);
   }
   
   public VCloudDirectorRestClientModule() {
      super(ADMIN_DELEGATE_MAP);
   }
   
   @Override
   protected void configure() {
      bind(new TypeLiteral<RestContext<VCloudDirectorAdminApi, VCloudDirectorAdminAsyncApi>>() {
      }).to(new TypeLiteral<RestContextImpl<VCloudDirectorAdminApi, VCloudDirectorAdminAsyncApi>>() {
      });
      
      // Bind apis that are used directly in Functions, Predicates and other circumstances
      bindClientAndAsyncClient(binder(), OrgApi.class, OrgAsyncApi.class);
      bindClientAndAsyncClient(binder(), SessionApi.class, SessionAsyncApi.class);
      bindClientAndAsyncClient(binder(), TaskApi.class, TaskAsyncApi.class);
      bindClientAndAsyncClient(binder(), VAppApi.class, VAppAsyncApi.class);
      bindClientAndAsyncClient(binder(), VmApi.class, VmAsyncApi.class);
      
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
      // TODO: technically, we should implement version api, but this will work
      return Suppliers2.compose(new Function<URI, URI>() {
         
         @Override
         public URI apply(URI arg0) {
            return URI.create(arg0.toASCIIString() + "/sessions");
         }
         
      }, provider);
   }
   
   @Provides
   protected Supplier<Session> currentSession(Supplier<SessionWithToken> in) {
      return Suppliers2.compose(new Function<SessionWithToken, Session>() {
         
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
      return Suppliers2.compose(new Function<SessionWithToken, String>() {
         
         @Override
         public String apply(SessionWithToken arg0) {
            return arg0.getToken();
         }
         
      }, in);
      
   }
   
   @Provides
   @Singleton
   protected Function<String, Entity> makeSureResolveEntityRetriesOnTimeout(ResolveEntity resolveEntity) {
      // we should retry on timeout exception logging in.
      return new RetryOnTimeOutExceptionFunction<String, Entity>(resolveEntity);
   }

   @Provides
   @Singleton
   public LoadingCache<String, Entity> resolveEntityCache(Function<String, Entity> getEntity,
            @Named(Constants.PROPERTY_SESSION_INTERVAL) int seconds) {
      return CacheBuilder.newBuilder().expireAfterWrite(seconds, TimeUnit.SECONDS).build(CacheLoader.from(getEntity));
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
