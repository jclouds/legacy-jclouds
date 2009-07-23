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
package org.jclouds.rackspace.cloudservers.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.UnknownHostException;

import org.apache.commons.io.IOUtils;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.rackspace.cloudservers.domain.BackupSchedule;
import org.jclouds.rackspace.cloudservers.domain.DailyBackup;
import org.jclouds.rackspace.cloudservers.domain.WeeklyBackup;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseBackupScheduleFromGsonResponse}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudservers.ParseBackupScheduleFromGsonResponseTest")
public class ParseBackupScheduleFromGsonResponseTest {

   Injector i = Guice.createInjector(new ParserModule());

   public void testApplyInputStreamDetails() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/test_list_backupschedule.json");

      ParseBackupScheduleFromGsonResponse parser = new ParseBackupScheduleFromGsonResponse(i
               .getInstance(Gson.class));
      BackupSchedule response = parser.apply(is);
      assertEquals(new BackupSchedule(WeeklyBackup.THURSDAY, DailyBackup.H_0400_0600, true),
               response);
   }

   public void testNoSchedule() throws UnknownHostException {

      ParseBackupScheduleFromGsonResponse parser = new ParseBackupScheduleFromGsonResponse(i
               .getInstance(Gson.class));
      BackupSchedule response = parser.apply(IOUtils
               .toInputStream("{\"backupSchedule\":{\"enabled\" : false}}"));
      assertEquals(new BackupSchedule(), response);
   }
}
