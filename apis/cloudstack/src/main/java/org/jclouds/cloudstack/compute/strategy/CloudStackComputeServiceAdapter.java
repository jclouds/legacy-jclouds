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
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.cloudstack.options.DeployVirtualMachineOptions.Builder.displayName;
import static org.jclouds.cloudstack.predicates.NetworkPredicates.supportsStaticNAT;
import static org.jclouds.cloudstack.predicates.TemplatePredicates.isReady;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.compute.options.CloudStackTemplateOptions;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.IPForwardingRule;
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.domain.PublicIPAddress;
import org.jclouds.cloudstack.domain.ServiceOffering;
import org.jclouds.cloudstack.domain.Template;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.domain.Zone;
import org.jclouds.cloudstack.functions.CreatePortForwardingRulesForIP;
import org.jclouds.cloudstack.functions.StaticNATVirtualMachineInNetwork;
import org.jclouds.cloudstack.functions.StaticNATVirtualMachineInNetwork.Factory;
import org.jclouds.cloudstack.options.DeployVirtualMachineOptions;
import org.jclouds.cloudstack.strategy.BlockUntilJobCompletesAndReturnResult;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.Cache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;

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
   private final BlockUntilJobCompletesAndReturnResult blockUntilJobCompletesAndReturnResult;
   private final Factory staticNATVMInNetwork;
   private final CreatePortForwardingRulesForIP setupPortForwardingRulesForIP;
   private final Cache<Long, Set<IPForwardingRule>> vmToRules;
   private final Map<String, Credentials> credentialStore;

   @Inject
   public CloudStackComputeServiceAdapter(CloudStackClient client, Predicate<Long> jobComplete,
         @Memoized Supplier<Map<Long, Network>> networkSupplier,
         BlockUntilJobCompletesAndReturnResult blockUntilJobCompletesAndReturnResult,
         StaticNATVirtualMachineInNetwork.Factory staticNATVMInNetwork,
         CreatePortForwardingRulesForIP setupPortForwardingRulesForIP, Cache<Long, Set<IPForwardingRule>> vmToRules,
         Map<String, Credentials> credentialStore) {
      this.client = checkNotNull(client, "client");
      this.jobComplete = checkNotNull(jobComplete, "jobComplete");
      this.networkSupplier = checkNotNull(networkSupplier, "networkSupplier");
      this.blockUntilJobCompletesAndReturnResult = checkNotNull(blockUntilJobCompletesAndReturnResult,
            "blockUntilJobCompletesAndReturnResult");
      this.staticNATVMInNetwork = checkNotNull(staticNATVMInNetwork, "staticNATVMInNetwork");
      this.setupPortForwardingRulesForIP = checkNotNull(setupPortForwardingRulesForIP, "setupPortForwardingRulesForIP");
      this.vmToRules = checkNotNull(vmToRules, "vmToRules");
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

      DeployVirtualMachineOptions options = displayName(name).name(name);
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

      if (templateOptions.getIpOnDefaultNetwork() != null) {
         options.ipOnDefaultNetwork(templateOptions.getIpOnDefaultNetwork());
      }

      if (templateOptions.getIpsToNetworks().size() > 0) {
         options.ipsToNetworks(templateOptions.getIpsToNetworks());
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
      VirtualMachine vm = blockUntilJobCompletesAndReturnResult.<VirtualMachine> apply(job);
      LoginCredentials credentials = null;
      if (vm.isPasswordEnabled()) {
         assert vm.getPassword() != null : vm;
         credentials = LoginCredentials.builder().password(vm.getPassword()).build();
      } else {
         credentials = LoginCredentials.builder(credentialStore.get("keypair#" + templateOptions.getKeyPair())).build();
      }
      if (templateOptions.shouldSetupStaticNat()) {
         // TODO: possibly not all network ids, do we want to do this
         for (long networkId : options.getNetworkIds()) {
            logger.debug(">> creating static NAT for virtualMachine(%s) in network(%s)", vm.getId(), networkId);
            PublicIPAddress ip = staticNATVMInNetwork.create(networks.get(networkId)).apply(vm);
            logger.trace("<< static NATed IPAddress(%s) to virtualMachine(%s)", ip.getId(), vm.getId());
            List<Integer> ports = Ints.asList(templateOptions.getInboundPorts());
            logger.debug(">> setting up IP forwarding for IPAddress(%s) rules(%s)", ip.getId(), ports);
            Set<IPForwardingRule> rules = setupPortForwardingRulesForIP.apply(ip, ports);
            logger.trace("<< setup %d IP forwarding rules on IPAddress(%s)", rules.size(), ip.getId());
         }
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
      long virtualMachineId = Long.parseLong(id);
      return client.getVirtualMachineClient().getVirtualMachine(virtualMachineId);
   }

   @Override
   public void destroyNode(String id) {
      long virtualMachineId = Long.parseLong(id);
      // There was a bug in 2.2.12 release happening when static nat IP address
      // was being released, and corresponding firewall rules were left behind.
      // So next time the same IP is allocated, it might be not be static nat
      // enabled, but there are still rules associated with it. And when you try
      // to release this IP, the release will fail.
      //
      // The bug was fixed in 2.2.13 release only, and the current system wasn't
      // updated yet.
      //
      // To avoid the issue, every time you release a static nat ip address, do
      // the following:

      // 1) Delete IP forwarding rules associated with IP.
      Set<Long> ipAddresses = deleteIPForwardingRulesForVMAndReturnDistinctIPs(virtualMachineId);

      // 2) Disable static nat rule for the IP.
      disableStaticNATOnIPAddresses(ipAddresses);

      // 3) Only after 1 and 2 release the IP address.
      disassociateIPAddresses(ipAddresses);

      destroyVirtualMachine(virtualMachineId);

      vmToRules.invalidate(virtualMachineId);
   }

   public void disassociateIPAddresses(Set<Long> ipAddresses) {
      for (long ipAddress : ipAddresses) {
         logger.debug(">> disassociating IPAddress(%s)", ipAddress);
         client.getAddressClient().disassociateIPAddress(ipAddress);
      }
   }

   public void destroyVirtualMachine(long virtualMachineId) {

      Long destroyVirtualMachine = client.getVirtualMachineClient().destroyVirtualMachine(virtualMachineId);
      if (destroyVirtualMachine != null) {
         logger.debug(">> destroying virtualMachine(%s) job(%s)", virtualMachineId, destroyVirtualMachine);
         awaitCompletion(destroyVirtualMachine);
      } else {
         logger.trace("<< virtualMachine(%s) not found", virtualMachineId);
      }

   }

   public void disableStaticNATOnIPAddresses(Set<Long> ipAddresses) {
      Builder<Long> jobsToTrack = ImmutableSet.<Long> builder();
      for (Long ipAddress : ipAddresses) {
         Long disableStaticNAT = client.getNATClient().disableStaticNATOnPublicIP(ipAddress);
         if (disableStaticNAT != null) {
            logger.debug(">> disabling static NAT IPAddress(%s) job(%s)", ipAddress, disableStaticNAT);
            jobsToTrack.add(disableStaticNAT);
         }
      }
      awaitCompletion(jobsToTrack.build());
   }

   public Set<Long> deleteIPForwardingRulesForVMAndReturnDistinctIPs(long virtualMachineId) {
      Builder<Long> jobsToTrack = ImmutableSet.<Long> builder();

      // immutable doesn't permit duplicates
      Set<Long> ipAddresses = Sets.newLinkedHashSet();

      Set<IPForwardingRule> forwardingRules = client.getNATClient().getIPForwardingRulesForVirtualMachine(
            virtualMachineId);
      for (IPForwardingRule rule : forwardingRules) {
         if (!"Deleting".equals(rule.getState())) {
            ipAddresses.add(rule.getIPAddressId());
            Long deleteForwardingRule = client.getNATClient().deleteIPForwardingRule(rule.getId());
            if (deleteForwardingRule != null) {
               logger.debug(">> deleting IPForwardingRule(%s) job(%s)", rule.getId(), deleteForwardingRule);
               jobsToTrack.add(deleteForwardingRule);
            }
         }
      }
      awaitCompletion(jobsToTrack.build());
      return ipAddresses;
   }

   public void awaitCompletion(Iterable<Long> jobs) {
      logger.debug(">> awaiting completion of jobs(%s)", jobs);
      for (long job : jobs)
         awaitCompletion(job);
      logger.trace("<< completed jobs(%s)", jobs);
   }

   public void awaitCompletion(long job) {
      boolean completed = jobComplete.apply(job);
      logger.trace("<< job(%s) complete(%s)", job, completed);
   }

   @Override
   public void rebootNode(String id) {
      long virtualMachineId = Long.parseLong(id);
      Long job = client.getVirtualMachineClient().rebootVirtualMachine(virtualMachineId);
      if (job != null) {
         logger.debug(">> rebooting virtualMachine(%s) job(%s)", virtualMachineId, job);
         awaitCompletion(job);
      }
   }

   @Override
   public void resumeNode(String id) {
      long virtualMachineId = Long.parseLong(id);
      Long job = client.getVirtualMachineClient().startVirtualMachine(Long.parseLong(id));
      if (job != null) {
         logger.debug(">> starting virtualMachine(%s) job(%s)", virtualMachineId, job);
         awaitCompletion(job);
      }
   }

   @Override
   public void suspendNode(String id) {
      long virtualMachineId = Long.parseLong(id);
      Long job = client.getVirtualMachineClient().stopVirtualMachine(Long.parseLong(id));
      if (job != null) {
         logger.debug(">> stopping virtualMachine(%s) job(%s)", virtualMachineId, job);
         awaitCompletion(job);
      }
   }

}