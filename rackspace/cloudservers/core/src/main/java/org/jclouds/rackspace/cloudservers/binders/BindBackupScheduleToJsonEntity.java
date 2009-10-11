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
package org.jclouds.rackspace.cloudservers.binders;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;

import org.jclouds.http.HttpRequest;
import org.jclouds.rackspace.cloudservers.domain.BackupSchedule;
import org.jclouds.rest.binders.BindToJsonEntity;

import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class BindBackupScheduleToJsonEntity extends BindToJsonEntity {

   @Override
   public void bindToRequest(HttpRequest request, Map<String, String> postParams) {
      throw new IllegalStateException(
               "Replace Backup Schedule needs an BackupSchedule object, not a Map");
   }

   @Override
   public void bindToRequest(HttpRequest request, Object toBind) {
      checkArgument(toBind instanceof BackupSchedule,
               "this binder is only valid for BackupSchedules!");
      super.bindToRequest(request, ImmutableMap.of("backupSchedule", toBind));
   }
}
