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

import org.jclouds.glesys.features.ArchiveAsyncApi;
import org.jclouds.glesys.features.DomainAsyncApi;
import org.jclouds.glesys.features.EmailAccountAsyncApi;
import org.jclouds.glesys.features.IpAsyncApi;
import org.jclouds.glesys.features.ServerAsyncApi;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides asynchronous access to GleSYS via their REST API.
 * <p/>
 * 
 * @see GleSYSApi
 * @see <a href="https://customer.glesys.com/api.php" />
 * @author Adrian Cole
 * @deprecated please use {@code org.jclouds.ContextBuilder#buildApi(GleSYSApi.class)} as
 *             {@link GleSYSAsyncApi} interface will be removed in jclouds 1.7.
 */
@Deprecated
public interface GleSYSAsyncApi extends Closeable {

   /**
    * Provides asynchronous access to Server features.
    */
   @Delegate
   ServerAsyncApi getServerApi();

   /**
    * Provides asynchronous access to Ip Address features.
    */
   @Delegate
   IpAsyncApi getIpApi();

   /**
    * Provides asynchronous access to Archive features.
    */
   @Delegate
   ArchiveAsyncApi getArchiveApi();

   /**
    * Provides asynchronous access to DNS features.
    */
   @Delegate
   DomainAsyncApi getDomainApi();

   /**
    * Provides asynchronous access to E-Mail features.
    */
   @Delegate
   EmailAccountAsyncApi getEmailAccountApi();
   
}
