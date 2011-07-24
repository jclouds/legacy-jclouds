/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.trmk.vcloud_0_8.compute.config;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudAsyncClient;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudClient;
import org.jclouds.trmk.vcloud_0_8.compute.TerremarkVCloudComputeService;
import org.jclouds.trmk.vcloud_0_8.compute.domain.KeyPairCredentials;
import org.jclouds.trmk.vcloud_0_8.compute.domain.OrgAndName;
import org.jclouds.trmk.vcloud_0_8.compute.functions.ImagesInVCloudExpressOrg;
import org.jclouds.trmk.vcloud_0_8.compute.functions.NodeMetadataToOrgAndName;
import org.jclouds.trmk.vcloud_0_8.compute.functions.ParseOsFromVAppTemplateName;
import org.jclouds.trmk.vcloud_0_8.compute.functions.VAppToNodeMetadata;
import org.jclouds.trmk.vcloud_0_8.compute.options.TerremarkVCloudTemplateOptions;
import org.jclouds.trmk.vcloud_0_8.compute.strategy.ParseVAppTemplateDescriptionToGetDefaultLoginCredentials;
import org.jclouds.trmk.vcloud_0_8.domain.Org;
import org.jclouds.trmk.vcloud_0_8.domain.Status;
import org.jclouds.trmk.vcloud_0_8.domain.VApp;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * Configures the {@link TerremarkVCloudComputeServiceContext}; requires
 * {@link TerremarkVCloudComputeClientImpl} bound.
 * 
 * @author Adrian Cole
 */
public class TerremarkVCloudComputeServiceContextModule extends BaseComputeServiceContextModule {

   @VisibleForTesting
   public static final Map<Status, NodeState> VAPPSTATUS_TO_NODESTATE = ImmutableMap.<Status, NodeState> builder()
         .put(Status.OFF, NodeState.SUSPENDED).put(Status.ON, NodeState.RUNNING)
         .put(Status.RESOLVED, NodeState.PENDING).put(Status.UNRECOGNIZED, NodeState.UNRECOGNIZED)
         .put(Status.DEPLOYED, NodeState.PENDING).put(Status.SUSPENDED, NodeState.SUSPENDED)
         .put(Status.UNRESOLVED, NodeState.PENDING).build();

   @Singleton
   @Provides
   protected Map<Status, NodeState> provideVAppStatusToNodeState() {
      return VAPPSTATUS_TO_NODESTATE;
   }

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<Function<NodeMetadata, OrgAndName>>() {
      }).to(new TypeLiteral<NodeMetadataToOrgAndName>() {
      });
      bind(TemplateOptions.class).to(TerremarkVCloudTemplateOptions.class);
      bind(ComputeService.class).to(TerremarkVCloudComputeService.class);
      bind(PopulateDefaultLoginCredentialsForImageStrategy.class).to(
            ParseVAppTemplateDescriptionToGetDefaultLoginCredentials.class);
      bind(SecureRandom.class).toInstance(new SecureRandom());
      install(new TerremarkBindComputeStrategiesByClass());
      install(new TerremarkBindComputeSuppliersByClass());
      bindVAppConverter();
      bind(new TypeLiteral<ComputeServiceContext>() {
      }).to(new TypeLiteral<ComputeServiceContextImpl<TerremarkVCloudClient, TerremarkVCloudAsyncClient>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<RestContext<TerremarkVCloudClient, TerremarkVCloudAsyncClient>>() {
      }).to(new TypeLiteral<RestContextImpl<TerremarkVCloudClient, TerremarkVCloudAsyncClient>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<Function<Org, Iterable<? extends Image>>>() {
      }).to(new TypeLiteral<ImagesInVCloudExpressOrg>() {
      });
      bind(new TypeLiteral<Function<String, OperatingSystem>>() {
      }).to(ParseOsFromVAppTemplateName.class);
   }

   protected void bindVAppConverter() {
      bind(new TypeLiteral<Function<VApp, NodeMetadata>>() {
      }).to(VAppToNodeMetadata.class);
   }

   @Provides
   @Singleton
   Supplier<String> provideSuffix(final SecureRandom random) {
      return new Supplier<String>() {
         @Override
         public String get() {
            return random.nextInt(4096) + "";
         }
      };

   }

   @Provides
   @Singleton
   ConcurrentMap<OrgAndName, KeyPairCredentials> credentialsMap() {
      return new ConcurrentHashMap<OrgAndName, KeyPairCredentials>();
   }

   @Named("PASSWORD")
   @Provides
   String providePassword(SecureRandom random) {
      return random.nextLong() + "";
   }

}
