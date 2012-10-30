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

import java.util.Map;

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
import org.jclouds.vcloud.domain.Status;
import org.jclouds.vcloud.domain.VAppTemplate;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class ImageForVAppTemplate implements Function<VAppTemplate, Image> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   public Logger logger = Logger.NULL;

   private final Map<Status, Image.Status> toPortableImageStatus;
   private final Function<VAppTemplate, Envelope> templateToEnvelope;
   private final FindLocationForResource findLocationForResource;


   @Inject
   protected ImageForVAppTemplate(Map<Status, Image.Status> toPortableImageStatus, Function<VAppTemplate, Envelope> templateToEnvelope,
            FindLocationForResource findLocationForResource) {
      this.toPortableImageStatus = checkNotNull(toPortableImageStatus, "toPortableImageStatus");
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
      if (from.getVDC() != null) {
         builder.location(findLocationForResource.apply(from.getVDC()));
      } else {
         // otherwise, it could be in a public catalog, which is not assigned to a VDC
      }
      builder.description(from.getDescription() != null ? from.getDescription() : from.getName());
      builder.operatingSystem(CIMOperatingSystem.toComputeOs(ovf));
      builder.status(toPortableImageStatus.get(from.getStatus()));
      return builder.build();
   }

}
