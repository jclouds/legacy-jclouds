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

package org.jclouds.abiquo.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.getFirst;

import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.compute.exception.NotEnoughResourcesException;
import org.jclouds.abiquo.compute.options.AbiquoTemplateOptions;
import org.jclouds.abiquo.domain.cloud.Conversion;
import org.jclouds.abiquo.domain.cloud.VirtualDatacenter;
import org.jclouds.abiquo.domain.cloud.VirtualMachine;
import org.jclouds.abiquo.domain.cloud.VirtualMachineTemplate;
import org.jclouds.abiquo.domain.enterprise.Enterprise;
import org.jclouds.abiquo.domain.enterprise.User;
import org.jclouds.abiquo.domain.exception.AbiquoException;
import org.jclouds.abiquo.domain.infrastructure.Datacenter;
import org.jclouds.abiquo.domain.network.Ip;
import org.jclouds.abiquo.domain.network.Network;
import org.jclouds.abiquo.domain.network.PrivateNetwork;
import org.jclouds.abiquo.domain.network.UnmanagedNetwork;
import org.jclouds.abiquo.features.services.CloudService;
import org.jclouds.abiquo.predicates.cloud.VirtualDatacenterPredicates;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.Logger;
import org.jclouds.rest.RestContext;

import com.abiquo.model.enumerator.ConversionState;
import com.abiquo.model.enumerator.HypervisorType;
import com.google.common.base.Predicate;

/**
 * Helper methods to perform {@link AbiquoComputeServiceAdapter} operations.
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class AbiquoComputeServiceHelper
{
    @Resource
    @Named(ComputeServiceConstants.COMPUTE_LOGGER)
    protected Logger logger = Logger.NULL;

    private RestContext<AbiquoApi, AbiquoAsyncApi> context;

    private CloudService cloudService;

    @Inject
    public AbiquoComputeServiceHelper(final RestContext<AbiquoApi, AbiquoAsyncApi> context,
        final CloudService cloudService)
    {
        super();
        this.context = checkNotNull(context, "context");
        this.cloudService = checkNotNull(cloudService, "cloudService");
    }

    /**
     * Gets a virtual datacenter where the given template can be deployed.
     * <p>
     * If no compatible virtual datacenter is found, one will be created, if possible.
     * 
     * @param user The current user.
     * @param enterprise The enterprise of the current user.
     * @param datacenter The datacenter of the template.
     * @param template The template to deploy.
     * @param options The template options
     * @return The virtual datacenter to be used to deploy the template or <code>null</code> if none
     *         was found and a compatible one could not be created.
     */
    public VirtualDatacenter getOrCreateVirtualDatacenter(final User user,
        final Enterprise enterprise, final Datacenter datacenter,
        final VirtualMachineTemplate template, final AbiquoTemplateOptions options)
    {
        Iterable<VirtualDatacenter> compatibles =
            findCompatibleVirtualDatacenters(datacenter, template);

        VirtualDatacenter vdc =
            options.getVirtualDatacenter() == null ? getFirst(compatibles, null) : find(
                compatibles, VirtualDatacenterPredicates.name(options.getVirtualDatacenter()));

        if (vdc == null)
        {
            vdc =
                createCompatibleVirtualDatacenter(user, enterprise, datacenter, template,
                    options.getVirtualDatacenter());
            if (vdc == null)
            {
                throw new NotEnoughResourcesException("There are not resources to deploy the given template");
            }
        }

        return vdc;
    }

    /**
     * Find the virtual datacenters compatible with the given template.
     * 
     * @param datacenter The datacenter of the template.
     * @param template The template to deploy.
     * @return The virtual datacenters compatible with the given template.
     */
    public Iterable<VirtualDatacenter> findCompatibleVirtualDatacenters(
        final Datacenter datacenter, final VirtualMachineTemplate template)
    {
        Iterable<VirtualDatacenter> vdcs =
            cloudService.listVirtualDatacenters(VirtualDatacenterPredicates.datacenter(datacenter));

        return filter(vdcs, new Predicate<VirtualDatacenter>()
        {
            @Override
            public boolean apply(final VirtualDatacenter vdc)
            {
                return isTemplateCompatibleWithHypervisor(template, vdc.getHypervisorType());
            }
        });
    }

    /**
     * Configure networking resources for the given virtual machine.
     * 
     * @param vm The virtual machine to configure.
     * @param gatewayNetwork The network to be used as a gateway.
     * @param ips The ips to attach to the virtual machine.
     */
    public void configureNetwork(final VirtualMachine vm,
        @Nullable final Network< ? > gatewayNetwork,
        @Nullable final List<Ip< ? , ? extends Network< ? >>> ips,
        @Nullable final List<UnmanagedNetwork> unmanagedIps)
    {
        if (ips != null)
        {
            // TODO: External ips don't have the right link
            // (http://jira.abiquo.com/browse/ABICLOUDPREMIUM-3650)

            if (gatewayNetwork == null)
            {
                // By default the network of the first ip will be used as a gateway
                vm.setNics(ips, unmanagedIps);
            }
            else
            {
                vm.setNics(gatewayNetwork, ips, unmanagedIps);
            }
        }
    }

    /**
     * Create a new virtual datacenter compatible with the given template.
     * 
     * @param user The current user.
     * @param enterprise The enterprise of the current user.
     * @param datacenter The datacenter of the template.
     * @param template The template to deploy.
     * @return
     */
    private VirtualDatacenter createCompatibleVirtualDatacenter(final User user,
        final Enterprise enterprise, final Datacenter datacenter,
        final VirtualMachineTemplate template, final String name)
    {
        PrivateNetwork defaultNetwork =
            PrivateNetwork.builder(context).name("DefaultNetwork").gateway("192.168.1.1")
                .address("192.168.1.0").mask(24).build();

        VirtualDatacenter vdc =
            VirtualDatacenter.builder(context, datacenter, enterprise).network(defaultNetwork)
                .build();

        // Find the first hypervisor in the datacenter compatible with the template
        for (HypervisorType type : HypervisorType.values())
        {
            if (isTemplateCompatibleWithHypervisor(template, type))
            {
                try
                {
                    logger.info("Trying to create a virtual datacenter of type %s", type.name());
                    vdc.setName(name != null ? name : "JC-" + type.name());
                    vdc.setHypervisorType(type);
                    vdc.save();

                    logger.info("Virtual datacenter created");

                    return vdc;
                }
                catch (AbiquoException ex)
                {
                    // Just catch the error thrown when no hypervisors of the given type are
                    // available in the datacenter
                    if (ex.hasError("VDC-1"))
                    {
                        continue;
                    }
                    else
                    {
                        throw ex;
                    }
                }
            }
        }

        logger.warn("Could not create a compatible virtual datacenter for template of type %s",
            template.getDiskFormatType().name());

        return null;
    }

    /**
     * Check if the given template type is compatible with the given hypervisor type.
     * 
     * @param template The template to check.
     * @param type The type of the hypervisor.
     * @return Boolean indicating if the given template type is compatible with the given hypervisor
     *         type.
     */
    private static boolean isTemplateCompatibleWithHypervisor(
        final VirtualMachineTemplate template, final HypervisorType type)
    {
        boolean compatible = type.isCompatible(template.getDiskFormatType());
        if (!compatible)
        {
            List<Conversion> compatibleConversions =
                template.listConversions(type, ConversionState.FINISHED);
            compatible = compatibleConversions != null && !compatibleConversions.isEmpty();
        }
        return compatible;
    }

}
