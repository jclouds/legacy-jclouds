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
package org.jclouds.glesys.compute;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.io.BaseEncoding.base16;
import static org.jclouds.compute.util.ComputeServiceUtils.metadataAndTagsAsCommaDelimitedValue;
import static org.jclouds.concurrent.FutureIterables.transformParallel;
import static org.jclouds.util.Predicates2.retry;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
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
import org.jclouds.glesys.GleSYSApi;
import org.jclouds.glesys.GleSYSAsyncApi;
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

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * defines the connection between the {@link GleSYSApi} implementation and
 * the jclouds {@link ComputeService}
 * 
 */
@Singleton
public class GleSYSComputeServiceAdapter implements ComputeServiceAdapter<ServerDetails, Hardware, OSTemplate, String> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final GleSYSApi api;
   private final GleSYSAsyncApi aapi;
   private final ListeningExecutorService userExecutor;
   private final Timeouts timeouts;
   private final Supplier<Set<? extends Location>> locations;

   @Inject
   public GleSYSComputeServiceAdapter(GleSYSApi api, GleSYSAsyncApi aapi,
         @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor, Timeouts timeouts,
         @Memoized Supplier<Set<? extends Location>> locations) {
      this.api = checkNotNull(api, "api");
      this.aapi = checkNotNull(aapi, "aapi");
      this.userExecutor = checkNotNull(userExecutor, "userExecutor");
      this.timeouts = checkNotNull(timeouts, "timeouts");
      this.locations = checkNotNull(locations, "locations");
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

      Map<String, String> md = metadataAndTagsAsCommaDelimitedValue(template.getOptions());
      if (md.size() > 0) {
         String description = Joiner.on('\n').withKeyValueSeparator("=").join(md);
         // TODO: get glesys to stop stripping out equals and commas!
         createServerOptions.description(base16().lowerCase().encode(description.getBytes(UTF_8)));
      }
      ServerSpec.Builder<?> builder = ServerSpec.builder();
      builder.datacenter(template.getLocation().getId());
      builder.templateName(template.getImage().getId());
      builder.platform(template.getHardware().getHypervisor());
      builder.memorySizeMB(template.getHardware().getRam());
      builder.diskSizeGB(Math.round(template.getHardware().getVolumes().get(0).getSize()));
      builder.cpuCores((int) template.getHardware().getProcessors().get(0).getCores());
      builder.transferGB(templateOptions.getTransferGB());
      ServerSpec spec = builder.build();

      
      // use random root password unless one was provided via template options
      String password = templateOptions.hasRootPassword() ? templateOptions.getRootPassword() : getRandomPassword();

      logger.debug(">> creating new Server spec(%s) name(%s) options(%s)", spec, name, createServerOptions);
      ServerDetails result = api.getServerApi().createWithHostnameAndRootPassword(spec, name, password,
            createServerOptions);
      logger.trace("<< server(%s)", result.getId());

      return new NodeAndInitialCredentials<ServerDetails>(result, result.getId() + "", LoginCredentials.builder()
            .password(password).build());
   }

   /**
    * @return a generated random password string
    */
   private String getRandomPassword() {
      return UUID.randomUUID().toString().replace("-","");
   }

   @Override
   public Iterable<Hardware> listHardwareProfiles() {
      Set<? extends Location> locationsSet = locations.get();
      ImmutableSet.Builder<Hardware> hardwareToReturn = ImmutableSet.builder();

      // do this loop after dupes are filtered, else OOM
      Set<OSTemplate> images = listImages();

      for (Entry<String, AllowedArgumentsForCreateServer> platformToArgs : api.getServerApi()
            .getAllowedArgumentsForCreateByPlatform().entrySet())
         for (String datacenter : platformToArgs.getValue().getDataCenters())
            for (int diskSizeGB : platformToArgs.getValue().getDiskSizesInGB().getAllowedUnits())
               for (int cpuCores : platformToArgs.getValue().getCpuCoreOptions().getAllowedUnits())
                  for (int memorySizeMB : platformToArgs.getValue().getMemorySizesInMB().getAllowedUnits()) {
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
                              .location(find(locationsSet, LocationPredicates.idEquals(datacenter)))
                              .supportsImage(ImagePredicates.idIn(templatesSupported)).build());
                  }

      return hardwareToReturn.build();
   }

   @Override
   public Set<OSTemplate> listImages() {
      return api.getServerApi().listTemplates().toSet();
   }
   
   // cheat until we have a getTemplate command
   @Override
   public OSTemplate getImage(final String id) {
      return find(listImages(), new Predicate<OSTemplate>(){

         @Override
         public boolean apply(OSTemplate input) {
            return input.getName().equals(id);
         }
         
      }, null);
   }
   
   @Override
   public Iterable<ServerDetails> listNodes() {
      return transformParallel(api.getServerApi().list(), new Function<Server, ListenableFuture<? extends ServerDetails>>() {
         public ListenableFuture<ServerDetails> apply(Server from) {
            return aapi.getServerApi().get(from.getId());
         }
      }, userExecutor, null, logger, "server details");
   }

   @Override
   public Iterable<ServerDetails> listNodesByIds(final Iterable<String> ids) {
      return filter(listNodes(), new Predicate<ServerDetails>() {

            @Override
            public boolean apply(ServerDetails server) {
               return contains(ids, server.getId());
            }
         });
   }

   @Override
   public Set<String> listLocations() {
      return FluentIterable.from(api.getServerApi().getAllowedArgumentsForCreateByPlatform().values())
            .transformAndConcat(new Function<AllowedArgumentsForCreateServer, Set<String>>() {
               @Override
               public Set<String> apply(AllowedArgumentsForCreateServer arg0) {
                  return arg0.getDataCenters();
               }
            }).toSet();
   }

   @Override
   public ServerDetails getNode(String id) {
      return api.getServerApi().get(id);
   }

   @Override
   public void destroyNode(String id) {
      retry(new Predicate<String>() {
         public boolean apply(String arg0) {
            try {
               api.getServerApi().destroy(arg0, DestroyServerOptions.Builder.discardIp());
               return true;
            } catch (IllegalStateException e) {
               return false;
            }
         }
      }, timeouts.nodeTerminated).apply(id);
   }

   @Override
   public void rebootNode(String id) {
      api.getServerApi().reboot(id);
   }

   @Override
   public void resumeNode(String id) {
      api.getServerApi().start(id);
   }

   @Override
   public void suspendNode(String id) {
      api.getServerApi().stop(id);
   }
}
