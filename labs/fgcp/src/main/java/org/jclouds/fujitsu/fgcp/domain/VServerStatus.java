/**
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
package org.jclouds.fujitsu.fgcp.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.CaseFormat;

/**
 * Possible statuses of a virtual server.
 * 
 * @author Dies Koper
 */
@XmlRootElement(name = "vserverStatus")
public enum VServerStatus {
   DEPLOYING, RUNNING, STOPPING, STOPPED, STARTING, FAILOVER, UNEXPECTED_STOP, RESTORING, BACKUP_ING, ERROR, START_ERROR, STOP_ERROR, CHANGE_TYPE, REGISTERING, UNRECOGNIZED;

   public String value() {
      return (CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name()));
   }

   @Override
   public String toString() {
      return value();
   }

   public static VServerStatus fromValue(String status) {
      try {
         return valueOf(CaseFormat.UPPER_CAMEL
               .to(CaseFormat.UPPER_UNDERSCORE,
                     checkNotNull(status, "status")));
      } catch (IllegalArgumentException e) {
         return UNRECOGNIZED;
      }
   }
}
