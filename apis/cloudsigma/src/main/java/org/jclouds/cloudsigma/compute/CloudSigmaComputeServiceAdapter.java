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
package org.jclouds.cloudsigma.compute;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Iterables.filter;
import static org.jclouds.concurrent.FutureIterables.transformParallel;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.cloudsigma.CloudSigmaClient;
import org.jclouds.cloudsigma.compute.options.CloudSigmaTemplateOptions;
import org.jclouds.cloudsigma.domain.AffinityType;
import org.jclouds.cloudsigma.domain.Device;
import org.jclouds.cloudsigma.domain.DriveInfo;
import org.jclouds.cloudsigma.domain.DriveType;
import org.jclouds.cloudsigma.domain.Server;
import org.jclouds.cloudsigma.domain.ServerInfo;
import org.jclouds.cloudsigma.options.CloneDriveOptions;
import org.jclouds.cloudsigma.reference.CloudSigmaConstants;
import org.jclouds.cloudsigma.util.Servers;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * defines the connection between the {@link CloudSigmaClient} implementation
 * and the jclouds {@link ComputeService}
 *
 */
@Singleton
public class CloudSigmaComputeServiceAdapter implements
      ComputeServiceAdapter<ServerInfo, Hardware, DriveInfo, Location> {
   private static final Predicate<DriveInfo> PREINSTALLED_DISK = Predicates.and(Predicates.notNull(),
         new Predicate<DriveInfo>() {

            @Override
            public boolean apply(DriveInfo drive) {
               return drive.getType().equals(DriveType.DISK) && drive.getDriveType().contains("preinstalled");
            }

         });
   private final CloudSigmaClient client;
   private final Predicate<DriveInfo> driveNotClaimed;
   private final String defaultVncPassword;
   private final LoadingCache<String, DriveInfo> cache;
   private final ListeningExecutorService userExecutor;

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   @Inject
   public CloudSigmaComputeServiceAdapter(CloudSigmaClient client, Predicate<DriveInfo> driveNotClaimed,
         @Named(CloudSigmaConstants.PROPERTY_VNC_PASSWORD) String defaultVncPassword,
         LoadingCache<String, DriveInfo> cache, @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor) {
      this.client = checkNotNull(client, "client");
      this.driveNotClaimed = checkNotNull(driveNotClaimed, "driveNotClaimed");
      this.defaultVncPassword = checkNotNull(defaultVncPassword, "defaultVncPassword");
      checkArgument(defaultVncPassword.length() <= 8, "vnc passwords should be less that 8 characters!"); 
      this.cache = checkNotNull(cache, "cache");
      this.userExecutor = checkNotNull(userExecutor, "userExecutor");
   }

   @Override
   public NodeAndInitialCredentials<ServerInfo> createNodeWithGroupEncodedIntoName(String tag, String name, Template template) {
      long bootSize = (long) (template.getHardware().getVolumes().get(0).getSize() * 1024 * 1024 * 1024l);
      AffinityType affinityType = AffinityType.HDD;
      if (template.getOptions() instanceof CloudSigmaTemplateOptions) {
         CloudSigmaTemplateOptions options = CloudSigmaTemplateOptions.class.cast(template.getOptions());
         affinityType = options.getDiskDriveAffinity();
      }
      logger.debug(">> imaging boot drive source(%s) bytes(%d) affinityType(%s)",
         template.getImage().getId(), bootSize, affinityType);
      DriveInfo drive = client.cloneDrive(template.getImage().getId(), template.getImage().getId(),
         new CloneDriveOptions().size(bootSize).affinity(affinityType));
      boolean success = driveNotClaimed.apply(drive);
      logger.debug("<< image(%s) complete(%s)", drive.getUuid(), success);
      if (!success) {
         client.destroyDrive(drive.getUuid());
         throw new IllegalStateException("could not image drive in time!");
      }

      Server toCreate = Servers.small(name, drive.getUuid(), defaultVncPassword).mem(template.getHardware().getRam())
            .cpu((int) (template.getHardware().getProcessors().get(0).getSpeed())).build();

      logger.debug(">> creating server");
      ServerInfo from = client.createServer(toCreate);
      logger.debug("<< created server(%s)", from.getUuid());
      logger.debug(">> starting server(%s)", from.getUuid());
      client.startServer(from.getUuid());
      return new NodeAndInitialCredentials<ServerInfo>(from, from.getUuid(), LoginCredentials.builder()
            .password(defaultVncPassword).authenticateSudo(true).build());
   }

   @Override
   public Iterable<Hardware> listHardwareProfiles() {
      Builder<Hardware> hardware = ImmutableSet.builder();
      for (double cpu : new double[] { 1000, 5000, 10000, 20000 })
         for (int ram : new int[] { 512, 1024, 4 * 1024, 16 * 1024, 32 * 1024 }) {
            final float size = (float) cpu / 100;
            String id = String.format("cpu=%f,ram=%s,disk=%f", cpu, ram, size);
            hardware.add(new HardwareBuilder().supportsImage(new Predicate<Image>() {

               @Override
               public boolean apply(Image input) {
                  String toParse = input.getUserMetadata().get("size");
                  return toParse != null && new Float(toParse) <= size;
               }

               @Override
               public String toString() {
                  return "sizeLessThanOrEqual(" + size + ")";
               }

            }).ids(id).ram(ram).processors(ImmutableList.of(new Processor(1, cpu))).hypervisor("kvm")
                  .volumes(ImmutableList.<Volume>of(new VolumeImpl(size, true, true))).build());
         }
      return hardware.build();
   }

   /**
    * look up the current standard images and do not error out, if they are not
    * found.
    */
   @Override
   public Iterable<DriveInfo> listImages() {
      return FluentIterable.from(transformParallel(client.listStandardDrives(),
            new Function<String, ListenableFuture<? extends DriveInfo>>() {

               @Override
               public ListenableFuture<DriveInfo> apply(String input) {
                  try {
                     return Futures.immediateFuture(cache.getUnchecked(input));
                  } catch (CacheLoader.InvalidCacheLoadException e) {
                     logger.debug("drive %s not found", input);
                  } catch (UncheckedExecutionException e) {
                     logger.warn(e, "error finding drive %s: %s", input, e.getMessage());
                  }
                  return Futures.immediateFuture(null);
               }

               @Override
               public String toString() {
                  return "seedDriveCache()";
               }
            }, userExecutor, null, logger, "drives")).filter(PREINSTALLED_DISK);
   }

   @SuppressWarnings("unchecked")
   @Override
   public Iterable<ServerInfo> listNodes() {
      return (Iterable<ServerInfo>) client.listServerInfo();
   }
   
   @Override
   public Iterable<ServerInfo> listNodesByIds(final Iterable<String> ids) {
      return filter(listNodes(), new Predicate<ServerInfo>() {

            @Override
            public boolean apply(ServerInfo server) {
               return contains(ids, server.getUuid());
            }
         });
   }

   @Override
   public Iterable<Location> listLocations() {
      // Not using the adapter to determine locations
      return ImmutableSet.<Location>of();
   }

   @Override
   public ServerInfo getNode(String id) {
      return client.getServerInfo(id);
   }
   
   @Override
   public DriveInfo getImage(String id) {
      return client.getDriveInfo(id);
   }
   
   @Override
   public void destroyNode(String id) {
      ServerInfo server = getNode(id);
      if (server != null) {
         client.stopServer(id);
         client.destroyServer(id);
         for (Device dev : server.getDevices().values())
            client.destroyDrive(dev.getDriveUuid());
      }
   }

   @Override
   public void rebootNode(String id) {
      client.resetServer(id);
   }

   @Override
   public void resumeNode(String id) {
      client.startServer(id);

   }

   @Override
   public void suspendNode(String id) {
      client.stopServer(id);
   }
}
