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
package org.jclouds.ultradns.ws;

import javax.inject.Named;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.ultradns.ws.domain.Account;
import org.jclouds.ultradns.ws.filters.SOAPWrapWithPasswordAuth;
import org.jclouds.ultradns.ws.xml.AccountHandler;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to Neustar UltraDNS via the SOAP API
 * <p/>
 * 
 * @see <a href="https://www.ultradns.net/api/NUS_API_XML_SOAP.pdf" />
 * @author Adrian Cole
 */
@RequestFilters(SOAPWrapWithPasswordAuth.class)
@VirtualHost
public interface UltraDNSWSAsyncApi {

   /**
    * @see UltraDNSWSApi#getCurrentAccount()
    */
   @Named("getAccountsListOfUser")
   @POST
   @Path("/")
   @XMLResponseParser(AccountHandler.class)
   @Payload("<getAccountsListOfUser/>")
   ListenableFuture<Account> getCurrentAccount();

}
