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

import org.jclouds.cloudstack.domain.Account;
import org.jclouds.cloudstack.domain.Account.State;
import org.jclouds.cloudstack.domain.Account.Type;
import org.jclouds.cloudstack.domain.User;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListAccountsResponseTest extends BaseSetParserTest<Account> {

   @Override
   public Class<Account> type() {
      return Account.class;
   }

   @Override
   public String resource() {
      return "/listaccountsresponse.json";
   }

   @Override
   public Set<Account> expected() {
      return ImmutableSet.<Account> of(Account
            .builder()
            .id(36)
            .name("adrian")
            .type(Type.USER)
            .domainId(1)
            .domain("ROOT")
            .receivedBytes(0)
            .sentBytes(0)
            .VMLimit(500l)
            .VMs(-3)
            .VMsAvailable(503l)
            .IPLimit(null)
            .IPs(0)
            .IPsAvailable(null)
            .volumeLimit(null)
            .volumes(0)
            .volumesAvailable(null)
            .snapshotLimit(null)
            .snapshots(0)
            .snapshotsAvailable(null)
            .templateLimit(null)
            .templates(0)
            .templatesAvailable(null)
            .VMsStopped(0)
            .VMsRunning(0)
            .state(State.ENABLED)
            .users(
                  ImmutableSet.of(User.builder().id(46).name("adrian").firstName("Adrian").lastName("test")
                        .email("adrian@jcloud.com")
                        .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-03-26T23:10:49-0700"))
                        .state("enabled").account("adrian").accountType(Type.USER).domainId(1).domain("ROOT")
                        .apiKey("APIKEY").secretKey("SECRETKEY").build())).build());
   }

}
