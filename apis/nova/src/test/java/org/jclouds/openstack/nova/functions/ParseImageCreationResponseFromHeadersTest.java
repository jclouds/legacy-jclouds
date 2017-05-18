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

import static org.testng.Assert.assertEquals;

import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.domain.Image;
import org.jclouds.openstack.nova.domain.ImageStatus;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Marek Kasztelnik
 */
@Test(groups = "unit")
public class ParseImageCreationResponseFromHeadersTest {

	private Injector i = Guice.createInjector(new AbstractModule() {
		@Override
		protected void configure() {
		}
	});

	@Test
	public void testParseImageIdFromHeadersTest() throws Exception {
		ParseImageCreationResponseFromHeaders parser = i
				.getInstance(ParseImageCreationResponseFromHeaders.class);

		HttpResponse response = new HttpResponse(202, "Accepted", null,
				ImmutableMultimap.<String, String> of("Location",
						"http://endpoint/vapi-version/imagees/23"));

		Image image = parser.apply(response);

		assertEquals(image.getId(), 23);
		assertEquals(image.getStatus(), ImageStatus.UNKNOWN);

	}
}
