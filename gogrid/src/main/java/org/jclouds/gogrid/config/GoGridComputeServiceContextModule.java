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
package org.jclouds.gogrid.config;

import static com.google.common.base.Preconditions.*;
import static org.jclouds.compute.domain.OsFamily.CENTOS;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
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
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.compute.domain.internal.SizeImpl;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.internal.TemplateBuilderImpl;
import org.jclouds.compute.options.GetNodesOptions;
import org.jclouds.compute.predicates.RunScriptRunning;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.*;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.gogrid.GoGridAsyncClient;
import org.jclouds.gogrid.GoGridClient;
import org.jclouds.gogrid.domain.Ip;
import org.jclouds.gogrid.domain.IpType;
import org.jclouds.gogrid.domain.PowerCommand;
import org.jclouds.gogrid.domain.Server;
import org.jclouds.gogrid.domain.ServerImage;
import org.jclouds.gogrid.options.GetIpListOptions;
import org.jclouds.gogrid.predicates.ServerLatestJobCompleted;
import org.jclouds.gogrid.util.GoGridUtils;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.RestContext;
import org.jclouds.ssh.SshClient;

import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * @author Oleksiy Yarmula
 */
public class GoGridComputeServiceContextModule extends GoGridContextModule {

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<Function<Server, NodeMetadata>>() {
      }).to(ServerToNodeMetadata.class);
      bind(AddNodeWithTagStrategy.class).to(GoGridAddNodeWithTagStrategy.class);
      bind(ListNodesStrategy.class).to(GoGridListNodesStrategy.class);
      bind(GetNodeMetadataStrategy.class).to(GoGridGetNodeMetadataStrategy.class);
      bind(RebootNodeStrategy.class).to(GoGridRebootNodeStrategy.class);
      bind(DestroyNodeStrategy.class).to(GoGridDestroyNodeStrategy.class);
   }

   @Provides
   TemplateBuilder provideTemplate(TemplateBuilderImpl template) {
      return template.osFamily(CENTOS).imageNameMatches(".*w/ None.*");
   }

   @Provides
   @Named("NAMING_CONVENTION")
   @Singleton
   String provideNamingConvention() {
      return "%s-%d";
   }

   @Singleton
   public static class GoGridAddNodeWithTagStrategy implements AddNodeWithTagStrategy {
      private final GoGridClient client;
      private final Function<Size, String> sizeToRam;
      private final Function<Server, NodeMetadata> serverToNodeMetadata;
      private RetryablePredicate<Server> serverLatestJobCompleted;
      private RetryablePredicate<Server> serverLatestJobCompletedShort;

      @Inject
      protected GoGridAddNodeWithTagStrategy(GoGridClient client,
               Function<Server, NodeMetadata> serverToNodeMetadata, Function<Size, String> sizeToRam) {
         this.client = client;
         this.serverToNodeMetadata = serverToNodeMetadata;
         this.sizeToRam = sizeToRam;
         this.serverLatestJobCompleted = new RetryablePredicate<Server>(
                  new ServerLatestJobCompleted(client.getJobServices()), 800, 20, TimeUnit.SECONDS);
         this.serverLatestJobCompletedShort = new RetryablePredicate<Server>(
                  new ServerLatestJobCompleted(client.getJobServices()), 60, 20, TimeUnit.SECONDS);
      }

      @Override
      public NodeMetadata execute(String tag, String name, Template template) {
         Server addedServer = null;
         boolean notStarted = true;
         int numOfRetries = 20;
         // lock-free consumption of a shared resource: IP address pool
         while (notStarted) { // TODO: replace with Predicate-based thread collision avoidance for
            // simplicity
            Set<Ip> availableIps = client.getIpServices().getIpList(
                     new GetIpListOptions().onlyUnassigned().onlyWithType(IpType.PUBLIC));
            if (availableIps.size() == 0)
               throw new RuntimeException("No public IPs available on this account.");
            int ipIndex = new SecureRandom().nextInt(availableIps.size());
            Ip availableIp = Iterables.get(availableIps, ipIndex);
            try {
               addedServer = client.getServerServices().addServer(name,
                        checkNotNull(template.getImage().getId()),
                        sizeToRam.apply(template.getSize()), availableIp.getIp());
               notStarted = false;
            } catch (Exception e) {
               if (--numOfRetries == 0)
                  Throwables.propagate(e);
               notStarted = true;
            }
         }
         serverLatestJobCompleted.apply(addedServer);

         client.getServerServices().power(addedServer.getName(), PowerCommand.START);
         serverLatestJobCompletedShort.apply(addedServer);

         addedServer = Iterables.getOnlyElement(client.getServerServices().getServersByName(
                  addedServer.getName()));
         return serverToNodeMetadata.apply(addedServer);
      }
   }

   @Singleton
   public static class GoGridRebootNodeStrategy implements RebootNodeStrategy {
      private final GoGridClient client;
      private RetryablePredicate<Server> serverLatestJobCompleted;
      private RetryablePredicate<Server> serverLatestJobCompletedShort;

      @Inject
      protected GoGridRebootNodeStrategy(GoGridClient client) {
         this.client = client;
         this.serverLatestJobCompleted = new RetryablePredicate<Server>(
                  new ServerLatestJobCompleted(client.getJobServices()), 800, 20, TimeUnit.SECONDS);
         this.serverLatestJobCompletedShort = new RetryablePredicate<Server>(
                  new ServerLatestJobCompleted(client.getJobServices()), 60, 20, TimeUnit.SECONDS);
      }

      @Override
      public boolean execute(ComputeMetadata node) {
         Server server = Iterables.getOnlyElement(client.getServerServices().getServersByName(
                  node.getName()));
         client.getServerServices().power(server.getName(), PowerCommand.RESTART);
         serverLatestJobCompleted.apply(server);
         client.getServerServices().power(server.getName(), PowerCommand.START);
         return serverLatestJobCompletedShort.apply(server);
      }
   }

   @Singleton
   public static class GoGridListNodesStrategy implements ListNodesStrategy {
      private final GoGridClient client;
      private final Function<Server, NodeMetadata> serverToNodeMetadata;

      @Inject
      protected GoGridListNodesStrategy(GoGridClient client,
               Function<Server, NodeMetadata> serverToNodeMetadata) {
         this.client = client;
         this.serverToNodeMetadata = serverToNodeMetadata;
      }

      @Override
      public Iterable<? extends ComputeMetadata> execute(GetNodesOptions options) {
         return Iterables.transform(client.getServerServices().getServerList(),
                  serverToNodeMetadata);
      }

   }

   @Singleton
   public static class GoGridGetNodeMetadataStrategy implements GetNodeMetadataStrategy {
      private final GoGridClient client;
      private final Function<Server, NodeMetadata> serverToNodeMetadata;

      @Inject
      protected GoGridGetNodeMetadataStrategy(GoGridClient client,
               Function<Server, NodeMetadata> serverToNodeMetadata) {
         this.client = client;
         this.serverToNodeMetadata = serverToNodeMetadata;
      }

      @Override
      public NodeMetadata execute(ComputeMetadata node) {
         Server server = Iterables.getOnlyElement(client.getServerServices().getServersByName(
                  node.getName()));
         return server == null ? null : serverToNodeMetadata.apply(server);
      }
   }

   @Singleton
   public static class GoGridDestroyNodeStrategy implements DestroyNodeStrategy {
      private final GoGridClient client;
      private RetryablePredicate<Server> serverLatestJobCompleted;

      @Inject
      protected GoGridDestroyNodeStrategy(GoGridClient client) {
         this.client = client;
         this.serverLatestJobCompleted = new RetryablePredicate<Server>(
                  new ServerLatestJobCompleted(client.getJobServices()), 800, 20, TimeUnit.SECONDS);
      }

      @Override
      public boolean execute(ComputeMetadata node) {
         Server server = Iterables.getOnlyElement(client.getServerServices().getServersByName(
                  node.getName()));
         client.getServerServices().deleteByName(server.getName());
         return serverLatestJobCompleted.apply(server);
      }

   }

   @Singleton
   @Provides
   Map<String, NodeState> provideServerToNodeState() {
      return ImmutableMap.<String, NodeState> builder().put("On", NodeState.RUNNING).put(
               "Starting", NodeState.PENDING).put("Off", NodeState.SUSPENDED).put("Saving",
               NodeState.PENDING).put("Restarting", NodeState.PENDING).put("Stopping",
               NodeState.PENDING).build();
   }

   @Singleton
   @Provides
   Function<String, InetAddress> provideStringIpToInetAddress() {
      return new Function<String, InetAddress>() {
         @Override
         public InetAddress apply(String from) {
            try {
               return InetAddress.getByName(from);
            } catch (UnknownHostException e) {
               // TODO: log the failure.
               return null;
            }
         }
      };
   }

   /**
    * Finds matches to required configurations. GoGrid's documentation only specifies how much RAM
    * one can get with different instance types. The # of cores and disk sizes are purely empyrical
    * and aren't guaranteed. However, these are the matches found: Ram: 512MB, CPU: 1 core, HDD: 28
    * GB Ram: 1GB, CPU: 1 core, HDD: 57 GB Ram: 2GB, CPU: 1 core, HDD: 113 GB Ram: 4GB, CPU: 3
    * cores, HDD: 233 GB Ram: 8GB, CPU: 6 cores, HDD: 462 GB (as of March 2010)
    * 
    * @return matched size
    */
   @Singleton
   @Provides
   Function<Size, String> provideSizeToRam() {
      return new Function<Size, String>() {
         @Override
         public String apply(Size size) {
            if (size.getRam() >= 8 * 1024 || size.getCores() >= 6 || size.getDisk() >= 450)
               return "8GB";
            if (size.getRam() >= 4 * 1024 || size.getCores() >= 3 || size.getDisk() >= 230)
               return "4GB";
            if (size.getRam() >= 2 * 1024 || size.getDisk() >= 110)
               return "2GB";
            if (size.getRam() >= 1024 || size.getDisk() >= 55)
               return "1GB";
            return "512MB"; /* smallest */
         }
      };
   }

   @Singleton
   private static class ServerToNodeMetadata implements Function<Server, NodeMetadata> {
      private final Map<String, NodeState> serverStateToNodeState;
      private final Function<String, InetAddress> stringIpToInetAddress;
      private final GoGridClient client;

      @SuppressWarnings("unused")
      @Inject
      ServerToNodeMetadata(Map<String, NodeState> serverStateToNodeState,
               Function<String, InetAddress> stringIpToInetAddress, GoGridClient client) {
         this.serverStateToNodeState = serverStateToNodeState;
         this.stringIpToInetAddress = stringIpToInetAddress;
         this.client = client;
      }

      @Override
      public NodeMetadata apply(Server from) {
         String locationId = "Unavailable";
         String tag = CharMatcher.JAVA_LETTER.retainFrom(from.getName());
         Set<InetAddress> ipSet = ImmutableSet
                  .of(stringIpToInetAddress.apply(from.getIp().getIp()));
         NodeState state = serverStateToNodeState.get(from.getState().getName());
         Credentials creds = client.getServerServices().getServerCredentialsList().get(
                  from.getName());
         return new NodeMetadataImpl(from.getId() + "", from.getName(), locationId, null,
                  ImmutableMap.<String, String> of(), tag, state, ipSet, ImmutableList
                           .<InetAddress> of(), ImmutableMap.<String, String> of(), creds);
      }
   }

   @Provides
   @Singleton
   ComputeServiceContext provideContext(ComputeService computeService,
            RestContext<GoGridAsyncClient, GoGridClient> context) {
      return new ComputeServiceContextImpl<GoGridAsyncClient, GoGridClient>(computeService, context);
   }

   @Provides
   @Singleton
   @Named("NOT_RUNNING")
   protected Predicate<SshClient> runScriptRunning(RunScriptRunning stateRunning) {
      return new RetryablePredicate<SshClient>(Predicates.not(stateRunning), 600, 3,
               TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   Location getDefaultLocation(Map<String, ? extends Location> locations) {
      return locations.get("SANFRANCISCO");
   }

   @Provides
   @Singleton
   Map<String, ? extends Location> getDefaultLocations(GoGridClient sync, LogHolder holder,
            Function<ComputeMetadata, String> indexer) {
      final Set<Location> locations = Sets.newHashSet();
      holder.logger.debug(">> providing locations");
      locations.add(new LocationImpl(LocationScope.ZONE, "SANFRANCISCO", "San Francisco, CA", null,
               true));
      holder.logger.debug("<< locations(%d)", locations.size());
      return Maps.uniqueIndex(locations, new Function<Location, String>() {

         @Override
         public String apply(Location from) {
            return from.getId();
         }
      });
   }

   @Provides
   @Singleton
   protected Function<ComputeMetadata, String> indexer() {
      return new Function<ComputeMetadata, String>() {
         @Override
         public String apply(ComputeMetadata from) {
            return from.getId();
         }
      };
   }

   @Provides
   @Singleton
   protected Map<String, ? extends Size> provideSizes(GoGridClient sync,
            Map<String, ? extends Image> images, LogHolder holder,
            Function<ComputeMetadata, String> indexer) throws InterruptedException,
            TimeoutException, ExecutionException {
      final Set<Size> sizes = Sets.newHashSet();
      holder.logger.debug(">> providing sizes");

      sizes.add(new SizeImpl("1", "1", null, null, ImmutableMap.<String, String> of(), 1, 512, 28,
               ImmutableSet.<Architecture> of(Architecture.X86_32, Architecture.X86_64)));
      sizes.add(new SizeImpl("2", "2", null, null, ImmutableMap.<String, String> of(), 1, 1024, 57,
               ImmutableSet.<Architecture> of(Architecture.X86_32, Architecture.X86_64)));
      sizes.add(new SizeImpl("3", "3", null, null, ImmutableMap.<String, String> of(), 1, 2048,
               113, ImmutableSet.<Architecture> of(Architecture.X86_32, Architecture.X86_64)));
      sizes.add(new SizeImpl("4", "4", null, null, ImmutableMap.<String, String> of(), 3, 4096,
               233, ImmutableSet.<Architecture> of(Architecture.X86_32, Architecture.X86_64)));
      sizes.add(new SizeImpl("5", "5", null, null, ImmutableMap.<String, String> of(), 6, 8192,
               462, ImmutableSet.<Architecture> of(Architecture.X86_32, Architecture.X86_64)));
      holder.logger.debug("<< sizes(%d)", sizes.size());
      return Maps.uniqueIndex(sizes, indexer);
   }

   private static class LogHolder {
      @Resource
      @Named(ComputeServiceConstants.COMPUTE_LOGGER)
      protected Logger logger = Logger.NULL;
   }

   public static final Pattern GOGRID_OS_NAME_PATTERN = Pattern.compile("([a-zA-Z]*)(.*)");

   @Provides
   @Singleton
   protected Map<String, ? extends Image> provideImages(final GoGridClient sync, LogHolder holder,
            Function<ComputeMetadata, String> indexer, Location location,
            PopulateDefaultLoginCredentialsForImageStrategy authenticator)
            throws InterruptedException, ExecutionException, TimeoutException {
      final Set<Image> images = Sets.newHashSet();
      holder.logger.debug(">> providing images");
      Set<ServerImage> allImages = sync.getImageServices().getImageList();
      for (ServerImage from : allImages) {
         OsFamily os = null;
         Architecture arch = (from.getOs().getName().indexOf("64") == -1 && from.getDescription()
                  .indexOf("64") == -1) ? Architecture.X86_32 : Architecture.X86_64;
         String osDescription;
         String version = "";

         osDescription = from.getOs().getName();

         String matchedOs = GoGridUtils.parseStringByPatternAndGetNthMatchGroup(from.getOs()
                  .getName(), GOGRID_OS_NAME_PATTERN, 1);
         try {
            os = OsFamily.fromValue(matchedOs.toLowerCase());
         } catch (IllegalArgumentException e) {
            holder.logger.debug("<< didn't match os(%s)", matchedOs);
         }
         Credentials defaultCredentials = authenticator.execute(from);
         images.add(new ImageImpl(from.getId() + "", from.getFriendlyName(), location.getId(),
                  null, ImmutableMap.<String, String> of(), from.getDescription(), version, os,
                  osDescription, arch, defaultCredentials));
      }
      holder.logger.debug("<< images(%d)", images.size());
      return Maps.uniqueIndex(images, indexer);
   }
}
