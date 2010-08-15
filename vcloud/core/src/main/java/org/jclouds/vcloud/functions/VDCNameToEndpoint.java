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

package org.jclouds.vcloud.functions;

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.vcloud.domain.Organization;
import org.jclouds.vcloud.endpoints.VDC;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class VDCNameToEndpoint implements Function<Object, URI> {
   private final URI defaultVDC;
   private final Organization org;

   @Inject
   public VDCNameToEndpoint(Organization org, @VDC URI defaultVDC) {
      this.org = org;
      this.defaultVDC = defaultVDC;
   }

   public URI apply(Object from) {
      try {
         return from == null ? defaultVDC : org.getVDCs().get(from).getId();
      } catch (NullPointerException e) {
         throw new IllegalArgumentException("vdc name: " + from + " not in " + org.getVDCs());
      }
   }

}