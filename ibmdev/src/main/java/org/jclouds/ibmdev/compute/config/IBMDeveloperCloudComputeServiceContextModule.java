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
package org.jclouds.ibmdev.compute.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.domain.OsFamily.RHEL;
import static org.jclouds.ibmdev.options.CreateInstanceOptions.Builder.authorizePublicKey;
import static org.jclouds.ibmdev.reference.IBMDeveloperCloudConstants.PROPERTY_IBMDEVELOPERCLOUD_LOCATION;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.LoadBalancerService;
import org.jclouds.compute.config.ComputeServiceTimeoutsModule;
import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.domain.internal.ImageImpl;
import org.jclouds.compute.domain.internal.SizeImpl;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.impl.EncodeTagIntoNameRunNodesAndAddToSetStrategy;
import org.jclouds.compute.util.ComputeUtils;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.ibmdev.IBMDeveloperCloudAsyncClient;
import org.jclouds.ibmdev.IBMDeveloperCloudClient;
import org.jclouds.ibmdev.compute.functions.InstanceToNodeMetadata;
import org.jclouds.ibmdev.domain.Instance;
import org.jclouds.ibmdev.reference.IBMDeveloperCloudConstants;
import org.jclouds.logging.Logger;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Providers;

/**
 * @author Adrian Cole
 */
public class IBMDeveloperCloudComputeServiceContextModule extends AbstractModule {

   @Override
   protected void configure() {
      install(new ComputeServiceTimeoutsModule());
      bind(new TypeLiteral<Function<Instance, NodeMetadata>>() {
      }).to(InstanceToNodeMetadata.class);
      bind(new TypeLiteral<ComputeServiceContext>() {
      })
               .to(
                        new TypeLiteral<ComputeServiceContextImpl<IBMDeveloperCloudClient, IBMDeveloperCloudAsyncClient>>() {
                        }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<RestContext<IBMDeveloperCloudClient, IBMDeveloperCloudAsyncClient>>() {
      })
               .to(
                        new TypeLiteral<RestContextImpl<IBMDeveloperCloudClient, IBMDeveloperCloudAsyncClient>>() {
                        }).in(Scopes.SINGLETON);
      bind(AddNodeWithTagStrategy.class).to(IBMDeveloperCloudAddNodeWithTagStrategy.class);
      bind(ListNodesStrategy.class).to(IBMDeveloperCloudListNodesStrategy.class);
      bind(GetNodeMetadataStrategy.class).to(IBMDeveloperCloudGetNodeMetadataStrategy.class);
      bind(RebootNodeStrategy.class).to(IBMDeveloperCloudRebootNodeStrategy.class);
      bind(DestroyNodeStrategy.class).to(IBMDeveloperCloudDestroyNodeStrategy.class);
      bind(LoadBalancerService.class).toProvider(Providers.<LoadBalancerService> of(null));
   }

   /**
    * tested known configuration
    */
   @Provides
   @Named("DEFAULT")
   protected TemplateBuilder provideTemplate(TemplateBuilder template) {
      return template.osFamily(RHEL);
   }

   @Provides
   @Named("NAMING_CONVENTION")
   @Singleton
   String provideNamingConvention() {
      return "%s-%s";
   }

   @Provides
   @Singleton
   @Named("CREDENTIALS")
   Map<String, String> credentialsMap() {
      return new ConcurrentHashMap<String, String>();
   }

