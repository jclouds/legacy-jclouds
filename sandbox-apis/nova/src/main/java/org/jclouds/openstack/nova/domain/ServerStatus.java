/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.openstack.nova.domain;

/**
 * 
 * Servers contain a status attribute that can be used as an indication of the current server state.
 * Servers with an ACTIVE status are available for use.
 * <p/>
 * <h2>Note</h2>
 * When the system changes a server's status from BUILD to ACTIVE the system will not be immediately
 * available. The 'ACTIVE' label is really misleading in the fact that it just means the system
 * doesn't have any activity going on related to it's configuration.
 * <p/>
 * Processes such as ssh will not be available until 5-10 seconds following the phase ACTIVE
 * <ul>
 * <li>[Web Hosting #119335]</li>
 * </ul>
 * 
 * @author Adrian Cole
 */
public enum ServerStatus {

   ACTIVE, SUSPENDED, QUEUE_RESIZE, PREP_RESIZE, RESIZE, VERIFY_RESIZE, RESCUE, BUILD, PASSWORD, REBUILD, REBOOT, HARD_REBOOT, UNKNOWN, DELETE_IP, UNRECOGNIZED, DELETED;
   
   public String value() {
      return name();
   }

   public static ServerStatus fromValue(String v) {
      try {
         return valueOf(v);
      } catch (IllegalArgumentException e) {
         return UNRECOGNIZED;
      }
   }

}
