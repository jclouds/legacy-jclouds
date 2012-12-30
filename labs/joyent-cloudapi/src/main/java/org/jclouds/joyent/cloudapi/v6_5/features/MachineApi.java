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
package org.jclouds.joyent.cloudapi.v6_5.features;

import java.util.Set;
import org.jclouds.joyent.cloudapi.v6_5.domain.Machine;
import org.jclouds.joyent.cloudapi.v6_5.options.CreateMachineOptions;

/**
 * Provides synchronous access to Machine.
 * <p/>
 * 
 * @author Gerald Pereira
 * @see MachineAsyncApi
 * @see <a href="http://apidocs.joyent.com/sdcapidoc/cloudapi/index.html#machines">api doc</a>
 */
public interface MachineApi {

   /**
    * Lists all machines we have on record for your account.
    * 
    * @return an account's associated machine objects.
    */
   Set<Machine> list();

   /**
    * Gets the details for an individual machine.
    * 
    * @param id
    *           the id of the machine
    * @return
    */
   Machine get(String id);

   /**
    * Allows you to provision a machine. Note that if you do not specify a
    * package, you'll get the datacenter defaults for it. If
    * you do not specify a name, CloudAPI will generate a random one for you.
    * Your machine will initially be not available for login (SmartDataCenter
    * must provision and boot it). You can poll {@link #get} for status. When the
    * state field is equal to running, you can login.
    * 
    * <p/>
    * With regards to login, if the machine is of type smartmachine, you can use
    * any of the SSH keys managed under the keys section of CloudAPI to login,
    * as any POSIX user on the OS. You can add/remove them over time, and the
    * machine will automatically work with that set.
    * 
    * <p/>
    * If the the machine is a virtualmachine, and of a UNIX-derived OS (e.g.
    * Linux), you must have keys uploaded before provisioning; that entire set
    * of keys will be written out to /root/.ssh/authorized_keys, and you can ssh
    * in using one of those. Changing the keys over time under your account will
    * not affect the running virtual machine in any way; those keys are
    * statically written at provisioning-time only, and you will need to
    * manually manage them.
    * 
    * <p/>
    * If the dataset you create a machine from is set to generate passwords for
    * you, the username/password pairs will be returned in the metadata response
    * as a nested object, like:
    * 
    * @param datasetURN urn of the dataset to install
    * 
    * @return the newly created machine
    */
   Machine createWithDataset(String datasetURN, CreateMachineOptions options);
   
   /**
    * 
    * @see #createWithDataset(CreateMachineOptions)
    */
   Machine createWithDataset(String datasetURN);

   /**
    * Allows you to shut down a machine.
    * 
    * @param id
    *           the id of the machine to stop
    */
   void stop(String id);

   /**
    * Allows you to boot up a machine.
    * 
    * @param id
    *           the id of the machine to start
    */
   void start(String id);

   /**
    * Allows you to reboot a machine.
    * 
    * @param id
    *           the id of the machine to reboot
    */
   void reboot(String id);

   /**
    * Allows you to resize a machine. (Works only for smart machines)
    * 
    * @param id
    *           the id of the machine to resize
    * @param packageJoyentCloud
    *           the package to use for the machine
    */
   void resize(String id, String packageJoyentCloud);

   /**
    * Allows you to delete a machine (the machine must be stopped before it can
    * be deleted).
    * 
    * @param id
    *           the id of the machine to delete
    */
   void delete(String id);

}
