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
package org.jclouds.openstack.nova.functions;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.io.Payloads;
import org.jclouds.json.config.GsonModule;
import org.jclouds.openstack.nova.domain.SecurityGroup;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * @author chamerling
 *
 */
@Test(groups = "unit")
public class ParseSecurityGroupsListFromJsonResponse {
	
	Injector i = Guice.createInjector(new GsonModule());

	@Test
	public void testParseSecurityGroupsFromJsonResponseTest() throws IOException {
		List<SecurityGroup> response = parseSecurityGroups();

		String json = new Gson().toJson(response);
		assertNotNull(json);
		
		assertEquals(response.size(), 1);
		assertEquals(response.get(0).getId(), 1);
		assertEquals(response.get(0).getName(), "name1");
		assertEquals(response.get(0).getDescription(), "description1");
		assertEquals(response.get(0).getTenantId(), "tenant1");
	}

	public static List<SecurityGroup> parseSecurityGroups() {
		Injector i = Guice.createInjector(new GsonModule());

		InputStream is = ParseFloatingIPFromJsonResponse.class
				.getResourceAsStream("/test_list_security_groups.json");

		UnwrapOnlyJsonValue<List<SecurityGroup>> parser = i.getInstance(Key
				.get(new TypeLiteral<UnwrapOnlyJsonValue<List<SecurityGroup>>>() {
				}));
		return parser.apply(new HttpResponse(200, "ok", Payloads
				.newInputStreamPayload(is)));
	}
}
