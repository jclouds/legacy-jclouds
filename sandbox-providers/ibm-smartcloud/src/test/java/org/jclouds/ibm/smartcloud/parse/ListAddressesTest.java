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
package org.jclouds.ibm.smartcloud.parse;

import java.util.Set;

import org.jclouds.ibm.smartcloud.domain.Address;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.rest.annotations.Unwrap;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ListAddressesTest")
public class ListAddressesTest extends BaseSetParserTest<Address> {

   @Override
   public String resource() {
      return "/addresses.json";
   }
   
   @Override
   @Unwrap
   public Set<Address> expected() {
      return ImmutableSet.of(new Address(2, "1", "129.33.196.243", "1217", "1"), new Address(3, "2", "129.33.196.244",
               "1218", null));
   }
}
