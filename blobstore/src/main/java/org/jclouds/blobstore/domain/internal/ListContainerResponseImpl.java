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
package org.jclouds.blobstore.domain.internal;

import org.jclouds.blobstore.domain.ListContainerResponse;

public class ListContainerResponseImpl<T> extends ListResponseImpl<T> implements ListContainerResponse<T> {

   /** The serialVersionUID */
   private static final long serialVersionUID = -7133632087734650835L;
   protected final String path;

   public ListContainerResponseImpl(Iterable<T> contents, String path, String marker,
            Integer maxResults, boolean isTruncated) {
      super(contents, marker, maxResults, isTruncated);
      this.path = path;
   }

   public String getPath() {
      return path;
   }

}