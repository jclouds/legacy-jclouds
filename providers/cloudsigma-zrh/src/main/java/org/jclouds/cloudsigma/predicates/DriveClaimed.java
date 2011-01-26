/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.cloudsigma.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.cloudsigma.CloudSigmaClient;
import org.jclouds.cloudsigma.domain.DriveInfo;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class DriveClaimed implements Predicate<DriveInfo> {

   private final CloudSigmaClient client;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public DriveClaimed(CloudSigmaClient client) {
      this.client = client;
   }

   public boolean apply(DriveInfo drive) {
      logger.trace("looking for claims on drive %s", checkNotNull(drive, "drive"));
      drive = refresh(drive);
      if (drive == null)
         return false;
      logger.trace("%s: looking for drive claims: currently: %s", drive.getUuid(), drive.getClaimed());
      return drive.getClaimed().size() > 0;
   }

   private DriveInfo refresh(DriveInfo drive) {
      return client.getDriveInfo(drive.getUuid());
   }
}
