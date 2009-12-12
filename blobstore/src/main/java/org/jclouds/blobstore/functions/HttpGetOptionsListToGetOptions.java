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
package org.jclouds.blobstore.functions;

import javax.inject.Inject;

import org.jclouds.blobstore.options.GetOptions;
import org.jclouds.util.DateService;
import org.joda.time.DateTime;

import com.google.common.base.Function;

public class HttpGetOptionsListToGetOptions implements
         Function<org.jclouds.http.options.GetOptions[], org.jclouds.blobstore.options.GetOptions> {
   private final DateService dateService;

   @Inject
   HttpGetOptionsListToGetOptions(DateService dateService) {
      this.dateService = dateService;
   }

   public GetOptions apply(org.jclouds.http.options.GetOptions[] from) {
      org.jclouds.blobstore.options.GetOptions to = new org.jclouds.blobstore.options.GetOptions();
      if (from.length != 0) {
         if (from[0].getIfMatch() != null) {
            to.ifETagMatches(from[0].getIfMatch().replaceAll("\"", ""));
         }
         if (from[0].getIfModifiedSince() != null) {
            DateTime time = dateService.rfc822DateParse(from[0].getIfModifiedSince());
            to.ifModifiedSince(time);
         }
         if (from[0].getIfNoneMatch() != null) {
            to.ifETagDoesntMatch(from[0].getIfNoneMatch().replaceAll("\"", ""));
         }
         if (from[0].getIfUnmodifiedSince() != null) {
            DateTime time = dateService.rfc822DateParse(from[0].getIfUnmodifiedSince());
            to.ifUnmodifiedSince(time);
         }
         for (String range : from[0].getRanges()) {
            String[] firstLast = range.split("\\-");
            to.range(Long.parseLong(firstLast[0]), Long.parseLong(firstLast[1]));
         }
      }
      return to;
   }

}
