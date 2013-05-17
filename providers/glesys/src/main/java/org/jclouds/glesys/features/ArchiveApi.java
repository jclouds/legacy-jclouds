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
package org.jclouds.glesys.features;

import org.jclouds.glesys.domain.Archive;
import org.jclouds.glesys.domain.ArchiveAllowedArguments;

import com.google.common.collect.FluentIterable;

/**
 * Provides synchronous access to Archive requests.
 * <p/>
 *
 * @author Adam Lowe
 * @see ArchiveAsyncApi
 * @see <a href="https://github.com/GleSYS/API/wiki/API-Documentation" />
 */
public interface ArchiveApi {

   /**
    * Lists all active disks on this account.
    */
   FluentIterable<Archive> list();

   /**
    * Get detailed information about an archive volume.
    *
    * @param username the username associated with the archive
    * @return the archive information or null if not found
    */
   Archive get(String username);

   /**
    * Create a new backup volume.
    *
    * @param username the archive username, this must be prefixed by Glesys account name (in lower case) and an 
    *                 underscore, ex. "c100005_archive1"
    * @param password the new password
    * @param size     the new size required in GB
    */
   Archive createWithCredentialsAndSize(String username, String password, int size);

   /**
    * Delete an archive volume. All files on the volume
    *
    * @param username the username associated with the archive
    */
   void delete(String username);

   /**
    * Resize an archive volume. It is only possible to upgrade the size of the disk. Downgrading is currently not
    * supported. If you need to downgrade, please create a new volume and transfer all data to the new volume.
    * Then delete the old volume.
    *
    * @param username the username associated with the archive
    * @param size     the new size required, see #getAllowedArguments for valid values
    */
   Archive resize(String username, int size);

   /**
    * Change the password for an archive user.
    *
    * @param username the archive username
    * @param password the new password
    */
   Archive changePassword(String username, String password);

   /**
    * Lists the allowed arguments for some of the functions in this module such as archive size.
    */
   ArchiveAllowedArguments getAllowedArguments();

}
