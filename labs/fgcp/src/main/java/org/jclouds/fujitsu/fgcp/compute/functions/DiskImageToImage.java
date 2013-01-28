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
package org.jclouds.fujitsu.fgcp.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Image.Status;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.fujitsu.fgcp.domain.DiskImage;

import com.google.common.base.Function;

/**
 * Created by IntelliJ IDEA.
 * 
 * @author Dies Koper
 */
@Singleton
public class DiskImageToImage implements Function<DiskImage, Image> {

   private final DiskImageToOperatingSystem diskImageToOperatingSystem;

   @Inject
   public DiskImageToImage(DiskImageToOperatingSystem diskImageToOperatingSystem) {
      this.diskImageToOperatingSystem = checkNotNull(diskImageToOperatingSystem, "diskImageToOperatingSystem");
   }

   @Override
   public Image apply(DiskImage from) {
      checkNotNull(from, "disk image");

      ImageBuilder builder = new ImageBuilder();

      builder.ids(from.getId());
      builder.name(from.getName());
      builder.description(from.getDescription());
      // in fgcp, if the image is listed it is available
      builder.status(Status.AVAILABLE);
      OperatingSystem os = diskImageToOperatingSystem.apply(from);
      builder.operatingSystem(os);
      String user = os.getFamily() == OsFamily.WINDOWS ? "Administrator" : "root";
      builder.defaultCredentials(LoginCredentials.builder().identity(user).noPassword().build());
      return builder.build();
   }
}
