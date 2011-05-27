/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.ibm.smartcloud.predicates;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.ibm.smartcloud.IBMSmartCloudClient;
import org.jclouds.ibm.smartcloud.domain.Instance;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.inject.Inject;

/**
 * Tests to see if a task succeeds.
 * 
 * @author Adrian Cole
 */
@Singleton
public class InstanceActive implements Predicate<Instance> {

   private final IBMSmartCloudClient client;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public InstanceActive(IBMSmartCloudClient client) {
      this.client = client;
   }

   public boolean apply(Instance instance) {
      logger.trace("looking for state on instance %s", instance);
      instance = client.getInstance(instance.getId());
      if (instance == null)
         return false;
      logger.trace("%s: looking for instance state %s: currently: %s", instance
            .getId(), Instance.Status.ACTIVE, instance.getStatus());
      if (instance.getStatus() == Instance.Status.FAILED)
         throw new IllegalStateException("node " + instance.getId()
               + " in location " + instance.getLocation()
               + " is in a failed state");
      return instance.getStatus() == Instance.Status.ACTIVE;
   }

}
