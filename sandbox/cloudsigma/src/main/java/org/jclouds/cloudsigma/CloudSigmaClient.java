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

package org.jclouds.cloudsigma;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.cloudsigma.domain.DriveInfo;
import org.jclouds.concurrent.Timeout;
import org.jclouds.elasticstack.CommonElasticStackClient;
import org.jclouds.elasticstack.domain.CreateDriveRequest;
import org.jclouds.elasticstack.domain.DriveData;

/**
 * Provides synchronous access to CloudSigma.
 * <p/>
 * 
 * @see CloudSigmaAsyncClient
 * @see <a href="TODO: insert URL of elasticstack documentation" />
 * @author Adrian Cole
 */
@Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
public interface CloudSigmaClient extends CommonElasticStackClient {

   /**
    * list of drive uuids that are in the library
    * 
    * @return or empty set if no drives are found
    */
   Set<String> listStandardDrives();

   /**
    * list of cd uuids that are in the library
    * 
    * @return or empty set if no cds are found
    */
   Set<String> listStandardCds();

   /**
    * list of image uuids that are in the library
    * 
    * @return or empty set if no images are found
    */
   Set<String> listStandardImages();

   /**
    * {@inheritDoc}
    */
   @Override
   Set<? extends DriveInfo> listDriveInfo();

   /**
    * {@inheritDoc}
    */
   @Override
   DriveInfo getDriveInfo(String uuid);

   /**
    * {@inheritDoc}
    */
   @Override
   DriveInfo createDrive(CreateDriveRequest createDrive);

   /**
    * {@inheritDoc}
    */
   @Override
   DriveInfo setDriveData(String uuid, DriveData driveData);

}
