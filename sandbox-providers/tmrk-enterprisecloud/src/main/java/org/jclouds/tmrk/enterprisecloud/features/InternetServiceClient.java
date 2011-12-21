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
package org.jclouds.tmrk.enterprisecloud.features;

import org.jclouds.concurrent.Timeout;
import org.jclouds.tmrk.enterprisecloud.domain.Task;
import org.jclouds.tmrk.enterprisecloud.domain.service.internet.InternetService;

import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * Provides synchronous access to Internet Service.
 * <p/>
 * 
 * @see org.jclouds.tmrk.enterprisecloud.features.LayoutAsyncClient
 * @see <a href=
 *      "http://support.theenterprisecloud.com/kb/default.asp?id=984&Lang=1&SID="
 *      />
 * @author Jason King
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface InternetServiceClient {

   
   /**
    * The getInternetService call returns information regarding a specified Internet service defined in an environment.
    * @param uri the uri of the internet service
    *  e.g. /cloudapi/ecloud/internetservices/{internet service id}
    * @return the internet service
    */
   InternetService getInternetService(URI uri);

   /**
    * The editInternetService call edits the name, enablement, description, persistence,
    * redirect URL, trusted network group, or backup Internet service
    * on a specified Internet service in an environment.
    * If successful, the call returns the task that modified the Internet service.
    *
    * The name property on InternetService is required and may be changed.
    * Note: The name may not be changed to that of another Internet service.
    * Port is optional and ignored if present.
    * Enabled is required.
    * Persistence Type refers to the method for persisting a connection session.
    * e.g. SourceIp – use the IP address of the source device for persistence.
    * If Timeout is absent with Type=SourceIp, then Timeout defaults to 2 minutes.
    * Omit Timeout if Type=none.
    * Note: The minimum value for Timeout is 2 (for two minutes) and the maximum is 5.

    * Both TrustedNetworkGroup and BackupInternetService are optional.
    * Including a TrustedNetworkGroup or BackupInternetService not currently on the Internet service
    * adds that trusted network group or backup Internet service to the Internet service.
    * Note: If any TrustedNetworkGroup is valued on the Internet service and not present in the call,
    * that trusted network group is removed from the Internet service.
    * Similarly, if any BackupInternetService is valued on the Internet service and not present in the call,
    * that backup Internet service is removed from the Internet service.
    *
    * @param service the internet service to edit
    * @return the Task representing the create action
    */
   Task editInternetService(InternetService service);

   /**
    * The createInternetService call creates an Internet service on a public IP address in an environment.
    * If successful, the call returns information regarding the Internet service that was created.
    * Input InternetService object Properties:
    * The name is required and may not be that of another Internet service.
    * Protocol is required.
    * Port is required and must be in the range of 1 to 65535.
    * Enabled is required.
    * Type refers to the method for persisting a connection session.
    * If Timeout is absent with a type of SourceIp then Timeout defaults to 2 minutes.
    * Omit Timeout if type is None.
    *
    * Note: The href of the InternetService data parameter is not used.
    * When creating the input object you may use any value or an empty URI
    * e.g. java.net.URI.create("")
    *
    * @param uri The uri of the call based on the public IP identifier
    * e.g. /cloudapi/ecloud/internetServices/publicIps/{public IP identifier}/action/createInternetService
    * @param data The input internet service data used to create the service
    * @return
    */
   InternetService createInternetService(URI uri, InternetService data);

   /**
    * The removeInternetService call removes an Internet service from an environment.
    * If successful, the call returns the task that removed the Internet service.
    * Note: The Internet service must have no node services associated to remove.
    * @param uri the uri of the call based on the internet service
    *   e.g. /cloudapi/ecloud/internetServices/{internet service identifier}
    * @return
    */
   Task removeInternetService(URI uri);
}
