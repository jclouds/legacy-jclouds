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
package org.jclouds.cloudstack.parse;

import java.util.Set;

import org.jclouds.cloudstack.domain.PublicIPAddress;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListPublicIPAddressesResponseTest extends BaseSetParserTest<PublicIPAddress> {

   @Override
   public Class<PublicIPAddress> type() {
      return PublicIPAddress.class;
   }

   @Override
   public String resource() {
      return "/listpublicipaddressesresponse.json";
   }

   @Override
   public Set<PublicIPAddress> expected() {
      return ImmutableSet.of(PublicIPAddress.builder().id(30).IPAddress("72.52.126.59")
            .allocated(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-02-19T21:15:01-0800")).zoneId(1)
            .zoneName("San Jose 1").isSourceNAT(false).account("adrian").domainId(1).domain("ROOT")
            .usesVirtualNetwork(true).isStaticNAT(false).associatedNetworkId(204).networkId(200)
            .state(PublicIPAddress.State.ALLOCATED).build());
   }

}
