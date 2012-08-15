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
package org.jclouds.snia.cdmi.v1.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import javax.inject.Singleton;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;
import org.jclouds.snia.cdmi.v1.queryparams.CDMIObjectQueryParams;

/**
 * This binding solves the problem jax-rs encoding ? ; : which some servers can
 * not handle
 * 
 * @author Kenneth Nagin
 */
@Singleton
public class BindQueryParmsToSuffix implements Binder {
	@SuppressWarnings("unchecked")
	@Override
	public <R extends HttpRequest> R bindToRequest(R request, Object input) {
		checkArgument(
				checkNotNull(input, "input") instanceof CDMIObjectQueryParams,
				"this binder is only valid for CDMIObjectQueryParams!");
		checkNotNull(request, "request");
		String queryParams = input.toString();
		return (R) request.toBuilder()
				.endpoint(request.getEndpoint() + "?" + queryParams).build();
	}

}
