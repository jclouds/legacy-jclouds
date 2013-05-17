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
package org.jclouds.ec2.features;
import static org.testng.Assert.assertEquals;

import java.util.Properties;

import org.jclouds.Constants;
import org.jclouds.ec2.EC2Api;
import org.jclouds.ec2.internal.BaseEC2ApiExpectTest;
import org.jclouds.ec2.parse.DescribeSubnetsResponseTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.annotations.SinceApiVersion;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class SubnetApiExpectTest extends BaseEC2ApiExpectTest<EC2Api> {

   /**
    * @see SubnetApi
    * @see SinceApiVersion
    */
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      props.put(Constants.PROPERTY_API_VERSION, "2011-01-01");
      return props;
   }
   
   HttpRequest list = HttpRequest.builder().method("POST")
                                 .endpoint("https://ec2.us-east-1.amazonaws.com/")
                                 .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                                 .addFormParam("Action", "DescribeSubnets")
                                 .addFormParam("Signature", "Uuafp9lnYQmMUcf/JE1epPTQVCSMPqfns%2BwlZssUsi4%3D")
                                 .addFormParam("SignatureMethod", "HmacSHA256")
                                 .addFormParam("SignatureVersion", "2")
                                 .addFormParam("Timestamp", "2012-04-16T15%3A54%3A08.897Z")
                                 .addFormParam("Version", "2011-01-01")
                                 .addFormParam("AWSAccessKeyId", "identity")
                                 .build();
   
   public void testListWhenResponseIs2xx() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/describe_subnets.xml", "text/xml")).build();

      EC2Api apiWhenExist = requestSendsResponse(
            list, listResponse);

      assertEquals(apiWhenExist.getSubnetApi().get().list().toString(), new DescribeSubnetsResponseTest().expected().toString());
   }

   public void testListWhenResponseIs404() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(404).build();

      EC2Api apiWhenDontExist = requestSendsResponse(
            list, listResponse);

      assertEquals(apiWhenDontExist.getSubnetApi().get().list().toSet(), ImmutableSet.of());
   }
   
   HttpRequest filter = HttpRequest.builder().method("POST")
                                   .endpoint("https://ec2.us-east-1.amazonaws.com/")
                                   .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                                   .addFormParam("Action", "DescribeSubnets")
                                   .addFormParam("Filter.1.Name", "subnet-id")
                                   .addFormParam("Filter.1.Value.1", "subnet-9d4a7b6c")
                                   .addFormParam("Signature", "%2Bp34YACfLk9km1H3eALnDmrkst9FhJttojVSf7VztLk%3D")
                                   .addFormParam("SignatureMethod", "HmacSHA256")
                                   .addFormParam("SignatureVersion", "2")
                                   .addFormParam("Timestamp", "2012-04-16T15%3A54%3A08.897Z")
                                   .addFormParam("Version", "2011-01-01")
                                   .addFormParam("AWSAccessKeyId", "identity").build();

   public void testFilterWhenResponseIs2xx() throws Exception {
      
      HttpResponse filterResponse = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/describe_subnets.xml", "text/xml")).build();

      EC2Api apiWhenExist = requestSendsResponse(filter, filterResponse);

      assertEquals(apiWhenExist.getSubnetApi().get().filter(ImmutableMultimap.<String, String> builder()
                                                         .put("subnet-id", "subnet-9d4a7b6c")
                                                         .build()).toString(),
               new DescribeSubnetsResponseTest().expected().toString());
   }
   
   public void testFilterWhenResponseIs404() throws Exception {

      HttpResponse filterResponse = HttpResponse.builder().statusCode(404).build();

      EC2Api apiWhenDontExist = requestSendsResponse(filter, filterResponse);

      assertEquals(apiWhenDontExist.getSubnetApi().get().filter(ImmutableMultimap.<String, String> builder()
                                                                .put("subnet-id", "subnet-9d4a7b6c")
                                                                .build()).toSet(), ImmutableSet.of());
   }

}
