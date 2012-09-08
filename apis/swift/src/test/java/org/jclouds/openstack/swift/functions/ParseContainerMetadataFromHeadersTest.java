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
package org.jclouds.openstack.swift.functions;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.swift.domain.ContainerMetadata;
import org.jclouds.openstack.swift.functions.ParseContainerMetadataFromHeaders;
import org.jclouds.openstack.swift.reference.SwiftHeaders;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseContainerMetadataFromHeaders}
 * 
 * @author Everett Toews
 */
@Test(groups = "unit")
public class ParseContainerMetadataFromHeadersTest {
	
	Injector i = Guice.createInjector(new AbstractModule() {
	
		@Override
		protected void configure() {
		}	
	});

	public void testParseContainerMetadataHeaders() {
		ParseContainerMetadataFromHeaders parser = i.getInstance(ParseContainerMetadataFromHeaders.class);
		GeneratedHttpRequest request = createMock(GeneratedHttpRequest.class);
		expect(request.getArgs()).andReturn(ImmutableList.<Object> of("container", "key")).atLeastOnce();
		expect(request.getEndpoint()).andReturn(URI.create("http://localhost/test")).atLeastOnce();
		replay(request);
		parser.setContext(request);
		
		HttpResponse response = HttpResponse.builder().statusCode(204).message("No Content").payload("")
			.addHeader(SwiftHeaders.CONTAINER_BYTES_USED, "42")
			.addHeader(SwiftHeaders.CONTAINER_OBJECT_COUNT, "1")
			.addHeader(SwiftHeaders.CONTAINER_METADATA_PREFIX + "label1", "test1")
			.addHeader(SwiftHeaders.CONTAINER_METADATA_PREFIX + "label2", "test2").build();
		
		response.getPayload().getContentMetadata().setContentType("text/plain");
		ContainerMetadata containerMetadata = parser.apply(response);
		
		assertEquals(containerMetadata.getBytes(), 42);
		assertEquals(containerMetadata.getCount(), 1);
		assertEquals(containerMetadata.getMetadata().get("label1"), "test1");
		assertEquals(containerMetadata.getMetadata().get("label2"), "test2");
	}
}
