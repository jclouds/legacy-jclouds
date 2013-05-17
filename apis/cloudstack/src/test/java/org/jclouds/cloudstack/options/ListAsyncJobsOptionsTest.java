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

import static org.jclouds.cloudstack.options.ListAsyncJobsOptions.Builder.accountInDomain;
import static org.jclouds.cloudstack.options.ListAsyncJobsOptions.Builder.domainId;
import static org.jclouds.cloudstack.options.ListAsyncJobsOptions.Builder.startDate;
import static org.testng.Assert.assertEquals;

import java.util.Date;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code ListAsyncJobsOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListAsyncJobsOptionsTest {
   public void testAccountInDomainId() {
      ListAsyncJobsOptions options = new ListAsyncJobsOptions().accountInDomain("adrian", "6");
      assertEquals(ImmutableList.of("adrian"), options.buildQueryParameters().get("account"));
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testAccountInDomainIdStatic() {
      ListAsyncJobsOptions options = accountInDomain("adrian", "6");
      assertEquals(ImmutableList.of("adrian"), options.buildQueryParameters().get("account"));
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testStartDate() {
      ListAsyncJobsOptions options = new ListAsyncJobsOptions().startDate(new Date(100000));
      assertEquals(ImmutableList.of("1970-01-01T00:01:40Z"), options.buildQueryParameters().get("startdate"));
   }

   public void testDomainId() {
      ListAsyncJobsOptions options = new ListAsyncJobsOptions().domainId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testStartDateStatic() {
      ListAsyncJobsOptions options = startDate(new Date(100000));
      assertEquals(ImmutableList.of("1970-01-01T00:01:40Z"), options.buildQueryParameters().get("startdate"));
   }

   public void testDomainIdStatic() {
      ListAsyncJobsOptions options = domainId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }
}
