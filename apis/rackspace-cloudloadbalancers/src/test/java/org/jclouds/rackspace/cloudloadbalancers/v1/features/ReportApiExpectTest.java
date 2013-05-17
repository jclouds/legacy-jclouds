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
package org.jclouds.rackspace.cloudloadbalancers.v1.features;
import static javax.ws.rs.core.Response.Status.OK;
import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Calendar;
import java.util.Date;

import org.jclouds.http.HttpResponse;
import org.jclouds.rackspace.cloudloadbalancers.v1.CloudLoadBalancersApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.HistoricalUsage;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.LoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.LoadBalancerStats;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.LoadBalancerUsage;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.Protocol;
import org.jclouds.rackspace.cloudloadbalancers.v1.functions.DateParser;
import org.jclouds.rackspace.cloudloadbalancers.v1.internal.BaseCloudLoadBalancerApiExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

/**
 * @author Everett Toews
 */
@Test(groups = "unit")
public class ReportApiExpectTest extends BaseCloudLoadBalancerApiExpectTest<CloudLoadBalancersApi> {
   public void testListBillableLoadBalancers() {
      Calendar calendar = Calendar.getInstance();
      calendar.add(Calendar.DATE, -7);      
      Date aWeekAgo = calendar.getTime();
      Date today = new Date();
      
      String query = new StringBuilder()
         .append("?startTime=")
         .append(new DateParser().apply(aWeekAgo))
         .append("&endTime=")
         .append(new DateParser().apply(today))
         .toString();
            
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/billable" + query);
      ReportApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/report-billable-list.json")).build()
      ).getReportApiForZone("DFW");
            
      FluentIterable<LoadBalancer> loadBalancers = api.listBillableLoadBalancers(aWeekAgo, today).concat();
      
      assertEquals(Iterables.size(loadBalancers), 2);
   }

   public void testGetHistoricalUsage() {
      Calendar calendar = Calendar.getInstance();
      calendar.add(Calendar.DATE, -7);      
      Date aWeekAgo = calendar.getTime();
      Date today = new Date();
      
      String query = new StringBuilder()
         .append("?startTime=")
         .append(new DateParser().apply(aWeekAgo))
         .append("&endTime=")
         .append(new DateParser().apply(today))
         .toString();
            
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/usage" + query);
      ReportApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/report-historical-get.json")).build()
      ).getReportApiForZone("DFW");

      HistoricalUsage historicalUsage = api.getHistoricalUsage(aWeekAgo, today);
      
      assertEquals(historicalUsage.getAccountId(), 717071);
      assertEquals(Iterables.get(historicalUsage.getAccountUsage(), 0).getNumLoadBalancers(), 2);
      assertEquals(Iterables.size(historicalUsage.getLoadBalancerInfo()), 2);
   }

   public void testListLoadBalancerUsage() {
      Calendar calendar = Calendar.getInstance();
      calendar.add(Calendar.DATE, -7);      
      Date aWeekAgo = calendar.getTime();
      Date today = new Date();
      
      String query = new StringBuilder()
         .append("?startTime=")
         .append(new DateParser().apply(aWeekAgo))
         .append("&endTime=")
         .append(new DateParser().apply(today))
         .toString();
            
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/usage" + query);
      ReportApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/report-loadbalancerusage-list.json")).build()
      ).getReportApiForZone("DFW");

      FluentIterable<LoadBalancerUsage> loadBalancerUsages = api.listLoadBalancerUsage(2000, aWeekAgo, today).concat();
      
      assertEquals(Iterables.size(loadBalancerUsages), 25);
   }

   public void testListCurrentLoadBalancerUsage() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/usage/current");
      ReportApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/report-loadbalancerusage-list.json")).build()
      ).getReportApiForZone("DFW");
            
      FluentIterable<LoadBalancerUsage> loadBalancerUsages = api.listCurrentLoadBalancerUsage(2000).concat();
      
      assertEquals(Iterables.size(loadBalancerUsages), 25);
   }
   
   public void testGetLoadBalancerStats() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/stats");
      ReportApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/report-loadbalancerstats-get.json")).build()
      ).getReportApiForZone("DFW");
            
      LoadBalancerStats loadBalancerStats = api.getLoadBalancerStats(2000);
      
      assertEquals(loadBalancerStats.getConnectTimeOut(), 2);
      assertEquals(loadBalancerStats.getConnectError(), 0);
      assertEquals(loadBalancerStats.getConnectFailure(), 0);
      assertEquals(loadBalancerStats.getDataTimedOut(), 10);
      assertEquals(loadBalancerStats.getKeepAliveTimedOut(), 0);
      assertEquals(loadBalancerStats.getMaxConn(), 22);
   }   

   public void testListProtocols() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/protocols");
      ReportApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/report-protocols-list.json")).build()
      ).getReportApiForZone("DFW");
            
      Iterable<Protocol> protocols = api.listProtocols();
      
      assertEquals(Iterables.size(protocols), 20);
   }

   public void testListAlgorithms() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/algorithms");
      ReportApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/report-algorithms-list.json")).build()
      ).getReportApiForZone("DFW");
            
      Iterable<String> algorithms = api.listAlgorithms();
      
      assertEquals(Iterables.size(algorithms), 5);
   }
}
