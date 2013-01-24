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

package org.jclouds.virtualbox.config;

import java.io.File;

/**
 * Configuration properties used for interacting with VirtualBox instances.
 * 
 * @author Mattias Holmqvist, Andrea Turli, David Alves
 * 
 */
public interface VirtualBoxConstants {

   public static final String VIRTUALBOX_NODE_NAME_SEPARATOR = "-0x0-";

   public static final String VIRTUALBOX_IMAGE_PREFIX = "jclouds-image" + VIRTUALBOX_NODE_NAME_SEPARATOR;

   public static final String VIRTUALBOX_NODE_PREFIX = "jclouds-node" + VIRTUALBOX_NODE_NAME_SEPARATOR;

   public static final String VIRTUALBOX_PRECONFIGURATION_URL = "jclouds.virtualbox.preconfigurationurl";

   public static final String VIRTUALBOX_WORKINGDIR = "jclouds.virtualbox.workingdir";

   public static final String VIRTUALBOX_IMAGES_DESCRIPTOR = "jclouds.virtualbox.image.descriptor.yaml";

   public static final String VIRTUALBOX_MACHINE_GROUP = "jclouds.virtualbox.machinegroup";

   public static final String VIRTUALBOX_MACHINE_USERNAME = "jclouds.virtualbox.username";

   public static final String VIRTUALBOX_MACHINE_CREDENTIAL = "jclouds.virtualbox.credential";

   public static final String VIRTUALBOX_MACHINE_LOCATION = "jclouds.virtualbox.location";
   
   public static final String VIRTUALBOX_GUEST_MEMORY = "jclouds.virtualbox.guest.memory";

   public static final String VIRTUALBOX_HOST_ID = "jclouds.virtualbox.hostid";

   public static final String VIRTUALBOX_WEBSERVER_IDENTITY = "jclouds.virtualbox.webserver.identity";

   public static final String VIRTUALBOX_WEBSERVER_CREDENTIAL = "jclouds.virtualbox.webserver.credential";

   public static final String VIRTUALBOX_DEFAULT_DIR = System.getProperty("user.home") + File.separator
            + ".jclouds-vbox";
   
   public static final String VIRTUALBOX_PROVIDER = "virtualbox";
   
   public static final String GUEST_OS_PASSWORD = "guestPassword";
   
   public static final String GUEST_OS_USER = "guestUser";
}
