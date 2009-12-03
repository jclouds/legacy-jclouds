/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.vcloud.terremark.xml;

import static org.jclouds.vcloud.VCloudMediaType.CATALOG_XML;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.rest.domain.NamedResource;
import org.jclouds.rest.domain.internal.NamedLinkImpl;
import org.jclouds.rest.internal.NamedResourceImpl;
import org.jclouds.vcloud.endpoints.VCloudApi;
import org.jclouds.vcloud.terremark.domain.TerremarkVDC;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

/**
 * Tests behavior of {@code TerremarkVDCHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.TerremarkVDCHandlerTest")
public class TerremarkVDCHandlerTest {

	public void testApplyInputStream() {
		InputStream is = getClass().getResourceAsStream("/terremark/vdc.xml");
		Injector injector = Guice.createInjector(new ParserModule(),
				new AbstractModule() {

					@Override
					protected void configure() {
					}

					@SuppressWarnings("unused")
					@Provides
					@VCloudApi
					URI provide() {
						return URI
								.create("https://services.vcloudexpress.terremark.com/api/v0.8");
					}

				});
		Factory factory = injector.getInstance(ParseSax.Factory.class);

		TerremarkVDC result = (TerremarkVDC) factory.create(
				injector.getInstance(TerremarkVDCHandler.class)).parse(is);
		assertEquals(result.getName(), "Miami Environment 1");
		assertEquals(
				result.getLocation(),
				URI
						.create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32"));
		assertEquals(result.getResourceEntities(), ImmutableMap
				.<String, NamedResource> of());
		assertEquals(
				result.getAvailableNetworks(),
				ImmutableMap
						.of(
								"10.114.34.128/26",
								new NamedResourceImpl(
										"1708",
										"10.114.34.128/26",
										"application/vnd.vmware.vcloud.network+xml",
										URI
												.create("https://services.vcloudexpress.terremark.com/api/v0.8/network/1708"))));
		assertEquals(
				result.getCatalog(),
				new NamedLinkImpl(
						"Miami Environment 1",
						CATALOG_XML,
						URI
								.create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32/catalog")));
		assertEquals(
				result.getPublicIps(),
				new NamedLinkImpl(
						"Public IPs",
						"application/xml",
						URI
								.create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32/publicIps")));
		assertEquals(
				result.getInternetServices(),
				new NamedLinkImpl(
						"Internet Services",
						"application/xml",
						URI
								.create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32/internetServices")));
	}
}
