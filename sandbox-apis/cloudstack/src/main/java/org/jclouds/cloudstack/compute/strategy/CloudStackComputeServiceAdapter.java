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
import static com.google.common.collect.Iterables.filter;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.base.Predicates;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.compute.options.CloudStackTemplateOptions;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.AsyncJob;
import org.jclouds.cloudstack.domain.ServiceOffering;
import org.jclouds.cloudstack.domain.Template;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.domain.Zone;
import org.jclouds.cloudstack.options.DeployVirtualMachineOptions;
import org.jclouds.cloudstack.predicates.TemplatePredicates;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Credentials;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.base.Throwables;

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

   @Inject
   public CloudStackComputeServiceAdapter(CloudStackClient client, Predicate<Long> jobComplete) {
      this.client = checkNotNull(client, "client");
      this.jobComplete=checkNotNull(jobComplete, "jobComplete");
   }

   @Override
   public VirtualMachine createNodeWithGroupEncodedIntoNameThenStoreCredentials(String group, String name,
         org.jclouds.compute.domain.Template template, Map<String, Credentials> credentialStore) {
      checkNotNull(template, "template was null");
      checkNotNull(template.getOptions(), "template options was null");
      checkArgument(template.getOptions().getClass().isAssignableFrom(CloudStackTemplateOptions.class),
            "options class %s should have been assignable from CloudStackTemplateOptions", template.getOptions()
                  .getClass());
      CloudStackTemplateOptions templateOptions = template.getOptions().as(CloudStackTemplateOptions.class);

      DeployVirtualMachineOptions options = new DeployVirtualMachineOptions();
      if (templateOptions.getSecurityGroupIds().size() > 0)
         options.securityGroupIds(templateOptions.getSecurityGroupIds());

      if (templateOptions.getKeyPair() != null) {
         options.keyPair(templateOptions.getKeyPair());
         if (templateOptions.getRunScript() != null) {
           checkArgument(
                 credentialStore.containsKey("keypair#" + templateOptions.getKeyPair()),
                 "no private key configured for: %s; please use options.overrideLoginCredentialWith(rsa_private_text)",
                 templateOptions.getKeyPair());
         }
      }

      long zoneId = Long.parseLong(template.getLocation().getId());
      long templateId = Long.parseLong(template.getImage().getId());
      long serviceOfferingId = Long.parseLong(template.getHardware().getId());

      System.out.printf("serviceOfferingId %d, templateId %d, zoneId %d, options %s%n", serviceOfferingId, templateId,
            zoneId, options);
      AsyncCreateResponse job = client.getVirtualMachineClient().deployVirtualMachineInZone(zoneId, serviceOfferingId,
            templateId, options);
      assert jobComplete.apply(job.getJobId());
      AsyncJob<VirtualMachine> jobWithResult = client.getAsyncJobClient().<VirtualMachine> getAsyncJob(job.getJobId());
      if (jobWithResult.getError() != null)
         Throwables.propagate(new ExecutionException(String.format("job %s failed with exception %s", job.getId(),
               jobWithResult.getError().toString())) {
            private static final long serialVersionUID = 4371112085613620239L;
         });
      VirtualMachine vm = jobWithResult.getResult();
      if (vm.isPasswordEnabled()) {
         assert vm.getPassword() != null : vm;
         Credentials credentials = new Credentials("root", vm.getPassword());
         credentialStore.put("node#" + vm.getId(), credentials);
      } else {
         // assert templateOptions.getKeyPair() != null : vm;
         Credentials credentials = credentialStore.get("keypair#" + templateOptions.getKeyPair());
         credentialStore.put("node#" + vm.getId(), credentials);
      }
      return vm;
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
      return filter(client.getTemplateClient().listTemplates(), TemplatePredicates.isReady());
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
      client.getVirtualMachineClient().destroyVirtualMachine(guestId);
   }

   @Override
   public void rebootNode(String id) {
      client.getVirtualMachineClient().rebootVirtualMachine(Long.parseLong(id));
   }

   @Override
   public void resumeNode(String id) {
      client.getVirtualMachineClient().startVirtualMachine(Long.parseLong(id));
   }

   @Override
   public void suspendNode(String id) {
      client.getVirtualMachineClient().stopVirtualMachine(Long.parseLong(id));
   }

}