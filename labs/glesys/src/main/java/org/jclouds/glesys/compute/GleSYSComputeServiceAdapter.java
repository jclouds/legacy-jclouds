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
package org.jclouds.glesys.compute;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.concurrent.FutureIterables.transformParallel;

import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.collect.FindResourceInSet;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.compute.predicates.ImagePredicates;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.glesys.GleSYSAsyncClient;
import org.jclouds.glesys.GleSYSClient;
import org.jclouds.glesys.compute.options.GleSYSTemplateOptions;
import org.jclouds.glesys.domain.AllowedArgumentsForCreateServer;
import org.jclouds.glesys.domain.OSTemplate;
import org.jclouds.glesys.domain.Server;
import org.jclouds.glesys.domain.ServerDetails;
import org.jclouds.glesys.domain.ServerSpec;
import org.jclouds.glesys.options.CreateServerOptions;
import org.jclouds.glesys.options.DestroyServerOptions;
import org.jclouds.location.predicates.LocationPredicates;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.RetryablePredicate;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * defines the connection between the {@link GleSYSClient} implementation and
 * the jclouds {@link ComputeService}
 * 
 */
@Singleton
public class GleSYSComputeServiceAdapter implements ComputeServiceAdapter<ServerDetails, Hardware, OSTemplate, String> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final GleSYSClient client;
   private final GleSYSAsyncClient aclient;
   private final ExecutorService userThreads;
   private final Timeouts timeouts;
   private final Supplier<Set<? extends Location>> locations;
   private final Provider<String> passwordProvider;

   @Inject
   public GleSYSComputeServiceAdapter(GleSYSClient client, GleSYSAsyncClient aclient,
         @Named(Constants.PROPERTY_USER_THREADS) ExecutorService userThreads, Timeouts timeouts,
         @Memoized Supplier<Set<? extends Location>> locations, @Named("PASSWORD") Provider<String> passwordProvider) {
      this.client = checkNotNull(client, "client");
      this.aclient = checkNotNull(aclient, "aclient");
      this.userThreads = checkNotNull(userThreads, "userThreads");
      this.timeouts = checkNotNull(timeouts, "timeouts");
      this.locations = checkNotNull(locations, "locations");
      this.passwordProvider = checkNotNull(passwordProvider, "passwordProvider");
   }

   @Override
   public NodeAndInitialCredentials<ServerDetails> createNodeWithGroupEncodedIntoName(String group, String name,
         Template template) {
      checkNotNull(template, "template was null");
      checkNotNull(template.getOptions(), "template options was null");
      checkArgument(template.getOptions().getClass().isAssignableFrom(GleSYSTemplateOptions.class),
            "options class %s should have been assignable from GleSYSTemplateOptions", template.getOptions().getClass());

      GleSYSTemplateOptions templateOptions = template.getOptions().as(GleSYSTemplateOptions.class);

      CreateServerOptions createServerOptions = new CreateServerOptions();
      createServerOptions.ip(templateOptions.getIp());

      createServerOptions.description(name); // TODO: add to templateOptions and
                                             // set if present

      ServerSpec.Builder builder = ServerSpec.builder();
      builder.datacenter(template.getLocation().getId());
      builder.templateName(template.getImage().getId());
      builder.platform(template.getHardware().getHypervisor());
      builder.memorySizeMB(template.getHardware().getRam());
      builder.diskSizeGB(Math.round(template.getHardware().getVolumes().get(0).getSize()));
      builder.cpuCores((int) template.getHardware().getProcessors().get(0).getCores());
      builder.transferGB(50);// TODO: add to template options with default value
      ServerSpec spec = builder.build();

      String password = passwordProvider.get(); // TODO: add to templateOptions
                                                // and set if present

      logger.debug(">> creating new Server spec(%s) name(%s) options(%s)", spec, name, createServerOptions);
      ServerDetails result = client.getServerClient().createServerWithHostnameAndRootPassword(spec, name, password,
            createServerOptions);
      logger.trace("<< server(%s)", result.getId());

      return new NodeAndInitialCredentials<ServerDetails>(result, result.getId() + "", LoginCredentials.builder()
            .password(password).build());
   }

   @Singleton
   public static class FindLocationForServerSpec extends FindResourceInSet<ServerSpec, Location> {

      @Inject
      public FindLocationForServerSpec(@Memoized Supplier<Set<? extends Location>> location) {
         super(location);
      }

      @Override
      public boolean matches(ServerSpec from, Location input) {
         return input.getId().equals(from.getDatacenter());
      }
   }

   @Override
   public Iterable<Hardware> listHardwareProfiles() {
      Set<? extends Location> locationsSet = locations.get();
      ImmutableSet.Builder<Hardware> hardwareToReturn = ImmutableSet.builder();

      // do this loop after dupes are filtered, else OOM
      Set<OSTemplate> images = listImages();

      for (Entry<String, AllowedArgumentsForCreateServer> platformToArgs : client.getServerClient()
            .getAllowedArgumentsForCreateServerByPlatform().entrySet())
         for (String datacenter : platformToArgs.getValue().getDataCenters())
            for (int diskSizeGB : platformToArgs.getValue().getDiskSizesInGB())
               for (int cpuCores : platformToArgs.getValue().getCpuCoreOptions())
                  for (int memorySizeMB : platformToArgs.getValue().getMemorySizesInMB()) {
                     ImmutableSet.Builder<String> templatesSupportedBuilder = ImmutableSet.builder();
                     for (OSTemplate template : images) {
                        if (template.getPlatform().equals(platformToArgs.getKey())
                              && diskSizeGB >= template.getMinDiskSize() && memorySizeMB >= template.getMinMemSize())
                           templatesSupportedBuilder.add(template.getName());
                     }
                     ImmutableSet<String> templatesSupported = templatesSupportedBuilder.build();
                     if (templatesSupported.size() > 0)
                        hardwareToReturn.add(new HardwareBuilder()
                              .ids(String.format(
                                    "datacenter(%s)platform(%s)cpuCores(%d)memorySizeMB(%d)diskSizeGB(%d)", datacenter,
                                    platformToArgs.getKey(), cpuCores, memorySizeMB, diskSizeGB)).ram(memorySizeMB)
                              .processors(ImmutableList.of(new Processor(cpuCores, 1.0)))
                              .volumes(ImmutableList.<Volume> of(new VolumeImpl((float) diskSizeGB, true, true)))
                              .hypervisor(platformToArgs.getKey())
                              .location(Iterables.find(locationsSet, LocationPredicates.idEquals(datacenter)))
                              .supportsImage(ImagePredicates.idIn(templatesSupported)).build());
                  }

      return hardwareToReturn.build();
   }

   @Override
   public Set<OSTemplate> listImages() {
      return client.getServerClient().listTemplates();
   }

   @Override
   public Iterable<ServerDetails> listNodes() {
      return transformParallel(client.getServerClient().listServers(), new Function<Server, Future<ServerDetails>>() {
         @Override
         public Future<ServerDetails> apply(Server from) {
            return aclient.getServerClient().getServerDetails(from.getId());
         }

      }, userThreads, null, logger, "server details");
   }

   @Override
   public Set<String> listLocations() {
      return ImmutableSet.copyOf(Iterables.concat(Iterables.transform(client.getServerClient()
            .getAllowedArgumentsForCreateServerByPlatform().values(),
            new Function<AllowedArgumentsForCreateServer, Set<String>>() {

               @Override
               public Set<String> apply(AllowedArgumentsForCreateServer arg0) {
                  return arg0.getDataCenters();
               }

            })));
   }

   @Override
   public ServerDetails getNode(String id) {
      return client.getServerClient().getServerDetails(id);
   }

   @Override
   public void destroyNode(String id) {
      new RetryablePredicate<String>(new Predicate<String>() {

         @Override
         public boolean apply(String arg0) {
            try {
               client.getServerClient().destroyServer(arg0, DestroyServerOptions.Builder.discardIp());
               return true;
            } catch (IllegalStateException e) {
               return false;
            }
         }

      }, timeouts.nodeTerminated).apply(id);
   }

   @Override
   public void rebootNode(String id) {
      client.getServerClient().rebootServer(id);
   }

   @Override
   public void resumeNode(String id) {
      client.getServerClient().startServer(id);
   }

   @Override
   public void suspendNode(String id) {
      client.getServerClient().stopServer(id);
   }
}