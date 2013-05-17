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
package org.jclouds.cloudstack.options;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;

import com.google.common.collect.ImmutableSet;

/**
 * Options used to control what asyncJobs information is returned
 * 
 * @see <a
 *      href="http://download.cloud.com/releases/2.2.0/api/user/listAsyncJobs.html"
 *      />
 * @author Adrian Cole
 */
public class ListAsyncJobsOptions extends AccountInDomainOptions {
   private static final DateService dateService = new SimpleDateFormatDateService();

   public static final ListAsyncJobsOptions NONE = new ListAsyncJobsOptions();

   /**
    * @param startDate
    *           the start date of the async job
    */
   public ListAsyncJobsOptions startDate(Date startDate) {
      this.queryParameters.replaceValues("startdate",
            ImmutableSet.of(dateService.iso8601SecondsDateFormat(checkNotNull(startDate, "startDate"))));
      return this;
   }

   public static class Builder {

      /**
       * @see ListAsyncJobsOptions#startDate
       */
      public static ListAsyncJobsOptions startDate(Date startDate) {
         ListAsyncJobsOptions options = new ListAsyncJobsOptions();
         return options.startDate(startDate);
      }

      /**
       * @see ListAsyncJobsOptions#domainId
       */
      public static ListAsyncJobsOptions domainId(String id) {
         ListAsyncJobsOptions options = new ListAsyncJobsOptions();
         return options.domainId(id);
      }

      /**
       * @see ListAsyncJobsOptions#accountInDomain
       */
      public static ListAsyncJobsOptions accountInDomain(String account, String domain) {
         ListAsyncJobsOptions options = new ListAsyncJobsOptions();
         return options.accountInDomain(account, domain);
      }

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListAsyncJobsOptions accountInDomain(String account, String domain) {
      return ListAsyncJobsOptions.class.cast(super.accountInDomain(account, domain));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListAsyncJobsOptions domainId(String domainId) {
      return ListAsyncJobsOptions.class.cast(super.domainId(domainId));
   }
}
