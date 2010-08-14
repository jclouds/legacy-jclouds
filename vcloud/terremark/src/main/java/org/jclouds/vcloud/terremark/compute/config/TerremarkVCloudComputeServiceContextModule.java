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

package org.jclouds.vcloud.terremark.compute.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions.Builder.processorCount;

import java.net.URI;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.config.ComputeServiceTimeoutsModule;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.RunNodesAndAddToSetStrategy;
import org.jclouds.compute.strategy.impl.EncodeTagIntoNameRunNodesAndAddToSetStrategy;
import org.jclouds.vcloud.VCloudAsyncClient;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.compute.VCloudComputeClient;
import org.jclouds.vcloud.compute.config.VCloudComputeServiceContextModule;
import org.jclouds.vcloud.compute.strategy.VCloudDestroyNodeStrategy;
import org.jclouds.vcloud.compute.strategy.VCloudListNodesStrategy;
import org.jclouds.vcloud.compute.strategy.VCloudRebootNodeStrategy;
import org.jclouds.vcloud.terremark.TerremarkVCloudClient;
import org.jclouds.vcloud.terremark.compute.TerremarkVCloudComputeClient;
import org.jclouds.vcloud.terremark.compute.TerremarkVCloudComputeService;
import org.jclouds.vcloud.terremark.compute.config.providers.VAppTemplatesInOrgs;
import org.jclouds.vcloud.terremark.compute.domain.OrgAndName;
import org.jclouds.vcloud.terremark.compute.functions.NodeMetadataToOrgAndName;
import org.jclouds.vcloud.terremark.compute.options.TerremarkVCloudTemplateOptions;
import org.jclouds.vcloud.terremark.compute.strategy.ParseVAppTemplateDescriptionToGetDefaultLoginCredentials;
import org.jclouds.vcloud.terremark.compute.strategy.TerremarkVCloudGetNodeMetadataStrategy;
import org.jclouds.vcloud.terremark.domain.KeyPair;
import org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * Configures the {@link TerremarkVCloudComputeServiceContext}; requires
 * {@link TerremarkVCloudComputeClientImpl} bound.
 * 
 * @author Adrian Cole
 */
public class TerremarkVCloudComputeServiceContextModule extends VCloudComputeServiceContextModule {

   @Provides
   @Singleton
   Supplier<String> provideSuffix(final SecureRandom random) {
      return new Supplier<String>() {
         @Override
         public String get() {
            return random.nextInt(100) + "";
         }
      };

   }

   @Singleton
   public static class TerremarkVCloudAddNodeWithTagStrategy implements AddNodeWithTagStrategy {
      protected final TerremarkVCloudClient client;
      protected final TerremarkVCloudComputeClient computeClient;
      protected final GetNodeMetadataStrategy getNode;
      protected final TemplateToInstantiateOptions getOptions;

      @Inject
      protected TerremarkVCloudAddNodeWithTagStrategy(TerremarkVCloudClient client,
            TerremarkVCloudComputeClient computeClient, GetNodeMetadataStrategy getNode,
            TemplateToInstantiateOptions getOptions) {
         this.client = client;
         this.computeClient = computeClient;
         this.getNode = getNode;
         this.getOptions = checkNotNull(getOptions, "getOptions");
      }

      @Override
      public NodeMetadata execute(String tag, String name, Template template) {
         TerremarkInstantiateVAppTemplateOptions options = getOptions.apply(template);
         Map<String, String> metaMap = computeClient.start(URI.create(template.getLocation().getId()), URI
               .create(template.getImage().getId()), name, options, template.getOptions().getInboundPorts());
         return getNode.execute(metaMap.get("id"));
      }

   }

   @Singleton
   public static class TemplateToInstantiateOptions implements
         Function<Template, TerremarkInstantiateVAppTemplateOptions> {

      @Override
      public TerremarkInstantiateVAppTemplateOptions apply(Template from) {
         TerremarkInstantiateVAppTemplateOptions options = processorCount(
               Double.valueOf(from.getSize().getCores()).intValue()).memory(from.getSize().getRam());
         if (!from.getOptions().shouldBlockUntilRunning())
            options.blockOnDeploy(false);
         String sshKeyFingerprint = TerremarkVCloudTemplateOptions.class.cast(from.getOptions()).getSshKeyFingerprint();
         if (sshKeyFingerprint != null)
            options.sshKeyFingerprint(sshKeyFingerprint);

         return options;
      }
   }

   @Override
   protected void configure() {
      install(new ComputeServiceTimeoutsModule());
      // NOTE
      bind(AddNodeWithTagStrategy.class).to(TerremarkVCloudAddNodeWithTagStrategy.class);
      bind(new TypeLiteral<ComputeServiceContext>() {
      }).to(new TypeLiteral<ComputeServiceContextImpl<VCloudClient, VCloudAsyncClient>>() {
      }).in(Scopes.SINGLETON);
      // NOTE
      bind(RunNodesAndAddToSetStrategy.class).to(EncodeTagIntoNameRunNodesAndAddToSetStrategy.class);
      bind(ListNodesStrategy.class).to(VCloudListNodesStrategy.class);
      // NOTE
      bind(GetNodeMetadataStrategy.class).to(TerremarkVCloudGetNodeMetadataStrategy.class);
      bind(RebootNodeStrategy.class).to(VCloudRebootNodeStrategy.class);
      bind(DestroyNodeStrategy.class).to(VCloudDestroyNodeStrategy.class);
      bindLoadBalancer();
      bindSizes();
      bindImages();
      bindLocations();
      // MORE specifics...
      bind(new TypeLiteral<Function<NodeMetadata, OrgAndName>>() {
      }).to(new TypeLiteral<NodeMetadataToOrgAndName>() {
      });
      bind(TemplateOptions.class).to(TerremarkVCloudTemplateOptions.class);
      bind(ComputeService.class).to(TerremarkVCloudComputeService.class);
      bind(VCloudComputeClient.class).to(TerremarkVCloudComputeClient.class);
      bind(PopulateDefaultLoginCredentialsForImageStrategy.class).to(
            ParseVAppTemplateDescriptionToGetDefaultLoginCredentials.class);
      bind(SecureRandom.class).toInstance(new SecureRandom());

   }

   @Provides
   @Singleton
   ConcurrentMap<OrgAndName, KeyPair> credentialsMap() {
      return new ConcurrentHashMap<OrgAndName, KeyPair>();
   }

   // TODO
   // @Override
   // protected void bindLoadBalancer() {
   // bind(LoadBalanceNodesStrategy.class).to(TerremarkLoadBalanceNodesStrategy.class);
   // bind(DestroyLoadBalancerStrategy.class).to(TerremarkDestroyLoadBalancerStrategy.class);
   // }
   //   

   @Named("PASSWORD")
   @Provides
   String providePassword(SecureRandom random) {
      return random.nextLong() + "";
   }

   @Override
   protected TemplateBuilder provideTemplate(TemplateBuilder template) {
      return template.osFamily(OsFamily.UBUNTU);
   }

   @Override
   protected void bindImages() {
      bind(new TypeLiteral<Set<? extends Image>>() {
      }).toProvider(VAppTemplatesInOrgs.class).in(Scopes.SINGLETON);
   }

}
