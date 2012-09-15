/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;

import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.abiquo.domain.cloud.VirtualDatacenter;
import org.jclouds.abiquo.domain.cloud.VirtualMachine;
import org.jclouds.abiquo.domain.cloud.VirtualMachineTemplate;
import org.jclouds.abiquo.domain.infrastructure.Datacenter;
import org.jclouds.abiquo.domain.network.Ip;
import org.jclouds.abiquo.domain.network.PrivateIp;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.logging.Logger;
import org.jclouds.rest.AuthorizationException;

import com.abiquo.server.core.cloud.VirtualMachineState;
import com.google.common.base.Function;
import com.google.common.base.Predicates;

/**
 * Links a {@link VirtualMachine} object to a {@link NodeMetadata} one.
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class VirtualMachineToNodeMetadata implements Function<VirtualMachine, NodeMetadata>
{
    @Resource
    protected Logger logger = Logger.NULL;

    private final VirtualMachineTemplateToImage virtualMachineTemplateToImage;

    private final VirtualMachineTemplateToHardware virtualMachineTemplateToHardware;

    private final VirtualMachineStateToNodeState virtualMachineStateToNodeState;

    private final DatacenterToLocation datacenterToLocation;

    @Inject
    public VirtualMachineToNodeMetadata(
        final VirtualMachineTemplateToImage virtualMachineTemplateToImage,
        final VirtualMachineTemplateToHardware virtualMachineTemplateToHardware,
        final VirtualMachineStateToNodeState virtualMachineStateToNodeState,
        final DatacenterToLocation datacenterToLocation)
    {
        this.virtualMachineTemplateToImage =
            checkNotNull(virtualMachineTemplateToImage, "virtualMachineTemplateToImage");
        this.virtualMachineTemplateToHardware =
            checkNotNull(virtualMachineTemplateToHardware, "virtualMachineTemplateToHardware");
        this.virtualMachineStateToNodeState =
            checkNotNull(virtualMachineStateToNodeState, "virtualMachineStateToNodeState");
        this.datacenterToLocation = checkNotNull(datacenterToLocation, "datacenterToLocation");
    }

    @Override
    public NodeMetadata apply(final VirtualMachine vm)
    {
        NodeMetadataBuilder builder = new NodeMetadataBuilder();
        builder.ids(vm.getId().toString());
        builder.uri(vm.getURI());
        builder.name(vm.getNameLabel());
        builder.group(vm.getVirtualAppliance().getName());

        // TODO: builder.credentials() (http://jira.abiquo.com/browse/ABICLOUDPREMIUM-3647)
        VirtualDatacenter vdc = vm.getVirtualDatacenter();

        // Location details
        try
        {
            Datacenter datacenter = vdc.getDatacenter();
            builder.location(datacenterToLocation.apply(datacenter));
        }
        catch (AuthorizationException ex)
        {
            logger.debug("User does not have permissions to see the location of the node");
        }

        // Image details
        VirtualMachineTemplate template = vm.getTemplate();
        Image image = virtualMachineTemplateToImage.apply(template);
        builder.imageId(image.getId().toString());
        builder.operatingSystem(image.getOperatingSystem());

        // Hardware details
        Hardware defaultHardware = virtualMachineTemplateToHardware.apply(template);
        Hardware hardware =
            new HardwareBuilder() //
                .ids(defaultHardware.getId()) //
                .uri(defaultHardware.getUri()) //
                .name(defaultHardware.getName()) //
                .supportsImage(defaultHardware.supportsImage()) //
                .ram(vm.getRam()) //
                .hypervisor(vdc.getHypervisorType().name()) //
                .processor(
                    new Processor(vm.getCpu(), VirtualMachineTemplateToHardware.DEFAULT_CORE_SPEED)) //
                .build();
        builder.hardware(hardware);

        // Networking configuration
        List<Ip< ? , ? >> nics = vm.listAttachedNics();
        builder.privateAddresses(ips(filter(nics, Predicates.instanceOf(PrivateIp.class))));
        builder.publicAddresses(ips(filter(nics,
            Predicates.not(Predicates.instanceOf(PrivateIp.class)))));

        // Node state
        VirtualMachineState state = vm.getState();
        builder.status(virtualMachineStateToNodeState.apply(state));
        builder.backendStatus(state.name());

        return builder.build();
    }

    private static Iterable<String> ips(final Iterable<Ip< ? , ? >> nics)
    {
        return transform(nics, new Function<Ip< ? , ? >, String>()
        {
            @Override
            public String apply(final Ip< ? , ? > nic)
            {
                return nic.getIp();
            }
        });
    }

}
