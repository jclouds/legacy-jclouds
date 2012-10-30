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
package org.jclouds.virtualbox.predicates;

import javax.annotation.Resource;

import org.jclouds.logging.Logger;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshException;

import com.google.common.base.Predicate;

/**
 * 
 * @author Adrian Cole
 */
public class SshResponds implements Predicate<SshClient> {
   @Resource
   protected Logger logger = Logger.NULL;

   @Override
   public boolean apply(SshClient client) {

      try {
         client.connect();
         if (client.exec("id").getExitStatus() == 0) {
            return true;
         }
      } catch (SshException e) {
         logger.trace("No response from ssh daemon connecting to %s: %s", client, e.getMessage());
      } finally {
        if (client != null) {
         client.disconnect();
        }
      }
      return false;
   }
}
