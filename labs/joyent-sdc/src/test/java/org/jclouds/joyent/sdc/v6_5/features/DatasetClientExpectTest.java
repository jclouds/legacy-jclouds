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
import org.jclouds.joyent.sdc.v6_5.parse.ParseDatasetListTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Gerald Pereira
 */
@Test(groups = "unit", testName = "DatasetClientExpectTest")
public class DatasetClientExpectTest extends BaseSDCClientExpectTest {
   HttpRequest listDatasets = HttpRequest.builder().method("GET").endpoint(
            URI.create("https://api.joyentcloud.com/my/datasets")).headers(
            ImmutableMultimap.<String, String> builder().put("X-Api-Version", "~6.5").put("Accept", "application/json")
                     .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build()).build();

   public void testListDatasetsWhenResponseIs2xx() {
      HttpResponse listDatasetsResponse = HttpResponse.builder().statusCode(200).payload(
               payloadFromResource("/dataset_list.json")).build();

      SDCClient clientWhenDatasetsExists = requestSendsResponse(listDatasets, listDatasetsResponse);

      assertEquals(clientWhenDatasetsExists.getDatasetClient().listDatasets().toString(), new ParseDatasetListTest()
               .expected().toString());
   }

   public void testListDatasetsWhenResponseIs404() {
      HttpResponse listDatasetsResponse = HttpResponse.builder().statusCode(404).build();

      SDCClient listDatasetsWhenNone = requestSendsResponse(listDatasets, listDatasetsResponse);

      assertEquals(listDatasetsWhenNone.getDatasetClient().listDatasets(), ImmutableSet.of());
   }

   // [id=e4cd7b9e-4330-11e1-81cf-3bb50a972bda, name=centos-6, type=VIRTUALMACHINE, version=1.0.1,
   // urn=sdc:sdc:centos-6:1.0.1, default=false, created=Mon Feb 13 07:30:33 CET 2012],
   // [id=e4cd7b9e-4330-11e1-81cf-3bb50a972bda, name=centos-6, type=VIRTUALMACHINE, version=1.0.1,
   // urn=sdc:sdc:centos-6:1.0.1, default=false, created=Mon Feb 13 07:30:33 CET 2012],
   //	 
   // [id=e62c30b4-cdda-11e0-9dd4-af4d032032e3, name=nodejs, type=SMARTMACHINE, version=1.2.3,
   // urn=sdc:sdc:nodejs:1.2.3, default=false, created=Thu Sep 15 10:15:29 CEST 2011]]
   //	 
   // [id=e62c30b4-cdda-11e0-9dd4-af4d032032e3, name=nodejs, type=SMARTMACHINE, version=1.2.3,
   // urn=sdc:sdc:nodejs:1.2.3, default=false, created=Thu Sep 15 10:15:29 CEST 2011]] but got
}
