/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.abiquo.domain.cloud.VirtualMachineTemplate;
import org.jclouds.abiquo.domain.infrastructure.Datacenter;
import org.jclouds.abiquo.reference.rest.ParentLinkName;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Image.Status;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.domain.Location;

import com.abiquo.model.rest.RESTLink;
import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * Transforms a {@link VirtualMachineTemplate} into an {@link Image}.
 * <p>
 * Images are scoped to a region (physical datacenter).
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class VirtualMachineTemplateToImage implements Function<VirtualMachineTemplate, Image> {
   private final Function<Datacenter, Location> datacenterToLocation;

   private final Supplier<Map<Integer, Datacenter>> regionMap;

   @Inject
   public VirtualMachineTemplateToImage(final Function<Datacenter, Location> datacenterToLocation,
         @Memoized final Supplier<Map<Integer, Datacenter>> regionMap) {
      this.datacenterToLocation = checkNotNull(datacenterToLocation, "datacenterToLocation");
      this.regionMap = checkNotNull(regionMap, "regionMap");
   }

   @Override
   public Image apply(final VirtualMachineTemplate template) {
      ImageBuilder builder = new ImageBuilder();
      builder.ids(template.getId().toString());
      builder.name(template.getName());
      builder.description(template.getDescription());

      // Location information
      Datacenter region = regionMap.get().get(template.unwrap().getIdFromLink(ParentLinkName.DATACENTER));
      builder.location(datacenterToLocation.apply(region));

      // Only conversions have a status
      builder.status(Status.AVAILABLE);
      builder.backendStatus(Status.AVAILABLE.name()); // Abiquo images do not
                                                      // have a status

      RESTLink downloadLink = template.unwrap().searchLink("diskfile");
      builder.uri(downloadLink == null ? null : URI.create(downloadLink.getHref()));

      // TODO: Operating system not implemented in Abiquo Templates
      // TODO: Image credentials still not present in Abiquo template metadata
      // Will be added in Abiquo 2.4:
      // http://jira.abiquo.com/browse/ABICLOUDPREMIUM-3647
      builder.operatingSystem(OperatingSystem.builder().description(template.getName()).build());

      return builder.build();
   }
}
