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
package org.jclouds.openstack.nova.v2_0.parse;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.json.BaseItemParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.openstack.nova.v2_0.config.NovaParserModule;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.features.FlavorApiExpectTest;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @see FlavorApiExpectTest
 * @author Ilja Bobkevic
 */
@Test(groups = "unit", testName = "ParseCreateFlavorTest")
public class ParseCreateFlavorTest extends BaseItemParserTest<Flavor> {

	@Override
	public String resource() {
		return "/flavor_new.json";
	}

	@Override
	@SelectJson("flavor")
	@Consumes(MediaType.APPLICATION_JSON)
	public Flavor expected() {
		return Flavor.builder()
				.id("1cb47a44-9b84-4da4-bf81-c1976e8414ab")
				.name("128 MB Server").ram(128).vcpus(1)
				.disk(10).build();
	}

	@Override
	protected Injector injector() {
		return Guice.createInjector(new NovaParserModule(), new GsonModule());
	}
}
