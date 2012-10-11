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

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.jclouds.ContextBuilder;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.keystone.v2_0.KeystoneApi;
import org.jclouds.openstack.keystone.v2_0.KeystoneAsyncApi;
import org.jclouds.openstack.keystone.v2_0.domain.PasswordCredentials;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.annotations.Credential;
import org.jclouds.rest.annotations.Identity;
import org.jclouds.openstack.keystone.v2_0.AuthenticationApi;
import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.jclouds.openstack.keystone.v2_0.domain.Token;
import org.jclouds.openstack.keystone.v2_0.domain.Service;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Uses Openstack-Keystone token Authentication to sign the request. The
 * endpoint is in the form http://<keystone server>:<keystone port>/version/.
 * For instance http://dns-name-keystone:5000/V2.0/. The filter gets the
 * necessary token, authentication string, and retrieve the cdmi endpoint server
 * and transforms the request to a cdmi request.
 * 
 * 
 * @see <a href= "http://api.openstack.org/" />
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-identity-service/2.0/content/"
 *      />
 * 
 * @author Kenneth Nagin
 * 
 */
@Singleton
public class OpenstackKeystoneAuthReqFilter implements HttpRequestFilter {
	private String tokenId;
	private String tenantId;
	private String cdmiEndpoint;
	private int cdmiReqPathIndex;
	private final String tenant;
	private final String identity;
	private final String credential;

	@Inject
	public OpenstackKeystoneAuthReqFilter(@Identity String tenantNameAndUsername, @Credential String credential) {
		if (tenantNameAndUsername.indexOf(':') == -1) {
			throw new AuthorizationException(String.format("Identity %s does not match format tenantName:username",
						tenantNameAndUsername), null);
		}
		this.credential = credential;
		this.tenant = tenantNameAndUsername.substring(0, tenantNameAndUsername.indexOf(':'));
		this.identity = tenantNameAndUsername.substring(tenantNameAndUsername.indexOf(':') + 1);

	}

	private HttpRequest buildRequest(HttpRequest requestIn) {
		if (tokenId == null) {
			Iterable<Module> modules = ImmutableSet.<Module> of(new SLF4JLoggingModule());
			String authPath = requestIn.getEndpoint().getPath().split("/")[1]; // first
			// path
			// element
			// is
			// version
			cdmiReqPathIndex = authPath.length() + 2; // Index to cdmi request
			String keystoneEndpoint = requestIn.getEndpoint().getScheme() + "://" + requestIn.getEndpoint().getHost()
						+ ":" + requestIn.getEndpoint().getPort() + "/" + authPath;
			ContextBuilder contextBuilder = ContextBuilder.newBuilder("openstack-keystone");
			RestContext<KeystoneApi, KeystoneAsyncApi> keystone = contextBuilder.credentials(identity, credential)
						.endpoint(keystoneEndpoint).modules(modules).build();
			AuthenticationApi authenticationApi = keystone.utils().injector().getInstance(AuthenticationApi.class);
			Access access = authenticationApi.authenticateWithTenantNameAndCredentials(tenant,
						PasswordCredentials.createWithUsernameAndPassword(identity, credential));
			// Parse out token and endpoint for requests to object-store
			// object-store's request is almost the same as cdmi request,
			// but cdmi and tenant id are appended to host:port:
			// http://<object-store
			// server>:<port>/cdmi/AUTH_<tenantid>/<container>/
			Token token = access.getToken();
			tokenId = token.getId();
			tenantId = "AUTH_" + token.getTenant().getId();
			for (Service service : access.getServiceCatalog()) {
				if (service.getType().matches("object-store")) {
					URI uri = service.getEndpoints().iterator().next().getPublicURL();
					cdmiEndpoint = uri.getScheme() + "://" + uri.getHost() + ":" + uri.getPort() + "/cdmi/" + tenantId;
					break;
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
