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
package org.jclouds.blobstore.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.blobstore.options.GetOptions;
import org.jclouds.date.DateService;
import org.jclouds.util.Strings2;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class HttpGetOptionsListToGetOptions implements
      Function<org.jclouds.http.options.GetOptions[], org.jclouds.blobstore.options.GetOptions> {
   private final DateService dateService;

   @Inject
   HttpGetOptionsListToGetOptions(DateService dateService) {
      this.dateService = checkNotNull(dateService, "dateService");
   }

   public GetOptions apply(org.jclouds.http.options.GetOptions[] from) {
      checkNotNull(from, "options");

      org.jclouds.blobstore.options.GetOptions to = new org.jclouds.blobstore.options.GetOptions();
      if (from.length != 0) {
         if (from[0].getIfMatch() != null) {
            to.ifETagMatches(Strings2.replaceAll(from[0].getIfMatch(), '"', ""));
         }
         if (from[0].getIfModifiedSince() != null) {
            Date time = dateService.rfc822DateParse(from[0].getIfModifiedSince());
            to.ifModifiedSince(time);
         }
         if (from[0].getIfNoneMatch() != null) {
            to.ifETagDoesntMatch(Strings2.replaceAll(from[0].getIfNoneMatch(), '"', ""));
         }
         if (from[0].getIfUnmodifiedSince() != null) {
            Date time = dateService.rfc822DateParse(from[0].getIfUnmodifiedSince());
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
