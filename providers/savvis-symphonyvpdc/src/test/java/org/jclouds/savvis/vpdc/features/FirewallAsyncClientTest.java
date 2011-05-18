/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.savvis.vpdc.features;

import java.io.IOException;
import java.lang.reflect.Method;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.savvis.vpdc.domain.FirewallRule;
import org.jclouds.savvis.vpdc.xml.TaskHandler;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests annotation parsing of {@code FirewallAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class FirewallAsyncClientTest extends BaseVPDCAsyncClientTest<FirewallAsyncClient> {

	public void testAddFirewallRule() throws NoSuchMethodException, IOException{
		Method method = FirewallAsyncClient.class.getMethod("addFirewallRule", String.class, String.class, FirewallRule.class);
		HttpRequest request = processor.createRequest(method, "11", "22", FirewallRule.builder().firewallType("SERVER_TIER_FIREWALL").isEnabled(true).source("internet")
			  	.destination("VM Tier01").port("22").protocol("Tcp").policy("allow").description("Server Tier Firewall Rule").isLogged(false).build());

		assertRequestLineEquals(request, "PUT https://api.symphonyvpdc.savvis.net/vpdc/v1.0/org/11/vdc/22/FirewallService/ HTTP/1.1");
		assertNonPayloadHeadersEqual(request, "");
		assertPayloadEquals(request, Strings2.toStringAndClose(getClass().getResourceAsStream("/firewallService-default.xml")),
	               "application/xml", false);

		assertResponseParserClassEquals(method, request, ParseSax.class);
		assertSaxResponseParserClassEquals(method, TaskHandler.class);

		checkFilters(request);
	}
	
	public void testDeleteFirewallRule() throws NoSuchMethodException, IOException{
		Method method = FirewallAsyncClient.class.getMethod("deleteFirewallRule", String.class, String.class, FirewallRule.class);
		HttpRequest request = processor.createRequest(method, "11", "22", FirewallRule.builder().firewallType("SERVER_TIER_FIREWALL").isEnabled(true).source("internet")
			  	.destination("VM Tier01").port("22").protocol("Tcp").policy("allow").description("Server Tier Firewall Rule").isLogged(false).build());

		assertRequestLineEquals(request, "DELETE https://api.symphonyvpdc.savvis.net/vpdc/v1.0/org/11/vdc/22/FirewallService/ HTTP/1.1");
		assertNonPayloadHeadersEqual(request, "");
		assertPayloadEquals(request, Strings2.toStringAndClose(getClass().getResourceAsStream("/firewallService-default.xml")),
	               "application/xml", false);

		assertResponseParserClassEquals(method, request, ParseSax.class);
		assertSaxResponseParserClassEquals(method, TaskHandler.class);
		assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

		checkFilters(request);
	}

   @Override
   protected TypeLiteral<RestAnnotationProcessor<FirewallAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<FirewallAsyncClient>>() {
      };
   }

}
