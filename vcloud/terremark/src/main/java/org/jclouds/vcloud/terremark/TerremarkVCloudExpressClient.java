/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud.terremark;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.vcloud.terremark.domain.KeyPair;

/**
 * Provides access to VCloud resources via their REST API.
 * <p/>
 * 
 * @see <a href="https://community.vcloudexpress.terremark.com/en-us/discussion_forums/f/60.aspx" />
 * @author Adrian Cole
 */
@Timeout(duration = 300, timeUnit = TimeUnit.SECONDS)
public interface TerremarkVCloudExpressClient extends TerremarkVCloudClient {

   /**
    * This call returns the keys previously created for your organization.
    */
   Set<KeyPair> listKeyPairs();

   Set<KeyPair> listKeyPairsInOrg(String orgId);

   KeyPair generateKeyPairInOrg(String orgId, String name, boolean makeDefault);

   KeyPair getKeyPair(int keyPairId);

   // TODO
   // KeyPair configureKeyPair(int keyPairId, KeyPairConfiguration
   // keyPairConfiguration);

   void deleteKeyPair(int keyPairId);
}
