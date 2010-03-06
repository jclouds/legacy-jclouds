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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.*;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import org.jclouds.Constants;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.*;
import org.jclouds.compute.domain.internal.ImageImpl;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.compute.domain.internal.SizeImpl;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
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
import org.jclouds.gogrid.domain.PowerCommand;
import org.jclouds.gogrid.domain.Server;
import org.jclouds.gogrid.domain.ServerImage;
import org.jclouds.gogrid.predicates.ServerLatestJobCompleted;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.RestContext;
import org.jclouds.ssh.SshClient;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

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

        @Inject
        protected GoGridAddNodeWithTagStrategy(GoGridClient client,
                                               Function<Server, NodeMetadata> serverToNodeMetadata,
                                               Function<Size, String> sizeToRam) {
            this.client = client;
            this.serverToNodeMetadata = serverToNodeMetadata;
            this.sizeToRam = sizeToRam;
            this.serverLatestJobCompleted = new RetryablePredicate<Server>(
                    new ServerLatestJobCompleted(client.getJobServices()),
                    800, 20, TimeUnit.SECONDS);
        }

        @Override
        public NodeMetadata execute(String tag, String name, Template template) {
            Set<Ip> availableIps = client.getIpServices().getUnassignedIpList();
            Ip availableIp = Iterables.getLast(availableIps);
            Server addedServer = client.getServerServices().addServer(name, checkNotNull(template.getImage().getId()),
                    sizeToRam.apply(template.getSize()), availableIp.getIp());
            serverLatestJobCompleted.apply(addedServer);

            client.getServerServices().power(addedServer.getName(), PowerCommand.START);
            serverLatestJobCompleted.apply(addedServer);

            addedServer = Iterables.getOnlyElement(
                    client.getServerServices().getServersByName(addedServer.getName())
            );
            return serverToNodeMetadata.apply(addedServer);
        }
    }

    @Singleton
    public static class GoGridRebootNodeStrategy implements RebootNodeStrategy {
        private final GoGridClient client;
        private RetryablePredicate<Server> serverLatestJobCompleted;

        @Inject
        protected GoGridRebootNodeStrategy(GoGridClient client) {
            this.client = client;
            this.serverLatestJobCompleted = new RetryablePredicate<Server>(
                    new ServerLatestJobCompleted(client.getJobServices()),
                    800, 20, TimeUnit.SECONDS);
        }

        @Override
        public boolean execute(ComputeMetadata node) {
            Server server =
                    Iterables.getOnlyElement(client.getServerServices().getServersByName(node.getName()));
            client.getServerServices().power(server.getName(), PowerCommand.RESTART);
            serverLatestJobCompleted.apply(server);
            client.getServerServices().power(server.getName(), PowerCommand.START);
            return serverLatestJobCompleted.apply(server);
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
        public Iterable<? extends ComputeMetadata> execute() {
            return Iterables.transform(client.getServerServices().getServerList(), serverToNodeMetadata);
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
            Server server = Iterables.getOnlyElement(
                    client.getServerServices().getServersByName(node.getName())
            );
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
                    new ServerLatestJobCompleted(client.getJobServices()),
                    800, 20, TimeUnit.SECONDS);
        }

        @Override
        public boolean execute(ComputeMetadata node) {
            Server server = Iterables.getOnlyElement(
                    client.getServerServices().getServersByName(node.getName()));
            client.getServerServices().deleteByName(server.getName());
            return serverLatestJobCompleted.apply(server);
        }

    }

    @Singleton
    @Provides
    Map<String, NodeState> provideServerToNodeState() {
        return ImmutableMap.<String, NodeState> builder()
                .put("On", NodeState.RUNNING)
                .put("Starting", NodeState.PENDING)
                .put("Off", NodeState.SUSPENDED)
                .put("Saving", NodeState.PENDING)
                .put("Restarting", NodeState.PENDING)
                .put("Stopping", NodeState.PENDING)
                .build();
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

    @Singleton
    @Provides
    Function<Size, String> provideSizeToRam() {
        return new Function<Size, String>() {
            @Override
            public String apply(Size size) {
                int ramRequired = size.getRam();
                if(ramRequired >= 8 * 1024) return "8GB";
                if(ramRequired >= 4 * 1024) return "4GB";
                if(ramRequired >= 2 * 1024) return "2GB";
                if(ramRequired >= 1024) return "1GB";
                return "512MB"; /*smallest*/
            }
        };
    }

    @Singleton
    private static class ServerToNodeMetadata implements Function<Server, NodeMetadata> {
        private final Map<String, NodeState> serverStateToNodeState;
        private final Function<String, InetAddress> stringIpToInetAddress;
        private final GoGridClient client;

        @Inject
        ServerToNodeMetadata(Map<String, NodeState> serverStateToNodeState,
                             Function<String, InetAddress> stringIpToInetAddress,
                             GoGridClient client) {
            this.serverStateToNodeState = serverStateToNodeState;
            this.stringIpToInetAddress = stringIpToInetAddress;
            this.client = client;
        }

        @Override
        public NodeMetadata apply(Server from) {
            String locationId = "Unavailable";
            String tag = from.getName();
            Credentials creds = client.getServerServices().getServerCredentialsList().get(from.getName());
            Set<InetAddress> ipSet =
                    ImmutableSet.of(stringIpToInetAddress.apply(from.getIp().getIp()));
            NodeState state = serverStateToNodeState.get(from.getState().getName());

            return new NodeMetadataImpl(from.getId() + "", from.getName(), locationId, null,
                    ImmutableMap.<String, String> of(), tag, state,
                    ipSet,
                    ImmutableList.<InetAddress> of(), ImmutableMap.<String, String> of(), creds);

        }
    }

    @Provides
    @Singleton
    ComputeServiceContext provideContext(ComputeService computeService,
                                         RestContext<GoGridAsyncClient, GoGridClient> context) {
        return new ComputeServiceContextImpl<GoGridAsyncClient, GoGridClient>(
                computeService, context);
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
        return locations.get("SANJOSE");
    }

    @Provides
    @Singleton
    Map<String, ? extends Location> getDefaultLocations(GoGridClient sync, LogHolder holder,
                                                        Function<ComputeMetadata, String> indexer) {
        final Set<Location> locations = Sets.newHashSet();
        holder.logger.debug(">> providing empty locations because gogrid doesnt have any");
        locations.add(new LocationImpl(LocationScope.REGION, "SANJOSE", "GoGrid doesnt support locations so using " +
                "a made up one to comply with API", "Santa Clara County", true));
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

        sizes.add(new SizeImpl("1", "1", null, null,
                ImmutableMap.<String, String> of(), 0, 512, 0,
                ImmutableSet.<Architecture> of(Architecture.X86_32,
                        Architecture.X86_64)));
        sizes.add(new SizeImpl("2", "2", null, null,
                ImmutableMap.<String, String> of(), 0, 1024, 0,
                ImmutableSet.<Architecture> of(Architecture.X86_32,
                        Architecture.X86_64)));
        sizes.add(new SizeImpl("3", "3", null, null,
                ImmutableMap.<String, String> of(), 0, 2048, 0,
                ImmutableSet.<Architecture> of(Architecture.X86_32,
                        Architecture.X86_64)));
        sizes.add(new SizeImpl("4", "4", null, null,
                ImmutableMap.<String, String> of(), 0, 4096, 0,
                ImmutableSet.<Architecture> of(Architecture.X86_32,
                        Architecture.X86_64)));
        sizes.add(new SizeImpl("5", "5", null, null,
                ImmutableMap.<String, String> of(), 0, 8192, 0,
                ImmutableSet.<Architecture> of(Architecture.X86_32,
                        Architecture.X86_64)));
        holder.logger.debug("<< sizes(%d)", sizes.size());
        return Maps.uniqueIndex(sizes, indexer);
    }

    private static class LogHolder {
        @Resource
        @Named(ComputeServiceConstants.COMPUTE_LOGGER)
        protected Logger logger = Logger.NULL;
    }

    public static final Pattern GOGRID_PATTERN = Pattern.compile("([a-zA-Z]*)(.*)");

    @Provides
    @Singleton
    protected Map<String, ? extends Image> provideImages(final GoGridClient sync,
                                                         LogHolder holder, Function<ComputeMetadata, String> indexer)
            throws InterruptedException, ExecutionException, TimeoutException {
        final Set<Image> images = Sets.newHashSet();
        holder.logger.debug(">> providing images");
        Set<ServerImage> allImages = sync.getImageServices().getImageList();
        for (ServerImage from : allImages) {
            OsFamily os = null;
            Architecture arch = from.getDescription().indexOf("64") == -1 ? Architecture.X86_32
                    : Architecture.X86_64;
            String osDescription;
            String version = "";

            osDescription = from.getOs().getName();

            Matcher matcher = GOGRID_PATTERN.matcher(from.getOs().getName());
            if (matcher.find()) {
                try {
                    os = OsFamily.fromValue(matcher.group(1).toLowerCase());
                } catch (IllegalArgumentException e) {
                    holder.logger.debug("<< didn't match os(%s)", matcher.group(2));
                }
            }

            images.add(new ImageImpl(from.getName(), from.getDescription(), null, null, ImmutableMap
                    .<String, String> of(), from.getDescription(), version, os, osDescription, arch));
        }
        holder.logger.debug("<< images(%d)", images.size());
        return Maps.uniqueIndex(images, indexer);
    }
}
