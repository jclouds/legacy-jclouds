/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.openstack.swift.blobstore.functions;

import javax.inject.Singleton;

import org.jclouds.http.options.GetOptions;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class BlobToObjectGetOptions implements
         Function<org.jclouds.blobstore.options.GetOptions, GetOptions> {
   public GetOptions apply(org.jclouds.blobstore.options.GetOptions from) {
      GetOptions httpOptions = new GetOptions();
      if (from != null) {
         if (from.getIfMatch() != null) {
            httpOptions.ifETagMatches(from.getIfMatch());
         }
         if (from.getIfModifiedSince() != null) {
            httpOptions.ifModifiedSince(from.getIfModifiedSince());
         }
         if (from.getIfNoneMatch() != null) {
            httpOptions.ifETagDoesntMatch(from.getIfNoneMatch());
         }
         if (from.getIfUnmodifiedSince() != null) {
            httpOptions.ifUnmodifiedSince(from.getIfUnmodifiedSince());
         }
         for (String range : from.getRanges()) {
            String[] firstLast = range.split("\\-");
            if (firstLast.length == 2)
               httpOptions.range(Long.parseLong(firstLast[0]), Long.parseLong(firstLast[1]));
            else if (range.startsWith("-"))
               httpOptions.tail(Long.parseLong(firstLast[0]));
            else
               httpOptions.startAt(Long.parseLong(firstLast[0]));
         }
      }
      return httpOptions;
   }
}
