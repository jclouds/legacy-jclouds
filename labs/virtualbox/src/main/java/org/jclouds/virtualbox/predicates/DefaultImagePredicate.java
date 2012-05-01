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
package org.jclouds.virtualbox.predicates;

import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_DEFAULT_IMAGE_ARCH;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_DEFAULT_IMAGE_OS;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_DEFAULT_IMAGE_VERSION;

import org.jclouds.compute.domain.Image;

import com.google.common.base.Predicate;

public class DefaultImagePredicate implements Predicate<Image> {

   @Override
   public boolean apply(Image input) {
      return input.getOperatingSystem().getFamily() == VIRTUALBOX_DEFAULT_IMAGE_OS
               && input.getOperatingSystem().getVersion().equals(VIRTUALBOX_DEFAULT_IMAGE_VERSION)
               && input.getOperatingSystem().getArch().equals(VIRTUALBOX_DEFAULT_IMAGE_ARCH);
   }
}
