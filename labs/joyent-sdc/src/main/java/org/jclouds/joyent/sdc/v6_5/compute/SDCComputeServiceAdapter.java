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
package org.jclouds.joyent.sdc.v6_5.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.joyent.sdc.v6_5.SDCClient;
import org.jclouds.joyent.sdc.v6_5.domain.Dataset;
import org.jclouds.joyent.sdc.v6_5.domain.Machine;
import org.jclouds.joyent.sdc.v6_5.domain.datacenterscoped.DatacenterAndId;
import org.jclouds.joyent.sdc.v6_5.domain.datacenterscoped.DatasetInDatacenter;
import org.jclouds.joyent.sdc.v6_5.domain.datacenterscoped.MachineInDatacenter;
import org.jclouds.joyent.sdc.v6_5.domain.datacenterscoped.PackageInDatacenter;
import org.jclouds.joyent.sdc.v6_5.options.CreateMachineOptions;
import org.jclouds.location.Zone;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * The adapter used by the SDCComputeServiceContextModule to interface the
 * SDC-specific domain model to the computeService generic domain model.
 * 
 * @author Adrian Cole
 */
public class SDCComputeServiceAdapter implements
      ComputeServiceAdapter<MachineInDatacenter, PackageInDatacenter, DatasetInDatacenter, Location> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final SDCClient sdcClient;
   protected final Supplier<Set<String>> datacenterIds;

   @Inject
   public SDCComputeServiceAdapter(SDCClient sdcClient, @Zone Supplier<Set<String>> datacenterIds) {
      this.sdcClient = checkNotNull(sdcClient, "sdcClient");
      this.datacenterIds = checkNotNull(datacenterIds, "datacenterIds");
   }

   @Override
   public NodeAndInitialCredentials<MachineInDatacenter> createNodeWithGroupEncodedIntoName(String group, String name,
         Template template) {

      LoginCredentials.Builder credentialsBuilder = LoginCredentials.builder();

      CreateMachineOptions options = new CreateMachineOptions();
      // TODO: assign template.getOptions.metadata/tags

      String datacenterId = template.getLocation().getId();
      String datasetId = template.getImage().getProviderId();
      String packageId = template.getHardware().getProviderId();

      logger.debug(">> creating new machine datacenter(%s) name(%s) package(%s) dataset(%s) options(%s)", datacenterId,
            name, packageId, datasetId, options);
      Machine machine = sdcClient.getMachineClientForDatacenter(datacenterId).createMachine(name, packageId, datasetId,
            options);

      logger.trace("<< machine(%s)", machine.getId());

      MachineInDatacenter machineInDatacenter = new MachineInDatacenter(machine, datacenterId);
      // TODO: credentials or password
      // if (!privateKey.isPresent())
      // credentialsBuilder.password(lightweightMachine.getAdminPass());
      return new NodeAndInitialCredentials<MachineInDatacenter>(machineInDatacenter, machineInDatacenter.slashEncode(),
            credentialsBuilder.build());
   }

   @Override
   public Iterable<PackageInDatacenter> listHardwareProfiles() {
      Builder<PackageInDatacenter> builder = ImmutableSet.builder();
      for (final String datacenterId : datacenterIds.get()) {
         builder.addAll(transform(sdcClient.getPackageClientForDatacenter(datacenterId).listPackages(),
               new Function<org.jclouds.joyent.sdc.v6_5.domain.Package, PackageInDatacenter>() {

                  @Override
                  public PackageInDatacenter apply(org.jclouds.joyent.sdc.v6_5.domain.Package arg0) {
                     return new PackageInDatacenter(arg0, datacenterId);
                  }

               }));
      }
      return builder.build();
   }

   @Override
   public Iterable<DatasetInDatacenter> listImages() {
      Builder<DatasetInDatacenter> builder = ImmutableSet.builder();
      for (final String datacenterId : datacenterIds.get()) {
         builder.addAll(transform(sdcClient.getDatasetClientForDatacenter(datacenterId).listDatasets(),
               new Function<Dataset, DatasetInDatacenter>() {

                  @Override
                  public DatasetInDatacenter apply(Dataset arg0) {
                     return new DatasetInDatacenter(arg0, datacenterId);
                  }

               }));
      }
      return builder.build();
   }

   @Override
   public Iterable<MachineInDatacenter> listNodes() {
      Builder<MachineInDatacenter> builder = ImmutableSet.builder();
      for (final String datacenterId : datacenterIds.get()) {
         builder.addAll(transform(sdcClient.getMachineClientForDatacenter(datacenterId).listMachines(),
               new Function<Machine, MachineInDatacenter>() {

                  @Override
                  public MachineInDatacenter apply(Machine arg0) {
                     return new MachineInDatacenter(arg0, datacenterId);
                  }

               }));
      }
      return builder.build();
   }

   @Override
   public Iterable<Location> listLocations() {
      // locations provided by guice
      return ImmutableSet.of();
   }

   @Override
   public MachineInDatacenter getNode(String id) {
      DatacenterAndId datacenterAndId = DatacenterAndId.fromSlashEncoded(id);
      Machine machine = sdcClient.getMachineClientForDatacenter(datacenterAndId.getDatacenter()).getMachine(
            datacenterAndId.getId());
      return machine == null ? null : new MachineInDatacenter(machine, datacenterAndId.getDatacenter());
   }

   @Override
   public DatasetInDatacenter getImage(String id) {
      DatacenterAndId datacenterAndId = DatacenterAndId.fromSlashEncoded(id);
      Dataset dataset = sdcClient.getDatasetClientForDatacenter(datacenterAndId.getDatacenter()).getDataset(
            datacenterAndId.getId());
      return dataset == null ? null : new DatasetInDatacenter(dataset, datacenterAndId.getDatacenter());
   }

   @Override
   public void destroyNode(String id) {
      DatacenterAndId datacenterAndId = DatacenterAndId.fromSlashEncoded(id);
      sdcClient.getMachineClientForDatacenter(datacenterAndId.getDatacenter()).deleteMachine(datacenterAndId.getId());
   }

   @Override
   public void rebootNode(String id) {
      DatacenterAndId datacenterAndId = DatacenterAndId.fromSlashEncoded(id);
      sdcClient.getMachineClientForDatacenter(datacenterAndId.getDatacenter()).rebootMachine(datacenterAndId.getId());
   }

   @Override
   public void resumeNode(String id) {
      DatacenterAndId datacenterAndId = DatacenterAndId.fromSlashEncoded(id);
      sdcClient.getMachineClientForDatacenter(datacenterAndId.getDatacenter()).stopMachine(datacenterAndId.getId());

   }

   @Override
   public void suspendNode(String id) {
      DatacenterAndId datacenterAndId = DatacenterAndId.fromSlashEncoded(id);
      sdcClient.getMachineClientForDatacenter(datacenterAndId.getDatacenter()).startMachine(datacenterAndId.getId());

   }

}
