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

import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudClient;
import org.jclouds.trmk.vcloud_0_8.compute.domain.OrgAndName;
import org.jclouds.trmk.vcloud_0_8.domain.Org;
import org.jclouds.trmk.vcloud_0_8.endpoints.VDC;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class NodeMetadataToOrgAndName implements Function<NodeMetadata, OrgAndName> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   final Supplier<Map<String, String>> vdcToOrg;

   private final TerremarkVCloudClient client;

   @Inject
   NodeMetadataToOrgAndName(TerremarkVCloudClient client, @VDC Supplier<Map<String, String>> vdcToOrg) {
      this.vdcToOrg = vdcToOrg;
      this.client = client;
   }

   @Override
   public OrgAndName apply(NodeMetadata from) {
      if (from.getGroup() != null) {
         Org org = client.findOrgNamed(vdcToOrg.get().get(from.getLocation().getId()));
         if (org == null) {
            logger.warn("did not find an association for vdc %s in %s", from.getLocation().getId(), vdcToOrg);
         } else {
            return new OrgAndName(org.getHref(), from.getGroup());
         }
      }
      return null;
   }
}
