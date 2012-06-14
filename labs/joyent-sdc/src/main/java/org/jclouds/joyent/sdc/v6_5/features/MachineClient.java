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
package org.jclouds.joyent.sdc.v6_5.features;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.joyent.sdc.v6_5.domain.Machine;
import org.jclouds.joyent.sdc.v6_5.options.CreateMachineOptions;

/**
 * Provides synchronous access to Machine.
 * <p/>
 * 
 * @author Gerald Pereira
 * @see MachineAsyncClient
 * @see <a href="http://apidocs.joyent.com/sdcapidoc/cloudapi">api doc</a>
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface MachineClient {

   /**
    * Lists all machines we have on record for your account.
    * 
    * @return an account's associated machine objects.
    */
   Set<Machine> listMachines();

   /**
    * Gets the details for an individual machine.
    * 
    * @param id
    *           the id of the machine
    * @return
    */
   Machine getMachine(String id);

   /**
    * 
    * @param name
    *           friendly name for this machine; default is a randomly generated name
    * @param packageSDC
    *           Name of the package to use on provisioning; default is indicated in ListPackages
    * @param dataset
    *           dataset URN; default is indicated in ListDatasets
    * @param options
    *           optional parameters to be passed into the machine creation request
    * @return the newly created machine
    */
   Machine createMachine(String name, String packageSDC, String dataset, CreateMachineOptions... options);

   /**
    * Allows you to shut down a machine.
    * 
    * @param id
    *           the id of the machine to stop
    */
   void stopMachine(String id);

   /**
    * Allows you to boot up a machine.
    * 
    * @param id
    *           the id of the machine to start
    */
   void startMachine(String id);

   /**
    * Allows you to reboot a machine.
    * 
    * @param id
    *           the id of the machine to reboot
    */
   void rebootMachine(String id);

   /**
    * Allows you to resize a machine. (Works only for smart machines)
    * 
    * @param id
    *           the id of the machine to resize
    * @param packageSDC
    *           the package to use for the machine
    */
   void resizeMachine(String id, String packageSDC);

   /**
    * Allows you to delete a machine (the machine must be stopped before it can be deleted).
    * 
    * @param id
    *           the id of the machine to delete
    */
   void deleteMachine(String id);

}