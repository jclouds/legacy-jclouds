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
package org.jclouds.joyent.cloudapi.v6_5.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.compute.util.ComputeServiceUtils.metadataAndTagsAsCommaDelimitedValue;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.joyent.cloudapi.v6_5.JoyentCloudClient;
import org.jclouds.joyent.cloudapi.v6_5.domain.Dataset;
import org.jclouds.joyent.cloudapi.v6_5.domain.Machine;
import org.jclouds.joyent.cloudapi.v6_5.domain.datacenterscoped.DatacenterAndId;
import org.jclouds.joyent.cloudapi.v6_5.domain.datacenterscoped.DatasetInDatacenter;
import org.jclouds.joyent.cloudapi.v6_5.domain.datacenterscoped.MachineInDatacenter;
import org.jclouds.joyent.cloudapi.v6_5.domain.datacenterscoped.PackageInDatacenter;
import org.jclouds.joyent.cloudapi.v6_5.options.CreateMachineOptions;
import org.jclouds.location.Zone;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * The adapter used by the JoyentCloudComputeServiceContextModule to interface the
 * JoyentCloud-specific domain model to the computeService generic domain model.
 * 
 * @author Adrian Cole
 */
public class JoyentCloudComputeServiceAdapter implements
      ComputeServiceAdapter<MachineInDatacenter, PackageInDatacenter, DatasetInDatacenter, Location> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final JoyentCloudClient cloudApiClient;
   protected final Supplier<Set<String>> datacenterIds;

   @Inject
   public JoyentCloudComputeServiceAdapter(JoyentCloudClient cloudApiClient, @Zone Supplier<Set<String>> datacenterIds) {
      this.cloudApiClient = checkNotNull(cloudApiClient, "cloudApiClient");
      this.datacenterIds = checkNotNull(datacenterIds, "datacenterIds");
   }

   @Override
   public NodeAndInitialCredentials<MachineInDatacenter> createNodeWithGroupEncodedIntoName(String group, String name,
         Template template) {

      LoginCredentials.Builder credentialsBuilder = LoginCredentials.builder();

      CreateMachineOptions options = new CreateMachineOptions();
      options.name(name);
      options.packageName(template.getHardware().getProviderId());
      options.metadata(metadataAndTagsAsCommaDelimitedValue(template.getOptions()));

      String datacenterId = template.getLocation().getId();
      String datasetURN = template.getImage().getProviderId();

      logger.debug(">> creating new machine datacenter(%s) datasetURN(%s) options(%s)", datacenterId, datasetURN,
            options);
      Machine machine = cloudApiClient.getMachineClientForDatacenter(datacenterId).createWithDataset(datasetURN, options);

      logger.trace("<< machine(%s)", machine.getId());

      MachineInDatacenter machineInDatacenter = new MachineInDatacenter(machine, datacenterId);

      //TODO machineInDatacenter.metadata for password
      if (template.getOptions().getLoginPrivateKey() != null){
         credentialsBuilder.privateKey(template.getOptions().getLoginPrivateKey());
      }
      return new NodeAndInitialCredentials<MachineInDatacenter>(machineInDatacenter, machineInDatacenter.slashEncode(),
            credentialsBuilder.build());
   }

   @Override
   public Iterable<PackageInDatacenter> listHardwareProfiles() {
      Builder<PackageInDatacenter> builder = ImmutableSet.builder();
      for (final String datacenterId : datacenterIds.get()) {
         builder.addAll(transform(cloudApiClient.getPackageClientForDatacenter(datacenterId).list(),
               new Function<org.jclouds.joyent.cloudapi.v6_5.domain.Package, PackageInDatacenter>() {

                  @Override
                  public PackageInDatacenter apply(org.jclouds.joyent.cloudapi.v6_5.domain.Package arg0) {
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
         builder.addAll(transform(cloudApiClient.getDatasetClientForDatacenter(datacenterId).list(),
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
         builder.addAll(transform(cloudApiClient.getMachineClientForDatacenter(datacenterId).list(),
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
      Machine machine = cloudApiClient.getMachineClientForDatacenter(datacenterAndId.getDatacenter()).get(
            datacenterAndId.getId());
      return machine == null ? null : new MachineInDatacenter(machine, datacenterAndId.getDatacenter());
   }

   @Override
   public DatasetInDatacenter getImage(String id) {
      DatacenterAndId datacenterAndId = DatacenterAndId.fromSlashEncoded(id);
      Dataset dataset = cloudApiClient.getDatasetClientForDatacenter(datacenterAndId.getDatacenter()).get(
            datacenterAndId.getId());
      return dataset == null ? null : new DatasetInDatacenter(dataset, datacenterAndId.getDatacenter());
   }

   @Override
   public void destroyNode(String id) {
      DatacenterAndId datacenterAndId = DatacenterAndId.fromSlashEncoded(id);
      cloudApiClient.getMachineClientForDatacenter(datacenterAndId.getDatacenter()).delete(datacenterAndId.getId());
   }

   @Override
   public void rebootNode(String id) {
      DatacenterAndId datacenterAndId = DatacenterAndId.fromSlashEncoded(id);
      cloudApiClient.getMachineClientForDatacenter(datacenterAndId.getDatacenter()).reboot(datacenterAndId.getId());
   }

   @Override
   public void resumeNode(String id) {
      DatacenterAndId datacenterAndId = DatacenterAndId.fromSlashEncoded(id);
      cloudApiClient.getMachineClientForDatacenter(datacenterAndId.getDatacenter()).stop(datacenterAndId.getId());

   }

   @Override
   public void suspendNode(String id) {
      DatacenterAndId datacenterAndId = DatacenterAndId.fromSlashEncoded(id);
      cloudApiClient.getMachineClientForDatacenter(datacenterAndId.getDatacenter()).start(datacenterAndId.getId());

   }

}
