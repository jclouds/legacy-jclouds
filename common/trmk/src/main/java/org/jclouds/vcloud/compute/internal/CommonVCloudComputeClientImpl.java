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
package org.jclouds.vcloud.compute.internal;

import java.net.URI;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.CommonVCloudClient;
import org.jclouds.vcloud.compute.CommonVCloudComputeClient;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Status;
import org.jclouds.vcloud.domain.Task;

import com.google.common.base.Predicate;
import com.google.inject.Inject;

/**
 * @author Adrian Cole
 */
@Singleton
public abstract class CommonVCloudComputeClientImpl<T, A extends ReferenceType> implements CommonVCloudComputeClient {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final CommonVCloudClient client;
   protected final Predicate<URI> taskTester;

   @Inject
   public CommonVCloudComputeClientImpl(CommonVCloudClient client, Predicate<URI> successTester) {
      this.client = client;
      this.taskTester = successTester;
   }

   @Override
   public void reset(URI id) {
      A vApp = refreshVApp(id);
      logger.debug(">> resetting vApp(%s)", vApp.getName());
      Task task = reset(vApp);
      if (!taskTester.apply(task.getHref())) {
         throw new RuntimeException(String.format("failed to %s %s: %s", "resetVApp", vApp.getName(), task));
      }
      logger.debug("<< on vApp(%s)", vApp.getName());
   }

   protected abstract Task reset(A vApp);

   protected abstract A refreshVApp(URI id);

   @Override
   public void stop(URI id) {
      A vApp = refreshVApp(id);
      vApp = powerOffVAppIfDeployed(vApp);
      vApp = undeployVAppIfDeployed(vApp);
      deleteVApp(vApp);
      logger.debug("<< deleted vApp(%s)", vApp.getName());
   }
   
   protected abstract void deleteVApp(A vApp);
   
   private A undeployVAppIfDeployed(A vApp) {
      if (getStatus(vApp).compareTo(Status.RESOLVED) > 0) {
         logger.debug(">> undeploying vApp(%s), current status: %s", vApp.getName(), getStatus(vApp));
         Task task = undeploy(vApp);
         if (!taskTester.apply(task.getHref())) {
            // TODO timeout
            throw new RuntimeException(String.format("failed to %s %s: %s", "undeploy", vApp.getName(), task));
         }
         vApp = refreshVApp(vApp.getHref());
         logger.debug("<< %s vApp(%s)", getStatus(vApp), vApp.getName());
      }
      return vApp;
   }

   protected abstract Task undeploy(A vApp);

   private A powerOffVAppIfDeployed(A vApp) {
      if (getStatus(vApp).compareTo(Status.OFF) > 0) {
         logger.debug(">> powering off vApp(%s), current status: %s", vApp.getName(), getStatus(vApp));
         Task task = powerOff(vApp);
         if (!taskTester.apply(task.getHref())) {
            // TODO timeout
            throw new RuntimeException(String.format("failed to %s %s: %s", "powerOff", vApp.getName(), task));
         }
         vApp = refreshVApp(vApp.getHref());
         logger.debug("<< %s vApp(%s)", getStatus(vApp), vApp.getName());
      }
      return vApp;
   }

   protected abstract Task powerOff(A vApp);

   protected abstract Status getStatus(A vApp);

   @Override
   public abstract Set<String> getPrivateAddresses(URI id);

   @Override
   public abstract Set<String> getPublicAddresses(URI id);

}