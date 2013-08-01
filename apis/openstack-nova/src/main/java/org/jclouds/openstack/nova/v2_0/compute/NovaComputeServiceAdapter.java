/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.openstack.nova.v2_0.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.compute.util.ComputeServiceUtils.metadataAndTagsAsCommaDelimitedValue;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.location.Zone;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.compute.functions.RemoveFloatingIpFromNodeAndDeallocate;
import org.jclouds.openstack.nova.v2_0.compute.options.NovaTemplateOptions;
import org.jclouds.openstack.nova.v2_0.compute.strategy.ApplyNovaTemplateOptionsCreateNodesWithGroupEncodedIntoNameThenAddToSet;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.domain.Image;
import org.jclouds.openstack.nova.v2_0.domain.KeyPair;
import org.jclouds.openstack.nova.v2_0.domain.RebootType;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerCreated;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.FlavorInZone;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ImageInZone;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ServerInZone;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ZoneAndId;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ZoneAndName;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;
import org.jclouds.openstack.nova.v2_0.predicates.ImagePredicates;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * The adapter used by the NovaComputeServiceContextModule to interface the nova-specific domain
 * model to the computeService generic domain model.
 * 
 * @author Matt Stephenson, Adrian Cole
 */
public class NovaComputeServiceAdapter implements
         ComputeServiceAdapter<ServerInZone, FlavorInZone, ImageInZone, Location> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final NovaApi novaApi;
   protected final Supplier<Set<String>> zoneIds;
   protected final RemoveFloatingIpFromNodeAndDeallocate removeFloatingIpFromNodeAndDeallocate;
   protected final LoadingCache<ZoneAndName, KeyPair> keyPairCache;

   @Inject
   public NovaComputeServiceAdapter(NovaApi novaApi, @Zone Supplier<Set<String>> zoneIds,
            RemoveFloatingIpFromNodeAndDeallocate removeFloatingIpFromNodeAndDeallocate,
            LoadingCache<ZoneAndName, KeyPair> keyPairCache) {
      this.novaApi = checkNotNull(novaApi, "novaApi");
      this.zoneIds = checkNotNull(zoneIds, "zoneIds");
      this.removeFloatingIpFromNodeAndDeallocate = checkNotNull(removeFloatingIpFromNodeAndDeallocate,
               "removeFloatingIpFromNodeAndDeallocate");
      this.keyPairCache = checkNotNull(keyPairCache, "keyPairCache"); 
   }

   /**
    * Note that we do not validate extensions here, on basis that
    * {@link ApplyNovaTemplateOptionsCreateNodesWithGroupEncodedIntoNameThenAddToSet} has already
    * done so.
    */
   @Override
   public NodeAndInitialCredentials<ServerInZone> createNodeWithGroupEncodedIntoName(String group, String name,
            Template template) {

      LoginCredentials.Builder credentialsBuilder = LoginCredentials.builder();
      NovaTemplateOptions templateOptions = template.getOptions().as(NovaTemplateOptions.class);

      CreateServerOptions options = new CreateServerOptions();
      options.metadata(metadataAndTagsAsCommaDelimitedValue(template.getOptions()));
      if (templateOptions.getSecurityGroupNames().isPresent())
         options.securityGroupNames(templateOptions.getSecurityGroupNames().get());
      options.userData(templateOptions.getUserData());
      options.diskConfig(templateOptions.getDiskConfig());

      Optional<String> privateKey = Optional.absent();
      if (templateOptions.getKeyPairName() != null) {
         options.keyPairName(templateOptions.getKeyPairName());        
         KeyPair keyPair = keyPairCache.getIfPresent(ZoneAndName.fromZoneAndName(template.getLocation().getId(), templateOptions.getKeyPairName()));
         if (keyPair != null && keyPair.getPrivateKey() != null) {
            privateKey = Optional.of(keyPair.getPrivateKey());
            credentialsBuilder.privateKey(privateKey.get());
         }
      }

      String zoneId = template.getLocation().getId();
      String imageId = template.getImage().getProviderId();
      String flavorId = template.getHardware().getProviderId();

      logger.debug(">> creating new server zone(%s) name(%s) image(%s) flavor(%s) options(%s)", zoneId, name, imageId, flavorId, options);
      ServerCreated lightweightServer = novaApi.getServerApiForZone(zoneId).create(name, imageId, flavorId, options);
      Server server = novaApi.getServerApiForZone(zoneId).get(lightweightServer.getId());

      logger.trace("<< server(%s)", server.getId());

      ServerInZone serverInZone = new ServerInZone(server, zoneId);
      if (!privateKey.isPresent() && lightweightServer.getAdminPass().isPresent())
         credentialsBuilder.password(lightweightServer.getAdminPass().get());
      return new NodeAndInitialCredentials<ServerInZone>(serverInZone, serverInZone.slashEncode(), credentialsBuilder
               .build());
   }

   @Override
   public Iterable<FlavorInZone> listHardwareProfiles() {
      Builder<FlavorInZone> builder = ImmutableSet.builder();
      for (final String zoneId : zoneIds.get()) {
         builder.addAll(transform(novaApi.getFlavorApiForZone(zoneId).listInDetail().concat(),
                  new Function<Flavor, FlavorInZone>() {

                     @Override
                     public FlavorInZone apply(Flavor arg0) {
                        return new FlavorInZone(arg0, zoneId);
                     }

                  }));
      }
      return builder.build();
   }

   @Override
   public Iterable<ImageInZone> listImages() {
      Builder<ImageInZone> builder = ImmutableSet.builder();
      Set<String> zones = zoneIds.get();
      checkState(zones.size() > 0, "no zones found in supplier %s", zoneIds);
      for (final String zoneId : zones) {
         Set<? extends Image> images = novaApi.getImageApiForZone(zoneId).listInDetail().concat().toSet();
         if (images.size() == 0) {
            logger.debug("no images found in zone %s", zoneId);
            continue;
         }
         Iterable<? extends Image> active = filter(images, ImagePredicates.statusEquals(Image.Status.ACTIVE));
         if (images.size() == 0) {
            logger.debug("no images with status active in zone %s; non-active: %s", zoneId,
                     transform(active, new Function<Image, String>() {

                        @Override
                        public String apply(Image input) {
                           return Objects.toStringHelper("").add("id", input.getId()).add("status", input.getStatus())
                                    .toString();
                        }

                     }));
            continue;
         }
         builder.addAll(transform(active, new Function<Image, ImageInZone>() {

            @Override
            public ImageInZone apply(Image arg0) {
               return new ImageInZone(arg0, zoneId);
            }

         }));
      }
      return builder.build();
   }

   @Override
   public Iterable<ServerInZone> listNodes() {
      Builder<ServerInZone> builder = ImmutableSet.builder();
      for (final String zoneId : zoneIds.get()) {
         builder.addAll(novaApi.getServerApiForZone(zoneId).listInDetail().concat()
                  .transform(new Function<Server, ServerInZone>() {

                     @Override
                     public ServerInZone apply(Server arg0) {
                        return new ServerInZone(arg0, zoneId);
                     }

                  }));
      }
      return builder.build();
   }

   @Override
   public Iterable<ServerInZone> listNodesByIds(final Iterable<String> ids) {
      return filter(listNodes(), new Predicate<ServerInZone>() {

            @Override
            public boolean apply(ServerInZone server) {
               return contains(ids, server.slashEncode());
            }
         });
   }

   @Override
   public Iterable<Location> listLocations() {
      // locations provided by keystone
      return ImmutableSet.of();
   }

   @Override
   public ServerInZone getNode(String id) {
      ZoneAndId zoneAndId = ZoneAndId.fromSlashEncoded(id);
      Server server = novaApi.getServerApiForZone(zoneAndId.getZone()).get(zoneAndId.getId());
      return server == null ? null : new ServerInZone(server, zoneAndId.getZone());
   }

   @Override
   public ImageInZone getImage(String id) {
      ZoneAndId zoneAndId = ZoneAndId.fromSlashEncoded(id);
      Image image = novaApi.getImageApiForZone(zoneAndId.getZone()).get(zoneAndId.getId());
      return image == null ? null : new ImageInZone(image, zoneAndId.getZone());
   }

   @Override
   public void destroyNode(String id) {
      ZoneAndId zoneAndId = ZoneAndId.fromSlashEncoded(id);
      if (novaApi.getFloatingIPExtensionForZone(zoneAndId.getZone()).isPresent()) {
         try {
            removeFloatingIpFromNodeAndDeallocate.apply(zoneAndId);
         } catch (RuntimeException e) {
            logger.warn(e, "<< error removing and deallocating ip from node(%s): %s", id, e.getMessage());
         }
      }
      novaApi.getServerApiForZone(zoneAndId.getZone()).delete(zoneAndId.getId());
   }

   @Override
   public void rebootNode(String id) {
      ZoneAndId zoneAndId = ZoneAndId.fromSlashEncoded(id);
      novaApi.getServerApiForZone(zoneAndId.getZone()).reboot(zoneAndId.getId(), RebootType.HARD);
   }

   @Override
   public void resumeNode(String id) {
      ZoneAndId zoneAndId = ZoneAndId.fromSlashEncoded(id);
      if (novaApi.getServerAdminExtensionForZone(zoneAndId.getZone()).isPresent()) {
         novaApi.getServerAdminExtensionForZone(zoneAndId.getZone()).get().resume(zoneAndId.getId());
      }
      throw new UnsupportedOperationException("resume requires installation of the Admin Actions extension");
   }

   @Override
   public void suspendNode(String id) {
      ZoneAndId zoneAndId = ZoneAndId.fromSlashEncoded(id);
      if (novaApi.getServerAdminExtensionForZone(zoneAndId.getZone()).isPresent()) {
         novaApi.getServerAdminExtensionForZone(zoneAndId.getZone()).get().suspend(zoneAndId.getId());
      }
      throw new UnsupportedOperationException("suspend requires installation of the Admin Actions extension");
   }

}
