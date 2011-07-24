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
package org.jclouds.trmk.ecloud.suppliers;

import static org.jclouds.trmk.vcloud_0_8.options.AddInternetServiceOptions.Builder.withDescription;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.rest.InsufficientResourcesException;
import org.jclouds.trmk.ecloud.TerremarkECloudClient;
import org.jclouds.trmk.vcloud_0_8.domain.InternetService;
import org.jclouds.trmk.vcloud_0_8.domain.Protocol;
import org.jclouds.trmk.vcloud_0_8.domain.PublicIpAddress;
import org.jclouds.trmk.vcloud_0_8.domain.VApp;
import org.jclouds.trmk.vcloud_0_8.suppliers.InternetServiceAndPublicIpAddressSupplier;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class TerremarkECloudInternetServiceAndPublicIpAddressSupplier implements
         InternetServiceAndPublicIpAddressSupplier {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   public Logger logger = Logger.NULL;
   protected final TerremarkECloudClient client;

   @Inject
   public TerremarkECloudInternetServiceAndPublicIpAddressSupplier(TerremarkECloudClient client) {
      this.client = client;
   }

   @Override
   public Entry<InternetService, PublicIpAddress> getNewInternetServiceAndIp(VApp vApp, int port,
            Protocol protocol) {
      logger.debug(">> creating InternetService in vDC %s:%s:%d", vApp.getVDC().getHref(), protocol, port);
      InternetService is = null;
      PublicIpAddress ip = null;
      try {
         ip = client.activatePublicIpInVDC(vApp.getVDC().getHref());
      } catch (InsufficientResourcesException e) {
         logger.warn(">> no more ip addresses available, looking for one to re-use");
         for (PublicIpAddress existingIp : client.getPublicIpsAssociatedWithVDC(vApp.getVDC().getHref())) {
            Set<InternetService> services = client.getInternetServicesOnPublicIp(existingIp.getId());
            if (services.size() == 0) {
               ip = existingIp;
               break;
            }
         }
         if (ip == null)
            throw e;

      }
      is = client.addInternetServiceToExistingIp(ip.getId(), vApp.getName() + "-" + port, protocol, port,
               withDescription(String.format("port %d access to serverId: %s name: %s", port, vApp.getName(), vApp
                        .getName())));
      Map<InternetService, PublicIpAddress> result = ImmutableMap.<InternetService, PublicIpAddress> of(is, ip);
      Entry<InternetService, PublicIpAddress> entry = Iterables.getOnlyElement(result.entrySet());
      return entry;
   }
}