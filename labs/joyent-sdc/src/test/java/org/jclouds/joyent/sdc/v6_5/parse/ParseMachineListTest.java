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
package org.jclouds.joyent.sdc.v6_5.parse;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.joyent.sdc.v6_5.config.SDCParserModule;
import org.jclouds.joyent.sdc.v6_5.domain.Machine;
import org.jclouds.joyent.sdc.v6_5.domain.Type;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Gerald Pereira
 */
@Test(groups = "unit", testName = "ParseMachineListTest")
public class ParseMachineListTest extends BaseSetParserTest<Machine> {

	@Override
	public String resource() {
		return "/machine_list.json";
	}

	@Override
	@Consumes(MediaType.APPLICATION_JSON)
	public Set<Machine> expected() {
		return ImmutableSet
				.of(
						Machine
								.builder()
								.id("94eba336-ecb7-49f5-8a27-52f5e4dd57a1")
								.name("testJClouds")
								.type(Type.VIRTUALMACHINE)
								.state(Machine.State.RUNNING)
								.dataset("sdc:sdc:centos-5.7:1.2.1")
								.ips(
										ImmutableSet.<String> builder().add(
												"37.153.96.62").add(
												"10.224.0.63").build())
								.memorySizeMb(1024)
								.diskSizeGb(61440)
								.metadata(
										ImmutableMap
												.<String, String> builder()
												.put("root_authorized_keys",
														"ssh-rsa XXXXXX== test@xxxx.ovh.net\n")
												.build())
								.created(
										new SimpleDateFormatDateService()
												.iso8601SecondsDateParse("2012-05-09T13:32:46+00:00"))
								.updated(
										new SimpleDateFormatDateService()
												.iso8601SecondsDateParse("2012-05-11T09:00:33+00:00"))
								.build(),
								
						Machine
								.builder()
								.id("d73cb0b0-7d1f-44ef-8c40-e040eef0f726")
								.name("testJClouds2")
								.type(Type.SMARTMACHINE)
								.state(Machine.State.RUNNING)
								.dataset("sdc:sdc:smartosplus:3.1.0")
								.ips(
										ImmutableSet.<String> builder().add(
												"37.153.96.56").add(
												"10.224.0.57").build())
								.memorySizeMb(1024)
								.diskSizeGb(61440)
								.metadata(
										ImmutableMap
												.<String, String> of())
								.created(
										new SimpleDateFormatDateService()
												.iso8601SecondsDateParse("2012-05-09T13:39:43+00:00"))
								.updated(
										new SimpleDateFormatDateService()
												.iso8601SecondsDateParse("2012-05-09T13:43:45+00:00"))
								.build()

				);
	}

	protected Injector injector() {
		return Guice.createInjector(new SDCParserModule(), new GsonModule() {

			@Override
			protected void configure() {
				bind(DateAdapter.class).to(Iso8601DateAdapter.class);
				super.configure();
			}

		});
	}
}
