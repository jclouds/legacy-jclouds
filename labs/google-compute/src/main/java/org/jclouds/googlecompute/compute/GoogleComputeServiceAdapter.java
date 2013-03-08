/*
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

package org.jclouds.googlecompute.compute;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.UncheckedTimeoutException;
import com.google.inject.Inject;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.googlecompute.GoogleComputeApi;
import org.jclouds.googlecompute.compute.options.GoogleComputeTemplateOptions;
import org.jclouds.googlecompute.config.UserProject;
import org.jclouds.googlecompute.domain.Image;
import org.jclouds.googlecompute.domain.Instance;
import org.jclouds.googlecompute.domain.InstanceTemplate;
import org.jclouds.googlecompute.domain.MachineType;
import org.jclouds.googlecompute.domain.Operation;
import org.jclouds.googlecompute.domain.Zone;
import org.jclouds.http.HttpResponse;
import org.jclouds.logging.Logger;

import javax.annotation.Resource;
import javax.inject.Named;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.jclouds.googlecompute.GoogleComputeConstants.GOOGLE_PROJECT;
import static org.jclouds.googlecompute.GoogleComputeConstants.OPERATION_COMPLETE_INTERVAL;
import static org.jclouds.googlecompute.GoogleComputeConstants.OPERATION_COMPLETE_TIMEOUT;
import static org.jclouds.googlecompute.domain.Instance.NetworkInterface.AccessConfig.Type;
import static org.jclouds.util.Predicates2.retry;

/**
 * @author David Alves
 */
