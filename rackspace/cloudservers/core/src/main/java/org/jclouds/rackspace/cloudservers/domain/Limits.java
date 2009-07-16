/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.rackspace.cloudservers.domain;

import java.util.List;

import com.google.inject.internal.Lists;

public class Limits {

   private List<RateLimit> rate = Lists.newArrayList();
   private List<AbsoluteLimit> absolute = Lists.newArrayList();

   public void setRate(List<RateLimit> rate) {
      this.rate = rate;
   }

   public List<RateLimit> getRate() {
      return rate;
   }

   public void setAbsolute(List<AbsoluteLimit> absolute) {
      this.absolute = absolute;
   }

   public List<AbsoluteLimit> getAbsolute() {
      return absolute;
   }

}
