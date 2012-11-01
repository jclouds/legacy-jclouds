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
package ${package};

import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;

/**
 * Provides synchronous access to ${providerName}.
 * <p/>
 * 
 * @see ${providerName}AsyncClient
 * @see <a href="TODO: insert URL of ${providerName} documentation" />
 * @author ${author}
 */
@Timeout(duration = 4, timeUnit = TimeUnit.SECONDS)
public interface ${providerName}Client {
   /*
    * Note all these delegate to methods in ${providerName}AsyncClient with a specified or inherited timeout.
    *   The signatures should match those of ${providerName}AsyncClient, except the returnvals should not be
    *   wrapped in a Future 
    */
   
   String list();
   
   /**
    * @return null, if not found
    */
   String get(long id);
   
   void delete(long id);

}
