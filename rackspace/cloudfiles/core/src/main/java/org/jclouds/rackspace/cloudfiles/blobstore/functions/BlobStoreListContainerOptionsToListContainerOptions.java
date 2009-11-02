/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.rackspace.cloudfiles.blobstore.functions;

import javax.inject.Singleton;

import org.jclouds.blobstore.options.ListContainerOptions;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class BlobStoreListContainerOptionsToListContainerOptions
         implements
         Function<ListContainerOptions[], org.jclouds.rackspace.cloudfiles.options.ListContainerOptions> {
   public org.jclouds.rackspace.cloudfiles.options.ListContainerOptions apply(
            ListContainerOptions[] optionsList) {
      org.jclouds.rackspace.cloudfiles.options.ListContainerOptions options = new org.jclouds.rackspace.cloudfiles.options.ListContainerOptions();
      if (optionsList.length != 0) {

         if ((optionsList[0].getPath() == null) && (optionsList[0].isRecursive())) {
            options.withPrefix("");
         }
         if ((optionsList[0].getPath() == null) && (!optionsList[0].isRecursive())) {
            options.underPath("");
         }
         if ((optionsList[0].getPath() != null) && (optionsList[0].isRecursive())) {
            options.withPrefix(optionsList[0].getPath());
         }
         if ((optionsList[0].getPath() != null) && (!optionsList[0].isRecursive())) {
            options.underPath(optionsList[0].getPath());
         }
         if (optionsList[0].getMarker() != null) {
            options.afterMarker(optionsList[0].getMarker());
         }
         if (optionsList[0].getMaxResults() != null) {
            options.maxResults(optionsList[0].getMaxResults());
         }
      }
      return options;
   }

}