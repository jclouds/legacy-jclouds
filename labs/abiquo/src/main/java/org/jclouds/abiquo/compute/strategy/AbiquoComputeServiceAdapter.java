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

import java.util.Arrays;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.compute.options.AbiquoTemplateOptions;
import org.jclouds.abiquo.domain.cloud.VirtualAppliance;
import org.jclouds.abiquo.domain.cloud.VirtualDatacenter;
import org.jclouds.abiquo.domain.cloud.VirtualMachine;
import org.jclouds.abiquo.domain.cloud.VirtualMachineTemplate;
import org.jclouds.abiquo.domain.enterprise.Enterprise;
import org.jclouds.abiquo.domain.enterprise.User;
import org.jclouds.abiquo.domain.infrastructure.Datacenter;
import org.jclouds.abiquo.features.services.AdministrationService;
import org.jclouds.abiquo.features.services.CloudService;
import org.jclouds.abiquo.features.services.MonitoringService;
import org.jclouds.abiquo.monitor.VirtualMachineMonitor;
import org.jclouds.abiquo.predicates.cloud.VirtualAppliancePredicates;
import org.jclouds.abiquo.predicates.cloud.VirtualMachineTemplatePredicates;
import org.jclouds.abiquo.predicates.infrastructure.DatacenterPredicates;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.rest.RestContext;

import com.abiquo.server.core.cloud.VirtualMachineState;
import com.google.common.base.Predicate;

