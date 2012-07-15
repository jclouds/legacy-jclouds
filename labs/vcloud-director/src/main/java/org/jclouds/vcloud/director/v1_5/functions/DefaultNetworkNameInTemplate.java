/**
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
package org.jclouds.vcloud.director.v1_5.functions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Iterables.get;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.dmtf.ovf.Network;
import org.jclouds.dmtf.ovf.NetworkSection;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;

import com.google.common.base.Function;

@Singleton
public class DefaultNetworkNameInTemplate implements Function<VAppTemplate, String> {
   @Resource
   protected Logger logger = Logger.NULL;
   
   private SectionForVAppTemplate<NetworkSection> networkSelector = 
         new SectionForVAppTemplate<NetworkSection>(NetworkSection.class);

   @Override
   public String apply(VAppTemplate vAppTemplate) {
      checkArgument(vAppTemplate != null, "vAppTemplate was null!");
      vAppTemplate.getSections();
      Set<Network> networks = networkSelector.apply(vAppTemplate).getNetworks();
      checkArgument(networks.size() > 0, "no networks found in vAppTemplate %s", vAppTemplate);
      if (networks.size() > 1)
         logger.warn("multiple networks found for %s, choosing first from: %s", vAppTemplate.getName(), networks);
      return get(networks, 0).getName();
   }
}
