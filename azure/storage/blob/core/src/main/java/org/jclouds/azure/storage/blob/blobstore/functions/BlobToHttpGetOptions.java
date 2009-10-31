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
package org.jclouds.azure.storage.blob.blobstore.functions;

import javax.inject.Singleton;

import org.jclouds.http.options.GetOptions;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class BlobToHttpGetOptions implements
         Function<org.jclouds.blobstore.options.GetOptions[], GetOptions> {
   public GetOptions apply(org.jclouds.blobstore.options.GetOptions[] from) {
      GetOptions httpOptions = new GetOptions();
      if (from.length != 0) {
         if (from[0].getIfMatch() != null) {
            httpOptions.ifETagMatches(from[0].getIfMatch());
         }
         if (from[0].getIfModifiedSince() != null) {
            httpOptions.ifModifiedSince(from[0].getIfModifiedSince());
         }
         if (from[0].getIfNoneMatch() != null) {
            httpOptions.ifETagDoesntMatch(from[0].getIfNoneMatch());
         }
         if (from[0].getIfUnmodifiedSince() != null) {
            httpOptions.ifUnmodifiedSince(from[0].getIfUnmodifiedSince());
         }
         for (String range : from[0].getRanges()) {
            String[] firstLast = range.split("\\-");
            httpOptions.range(Long.parseLong(firstLast[0]), Long.parseLong(firstLast[1]));
         }
      }
      return httpOptions;
   }
}