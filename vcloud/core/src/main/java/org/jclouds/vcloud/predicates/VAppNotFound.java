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

package org.jclouds.vcloud.predicates;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.logging.Logger;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.domain.VApp;

import com.google.common.base.Predicate;
import com.google.inject.Inject;

/**
 * 
 * Tests to see if a task succeeds.
 * 
 * @author Adrian Cole
 */
@Singleton
public class VAppNotFound implements Predicate<VApp> {

   private final VCloudClient client;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public VAppNotFound(VCloudClient client) {
      this.client = client;
   }

   public boolean apply(VApp vApp) {
      try {
         return client.getVApp(vApp.getId()) == null;
      } catch (ResourceNotFoundException e) {
         return true;
      }
   }
}
