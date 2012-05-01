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
package org.jclouds.vcloud.director.v1_5.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.CIMOperatingSystem;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.ovf.Envelope;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.predicates.LinkPredicates;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class ImageForVAppTemplate implements Function<VAppTemplate, Image> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   public Logger logger = Logger.NULL;

   private final Function<VAppTemplate, Envelope> templateToEnvelope;
   private final FindLocationForResource findLocationForResource;

   @Inject
   protected ImageForVAppTemplate(Function<VAppTemplate, Envelope> templateToEnvelope,
            FindLocationForResource findLocationForResource) {
      this.templateToEnvelope = checkNotNull(templateToEnvelope, "templateToEnvelope");
      this.findLocationForResource = checkNotNull(findLocationForResource, "findLocationForResource");
   }

   @Override
   public Image apply(VAppTemplate from) {
      checkNotNull(from, "VAppTemplate");
      Envelope ovf = templateToEnvelope.apply(from);

      ImageBuilder builder = new ImageBuilder();
      builder.ids(from.getHref().toASCIIString());
      builder.uri(from.getHref());
      builder.name(from.getName());
      Link vdc = Iterables.find(checkNotNull(from, "from").getLinks(), LinkPredicates.typeEquals(VCloudDirectorMediaType.VDC));
      if (vdc != null) {
         builder.location(findLocationForResource.apply(vdc));
      } else {
         // otherwise, it could be in a public catalog, which is not assigned to a VDC
      }
      builder.description(from.getDescription() != null ? from.getDescription() : from.getName());
      builder.operatingSystem(CIMOperatingSystem.toComputeOs(ovf));
      return builder.build();
   }

}