   @Singleton
   public static class CreateKeyPairEncodeTagIntoNameRunNodesAndAddToSet extends
            EncodeTagIntoNameRunNodesAndAddToSetStrategy {
      private final IBMDeveloperCloudClient client;
      private final Map<String, String> credentialsMap;

      @Inject
      protected CreateKeyPairEncodeTagIntoNameRunNodesAndAddToSet(
               AddNodeWithTagStrategy addNodeWithTagStrategy, ListNodesStrategy listNodesStrategy,
               @Named("NAMING_CONVENTION") String nodeNamingConvention, ComputeUtils utils,
               @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor,
               IBMDeveloperCloudClient client,
               @Named("CREDENTIALS") Map<String, String> credentialsMap) {
         super(addNodeWithTagStrategy, listNodesStrategy, nodeNamingConvention, utils, executor);
         this.client = checkNotNull(client, "client");
         this.credentialsMap = checkNotNull(credentialsMap, "credentialsMap");
      }

      @Override
      public Map<?, ListenableFuture<Void>> execute(String tag, int count, Template template,
               Set<NodeMetadata> nodes, Map<NodeMetadata, Exception> badNodes) {
         String key = template.getOptions().getPublicKey();
         if (key != null) {
            template.getOptions().dontAuthorizePublicKey();
            try {
               client.addPublicKey(tag, key);
            } catch (IllegalStateException e) {
               // must not have been found
               client.updatePublicKey(tag, key);
            }
         } else {
            credentialsMap.put(tag, client.generateKeyPair(tag).getKeyMaterial());
         }
         return super.execute(tag, count, template, nodes, badNodes);
      }

   }

   @Singleton
   public static class IBMDeveloperCloudAddNodeWithTagStrategy implements AddNodeWithTagStrategy {

      private final IBMDeveloperCloudClient client;
      private final Function<Instance, NodeMetadata> instanceToNodeMetadata;

      @Inject
      protected IBMDeveloperCloudAddNodeWithTagStrategy(IBMDeveloperCloudClient client,
               Function<Instance, NodeMetadata> instanceToNodeMetadata) {
         this.client = checkNotNull(client, "client");
         this.instanceToNodeMetadata = checkNotNull(instanceToNodeMetadata,
                  "instanceToNodeMetadata");
      }

      @Override
      public NodeMetadata execute(String tag, String name, Template template) {
         Instance instance = client.createInstanceInLocation(template.getLocation().getId(), name,
                  template.getImage().getProviderId(), template.getSize().getProviderId(),
                  authorizePublicKey(tag));
         return instanceToNodeMetadata.apply(client.getInstance(instance.getId()));
      }
   }

   @Singleton
   public static class IBMDeveloperCloudRebootNodeStrategy implements RebootNodeStrategy {

      private final IBMDeveloperCloudClient client;
      private final GetNodeMetadataStrategy getNode;

      @Inject
      protected IBMDeveloperCloudRebootNodeStrategy(IBMDeveloperCloudClient client,
               GetNodeMetadataStrategy getNode) {
         this.client = checkNotNull(client, "client");
         this.getNode = checkNotNull(getNode, "getNode");
      }

      @Override
      public NodeMetadata execute(String id) {
         client.restartInstance(id);
         return getNode.execute(id);
      }
   }

   @Singleton
   @Provides
   Map<Instance.Status, NodeState> provideServerToNodeState() {
      return ImmutableMap.<Instance.Status, NodeState> builder().put(Instance.Status.ACTIVE,
               NodeState.RUNNING)//
               .put(Instance.Status.STOPPED, NodeState.SUSPENDED)//
               .put(Instance.Status.REMOVED, NodeState.TERMINATED)//
               .put(Instance.Status.DEPROVISIONING, NodeState.PENDING)//
               .put(Instance.Status.FAILED, NodeState.ERROR)//
               .put(Instance.Status.NEW, NodeState.PENDING)//
               .put(Instance.Status.PROVISIONING, NodeState.PENDING)//
               .put(Instance.Status.REJECTED, NodeState.ERROR)//
               .put(Instance.Status.RESTARTING, NodeState.PENDING)//
               .put(Instance.Status.STARTING, NodeState.PENDING)//
               .put(Instance.Status.STOPPING, NodeState.PENDING)//
               .put(Instance.Status.UNKNOWN, NodeState.UNKNOWN).build();
   }

   @Singleton
   public static class IBMDeveloperCloudListNodesStrategy implements ListNodesStrategy {
      private final IBMDeveloperCloudClient client;
      private final Function<Instance, NodeMetadata> instanceToNodeMetadata;

