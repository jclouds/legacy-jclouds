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
package org.jclouds.glesys;

import java.io.Closeable;

import org.jclouds.glesys.features.ArchiveApi;
import org.jclouds.glesys.features.DomainApi;
import org.jclouds.glesys.features.EmailAccountApi;
import org.jclouds.glesys.features.IpApi;
import org.jclouds.glesys.features.ServerApi;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides synchronous access to GleSYS.
 * <p/>
 * 
 * @see GleSYSAsyncApi
 * @see <a href="https://customer.glesys.com/api.php" />
 * @author Adrian Cole
 */
public interface GleSYSApi extends Closeable {

   /**
    * Provides synchronous access to Server features.
    */
   @Delegate
   ServerApi getServerApi();

   /**
    * Provides synchronous access to Ip Address features.
    */
   @Delegate
   IpApi getIpApi();

   /**
    * Provides synchronous access to Archive features.
    */
   @Delegate
   ArchiveApi getArchiveApi();

   /**
    * Provides synchronous access to DNS features.
    */
   @Delegate
   DomainApi getDomainApi();

   /**
    * Provides synchronous access to E-Mail features.
    */
   @Delegate
   EmailAccountApi getEmailAccountApi();

}
