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

package org.jclouds.googlecompute.compute.strategy;

import com.google.inject.Inject;
import org.jclouds.compute.domain.TemplateBuilderSpec;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.ssh.internal.RsaSshKeyPairGenerator;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.inject.Singleton;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.config.ComputeServiceProperties.TEMPLATE;

/**
 * @author David Alves
 */
@Singleton
public class GoogleComputePopulateDefaultLoginCredentialsForImageStrategy implements
        PopulateDefaultLoginCredentialsForImageStrategy {

   private final TemplateBuilderSpec templateBuilder;
   private final RsaSshKeyPairGenerator keyPairGenerator;
   private String compoundKey;

   @Inject
   GoogleComputePopulateDefaultLoginCredentialsForImageStrategy(@Named(TEMPLATE) String templateSpec,
                                                                RsaSshKeyPairGenerator keyPairGenerator)
           throws NoSuchAlgorithmException {
      this.templateBuilder = TemplateBuilderSpec.parse(checkNotNull(templateSpec, "template builder spec"));
      checkNotNull(templateBuilder.getLoginUser(), "template builder spec must provide a loginUser");
      this.keyPairGenerator = checkNotNull(keyPairGenerator, "keypair generator");
   }

   @PostConstruct
   private void generateKeys() {
      Map<String, String> keys = keyPairGenerator.get();
      // as we need to store both the pubk and the pk, store them separated by : (base64 does not contain that char)
      compoundKey = String.format("%s:%s", checkNotNull(keys.get("public"), "public key cannot be null"),
              checkNotNull(keys.get("private"), "private key cannot be null"));
   }

   @Override
   public LoginCredentials apply(Object image) {
      return LoginCredentials.builder()
              .authenticateSudo(templateBuilder.getAuthenticateSudo() != null ?
                      templateBuilder.getAuthenticateSudo() : false)
              .privateKey(compoundKey)
              .user(templateBuilder.getLoginUser()).build();
   }

}
