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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import org.jclouds.compute.domain.*;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.compute.strategy.*;
import org.jclouds.domain.Credentials;
import org.jclouds.gogrid.GoGridClient;
import org.jclouds.gogrid.domain.Ip;
import org.jclouds.gogrid.domain.PowerCommand;
import org.jclouds.gogrid.domain.Server;
import org.jclouds.gogrid.predicates.ServerLatestJobCompleted;
import org.jclouds.predicates.RetryablePredicate;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
        private final Function<String, InetAddress> stringIpToInetAddress;
        private final Function<Size, String> sizeToRam;
        private final Map<String, NodeState> serverStateToNodeState;
        private RetryablePredicate<Server> serverLatestJobCompleted;

        @Inject
        protected GoGridAddNodeWithTagStrategy(GoGridClient client,
                                               Function<String, InetAddress> stringIpToInetAddress,
                                               Map<String, NodeState> serverStateToNodeState,
                                               Function<Size, String> sizeToRam) {
            this.client = client;
            this.stringIpToInetAddress = stringIpToInetAddress;
            this.serverStateToNodeState = serverStateToNodeState;
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
            addedServer = Iterables.getOnlyElement(
                    client.getServerServices().getServersByName(addedServer.getName())
            );

            NodeMetadata node = new NodeMetadataImpl(String.valueOf(addedServer.getId()), addedServer.getName(),
                    template.getLocation().getId(), null, ImmutableMap.<String, String> of(), tag,
                    serverStateToNodeState.get(addedServer.getState().getName()),
                    ImmutableSet.<InetAddress>of(stringIpToInetAddress.apply(addedServer.getIp().getIp())),
                    ImmutableList.<InetAddress> of(), ImmutableMap.<String, String> of(),
                    /*todo give proper credentials*/ new Credentials("root", ""));
            return node;
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
                .put("Starting", NodeState.SUSPENDED)
                .put("Off", NodeState.TERMINATED)
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
                if(ramRequired > 8 * 1024) return "8GB";
                if(ramRequired > 4 * 1024) return "4GB";
                if(ramRequired > 2 * 1024) return "2GB";
                if(ramRequired > 1024) return "1GB";
                return "512MB"; /*smallest*/
            }
        };
    }

    @Singleton
    private static class ServerToNodeMetadata implements Function<Server, NodeMetadata> {
        private final Map<String, NodeState> serverStateToNodeState;
        private final Function<String, InetAddress> stringIpToInetAddress;

        @Inject
        ServerToNodeMetadata(Map<String, NodeState> serverStateToNodeState,
                             Function<String, InetAddress> stringIpToInetAddress) {
            this.serverStateToNodeState = serverStateToNodeState;
            this.stringIpToInetAddress = stringIpToInetAddress;
        }

        @Override
        public NodeMetadata apply(Server from) {
            String locationId = "Unavailable";
            String tag = from.getName();
            Credentials creds = null; //todo use password service to get the password
            Set<InetAddress> ipSet =
                    ImmutableSet.of(stringIpToInetAddress.apply(from.getIp().getIp()));
            NodeState state = serverStateToNodeState.get(from.getState().getName());

            return new NodeMetadataImpl(from.getId() + "", from.getName(), locationId, null,
                    ImmutableMap.<String, String> of(), tag, state,
                    ipSet,
                    ImmutableList.<InetAddress> of(), ImmutableMap.<String, String> of(), creds);

        }
    }


}
