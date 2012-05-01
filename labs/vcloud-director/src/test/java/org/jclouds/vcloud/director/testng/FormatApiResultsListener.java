/*
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.vcloud.director.testng;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Outputs test status to the {@code jclouds.vcloud.api} logger.
 * 
 * Adapted from {@link org.jclouds.test.testng.UnitTestTestNGListener}.
 * 
 * @author Adrian Cole
 */
public class FormatApiResultsListener extends TestListenerAdapter {

   public static final Logger logger = LoggerFactory.getLogger("jclouds.vcloud.api");

   private static final SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   private static final Set<String> apis = ImmutableSet.of("admin", "user");

   @Override
   synchronized public void onTestSuccess(ITestResult res) {
      if (methodInApiGroup(res)) {
         String statusLine = resultForState(res, "succeeded");
         logger.info(statusLine);
      }
   }

   @Override
   synchronized public void onTestFailure(ITestResult res) {
      if (methodInApiGroup(res)) {
         String statusLine = resultForState(res, "failed");
         logger.info(statusLine);
      }
   }

   @Override
   synchronized public void onTestSkipped(ITestResult res) {
      if (methodInApiGroup(res)) {
         String statusLine = resultForState(res, "skipped");
         logger.info(statusLine);
      }
   }

   private boolean methodInApiGroup(ITestResult res) {
      return Iterables.any(Arrays.asList(res.getMethod().getGroups()), Predicates.in(apis));
   }

   private String resultForState(ITestResult res, String state) {
      return Joiner.on(',').join(getApi(res), getOperation(res), getStart(res), getTest(res), getDuration(res), state);
   }

   private String getApi(ITestResult res) {
      Optional<String> found = Iterables.tryFind(Arrays.asList(res.getMethod().getGroups()), Predicates.in(apis));
      return found.isPresent() ? found.get() : "";
   }

   private String getOperation(ITestResult res) {
      return Strings.nullToEmpty(res.getMethod().getDescription());
   }

   private String getTest(ITestResult res) {
      return Strings.nullToEmpty(res.getName());
   }

   private String getStart(ITestResult res) {
      return timestamp.format(res.getStartMillis());
   }

   private String getDuration(ITestResult res) {
      return Long.toString(res.getEndMillis() - res.getStartMillis());
   }
}
