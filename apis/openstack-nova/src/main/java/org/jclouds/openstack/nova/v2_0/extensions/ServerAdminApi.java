/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.openstack.nova.v2_0.extensions;

import org.jclouds.openstack.nova.v2_0.domain.BackupType;
import org.jclouds.openstack.nova.v2_0.options.CreateBackupOfServerOptions;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;

import com.google.common.annotations.Beta;

/**
 * Provide additional actions for servers:
 * 'suspend', 'resume', 'migrate', 'lock', 'unlock', 'resetNetwork', 'createBackup', 'pause', 'migrateLive',
 * 'injectNetworkInfo', 'unpause'
 *
 * @author Adam Lowe
 * @see org.jclouds.openstack.nova.v2_0.extensions.ServerAdminAsyncApi
 */
@Beta
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.ADMIN_ACTIONS)
public interface ServerAdminApi {

   /**
    * Suspend a server.
    *
    * @param id id of the server
    */
   Boolean suspend(String id);

   /**
    * Resume a server.
    *
    * @param id id of the server
    */
   Boolean resume(String id);

   /**
    * Migrate a server.
    *
    * @param id id of the server
    */
   Boolean migrate(String id);

   /**
    * Lock a server.
    *
    * @param id id of the server
    */
   Boolean lock(String id);

   /**
    * Unlock a server.
    *
    * @param id id of the server
    */
   Boolean unlock(String id);

   /**
    * Reset network of a server.
    *
    * @param id id of the server
    */
   Boolean resetNetwork(String id);

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
   String createBackup(String id, String imageName, BackupType backupType, int rotation, CreateBackupOfServerOptions... options);

   /**
    * Pause a server.
    *
    * @param id id of the server
    */
   Boolean pause(String id);

   /**
    * Unpause a server.
    *
    * @param id id of the server
    */
   Boolean unpause(String id);


   /**
    * Live migrate a server.
    *
    * @param id id of the server
    */
   Boolean liveMigrate(String id, String host, boolean blockMigration, boolean diskOverCommit);

   /**
    * Inject network info into a server.
    *
    * @param id id of the server
    */
   Boolean injectNetworkInfo(String id);
}
