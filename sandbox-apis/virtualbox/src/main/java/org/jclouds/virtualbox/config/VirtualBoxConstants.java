/*
 * *
 *  * Licensed to jclouds, Inc. (jclouds) under one or more
 *  * contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  jclouds licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.jclouds.virtualbox.config;

/**
 * Configuration properties used for interacting with VirtualBox instances.
 *
 * @author Mattias Holmqvist
 * 
 */
public interface VirtualBoxConstants {

   public static final String VIRTUALBOX_PRESEED_URL = "jclouds.virtualbox.preseedurl";

   public static final String VIRTUALBOX_SNAPSHOT_DESCRIPTION = "jclouds.virtualbox.snapshotDescription";

   public static final String VIRTUALBOX_INSTALLATION_KEY_SEQUENCE = "jclouds.virtualbox.installationkeysequence";
   
   public static final String VIRTUALBOX_HOSTNAME = "jclouds.virtualbox.hostname";
   
   public static final String VIRTUALBOX_WORKINGDIR = "jclouds.virtualbox.workingdir";
   
   public static final String VIRTUALBOX_ISOFILE = "jclouds.virtualbox.isofile";

   public static final String VIRTUALBOX_MACHINE_GROUP = "jclouds.virtualbox.machinegroup";

   public static final String VIRTUALBOX_MACHINE_USERNAME = "jclouds.virtualbox.username";

   public static final String VIRTUALBOX_MACHINE_CREDENTIAL = "jclouds.virtualbox.credential";

   public static final String VIRTUALBOX_MACHINE_LOCATION = "jclouds.virtualbox.location";

   public static final String VIRTUALBOX_HOST_ID = "jclouds.virtualbox.hostid";

   public static final String VIRTUALBOX_DISTRO_ISO_NAME = "jclouds.virtualbox.distroIsoName";
}
