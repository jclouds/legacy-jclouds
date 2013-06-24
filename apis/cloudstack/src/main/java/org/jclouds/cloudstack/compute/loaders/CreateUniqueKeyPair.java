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
package org.jclouds.cloudstack.compute.loaders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.SshKeyPair;
import org.jclouds.cloudstack.strategy.BlockUntilJobCompletesAndReturnResult;

import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheLoader;
import com.google.inject.Inject;

/**
 * @author Adam Lowe
 * @author Andrew Bayer
 */
@Singleton
public class CreateUniqueKeyPair extends CacheLoader<String, SshKeyPair> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final CloudStackClient client;

   @Inject
   public CreateUniqueKeyPair(CloudStackClient client) {
      this.client = checkNotNull(client, "client");
   }

   @Override
   public SshKeyPair load(String input) {
      SshKeyPair keyPair = null;
      while (keyPair == null) {
         try {
            keyPair = client.getSSHKeyPairClient().createSSHKeyPair(input);
            logger.debug(">> creating SSH key pair with name %s", input);
         } catch (IllegalStateException e) {
            logger.error(e, "<< error creating SSH key pair with name %s: ",
                         Throwables.getRootCause(e).getMessage());
            throw Throwables.propagate(e);
         }
      }

      logger.debug("<< created keyPair(%s)", keyPair.getName());
      return keyPair;
   }

}
