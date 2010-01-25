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
package org.jclouds.aws.s3.blobstore.functions;

import javax.inject.Singleton;

import org.jclouds.aws.s3.options.ListBucketOptions;
import org.jclouds.blobstore.options.ListContainerOptions;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class ContainerToBucketListOptions implements
         Function<ListContainerOptions, ListBucketOptions> {
   public ListBucketOptions apply(ListContainerOptions from) {
      ListBucketOptions httpOptions = new ListBucketOptions();
      if (from != null && from != ListContainerOptions.NONE) {
         if (!from.isRecursive()) {
            httpOptions.delimiter("/");
         }
         if (from.getDir() != null) {// TODO unit test
            String path = from.getDir();
            if (!path.endsWith("/"))
               path = path + "/";
            httpOptions.withPrefix(path);
         }
         if (from.getMarker() != null) {
            httpOptions.afterMarker(from.getMarker());
         }
         if (from.getMaxResults() != null) {
            httpOptions.maxResults(from.getMaxResults());
         }
      }
      return httpOptions;
   }
}