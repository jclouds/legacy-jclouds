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
package org.jclouds.openstack.glance.v1_0.functions;

import javax.inject.Inject;

import org.jclouds.date.DateService;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.glance.v1_0.domain.ContainerFormat;
import org.jclouds.openstack.glance.v1_0.domain.DiskFormat;
import org.jclouds.openstack.glance.v1_0.domain.Image;
import org.jclouds.openstack.glance.v1_0.domain.ImageDetails;

import com.google.common.base.Function;

/**
 * This parses {@link ImageDetails} from HTTP headers.
 * 
 * @author Adrian Cole
 */
public class ParseImageDetailsFromHeaders implements Function<HttpResponse, ImageDetails> {
   private final DateService dateService;

   @Inject
   public ParseImageDetailsFromHeaders(DateService dateService) {
      this.dateService = dateService;
   }

   public ImageDetails apply(HttpResponse from) {
      ImageDetails.Builder<?> builder = ImageDetails.builder()
                .id(from.getFirstHeaderOrNull("X-Image-Meta-Id"))
                .name(from.getFirstHeaderOrNull("X-Image-Meta-Name"))
                .checksum(from.getFirstHeaderOrNull("X-Image-Meta-Checksum"))
                .containerFormat(ContainerFormat.fromValue(from.getFirstHeaderOrNull("X-Image-Meta-Container_format")))
                .diskFormat(DiskFormat.fromValue(from.getFirstHeaderOrNull("X-Image-Meta-Disk_format")))
                .size(Long.parseLong(from.getFirstHeaderOrNull("X-Image-Meta-Size")))
                .minDisk(Long.parseLong(from.getFirstHeaderOrNull("X-Image-Meta-Min_disk")))
                .minRam(Long.parseLong(from.getFirstHeaderOrNull("X-Image-Meta-Min_ram")))
                .isPublic(Boolean.parseBoolean(from.getFirstHeaderOrNull("X-Image-Meta-Is_public")))
                .createdAt(dateService.iso8601SecondsDateParse(from.getFirstHeaderOrNull("X-Image-Meta-Created_at")))
                .updatedAt(dateService.iso8601SecondsDateParse(from.getFirstHeaderOrNull("X-Image-Meta-Updated_at")))
                .owner(from.getFirstHeaderOrNull("X-Image-Meta-Owner"))
                .status(Image.Status.fromValue(from.getFirstHeaderOrNull("X-Image-Meta-Status")));
                         
      String deletedAt = from.getFirstHeaderOrNull("X-Image-Meta-Deleted_at");
      if (deletedAt != null)
         builder.deletedAt(dateService.iso8601SecondsDateParse(deletedAt));

      return builder.build();
   }
}