      @Inject
      protected IBMDeveloperCloudListNodesStrategy(IBMDeveloperCloudClient client,
               Function<Instance, NodeMetadata> instanceToNodeMetadata) {
         this.client = client;
         this.instanceToNodeMetadata = instanceToNodeMetadata;
      }

      @Override
      public Iterable<? extends ComputeMetadata> list() {
         return listDetailsOnNodesMatching(NodePredicates.all());
      }

      @Override
      public Iterable<? extends NodeMetadata> listDetailsOnNodesMatching(
               Predicate<ComputeMetadata> filter) {
         return Iterables.filter(Iterables
                  .transform(client.listInstances(), instanceToNodeMetadata), filter);
      }
   }

   @Singleton
   public static class IBMDeveloperCloudGetNodeMetadataStrategy implements GetNodeMetadataStrategy {
      private final IBMDeveloperCloudClient client;
      private final Function<Instance, NodeMetadata> instanceToNodeMetadata;

      @Inject
      protected IBMDeveloperCloudGetNodeMetadataStrategy(IBMDeveloperCloudClient client,
               Function<Instance, NodeMetadata> instanceToNodeMetadata) {
         this.client = client;
         this.instanceToNodeMetadata = instanceToNodeMetadata;
      }

      @Override
      public NodeMetadata execute(String id) {
         Instance instance = client.getInstance(checkNotNull(id, "id"));
         return instance == null ? null : instanceToNodeMetadata.apply(instance);
      }
   }

   @Singleton
   public static class IBMDeveloperCloudDestroyNodeStrategy implements DestroyNodeStrategy {
      private final IBMDeveloperCloudClient client;
      private final GetNodeMetadataStrategy getNode;

      @Inject
      protected IBMDeveloperCloudDestroyNodeStrategy(IBMDeveloperCloudClient client,
               GetNodeMetadataStrategy getNode) {
         this.client = checkNotNull(client, "client");
         this.getNode = checkNotNull(getNode, "getNode");
      }

      @Override
      public NodeMetadata execute(String id) {
         client.deleteInstance(id);
         return getNode.execute(id);
      }
   }

   @Provides
   @Singleton
   Location getDefaultLocation(
            @Named(PROPERTY_IBMDEVELOPERCLOUD_LOCATION) final String defaultLocation,
            Set<? extends Location> locations) {
      return Iterables.find(locations, new Predicate<Location>() {

         @Override
         public boolean apply(Location input) {
            return input.getId().equals(defaultLocation);
         }

      });
   }

   @Provides
   @Singleton
   Set<? extends Location> getAssignableLocations(IBMDeveloperCloudClient sync, LogHolder holder,
            @org.jclouds.rest.annotations.Provider String providerName) {
      final Set<Location> assignableLocations = Sets.newHashSet();
      holder.logger.debug(">> providing locations");
      Location parent = new LocationImpl(LocationScope.PROVIDER, providerName, providerName, null);

      for (org.jclouds.ibmdev.domain.Location location : sync.listLocations())
         assignableLocations.add(new LocationImpl(LocationScope.ZONE, location.getId(), location
                  .getName(), parent));

      holder.logger.debug("<< locations(%d)", assignableLocations.size());
      return assignableLocations;
   }

   @Provides
   @Singleton
   protected Set<? extends Size> provideSizes(IBMDeveloperCloudClient sync, LogHolder holder,
            Map<String, ? extends Location> locations) {
      final Set<Size> sizes = Sets.newHashSet();
      holder.logger.debug(">> providing sizes");

      for (org.jclouds.ibmdev.domain.Location location : sync.listLocations()) {
         Location assignedLocation = locations.get(location.getId());
         // TODO we cannot query actual size, yet, so lets make the
         // multipliers work out
         int sizeMultiplier = 1;
         for (String i386 : location.getCapabilities().get(
                  IBMDeveloperCloudConstants.CAPABILITY_I386).keySet())
            sizes.add(buildSize(location, i386, assignedLocation, sizeMultiplier++));
         for (String x86_64 : location.getCapabilities().get(
                  IBMDeveloperCloudConstants.CAPABILITY_x86_64).keySet())
            sizes.add(buildSize(location, x86_64, assignedLocation, sizeMultiplier++));
      }
      holder.logger.debug("<< sizes(%d)", sizes.size());
      return sizes;
   }

