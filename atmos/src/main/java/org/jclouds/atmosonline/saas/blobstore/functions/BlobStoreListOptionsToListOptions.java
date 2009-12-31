/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.atmosonline.saas.blobstore.functions;

import javax.inject.Singleton;

import org.jclouds.blobstore.options.ListContainerOptions;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class BlobStoreListOptionsToListOptions implements Function<ListContainerOptions[], org.jclouds.atmosonline.saas.options.ListOptions> {
   public org.jclouds.atmosonline.saas.options.ListOptions apply(ListContainerOptions[] optionsList) {
      org.jclouds.atmosonline.saas.options.ListOptions httpOptions = new org.jclouds.atmosonline.saas.options.ListOptions();
      if (optionsList.length != 0) {
         if (optionsList[0].getMarker() != null) {
            httpOptions.token(optionsList[0].getMarker());
         }
         if (optionsList[0].getMaxResults() != null) {
            httpOptions.limit(optionsList[0].getMaxResults());
         }
      }
      return httpOptions;
   }
}