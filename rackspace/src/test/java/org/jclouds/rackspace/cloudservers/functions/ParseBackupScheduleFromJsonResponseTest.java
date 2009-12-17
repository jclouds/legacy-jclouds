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
package org.jclouds.rackspace.cloudservers.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.UnknownHostException;

import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.rackspace.cloudservers.domain.BackupSchedule;
import org.jclouds.rackspace.cloudservers.domain.DailyBackup;
import org.jclouds.rackspace.cloudservers.domain.WeeklyBackup;
import org.jclouds.util.Utils;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseBackupScheduleFromJsonResponse}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudservers.ParseBackupScheduleFromJsonResponseTest")
public class ParseBackupScheduleFromJsonResponseTest {

   Injector i = Guice.createInjector(new ParserModule());

   public void testApplyInputStreamDetails() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/cloudservers/test_list_backupschedule.json");

      ParseBackupScheduleFromJsonResponse parser = new ParseBackupScheduleFromJsonResponse(i
               .getInstance(Gson.class));
      BackupSchedule response = parser.apply(is);
      assertEquals(new BackupSchedule(WeeklyBackup.THURSDAY, DailyBackup.H_0400_0600, true),
               response);
   }

   public void testNoSchedule() throws UnknownHostException {

      ParseBackupScheduleFromJsonResponse parser = new ParseBackupScheduleFromJsonResponse(i
               .getInstance(Gson.class));
      BackupSchedule response = parser.apply(Utils
               .toInputStream("{\"backupSchedule\":{\"enabled\" : false}}"));
      assertEquals(new BackupSchedule(), response);
   }
}
