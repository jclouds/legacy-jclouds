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
package org.jclouds.trmk.vcloudexpress.suppliers;

import static org.jclouds.trmk.vcloud_0_8.options.AddInternetServiceOptions.Builder.withDescription;

import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.trmk.vcloud_0_8.domain.InternetService;
import org.jclouds.trmk.vcloud_0_8.domain.Protocol;
import org.jclouds.trmk.vcloud_0_8.domain.PublicIpAddress;
import org.jclouds.trmk.vcloud_0_8.domain.VApp;
import org.jclouds.trmk.vcloud_0_8.suppliers.InternetServiceAndPublicIpAddressSupplier;
import org.jclouds.trmk.vcloudexpress.TerremarkVCloudExpressClient;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class TerremarkVCloudExpressInternetServiceAndPublicIpAddressSupplier implements
      InternetServiceAndPublicIpAddressSupplier {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   public Logger logger = Logger.NULL;
   protected final TerremarkVCloudExpressClient client;

   @Inject
   public TerremarkVCloudExpressInternetServiceAndPublicIpAddressSupplier(TerremarkVCloudExpressClient client) {
      this.client = client;
   }

   @Override
   public Entry<InternetService, PublicIpAddress> getNewInternetServiceAndIp(VApp vApp, int port, Protocol protocol) {
      logger.debug(">> creating InternetService in vDC %s:%s:%d", vApp.getVDC().getHref(), protocol, port);
      InternetService is = client.addInternetServiceToVDC(
            vApp.getVDC().getHref(),
            vApp.getName() + "-" + port,
            protocol,
            port,
            withDescription(String.format("port %d access to serverId: %s name: %s", port, vApp.getName(),
                  vApp.getName())));
      PublicIpAddress ip = is.getPublicIpAddress();
      Map<InternetService, PublicIpAddress> result = ImmutableMap.<InternetService, PublicIpAddress> of(is, ip);
      Entry<InternetService, PublicIpAddress> entry = Iterables.getOnlyElement(result.entrySet());
      return entry;
   }
}
