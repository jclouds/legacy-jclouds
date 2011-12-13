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
package org.jclouds.tmrk.enterprisecloud.features;

import com.google.common.util.concurrent.ListenableFuture;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.*;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.tmrk.enterprisecloud.domain.keys.SSHKey;
import org.jclouds.tmrk.enterprisecloud.domain.keys.SSHKeys;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import java.net.URI;

/**
 * Provides asynchronous access to ssh keys via their REST API.
 * <p/>
 * 
 * @see SSHKeyClient
 * @see <a href=
 *      "http://support.theenterprisecloud.com/kb/default.asp?id=984&Lang=1&SID="
 *      />
 * @author Jason King
 */
@RequestFilters(BasicAuthentication.class)
@Headers(keys = "x-tmrk-version", values = "{jclouds.api-version}")
public interface SSHKeyAsyncClient {

   /**
    * @see SSHKeyClient#getSSHKeys
    */
   @GET
   @Consumes("application/vnd.tmrk.cloud.admin.sshKey; type=collection")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   public ListenableFuture<SSHKeys> getSSHKeys(@EndpointParam URI uri);

   /**
    * @see SSHKeyClient#getSSHKey
    */
   @GET
   @Consumes("application/vnd.tmrk.cloud.admin.sshKey")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   public ListenableFuture<SSHKey> getSSHKey(@EndpointParam URI uri);
}
