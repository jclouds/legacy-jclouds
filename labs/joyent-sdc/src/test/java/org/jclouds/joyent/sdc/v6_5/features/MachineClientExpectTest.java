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
 * Unles required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either expres or implied.  See the License for the
 * specific language governing permisions and limitations
 * under the License.
 */
package org.jclouds.joyent.sdc.v6_5.features;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.joyent.sdc.v6_5.SDCClient;
import org.jclouds.joyent.sdc.v6_5.internal.BaseSDCClientExpectTest;
import org.jclouds.joyent.sdc.v6_5.parse.ParseCreatedMachineTest;
import org.jclouds.joyent.sdc.v6_5.parse.ParseMachineListTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Gerald Pereira
 */
@Test(groups = "unit", testName = "MachineClientExpectTest")
public class MachineClientExpectTest extends BaseSDCClientExpectTest {
	HttpRequest listMachines = HttpRequest.builder().method("GET").endpoint(
			URI.create("https://api.joyentcloud.com/my/machines")).headers(
			ImmutableMultimap.<String, String> builder().put("X-Api-Version",
					"~6.5").put("Accept", "application/json").put(
					"Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
					.build()).build();

	public void testListMachinesWhenResponseIs2xx() {
		HttpResponse listMachinesResponse = HttpResponse.builder()
				.statusCode(200).payload(
						payloadFromResource("/machine_list.json")).build();

		SDCClient clientWhenMachinesExists = requestSendsResponse(listMachines,
				listMachinesResponse);

		assertEquals(
				clientWhenMachinesExists.getMachineClient().listMachines(),
				new ParseMachineListTest().expected());
	}

	public void testListMachinesWhenResponseIs404() {
		HttpResponse listMachinesResponse = HttpResponse.builder().statusCode(
				404).build();

		SDCClient listMachinesWhenNone = requestSendsResponse(listMachines,
				listMachinesResponse);

		assertEquals(listMachinesWhenNone.getMachineClient().listMachines(),
				ImmutableSet.of());
	}
	
	  public void testCreateMachineWhenResponseIs202() throws Exception {
		  HttpRequest createMachine = HttpRequest.builder().method("POST").endpoint(
					URI.create("https://api.joyentcloud.com/my/machines")).headers(
					ImmutableMultimap.<String, String> builder().put("X-Api-Version",
							"~6.5").put("Accept", "application/json").put(
							"Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
							.build()).payload(payloadFromStringWithContentType(
				                     "{\"name\":\"testJClouds\",\"package\":\"Small 1GB\",\"dataset\":\"sdc:sdc:centos-5.7:1.2.1\"}","application/json")).build();

	      HttpResponse createMachineResponse = HttpResponse.builder().statusCode(202).message("HTTP/1.1 202 Accepted")
	            .payload(payloadFromResourceWithContentType("/new_machine.json","application/json; charset=UTF-8")).build();

	      SDCClient clientWithNewMachine = requestSendsResponse(createMachine, createMachineResponse);

	      assertEquals(clientWithNewMachine.getMachineClient().createMachine("testJClouds", "Small 1GB", "sdc:sdc:centos-5.7:1.2.1").toString(),
	              new ParseCreatedMachineTest().expected().toString());
	   }
}
