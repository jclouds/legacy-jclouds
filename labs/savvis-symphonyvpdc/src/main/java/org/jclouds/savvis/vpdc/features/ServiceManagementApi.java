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
package org.jclouds.savvis.vpdc.features;

import java.net.URI;
import org.jclouds.savvis.vpdc.domain.Task;

/**
 * Provides access to Symphony VPDC resources via their REST API.
 * <p/>
 * 
 * @see <a href="https://api.sandbox.savvis.net/doc/spec/api/" />
 * @author Adrian Cole
 */
public interface ServiceManagementApi {
   /**
    * Powers on the VM
    * <p/>
    * <h4>Pre-conditions:</h4>
    * <p/>
    * No other API operation is being performed on the VM.
    * 
    * @param billingSiteId
    *           billing site Id, or null for default
    * @param vpdcId
    *           vpdc Id
    * @param vmId
    *           vm you wish to remove
    * @return task of the power operation
    */
   Task powerOnVMInVDC(String billingSiteId, String vpdcId, String vmId);

   /**
    * 
    * @param vm
    *           href of the vm
    * @see #powerOnVMInVDC
    */
   Task powerOnVM(URI vm);

   /**
    * Powers off the VM
    * <p/>
    * <h4>Pre-conditions:</h4>
    * <p/>
    * No other API operation is being performed on the VM.
    * 
    * @param billingSiteId
    *           billing site Id, or null for default
    * @param vpdcId
    *           vpdc Id
    * @param vmId
    *           vm you wish to remove
    * @return task of the power operation
    */
   Task powerOffVMInVDC(String billingSiteId, String vpdcId, String vmId);

   /**
    * 
    * @param vm
    *           href of the vm
    * @see #powerOffVMInVDC
    */
   Task powerOffVM(URI vm);

}
