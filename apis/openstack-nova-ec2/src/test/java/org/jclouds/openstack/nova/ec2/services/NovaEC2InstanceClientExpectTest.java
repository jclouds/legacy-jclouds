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
package org.jclouds.openstack.nova.ec2.services;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;

import java.util.Set;

import org.jclouds.ec2.domain.BlockDevice;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.services.InstanceClient;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.ec2.internal.BaseNovaEC2RestClientExpectTest;

import com.google.common.collect.Iterables;

/**
 * @author Adam Lowe
 */
public class NovaEC2InstanceClientExpectTest extends BaseNovaEC2RestClientExpectTest {

   public void testDescribeInstancesWithDashesInPlaceOfNullDates() {
      InstanceClient client = requestsSendResponses(
            describeAvailabilityZonesRequest,
            describeAvailabilityZonesResponse,
            HttpRequest.builder().method("POST")
                  .endpoint("http://localhost:8773/services/Cloud/")
                  .addHeader("Host", "localhost:8773")
                  .payload(payloadFromStringWithContentType("Action=DescribeInstances&Signature=kkCE1HzyntmkICEidOizw50B9yjLdNZvAWUXVse1c8o%3D&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2012-04-16T15%3A54%3A08.897Z&Version=2009-04-04&AWSAccessKeyId=identity", "application/x-www-form-urlencoded")).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/nova_ec2_describe_instances.xml")).build()
      ).getInstanceServices();

      Set<? extends Reservation<? extends RunningInstance>> response = client.describeInstancesInRegion("nova");
      
      assertEquals(response.size(), 3);

      Reservation<? extends RunningInstance> target = Iterables.get(response, 2);
      RunningInstance runningInstance = Iterables.getOnlyElement(target);
      BlockDevice bd = Iterables.getOnlyElement(runningInstance.getEbsBlockDevices().values());
      
      // this is a '-' in the nova_ec2_describe_instances.xml
      assertNull(bd.getAttachTime());

      // double-check the other fields
      assertFalse(bd.isDeleteOnTermination());
      assertEquals(bd.getVolumeId(), "1");
   }

}
