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
package org.jclouds.vcloud.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.CIMOperatingSystem;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.ovf.Envelope;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.domain.VAppTemplate;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class ImageForVAppTemplate implements Function<VAppTemplate, Image> {
   private final VCloudClient client;
   private final FindLocationForResource findLocationForResource;

   @Inject
   protected ImageForVAppTemplate(VCloudClient client, FindLocationForResource findLocationForResource) {
      this.client = checkNotNull(client, "client");
      this.findLocationForResource = checkNotNull(findLocationForResource, "findLocationForResource");
   }


   @Override
   public Image apply(VAppTemplate from) {
      ImageBuilder builder = new ImageBuilder();
      builder.ids(from.getHref().toASCIIString());
      builder.uri(from.getHref());
      builder.name(from.getName());
      if (from.getVDC() != null) {
         builder.location(findLocationForResource.apply(from.getVDC()));
      } else {
         // otherwise, it could be in a public catalog, which is not assigned to a VDC
      }
      builder.description(from.getDescription() != null ? from.getDescription() : from.getName());
      Envelope ovf = client.getVAppTemplateClient().getOvfEnvelopeForVAppTemplate(from.getHref());
      builder.operatingSystem(CIMOperatingSystem.toComputeOs(ovf));
      return builder.build();
   }

}