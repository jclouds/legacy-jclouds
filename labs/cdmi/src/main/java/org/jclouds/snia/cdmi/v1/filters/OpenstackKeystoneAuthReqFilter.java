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
package org.jclouds.snia.cdmi.v1.filters;

import static com.google.common.base.Preconditions.checkNotNull;
import java.net.URI;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jclouds.ContextBuilder;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.RestContext;
import org.jclouds.domain.Credentials;
import org.jclouds.location.Provider;
import org.jclouds.openstack.keystone.v2_0.AuthenticationApi;
import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.jclouds.openstack.keystone.v2_0.domain.Endpoint;
import org.jclouds.openstack.keystone.v2_0.domain.Token;
import org.jclouds.openstack.keystone.v2_0.domain.Service;
import org.jclouds.openstack.keystone.v2_0.KeystoneApi;
import org.jclouds.openstack.keystone.v2_0.KeystoneAsyncApi;
import org.jclouds.openstack.keystone.v2_0.domain.PasswordCredentials;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Uses Openstack-Keystone token Authentication to sign the request. The endpoint is in the form http://<keystone
 * server>:<keystone port>/version/. For instance http://dns-name-keystone:5000/V2.0/. The filter gets the necessary
 * token, authentication string, and retrieve the cdmi endpoint server and transforms the request to a cdmi request.
 * 
 * 
 * @see <a href= "http://api.openstack.org/" />
 * @see <a href= "http://docs.openstack.org/api/openstack-identity-service/2.0/content/" />
 * 
 * @author Kenneth Nagin
 * 
 */
@Singleton
public class OpenstackKeystoneAuthReqFilter implements HttpRequestFilter {
	private final Supplier<Credentials> creds;
	private String tokenId;
	private String cdmiEndpoint;
	private int cdmiReqPathIndex;
   @Inject
   public OpenstackKeystoneAuthReqFilter(@Provider Supplier<Credentials> creds) {
   	this.creds = checkNotNull(creds, "creds");
   }

   private HttpRequest buildRequest(HttpRequest requestIn) {
      if (tokenId == null) {
         Credentials currentCreds = checkNotNull(creds.get(), "credential supplier returned null");
         if (currentCreds.identity.indexOf(':') == -1) {
            throw new AuthorizationException(String.format("Identity %s does not match format tenantId:username",
                  currentCreds.identity), null);
         }
         String tenantId = currentCreds.identity.substring(0, currentCreds.identity.indexOf(':'));
         String username = currentCreds.identity.substring(currentCreds.identity.indexOf(':') + 1);

         Iterable<Module> modules = ImmutableSet.<Module> of();
         // first path element is version
         String authPath = requestIn.getEndpoint().getPath().split("/")[1];
         cdmiReqPathIndex = authPath.length() + 2; // Index to cdmi request
         String keystoneEndpoint = requestIn.getEndpoint().getScheme() + "://" + requestIn.getEndpoint().getHost()
                  + ":" + requestIn.getEndpoint().getPort() + "/" + authPath;
         ContextBuilder contextBuilder = ContextBuilder.newBuilder("openstack-keystone");
         RestContext<KeystoneApi, KeystoneAsyncApi> keystone = contextBuilder.credentials(username, currentCreds.credential)
                  .endpoint(keystoneEndpoint).modules(modules).build();
         AuthenticationApi authenticationApi = keystone.utils().injector().getInstance(AuthenticationApi.class);
         Access access = authenticationApi.authenticateWithTenantNameAndCredentials(tenantId,
                  PasswordCredentials.createWithUsernameAndPassword(username, currentCreds.credential));
         // Parse out token and endpoint for requests to object-store
         // object-store's request is almost the same as cdmi request,
         // but cdmi and tenant id are appended to host:port:
         // http://<object-store
         // server>:<port>/cdmi/AUTH_<tenantid>/<container>/
         Token token = access.getToken();
         tokenId = token.getId();
         tenantId = "AUTH_" + token.getTenant().get().getId();
         for (Service service: access) {
         	if (service.getType().matches("object-store")) {
         		for(Endpoint endpoint: service) {
                  URI uri = endpoint.getPublicURL();
                  cdmiEndpoint = uri.getScheme() + "://" + uri.getHost() + ":" + uri.getPort() + "/cdmi/" + tenantId;
                  break;         			
         		}
         	}
         }
      }
      String queryString = "";
      if (requestIn.getEndpoint().getQuery() != null) {
         queryString = "?" + requestIn.getEndpoint().getQuery();
      }
      // replace keystone endpoint with cdmiEndpoint
      // and append with remainder, i.e. container, dataObject, and
      // queryString.
      HttpRequest requestOut = requestIn
               .toBuilder()
               .replaceHeader("X-Auth-Token", tokenId)
               .endpoint(
                        cdmiEndpoint + "/" + requestIn.getEndpoint().getPath().substring(cdmiReqPathIndex)
                                 + queryString).build();   
      return requestOut;
   }
   @Override
   public HttpRequest filter(HttpRequest requestIn) throws HttpException {
      return buildRequest(requestIn);
   }
}
