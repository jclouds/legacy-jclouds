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
package org.jclouds.vcloud.features;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.vcloud.domain.VDC;

/**
 * Provides access to VDC functionality in vCloud
 * <p/>
 * 
 * @author Adrian Cole
 */
@Timeout(duration = 300, timeUnit = TimeUnit.SECONDS)
public interface VDCClient {
   VDC getVDC(URI vdc);

   /**
    * returns the VDC in the organization associated with the specified name. Note that both
    * parameters can be null to choose default.
    * 
    * @param orgName
    *           organization name, or null for the default
    * @param vdcName
    *           catalog name, or null for the default
    * @throws NoSuchElementException
    *            if you specified an org or vdc name that isn't present
    */
   VDC findVDCInOrgNamed(String orgName, String vdcName);

}
