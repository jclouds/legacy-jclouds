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
package org.jclouds.openstack.nova.v2_0.extensions;

import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.openstack.nova.v2_0.domain.BackupType;
import org.jclouds.openstack.nova.v2_0.options.CreateBackupOfServerOptions;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;

/**
 * Provide additional actions for servers:
 * 'suspend', 'resume', 'migrate', 'lock', 'unlock', 'resetNetwork', 'createBackup', 'pause', 'migrateLive',
 * 'injectNetworkInfo', 'unpause'
 *
 * @author Adam Lowe
 * @see org.jclouds.openstack.nova.v2_0.extensions.AdminActionsAsyncClient
 */
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.ADMIN_ACTIONS)
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface AdminActionsClient {

   /**
    * Suspend a server.
    *
    * @param id id of the server
    */
   Boolean suspendServer(String id);

   /**
    * Resume a server.
    *
    * @param id id of the server
    */
   Boolean resumeServer(String id);

   /**
    * Migrate a server.
    *
    * @param id id of the server
    */
   Boolean migrateServer(String id);

   /**
    * Lock a server.
    *
    * @param id id of the server
    */
   Boolean lockServer(String id);

   /**
    * Unlock a server.
    *
    * @param id id of the server
    */
   Boolean unlockServer(String id);

   /**
    * Reset network of a server.
    *
    * @param id id of the server
    */
   Boolean resetNetworkOfServer(String id);

   /**
    * Create backup of a server.
    *
    * @param id         id of the server
    * @param imageName  the name of the image to create
    * @param backupType the type of backup
    * @param rotation   the number of images to retain (0 to simply overwrite)
    * @param options    optional rotation and/or metadata parameters
    * @return the id of the newly created image
    */
   String createBackupOfServer(String id, String imageName, BackupType backupType, int rotation, CreateBackupOfServerOptions... options);

   /**
    * Pause a server.
    *
    * @param id id of the server
    */
   Boolean pauseServer(String id);

   /**
    * Unpause a server.
    *
    * @param id id of the server
    */
   Boolean unpauseServer(String id);


   /**
    * Live migrate a server.
    *
    * @param id id of the server
    */
   Boolean liveMigrateServer(String id, String host, boolean blockMigration, boolean diskOverCommit);

   /**
    * Inject network info into a server.
    *
    * @param id id of the server
    */
   Boolean injectNetworkInfoIntoServer(String id);
}
