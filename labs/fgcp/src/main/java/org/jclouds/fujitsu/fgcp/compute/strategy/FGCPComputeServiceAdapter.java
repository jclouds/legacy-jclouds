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
package org.jclouds.fujitsu.fgcp.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.fujitsu.fgcp.FGCPApi;
import org.jclouds.fujitsu.fgcp.FGCPAsyncApi;
import org.jclouds.fujitsu.fgcp.domain.DiskImage;
import org.jclouds.fujitsu.fgcp.domain.ServerType;
import org.jclouds.fujitsu.fgcp.domain.VServer;
import org.jclouds.fujitsu.fgcp.domain.VServerStatus;
import org.jclouds.fujitsu.fgcp.domain.VServerWithDetails;
import org.jclouds.fujitsu.fgcp.domain.VSystem;
import org.jclouds.fujitsu.fgcp.domain.VSystemWithDetails;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.RetryablePredicate;

import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Defines the connection between the {@link org.jclouds.fujitsu.fgcp.FGCPApi}
 * implementation and the jclouds {@link org.jclouds.compute.ComputeService}.
 * Bound in FGCPComputeServiceAdapter.
 * 
 * @author Dies Koper
 */
@Singleton
public class FGCPComputeServiceAdapter implements
        ComputeServiceAdapter<VServerMetadata, ServerType, DiskImage, Location> {

    @Resource
    @Named(ComputeServiceConstants.COMPUTE_LOGGER)
    protected Logger logger = Logger.NULL;

    private final FGCPApi api;
    private final FGCPAsyncApi asyncApi;
    protected final Predicate<String> serverStopped = null;

    @Inject
    public FGCPComputeServiceAdapter(FGCPApi api, FGCPAsyncApi asyncApi) {
        this.api = checkNotNull(api, "api");
        this.asyncApi = checkNotNull(asyncApi, "asyncApi");
        // this.serverStopped = new RetryablePredicate<String>(serverStopped,
        // timeouts.nodeSuspended);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeAndInitialCredentials<VServerMetadata> createNodeWithGroupEncodedIntoName(
            String group, String name, Template template) {
        String id = api.getVirtualSystemApi().createServer(name,
                template.getHardware().getName(), template.getImage().getId(),
                template.getLocation().getId());
        // Find vsys (how? create new? default to first found?)
        // Target network DMZ/SECURE1/SECURE2 (how? default to DMZ?)
        //
        // Determine remaining params: [vserverType,diskImageId,networkId]
        // what if no vsys exists yet? Location.AU(.contractId) creates 3? tier
        // skeleton vsys and DMZ is picked?
        String user = template.getImage().getOperatingSystem().getFamily() == OsFamily.WINDOWS ? "Administrator"
                : "root";
        // three options: use function to fill from existing data (as is done in
        // DC) or
        // api.getVirtualServerApi().get(id); or
        // return getNode(id);
        VServerMetadata server = new VServerMetadata(id, name, template);
        return new NodeAndInitialCredentials<VServerMetadata>(server, id,
                LoginCredentials
                        .builder()
                        .identity(user)
                        .password(
                                api.getVirtualServerApi()
                                        .getInitialPassword(id)).build());
        // throw new
        // UnsupportedOperationException("createNodeWithGroupEncodedIntoName not supported");
    }

    @Override
    public Iterable<ServerType> listHardwareProfiles() {
        return api.getVirtualDCApi().listServerTypes();
    }

    @Override
    public Iterable<DiskImage> listImages() {
        return api.getVirtualDCApi().listDiskImages();
    }

    @Override
    public DiskImage getImage(String id) {
        return api.getDiskImageApi().get(id);
    }

    @Override
    public Iterable<Location> listLocations() {
        // Not using the adapter to determine locations
        // see SystemAndNetworkSegmentToLocationSupplier
        return ImmutableSet.<Location> of();
    }

    @Override
    public VServerMetadata getNode(String id) {
        List<ListenableFuture> futures = new ArrayList<ListenableFuture>();
        // retrieve with details
        futures.add(asyncApi.getVirtualServerApi().getDetails(id));
        futures.add(asyncApi.getVirtualServerApi().getStatus(id));
        futures.add(asyncApi.getVirtualServerApi().getInitialPassword(id));
        // get status
        // get init pwd
        // public ips
        // list disk images to get user name (Windows/root)
        return null;// api.getVirtualServerApi().get(id);
    }

    @Override
    public Iterable<VServerMetadata> listNodes() {
        ImmutableSet.Builder<VServerMetadata> servers = ImmutableSet
                .<VServerMetadata> builder();

        Set<VSystem> systems = api.getVirtualDCApi().listVirtualSystems();
        List<ListenableFuture<VSystemWithDetails>> futures = new ArrayList<ListenableFuture<VSystemWithDetails>>();
        for (VSystem system : systems) {

            futures.add(asyncApi.getVirtualSystemApi().getDetails(
                    system.getId()));
        }
        try {
            for (VSystemWithDetails system : Futures.successfulAsList(futures)
                    .get()) {

                if (system != null) {

                    for (VServer server : system.getServers()) {

                        servers.add(new VServerMetadata(server));
                    }
                }
            }
        } catch (InterruptedException e) {
            throw Throwables.propagate(e);
        } catch (ExecutionException e) {
            throw Throwables.propagate(e);
        }

        return servers.build();
    }

    @Override
    public void destroyNode(String id) {
        api.getVirtualServerApi().destroy(id);
    }

    @Override
    public void rebootNode(String id) {
        suspendNode(id);
        // wait until fully stopped.
        serverStopped.apply(id);
        resumeNode(id);
    }

    @Override
    public void resumeNode(String id) {
        api.getVirtualServerApi().start(id);
    }

    @Override
    public void suspendNode(String id) {
        api.getVirtualServerApi().stop(id);
    }
}
