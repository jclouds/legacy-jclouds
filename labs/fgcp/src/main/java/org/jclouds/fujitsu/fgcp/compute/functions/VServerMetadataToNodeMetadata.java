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
package org.jclouds.fujitsu.fgcp.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.fujitsu.fgcp.compute.strategy.VServerMetadata;
import org.jclouds.fujitsu.fgcp.domain.VServerWithDetails;
import org.jclouds.fujitsu.fgcp.domain.VServerStatus;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * @author Dies Koper
 */
@Singleton
public class VServerMetadataToNodeMetadata implements
        Function<VServerMetadata, NodeMetadata> {

    public static final Map<VServerStatus, Status> vServerToStatus = ImmutableMap
            .<VServerStatus, Status> builder()
            .put(VServerStatus.DEPLOYING, Status.PENDING)
            .put(VServerStatus.RUNNING, Status.RUNNING)
            .put(VServerStatus.STOPPING, Status.PENDING)
            .put(VServerStatus.STOPPED, Status.SUSPENDED)
            .put(VServerStatus.STARTING, Status.PENDING)
            .put(VServerStatus.FAILOVER, Status.RUNNING)
            .put(VServerStatus.UNEXPECTED_STOP, Status.SUSPENDED)
            .put(VServerStatus.RESTORING, Status.PENDING)
            .put(VServerStatus.BACKUP_ING, Status.PENDING)
            .put(VServerStatus.ERROR, Status.ERROR)
            .put(VServerStatus.START_ERROR, Status.ERROR)
            .put(VServerStatus.STOP_ERROR, Status.ERROR)
            .put(VServerStatus.CHANGE_TYPE, Status.PENDING)
            .put(VServerStatus.REGISTERING, Status.PENDING)
            .put(VServerStatus.UNRECOGNIZED, Status.UNRECOGNIZED).build();

    @Resource
    @Named(ComputeServiceConstants.COMPUTE_LOGGER)
    protected Logger logger = Logger.NULL;

    protected final Supplier<Set<? extends Location>> locations;
    protected final Supplier<Set<? extends Image>> images;
    protected final Supplier<Set<? extends Hardware>> hardwares;

    private static class FindImageForVServer implements Predicate<Image> {
        private final VServerWithDetails server;

        private FindImageForVServer(VServerWithDetails server) {
            this.server = server;
        }

        @Override
        public boolean apply(Image input) {
            return input.getUri().equals(server.getDiskimageId());
        }
    }

    /*
     * private static class FindHardwareForVServer implements
     * Predicate<Hardware> { private final server server;
     * 
     * private FindHardwareForVServer(server server) { this.VServer = server; }
     * 
     * @Override public boolean apply(Hardware input) { return
     * input.getUri().equals(server.getHardwareProfile()); } }
     */

    /*
     * protected Hardware parseHardware(VServerMetadata from) { try { return
     * Iterables.find(hardwares.get(), new FindHardwareForVServer(from)); }
     * catch (NoSuchElementException e) {
     * logger.warn("could not find a matching hardware for server %s", from); }
     * return null; }
     */

    protected OperatingSystem parseOperatingSystem(VServerWithDetails from) {
        try {
            return Iterables.find(images.get(), new FindImageForVServer(from))
                    .getOperatingSystem();
        } catch (NoSuchElementException e) {
            logger.warn("could not find a matching image for server %s", from);
        }
        return null;
    }

    /*
     * private static class FindLocationForVServer implements
     * Predicate<Location> { private final server server;
     * 
     * private FindLocationForVServer(server server) { this.VServer = server; }
     * 
     * @Override public boolean apply(Location input) { return
     * input.getId().equals(server.getRealm().toASCIIString()); } }
     */

    /*
     * protected Location parseLocation(VServerMetadata from) { try { return
     * Iterables.find(locations.get(), new FindLocationForVServer(from)); }
     * catch (NoSuchElementException e) {
     * logger.warn("could not find a matching realm for server %s", from); }
     * return null; }
     */

    @Inject
    VServerMetadataToNodeMetadata(
            @Memoized Supplier<Set<? extends Location>> locations,
            @Memoized Supplier<Set<? extends Image>> images,
            @Memoized Supplier<Set<? extends Hardware>> hardwares) {
        this.images = checkNotNull(images, "images");
        this.locations = checkNotNull(locations, "locations");
        this.hardwares = checkNotNull(hardwares, "hardwares");
    }

    @Override
    public NodeMetadata apply(VServerMetadata from) {
        NodeMetadataBuilder builder = new NodeMetadataBuilder();
        builder.id(from.getServer().getId());
        builder.name(from.getServer().getName());
        // builder.
        // builder.hardware(from.getServer().getType());
        builder.status(vServerToStatus.get(from.getStatus()));

        builder.imageId(from.getServer().getDiskimageId());
        /*
         * builder.ids(from.getHref().toASCIIString());
         * builder.name(from.getName()); builder.location(parseLocation(from));
         * // * builder.group(parseGroupFromName(from.getName())); // *
         * builder.imageId(from.getImage().toASCIIString());
         * builder.operatingSystem(parseOperatingSystem(from)); // *
         * builder.hardware(parseHardware(from)); // *
         * builder.state(VServerToStatus.get(from.getState()));
         * builder.publicAddresses(from.getPublicAddresses());
         * builder.privateAddresses(from.getPrivateAddresses());
         */
        return builder.build();
    }
}
