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

package org.jclouds.cloudsigma.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static org.jclouds.concurrent.FutureIterables.transformParallel;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.cloudsigma.CloudSigmaAsyncClient;
import org.jclouds.cloudsigma.CloudSigmaClient;
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
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.location.suppliers.JustProvider;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * defines the connection between the {@link CloudSigmaClient} implementation and the jclouds
 * {@link ComputeService}
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
   private final CloudSigmaAsyncClient aclient;
   private final Predicate<DriveInfo> driveNotClaimed;
   private final JustProvider locationSupplier;
   private final String defaultVncPassword;
   private final Map<String, DriveInfo> cache;
   private final ExecutorService executor;

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   @Inject
   public CloudSigmaComputeServiceAdapter(CloudSigmaClient client, CloudSigmaAsyncClient aclient,
            Predicate<DriveInfo> driveNotClaimed, JustProvider locationSupplier,
            @Named(CloudSigmaConstants.PROPERTY_VNC_PASSWORD) String defaultVncPassword, Map<String, DriveInfo> cache,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      this.client = checkNotNull(client, "client");
      this.aclient = checkNotNull(aclient, "aclient");
      this.driveNotClaimed = checkNotNull(driveNotClaimed, "driveNotClaimed");
      this.locationSupplier = checkNotNull(locationSupplier, "locationSupplier");
      this.defaultVncPassword = checkNotNull(defaultVncPassword, "defaultVncPassword");
      this.cache = checkNotNull(cache, "cache");
      this.executor = checkNotNull(executor, "executor");
   }

   @Override
   public ServerInfo createNodeWithGroupEncodedIntoNameThenStoreCredentials(String tag, String name, Template template,
            Map<String, Credentials> credentialStore) {
      long bootSize = (long) (template.getHardware().getVolumes().get(0).getSize() * 1024 * 1024 * 1024l);
      logger.debug(">> imaging boot drive source(%s) bytes(%d)", template.getImage().getId(), bootSize);
      DriveInfo drive = client.cloneDrive(template.getImage().getId(), template.getImage().getId(),
               new CloneDriveOptions().size(bootSize));
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
      // store the credentials so that later functions can use them
      credentialStore.put(from.getUuid() + "", new Credentials("cloudsigma", "cloudsigma"));
      return from;
   }

   @Override
   public Iterable<Hardware> listHardwareProfiles() {
      Builder<Hardware> hardware = ImmutableSet.<Hardware> builder();
      for (double cpu : new double[] { 1000, 5000, 10000, 20000 })
         for (int ram : new int[] { 512, 1024, 4 * 1024, 16 * 1024, 32 * 1024 }) {
            final float size = (float) cpu / 100;
            String id = String.format("cpu=%f,ram=%s,disk=%f", cpu, ram, size);
            hardware.add(new HardwareBuilder().supportsImage(new Predicate<Image>() {

               @Override
               public boolean apply(Image input) {
                  String toParse = input.getUserMetadata().get("size");
                  return (toParse != null && new Float(toParse) <= size);
               }

               @Override
               public String toString() {
                  return "sizeLessThanOrEqual(" + size + ")";
               }

            }).ids(id).ram(ram).processors(ImmutableList.of(new Processor(1, cpu))).volumes(
                     ImmutableList.<Volume> of(new VolumeImpl(size, true, true))).build());
         }
      return hardware.build();
   }

   /**
    * look up the current standard images and do not error out, if they are not found.
    */
   @Override
   public Iterable<DriveInfo> listImages() {
      Iterable<DriveInfo> drives = transformParallel(client.listStandardDrives(),
               new Function<String, Future<DriveInfo>>() {

                  @Override
                  public Future<DriveInfo> apply(String input) {
                     return aclient.getDriveInfo(input);
                  }

               }, executor, null, logger, "drives");
      Iterable<DriveInfo> returnVal = filter(drives, PREINSTALLED_DISK);
      for (DriveInfo drive : returnVal)
         cache.put(drive.getUuid(), drive);
      return returnVal;
   }

   @SuppressWarnings("unchecked")
   @Override
   public Iterable<ServerInfo> listNodes() {
      return (Iterable<ServerInfo>) client.listServerInfo();
   }

   @SuppressWarnings("unchecked")
   @Override
   public Iterable<Location> listLocations() {
      return (Iterable<Location>) locationSupplier.get();
   }

   @Override
   public ServerInfo getNode(String id) {
      return client.getServerInfo(id);
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