/**
 * Defines the connection between the {@link AbiquoApi} implementation and the jclouds
 * {@link ComputeService}.
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class AbiquoComputeServiceAdapter
    implements
    ComputeServiceAdapter<VirtualMachine, VirtualMachineTemplate, VirtualMachineTemplate, Datacenter>
{
    @Resource
    @Named(ComputeServiceConstants.COMPUTE_LOGGER)
    protected Logger logger = Logger.NULL;

    private final RestContext<AbiquoApi, AbiquoAsyncApi> context;

    private final AdministrationService adminService;

    private final CloudService cloudService;

    private final MonitoringService monitoringService;

    private AbiquoComputeServiceHelper helper;

    @Inject
    public AbiquoComputeServiceAdapter(final RestContext<AbiquoApi, AbiquoAsyncApi> context,
        final AdministrationService adminService, final CloudService cloudService,
        final MonitoringService monitoringService, final AbiquoComputeServiceHelper helper)
    {
        super();
        this.context = checkNotNull(context, "context");
        this.adminService = checkNotNull(adminService, "adminService");
        this.cloudService = checkNotNull(cloudService, "cloudService");
        this.monitoringService = checkNotNull(monitoringService, "monitoringService");
        this.helper = checkNotNull(helper, "helper");
    }

    @Override
    public NodeAndInitialCredentials<VirtualMachine> createNodeWithGroupEncodedIntoName(
        final String tag, final String name, final Template template)
    {
        AbiquoTemplateOptions options = template.getOptions().as(AbiquoTemplateOptions.class);

        User user = adminService.getCurrentUser();
        Enterprise enterprise = adminService.getCurrentEnterprise();

        Datacenter datacenter =
            enterprise.findAllowedDatacenter(DatacenterPredicates.id(Integer.valueOf(template
                .getLocation().getId())));

        VirtualMachineTemplate virtualMachineTemplate =
            enterprise.getTemplateInRepository(datacenter,
                Integer.valueOf(template.getImage().getId()));

        VirtualDatacenter vdc =
            helper.getOrCreateVirtualDatacenter(user, enterprise, datacenter,
                virtualMachineTemplate, options);

        // Load the virtual appliance or create it
        VirtualAppliance vapp = vdc.findVirtualAppliance(VirtualAppliancePredicates.name(tag));
        if (vapp == null)
        {
            vapp = VirtualAppliance.builder(context, vdc).name(tag).build();
            vapp.save();
        }

        Integer overrideCores = options.getOverrideCores();
        Integer overrideRam = options.getOverrideRam();

        VirtualMachine vm = VirtualMachine.builder(context, vapp, virtualMachineTemplate) //
            .nameLabel(name) //
            .cpu(overrideCores != null ? overrideCores : totalCores(template.getHardware())) //
            .ram(overrideRam != null ? overrideRam : template.getHardware().getRam()) //
            .password(options.getVncPassword()) // Can be null
            .build();

        vm.save();

        // Once the virtual machine is created, override the default network settings if needed
        helper.configureNetwork(vm, options.getGatewayNetwork(), options.getIps() == null ? null
            : Arrays.asList(options.getIps()),
            options.getUnmanagedIps() == null ? null : Arrays.asList(options.getUnmanagedIps()));

        VirtualMachineMonitor monitor = monitoringService.getVirtualMachineMonitor();
        vm.deploy();
        monitor.awaitCompletionDeploy(vm);

        return new NodeAndInitialCredentials<VirtualMachine>(vm, vm.getId().toString(), template
            .getImage().getDefaultCredentials());
    }

    @Override
    public Iterable<VirtualMachineTemplate> listHardwareProfiles()
    {
        // Abiquo does not have the hardwre profiles concept. Users can consume CPU and RAM
        // resources limited only by the Enterprise or Virtual datacenter limits.
        return listImages();
    }

    @Override
    public Iterable<VirtualMachineTemplate> listImages()
    {
        Enterprise enterprise = adminService.getCurrentEnterprise();
        return enterprise.listTemplates();
    }

    @Override
    public VirtualMachineTemplate getImage(final String id)
    {
        Enterprise enterprise = adminService.getCurrentEnterprise();
        return enterprise.findTemplate(VirtualMachineTemplatePredicates.id(Integer.valueOf(id)));
    }

    @Override
    public Iterable<Datacenter> listLocations()
    {
        Enterprise enterprise = adminService.getCurrentEnterprise();
        return enterprise.listAllowedDatacenters();
    }

    @Override
    public VirtualMachine getNode(final String id)
    {
        // FIXME: Try to avoid calling the cloudService.findVirtualMachine. Navigate the hierarchy
        // instead.
        return cloudService.findVirtualMachine(vmId(id));
    }

    @Override
    public void destroyNode(final String id)
    {
        VirtualMachine vm = getNode(id);
        vm.delete();
    }

    @Override
    public void rebootNode(final String id)
    {
        VirtualMachineMonitor monitor = monitoringService.getVirtualMachineMonitor();
        VirtualMachine vm = getNode(id);
        vm.reboot();
        monitor.awaitState(VirtualMachineState.ON, vm);
    }

    @Override
    public void resumeNode(final String id)
    {
        VirtualMachineMonitor monitor = monitoringService.getVirtualMachineMonitor();
        VirtualMachine vm = getNode(id);
        vm.changeState(VirtualMachineState.ON);
        monitor.awaitState(VirtualMachineState.ON, vm);
    }

    @Override
    public void suspendNode(final String id)
    {
        VirtualMachineMonitor monitor = monitoringService.getVirtualMachineMonitor();
        VirtualMachine vm = getNode(id);
        vm.changeState(VirtualMachineState.PAUSED);
        monitor.awaitState(VirtualMachineState.PAUSED, vm);
    }

    @Override
    public Iterable<VirtualMachine> listNodes()
    {
        return cloudService.listVirtualMachines();
    }

    private static Predicate<VirtualMachine> vmId(final String id)
    {
        return new Predicate<VirtualMachine>()
        {
            @Override
            public boolean apply(final VirtualMachine input)
            {
                return Integer.valueOf(id).equals(input.getId());
            }
        };
    }

    private static int totalCores(final Hardware hardware)
    {
        double cores = 0;
        for (Processor processor : hardware.getProcessors())
        {
            cores += processor.getCores();
        }
        return Double.valueOf(cores).intValue();
    }

}
