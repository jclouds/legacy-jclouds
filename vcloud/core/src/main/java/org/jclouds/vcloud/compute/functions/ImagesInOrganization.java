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

package org.jclouds.vcloud.compute.functions;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.transform;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.vcloud.domain.Organization;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * @author Adrian Cole
 */
@Singleton
public class ImagesInOrganization implements Function<Organization, Iterable<? extends Image>> {

   private final Supplier<Map<String, Map<String, ? extends org.jclouds.vcloud.domain.VDC>>> allVDCsInOrganization;
   private final ImagesInVDC imagesInVDC;

   @Inject
   public ImagesInOrganization(
         Supplier<Map<String, Map<String, ? extends org.jclouds.vcloud.domain.VDC>>> allVDCsInOrganization,
         ImagesInVDC imagesInVDC) {
      this.allVDCsInOrganization = allVDCsInOrganization;
      this.imagesInVDC = imagesInVDC;
   }

   @SuppressWarnings("unchecked")
   @Override
   public Iterable<? extends Image> apply(Organization from) {
      return concat(transform(concat(allVDCsInOrganization.get().get(from).values()), imagesInVDC));
   }

}