/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.vcloud.compute.domain;

import java.net.URI;
import java.util.Map;

import javax.annotation.Nullable;

import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.internal.ImageImpl;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.vcloud.domain.VAppTemplate;

/**
 * @author Adrian Cole
 */
public class VCloudImage extends ImageImpl {

   /** The serialVersionUID */
   private static final long serialVersionUID = -8520373150950058296L;

   private final VAppTemplate template;

   public VCloudImage(VAppTemplate template, String providerId, String name, String id, Location location, URI uri,
            Map<String, String> userMetadata, String description, String version, @Nullable OsFamily osFamily,
            String osDescription, Architecture architecture, Credentials defaultCredentials) {
      super(providerId, name, id, location, uri, userMetadata, description, version, osFamily, osDescription,
               architecture, defaultCredentials);
      this.template = template;
   }

   public VAppTemplate getVAppTemplate() {
      return template;
   }

}