   private SizeImpl buildSize(org.jclouds.ibmdev.domain.Location location, final String id,
            Location assignedLocation, int multiplier) {
      return new SizeImpl(id, id, location.getId() + "/" + id, assignedLocation, null, ImmutableMap
               .<String, String> of(), multiplier, multiplier * 1024, multiplier * 10,
               new Predicate<Image>() {
                  @Override
                  public boolean apply(Image input) {
                     if (input instanceof IBMImage)
                        return IBMImage.class.cast(input).rawImage.getSupportedInstanceTypes()
                                 .contains(id);
                     return false;
                  }

               });
   }

   @Provides
   @Singleton
   protected Map<String, ? extends Image> provideImageMap(Set<? extends Image> locations) {
      return Maps.uniqueIndex(locations, new Function<Image, String>() {

         @Override
         public String apply(Image from) {
            return from.getId();
         }

      });
   }

   @Provides
   @Singleton
   protected Map<String, ? extends Location> provideLocationMap(Set<? extends Location> locations) {
      return Maps.uniqueIndex(locations, new Function<Location, String>() {

         @Override
         public String apply(Location from) {
            return from.getId();
         }

      });
   }

   @Provides
   @Singleton
   protected Set<? extends Image> provideImages(final IBMDeveloperCloudClient sync,
            LogHolder holder, Map<String, ? extends Location> locations) {
      final Set<Image> images = Sets.newHashSet();
      holder.logger.debug(">> providing images");

      for (org.jclouds.ibmdev.domain.Image image : sync.listImages())
         images.add(new IBMImage(image, locations.get(image.getLocation())));

      holder.logger.debug("<< images(%d)", images.size());
      return images;
   }

   private static class IBMImage extends ImageImpl {

      /** The serialVersionUID */
      private static final long serialVersionUID = -8520373150950058296L;

      private final org.jclouds.ibmdev.domain.Image rawImage;

      public IBMImage(org.jclouds.ibmdev.domain.Image in, Location location) {
         // TODO parse correct OS
         // TODO manifest fails to parse due to encoding issues in the path
         // TODO get correct default credentials
         // http://www-180.ibm.com/cloud/enterprise/beta/ram/community/_rlvid.jsp.faces?_rap=pc_DiscussionForum.doDiscussionTopic&_rvip=/community/discussionForum.jsp&guid={DA689AEE-783C-6FE7-6F9F-DFEE9763F806}&v=1&submission=false&fid=1068&tid=1527
         super(in.getId(), in.getName(), in.getId(), location, null, ImmutableMap
                  .<String, String> of(), in.getDescription(), in.getCreatedTime().getTime() + "",
                  (in.getPlatform().indexOf("Redhat") != -1) ? OsFamily.RHEL : OsFamily.SUSE, in
                           .getPlatform(),
                  (in.getPlatform().indexOf("32") != -1) ? Architecture.X86_32
                           : Architecture.X86_64, new Credentials("idcuser", null));
         this.rawImage = in;
      }

      @Override
      public boolean equals(Object obj) {
         return rawImage.equals(obj);
      }

      @Override
      public int hashCode() {
         return rawImage.hashCode();
      }

      @Override
      public String toString() {
         return rawImage.toString();
      }

   }

   @Singleton
   private static class LogHolder {
      @Resource
      @Named(ComputeServiceConstants.COMPUTE_LOGGER)
      protected Logger logger = Logger.NULL;
   }
}