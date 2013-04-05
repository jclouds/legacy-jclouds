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

package org.jclouds.management.internal;

import org.jclouds.JcloudsVersion;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public final class ManagementUtils {

   private static final JcloudsVersion VERSION = JcloudsVersion.get();
   private static final String OBJECT_NAME_FORMAT = "org.jclouds:type=%s,name=%s,version=%d.%d";

   private ManagementUtils() {
      //Utility Class
   }

   /**
    * Registers a managed object to the mbean server.
    *
    * @param mBeanServer
    * @param mbean
    * @param type
    * @param name
    */
   public static void register(MBeanServer mBeanServer, Object mbean, String type, String name) {
      try {
         ObjectName objectName = objectNameFor(type, name);
         if (!mBeanServer.isRegistered(objectName)) {
            mBeanServer.registerMBean(mbean, objectName);
         }
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   /**
    * Un-registers a managed object to the mbean server.
    *
    * @param mBeanServer
    * @param type
    * @param name
    */
   public static void unregister(MBeanServer mBeanServer, String type, String name) {
      try {
         ObjectName objectName = objectNameFor(type, name);
         if (!mBeanServer.isRegistered(objectName)) {
            mBeanServer.unregisterMBean(objectName);
         }
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }


   /**
    * Creates a jclouds {@link ObjectName} for the mbean type.
    *
    * @param type
    * @return
    * @throws MalformedObjectNameException
    */
   public static ObjectName objectNameFor(String type, String name) throws MalformedObjectNameException {
      return new ObjectName(String.format(OBJECT_NAME_FORMAT, type, name, VERSION.majorVersion, VERSION.minorVersion));
   }
}
