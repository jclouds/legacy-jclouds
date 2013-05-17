/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.openstack.swift.extensions;


/**
 * @author Andrei Savu
 * @see <a href="http://docs.rackspace.com/files/api/v1/cf-devguide/content/Public_Access_to_Account-d1a4440.html" />
 */
public interface TemporaryUrlKeyApi {
   /**
    * Retrieve the key used to generate Temporary object access URLs
    *
    * @return shared secret key or null
    * @see <a href="http://docs.rackspace.com/files/api/v1/cf-devguide/content/Set_Account_Metadata-d1a4460.html" />
    */
   String getTemporaryUrlKey();

   /**
    * To create a Temporary URL you must first set a key as account metadata.
    * <p/>
    * Once the key is set, you should not change it while you still want others to be
    * able to access your temporary URL. If you change it, the TempURL becomes invalid
    * (within 60 seconds, which is the cache time for a key) and others will not be allowed
    * to access it.
    *
    * @param temporaryUrlKey
    * @see <a href="http://docs.rackspace.com/files/api/v1/cf-devguide/content/Set_Account_Metadata-d1a4460.html" />
    */
   void setTemporaryUrlKey(String temporaryUrlKey);
}
