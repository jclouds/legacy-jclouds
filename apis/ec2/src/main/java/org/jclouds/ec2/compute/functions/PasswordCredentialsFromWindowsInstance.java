/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.ec2.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.compute.domain.PasswordDataAndPrivateKey;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.ec2.domain.PasswordData;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.features.WindowsApi;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.Atomics;

/**
 * @author Adrian Cole
 */
@Singleton
public class PasswordCredentialsFromWindowsInstance implements Function<RunningInstance, LoginCredentials> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final ConcurrentMap<RegionAndName, KeyPair> credentialsMap;
   private final EC2Client ec2Client;
   private final Function<PasswordDataAndPrivateKey, LoginCredentials> pwDataToLoginCredentials;

   @Inject
   protected PasswordCredentialsFromWindowsInstance(ConcurrentMap<RegionAndName, KeyPair> credentialsMap,
            EC2Client ec2Client, Function<PasswordDataAndPrivateKey, LoginCredentials> pwDataToLoginCredentials) {
      this.credentialsMap = checkNotNull(credentialsMap, "credentialsMap");
      this.ec2Client = checkNotNull(ec2Client, "ec2Client");
      this.pwDataToLoginCredentials = checkNotNull(pwDataToLoginCredentials, "pwDataToLoginCredentials");
   }

   @Override
   public LoginCredentials apply(final RunningInstance instance) {
      Optional<? extends WindowsApi> windowsOption = ec2Client.getWindowsApiForRegion(instance.getRegion());
      checkState(windowsOption.isPresent(), "windows feature not present in region %s", instance.getRegion());
      
      final WindowsApi windowsApi = windowsOption.get();
      
      LoginCredentials credentials = LoginCredentials.builder().user("Administrator").noPrivateKey().build();
      String privateKey = getPrivateKeyOrNull(instance);
      if (privateKey == null) {
         return credentials;
      }
      // The Administrator password will take some time before it is ready - Amazon says
      // sometimes
      // 15 minutes.
      // So we create a predicate that tests if the password is ready, and wrap it in a retryable
      // predicate.
      final AtomicReference<PasswordData> data = Atomics.newReference();
      Predicate<String> passwordReady = new Predicate<String>() {
         @Override
         public boolean apply(@Nullable String s) {
            if (Strings.isNullOrEmpty(s))
               return false;
            data.set(windowsApi.getPasswordDataForInstance(instance.getId()));
            if (data.get() == null)
               return false;
            return !Strings.isNullOrEmpty(data.get().getPasswordData());
         }
      };

      // TODO: parameterize
      Predicate<String> passwordReadyRetryable = retry(passwordReady, 600, 10, SECONDS);

      logger.debug(">> awaiting password data for instance(%s/%s)", instance.getRegion(), instance.getId());
      if (passwordReadyRetryable.apply(instance.getId())) {
         credentials = pwDataToLoginCredentials.apply(new PasswordDataAndPrivateKey(data.get(), privateKey));
         logger.debug("<< obtained password data for instance(%s/%s)", instance.getRegion(), instance.getId());
      } else {
         logger.debug("<< unable to get password data for instance(%s/%s)", instance.getRegion(), instance.getId());
      }
      return credentials;
   }

   @VisibleForTesting
   String getPrivateKeyOrNull(RunningInstance instance) {
      KeyPair keyPair = credentialsMap.get(new RegionAndName(instance.getRegion(), instance.getKeyName()));
      return keyPair != null ? keyPair.getKeyMaterial() : null;
   }

}
