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
package org.jclouds.cloudstack.compute.strategy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.and;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.cloudstack.options.DeployVirtualMachineOptions.Builder.displayName;
import static org.jclouds.cloudstack.predicates.NetworkPredicates.supportsStaticNAT;
import static org.jclouds.cloudstack.predicates.TemplatePredicates.isReady;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.compute.options.CloudStackTemplateOptions;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.AsyncJob;
import org.jclouds.cloudstack.domain.IPForwardingRule;
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.domain.PublicIPAddress;
import org.jclouds.cloudstack.domain.ServiceOffering;
import org.jclouds.cloudstack.domain.Template;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.domain.Zone;
import org.jclouds.cloudstack.functions.StaticNATVirtualMachineInNetwork;
import org.jclouds.cloudstack.functions.StaticNATVirtualMachineInNetwork.Factory;
import org.jclouds.cloudstack.options.DeployVirtualMachineOptions;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Credentials;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;

/**
 * defines the connection between the {@link CloudStackClient} implementation
 * and the jclouds {@link ComputeService}
 * 
 */
@Singleton
public class CloudStackComputeServiceAdapter implements
      ComputeServiceAdapter<VirtualMachine, ServiceOffering, Template, Zone> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final CloudStackClient client;
   private final Predicate<Long> jobComplete;
   private final Supplier<Map<Long, Network>> networkSupplier;
   private final Factory staticNATVMInNetwork;
   private final Map<String, Credentials> credentialStore;

   @Inject
   public CloudStackComputeServiceAdapter(CloudStackClient client, Predicate<Long> jobComplete,
         @Memoized Supplier<Map<Long, Network>> networkSupplier,
         StaticNATVirtualMachineInNetwork.Factory staticNATVMInNetwork, Map<String, Credentials> credentialStore) {
      this.client = checkNotNull(client, "client");
      this.jobComplete = checkNotNull(jobComplete, "jobComplete");
      this.networkSupplier = checkNotNull(networkSupplier, "networkSupplier");
      this.staticNATVMInNetwork = checkNotNull(staticNATVMInNetwork, "staticNATVMInNetwork");
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
   }

   @Override
   public NodeAndInitialCredentials<VirtualMachine> createNodeWithGroupEncodedIntoName(String group, String name,
         org.jclouds.compute.domain.Template template) {
      checkNotNull(template, "template was null");
      checkNotNull(template.getOptions(), "template options was null");
      checkArgument(template.getOptions().getClass().isAssignableFrom(CloudStackTemplateOptions.class),
            "options class %s should have been assignable from CloudStackTemplateOptions", template.getOptions()
                  .getClass());
      Map<Long, Network> networks = networkSupplier.get();

      final long zoneId = Long.parseLong(template.getLocation().getId());

      CloudStackTemplateOptions templateOptions = template.getOptions().as(CloudStackTemplateOptions.class);

      // not all hypervisors support setting name
      DeployVirtualMachineOptions options = displayName(name);
      if (templateOptions.getSecurityGroupIds().size() > 0) {
         options.securityGroupIds(templateOptions.getSecurityGroupIds());
      } else if (templateOptions.getNetworkIds().size() > 0) {
         options.networkIds(templateOptions.getNetworkIds());
      } else if (networks.size() > 0) {
         try {
            options.networkId(getOnlyElement(filter(networks.values(), and(new Predicate<Network>() {

               @Override
               public boolean apply(Network arg0) {
                  return arg0.getZoneId() == zoneId && arg0.isDefault();
               }

            }, supportsStaticNAT()))).getId());
         } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("please choose a specific network in zone " + zoneId + ": " + networks);
         }
      } else {
         throw new IllegalArgumentException("please setup a network or security group for zone: " + zoneId);
      }

      if (templateOptions.getKeyPair() != null) {
         options.keyPair(templateOptions.getKeyPair());
         if (templateOptions.getRunScript() != null) {
            checkArgument(
                  credentialStore.containsKey("keypair#" + templateOptions.getKeyPair()),
                  "no private key configured for: %s; please use options.overrideLoginCredentialWith(rsa_private_text)",
                  templateOptions.getKeyPair());
         }
      }

      long templateId = Long.parseLong(template.getImage().getId());
      long serviceOfferingId = Long.parseLong(template.getHardware().getId());

      logger.info("serviceOfferingId %d, templateId %d, zoneId %d, options %s%n", serviceOfferingId, templateId,
            zoneId, options);
      AsyncCreateResponse job = client.getVirtualMachineClient().deployVirtualMachineInZone(zoneId, serviceOfferingId,
            templateId, options);
      boolean completed = jobComplete.apply(job.getJobId());
      AsyncJob<VirtualMachine> jobWithResult = client.getAsyncJobClient().<VirtualMachine> getAsyncJob(job.getJobId());
      assert completed : jobWithResult;
      if (jobWithResult.getError() != null)
         propagate(new ExecutionException(String.format("job %s failed with exception %s", job.getId(), jobWithResult
               .getError().toString())) {
            private static final long serialVersionUID = 4371112085613620239L;
         });
      VirtualMachine vm = jobWithResult.getResult();
      Credentials credentials = null;
      if (vm.isPasswordEnabled()) {
         assert vm.getPassword() != null : vm;
         credentials = new Credentials(null, vm.getPassword());
      } else {
         credentials = credentialStore.get("keypair#" + templateOptions.getKeyPair());
      }
      // TODO: possibly not all network ids, do we want to do this
      for (long networkId : options.getNetworkIds()) {
         // TODO: log this
         PublicIPAddress ip = staticNATVMInNetwork.create(networks.get(networkId)).apply(vm);
      }
      return new NodeAndInitialCredentials<VirtualMachine>(vm, vm.getId() + "", credentials);
   }

   @Override
   public Iterable<ServiceOffering> listHardwareProfiles() {
      // TODO: we may need to filter these
      return client.getOfferingClient().listServiceOfferings();
   }

   @Override
   public Iterable<Template> listImages() {
      // TODO: we may need to filter these further
      // we may also want to see if we can work with ssh keys
      return filter(client.getTemplateClient().listTemplates(), isReady());
   }

   @Override
   public Iterable<VirtualMachine> listNodes() {
      return client.getVirtualMachineClient().listVirtualMachines();
   }

   @Override
   public Iterable<Zone> listLocations() {
      // TODO: we may need to filter these
      return client.getZoneClient().listZones();
   }

   @Override
   public VirtualMachine getNode(String id) {
      long guestId = Long.parseLong(id);
      return client.getVirtualMachineClient().getVirtualMachine(guestId);
   }

   @Override
   public void destroyNode(String id) {
      long guestId = Long.parseLong(id);
      Long job = client.getVirtualMachineClient().destroyVirtualMachine(guestId);
      boolean completed = jobComplete.apply(job);
      IPForwardingRule forwardingRule = client.getNATClient().getIPForwardingRuleForVirtualMachine(guestId);
      if (forwardingRule != null)
         client.getNATClient().disableStaticNat(forwardingRule.getIPAddressId());
   }

   @Override
   public void rebootNode(String id) {
      Long job = client.getVirtualMachineClient().rebootVirtualMachine(Long.parseLong(id));
      boolean completed = jobComplete.apply(job);
   }

   @Override
   public void resumeNode(String id) {
      Long job = client.getVirtualMachineClient().startVirtualMachine(Long.parseLong(id));
      boolean completed = jobComplete.apply(job);
   }

   @Override
   public void suspendNode(String id) {
      Long job = client.getVirtualMachineClient().stopVirtualMachine(Long.parseLong(id));
      boolean completed = jobComplete.apply(job);
   }

}