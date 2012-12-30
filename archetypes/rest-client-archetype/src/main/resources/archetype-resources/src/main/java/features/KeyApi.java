#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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
package ${package}.features;

import java.util.Set;
import ${package}.domain.Key;

/**
 * Description here
 * 
 * @author ${author}
 * @see KeyAsyncApi
 * @see <a href="TODO!">api doc</a>
 */
public interface KeyApi {

   /**
    * Lists all public keys we have on record for the specified account.
    */
   Set<Key> list();

   /**
    * Retrieves an individual key record.
    */
   Key get(String name);

   /**
    * Uploads a new OpenSSH key to SmartDataCenter for use in SSH and HTTP
    * signing.
    */
   Key create(Key key);

   /**
    * Deletes an SSH key by name.
    */
   void delete(String name);
}
