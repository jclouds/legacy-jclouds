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

package org.jclouds.serverlove;

import org.jclouds.domain.Credentials;
import org.jclouds.elasticstack.ElasticStackClientLiveTest;
import org.jclouds.elasticstack.domain.Server;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true)
public class ServerloveManchesterClientLiveTest extends ElasticStackClientLiveTest {
   public ServerloveManchesterClientLiveTest() {
      provider = "serverlove-z1-man";
      bootDrive = "5f2e0e29-2937-42b9-b362-d2d07eddbdeb";
   }

   protected Credentials getSshCredentials(Server server) {
      return new Credentials("root", server.getVnc().getPassword());
   }
}
