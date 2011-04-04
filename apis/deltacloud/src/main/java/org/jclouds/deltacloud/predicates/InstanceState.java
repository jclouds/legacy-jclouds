/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.deltacloud.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.deltacloud.DeltacloudClient;
import org.jclouds.deltacloud.domain.Instance;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.inject.Inject;

/**
 * 
 * Tests to see if a instance is at a specific state
 * 
 * @author Adrian Cole
 */
@Singleton
public abstract class InstanceState implements Predicate<Instance> {

   private final DeltacloudClient client;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public InstanceState(DeltacloudClient client) {
      this.client = client;
   }

   public boolean apply(Instance instance) {
      logger.trace("looking for state on instance %s", checkNotNull(instance, "instance"));
      instance = refresh(instance);
      if (instance == null || instance.getState() == Instance.State.FINISH)
         return false;
      logger.trace("%s: looking for instance state %s: currently: %s", instance.getId(), getState(), instance
               .getState());
      return instance.getState() == getState();
   }

   protected abstract Instance.State getState();

   private Instance refresh(Instance instance) {
      return client.getInstance(instance.getHref());
   }
}
