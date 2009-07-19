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

/**
 * A backup schedule can be defined to create server images at regular intervals (daily and weekly).
 * Backup schedules are configurable per server.
 * 
 * @author Adrian Cole
 */
public class BackupSchedule {

   protected DailyBackup daily;
   protected boolean enabled;
   protected String weekly;

   public DailyBackup getDaily() {
      return daily;
   }

   public void setDaily(DailyBackup value) {
      this.daily = value;
   }

   public boolean isEnabled() {
      return enabled;
   }

   public void setEnabled(boolean value) {
      this.enabled = value;
   }

   public String getWeekly() {
      return weekly;
   }

   public void setWeekly(String value) {
      this.weekly = value;
   }

}
