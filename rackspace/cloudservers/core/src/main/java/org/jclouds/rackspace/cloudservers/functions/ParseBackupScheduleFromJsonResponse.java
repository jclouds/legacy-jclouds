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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.jclouds.http.functions.ParseJson;
import org.jclouds.rackspace.cloudservers.domain.BackupSchedule;

import com.google.gson.Gson;
import com.google.inject.Inject;

/**
 * This parses {@link BackupSchedule} from a gson string.
 * 
 * @author Adrian Cole
 */
public class ParseBackupScheduleFromJsonResponse extends ParseJson<BackupSchedule> {

   @Inject
   public ParseBackupScheduleFromJsonResponse(Gson gson) {
      super(gson);
   }

   private static class BackupScheduleResponse {
      BackupSchedule backupSchedule;
   }

   public BackupSchedule apply(InputStream stream) {

      try {
         return gson.fromJson(new InputStreamReader(stream, "UTF-8"), BackupScheduleResponse.class).backupSchedule;
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("jclouds requires UTF-8 encoding", e);
      }
   }
}