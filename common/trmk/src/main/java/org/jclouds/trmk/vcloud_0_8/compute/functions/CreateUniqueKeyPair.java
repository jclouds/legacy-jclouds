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
package org.jclouds.trmk.vcloud_0_8.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.Logger;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudClient;
import org.jclouds.trmk.vcloud_0_8.compute.domain.OrgAndName;
import org.jclouds.trmk.vcloud_0_8.domain.KeyPair;
import org.jclouds.util.Throwables2;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class CreateUniqueKeyPair implements Function<OrgAndName, KeyPair> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final TerremarkVCloudClient trmkClient;
   protected Supplier<String> randomSuffix;

   @Inject
   CreateUniqueKeyPair(TerremarkVCloudClient trmkClient, Supplier<String> randomSuffix) {
      this.trmkClient = trmkClient;
      this.randomSuffix = randomSuffix;
   }

   @Override
   public KeyPair apply(OrgAndName from) {
      return createNewKeyPairInOrg(from.getOrg(), from.getName());
   }

   private KeyPair createNewKeyPairInOrg(URI org, String keyPairName) {
      checkNotNull(org, "org");
      checkNotNull(keyPairName, "keyPairName");
      logger.debug(">> creating keyPair org(%s) name(%s)", org, keyPairName);
      KeyPair keyPair = null;
      while (keyPair == null) {
         try {
            keyPair = trmkClient.generateKeyPairInOrg(org, getNextName(keyPairName), false);
            logger.debug("<< created keyPair(%s)", keyPair.getName());
         } catch (RuntimeException e) {
            HttpResponseException ht = Throwables2.getFirstThrowableOfType(e, HttpResponseException.class);
            if (ht == null || ht.getContent() == null
                  || ht.getContent().indexOf("Security key with same name exists") == -1)
               throw e;
         }
      }
      return keyPair;
   }

   private String getNextName(String keyPairName) {
      return "jclouds_" + keyPairName.replaceAll("-", "_") + "_" + randomSuffix.get();
   }
}