public class GoogleComputeServiceAdapter implements ComputeServiceAdapter<Instance, MachineType, Image, Zone> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final GoogleComputeApi api;
   private final Supplier<String> userProject;
   private final Function<TemplateOptions, ImmutableMap.Builder<String, String>> metatadaFromTemplateOptions;
   private final Predicate<AtomicReference<Operation>> retryOperationDonePredicate;
   private final long operationCompleteCheckInterval;
   private final long operationCompleteCheckTimeout;

   @Inject
   public GoogleComputeServiceAdapter(GoogleComputeApi api,
                                      @UserProject Supplier<String> userProject,
                                      Function<TemplateOptions,
                                              ImmutableMap.Builder<String, String>> metatadaFromTemplateOptions,
                                      Predicate<AtomicReference<Operation>> operationDonePredicate,
                                      @Named(OPERATION_COMPLETE_INTERVAL) Long operationCompleteCheckInterval,
                                      @Named(OPERATION_COMPLETE_TIMEOUT) Long operationCompleteCheckTimeout) {
      this.api = checkNotNull(api, "google compute api");
      this.userProject = checkNotNull(userProject, "user project name");
      this.metatadaFromTemplateOptions = checkNotNull(metatadaFromTemplateOptions,
              "metadata from template options function");
      this.operationCompleteCheckInterval = checkNotNull(operationCompleteCheckInterval,
              "operation completed check interval");
      this.operationCompleteCheckTimeout = checkNotNull(operationCompleteCheckTimeout,
              "operation completed check timeout");
      this.retryOperationDonePredicate = retry(operationDonePredicate, operationCompleteCheckTimeout,
              operationCompleteCheckInterval, TimeUnit.MILLISECONDS);
   }

   @Override
   public NodeAndInitialCredentials<Instance> createNodeWithGroupEncodedIntoName(
           final String group, final String name, Template template) {

      checkNotNull(template, "template");

      GoogleComputeTemplateOptions options = GoogleComputeTemplateOptions.class.cast(template.getOptions()).clone();
      checkState(options.getNetwork().isPresent(), "network was not present in template options");
      Hardware hardware = checkNotNull(template.getHardware(), "hardware must be set");
      URI machineType = checkNotNull(hardware.getUri(), "hardware uri must be set");

      InstanceTemplate instanceTemplate = InstanceTemplate.builder()
              .forMachineType(machineType);

      if (options.isEnableNat()) {
         instanceTemplate.addNetworkInterface(options.getNetwork().get(), Type.ONE_TO_ONE_NAT);
      } else {
         instanceTemplate.addNetworkInterface(options.getNetwork().get());
      }

      LoginCredentials credentials = getFromImageAndOverrideIfRequired(template.getImage(), options);

      ImmutableMap.Builder<String, String> metadataBuilder = metatadaFromTemplateOptions.apply(options);
      instanceTemplate.metadata(metadataBuilder.build());
      instanceTemplate.tags(options.getTags());
      instanceTemplate.serviceAccounts(options.getServiceAccounts());
      instanceTemplate.image(checkNotNull(template.getImage().getUri(), "image URI is null"));

      Operation operation = api.getInstanceApiForProject(userProject.get())
              .createInZone(name, instanceTemplate, template.getLocation().getId());

      if (options.shouldBlockUntilRunning()) {
         waitOperationDone(operation);
      }

      // some times the newly created instances are not immediately returned
      AtomicReference<Instance> instance = new AtomicReference<Instance>();

      retry(new Predicate<AtomicReference<Instance>>() {
         @Override
         public boolean apply(AtomicReference<Instance> input) {
            input.set(api.getInstanceApiForProject(userProject.get()).get(name));
            return input.get() != null;
         }
      }, operationCompleteCheckTimeout, operationCompleteCheckInterval, MILLISECONDS).apply(instance);

      return new NodeAndInitialCredentials<Instance>(instance.get(), name, credentials);
   }


   @Override
   public Iterable<MachineType> listHardwareProfiles() {
      return api.getMachineTypeApiForProject(userProject.get()).list().concat();
   }

   @Override
   public Iterable<Image> listImages() {
      return ImmutableSet.<Image>builder()
              .addAll(api.getImageApiForProject(userProject.get()).list().concat())
              .addAll(api.getImageApiForProject(GOOGLE_PROJECT).list().concat())
              .build();
   }

   @Override
   public Image getImage(String id) {
      return Objects.firstNonNull(api.getImageApiForProject(userProject.get()).get(id),
              api.getImageApiForProject(GOOGLE_PROJECT).get(id));
   }

   @Override
   public Iterable<Zone> listLocations() {
      return api.getZoneApiForProject(userProject.get()).list().concat();
   }

   @Override
   public Instance getNode(String name) {
      return api.getInstanceApiForProject(userProject.get()).get(name);
   }

   @Override
   public Iterable<Instance> listNodes() {
      return api.getInstanceApiForProject(userProject.get()).list().concat();
   }

   @Override
   public void destroyNode(final String name) {
      waitOperationDone(api.getInstanceApiForProject(userProject.get()).delete(name));
   }

   @Override
   public void rebootNode(String name) {
      throw new UnsupportedOperationException("reboot is not supported by GCE");
   }

   @Override
   public void resumeNode(String name) {
      throw new UnsupportedOperationException("resume is not supported by GCE");
   }

   @Override
   public void suspendNode(String name) {
      throw new UnsupportedOperationException("suspend is not supported by GCE");
   }

   private LoginCredentials getFromImageAndOverrideIfRequired(org.jclouds.compute.domain.Image image,
                                                              GoogleComputeTemplateOptions options) {
      LoginCredentials defaultCredentials = image.getDefaultCredentials();
      String[] keys = defaultCredentials.getPrivateKey().split(":");
      String publicKey = keys[0];
      String privateKey = keys[1];

      LoginCredentials.Builder credentialsBuilder = defaultCredentials.toBuilder();
      credentialsBuilder.privateKey(privateKey);

      // LoginCredentials from image stores the public key along with the private key in the privateKey field
      // @see GoogleComputePopulateDefaultLoginCredentialsForImageStrategy
      // so if options doesn't have a public key set we set it from the default
      if (options.getPublicKey() == null) {
         options.authorizePublicKey(publicKey);
      }
      if (options.hasLoginPrivateKeyOption()) {
         credentialsBuilder.privateKey(options.getPrivateKey());
      }
      if (options.getLoginUser() != null) {
         credentialsBuilder.identity(options.getLoginUser());
      }
      if (options.hasLoginPasswordOption()) {
         credentialsBuilder.password(options.getLoginPassword());
      }
      if (options.shouldAuthenticateSudo() != null) {
         credentialsBuilder.authenticateSudo(options.shouldAuthenticateSudo());
      }
      LoginCredentials credentials = credentialsBuilder.build();
      options.overrideLoginCredentials(credentials);
      return credentials;
   }

   private void waitOperationDone(Operation operation) {
      AtomicReference<Operation> operationRef = new AtomicReference<Operation>(operation);

      // wait for the operation to complete
      if (!retryOperationDonePredicate.apply(operationRef)) {
         throw new UncheckedTimeoutException("operation did not reach DONE state" + operationRef.get());
      }

      // check if the operation failed
      if (operationRef.get().getHttpError().isPresent()) {
         HttpResponse response = operationRef.get().getHttpError().get();
         throw new IllegalStateException("operation failed. Http Error Code: " + response.getStatusCode() +
                 " HttpError: " + response.getMessage());
      }
   }

}
