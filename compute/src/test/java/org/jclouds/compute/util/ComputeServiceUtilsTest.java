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
package org.jclouds.compute.util;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.jclouds.compute.util.ComputeServiceUtils.parseVersionOrReturnEmptyString;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.net.URI;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.easymock.EasyMock;
import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.predicates.SocketOpenPredicates;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.SocketOpen;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.HostAndPort;
import com.google.inject.Guice;

/**
 * Test the compute utils.
 * 
 * @author Adrian Cole
 * 
 */
@Test(groups = "unit")
public class ComputeServiceUtilsTest {
   Map<OsFamily, Map<String, String>> map = new BaseComputeServiceContextModule() {
   }.provideOsVersionMap(new ComputeServiceConstants.ReferenceData(), Guice.createInjector(new GsonModule())
         .getInstance(Json.class));

   @Test
   public void testParseVersionOrReturnEmptyStringUbuntu1004() {
      assertEquals(parseVersionOrReturnEmptyString(OsFamily.UBUNTU, "Ubuntu 10.04", map), "10.04");
   }

   @Test
   public void testParseVersionOrReturnEmptyStringUbuntu1104() {
      assertEquals(parseVersionOrReturnEmptyString(OsFamily.UBUNTU, "ubuntu 11.04 server (i386)", map), "11.04");
   }

   @Test
   public void testExecHttpResponse() {
      HttpRequest request = new HttpRequest("GET", URI.create("https://adriancolehappy.s3.amazonaws.com/java/install"),
            ImmutableMultimap.of("Host", "adriancolehappy.s3.amazonaws.com", "Date", "Sun, 12 Sep 2010 08:25:19 GMT",
                  "Authorization", "AWS 0ASHDJAS82:JASHFDA="));

      assertEquals(
            ComputeServiceUtils.execHttpResponse(request).render(org.jclouds.scriptbuilder.domain.OsFamily.UNIX),
            "curl -q -s -S -L --connect-timeout 10 --max-time 600 --retry 20 -X GET -H \"Host: adriancolehappy.s3.amazonaws.com\" -H \"Date: Sun, 12 Sep 2010 08:25:19 GMT\" -H \"Authorization: AWS 0ASHDJAS82:JASHFDA=\" https://adriancolehappy.s3.amazonaws.com/java/install |(bash)\n");

   }

   @Test
   public void testTarxzpHttpResponse() {
      HttpRequest request = new HttpRequest("GET", URI.create("https://adriancolehappy.s3.amazonaws.com/java/install"),
            ImmutableMultimap.of("Host", "adriancolehappy.s3.amazonaws.com", "Date", "Sun, 12 Sep 2010 08:25:19 GMT",
                  "Authorization", "AWS 0ASHDJAS82:JASHFDA="));

      assertEquals(
            ComputeServiceUtils.extractTargzIntoDirectory(request, "/stage/").render(
                  org.jclouds.scriptbuilder.domain.OsFamily.UNIX),
            "curl -q -s -S -L --connect-timeout 10 --max-time 600 --retry 20 -X GET -H \"Host: adriancolehappy.s3.amazonaws.com\" -H \"Date: Sun, 12 Sep 2010 08:25:19 GMT\" -H \"Authorization: AWS 0ASHDJAS82:JASHFDA=\" https://adriancolehappy.s3.amazonaws.com/java/install |(mkdir -p /stage/ &&cd /stage/ &&tar -xpzf -)\n");
   }
   
   @Test
   public void testFindReachableSocketOnNodeTimesOut() throws Exception {
      final long timeoutSecs = 2;
      final long timeoutMs = timeoutSecs * 1000;
      final long SLOW_GRACE = 500;
      final long EARLY_GRACE = 10;
      
      SocketOpen socketTester = SocketOpenPredicates.alwaysFail;
      NodeMetadata node = createMock(NodeMetadata.class);
      expect(node.getPublicAddresses()).andReturn(ImmutableSet.of("1.2.3.4")).atLeastOnce();
      expect(node.getPrivateAddresses()).andReturn(ImmutableSet.of("1.2.3.5")).atLeastOnce();
      
      replay(node);
      
      Stopwatch stopwatch = new Stopwatch();
      stopwatch.start();
      try {
         ComputeServiceUtils.findReachableSocketOnNode(socketTester, node, 1234, timeoutMs, TimeUnit.MILLISECONDS, Logger.CONSOLE);
         fail();
      } catch (NoSuchElementException success) {
         // expected
      }
      long timetaken = stopwatch.elapsedMillis();
      
      assertTrue(timetaken >= timeoutMs-EARLY_GRACE && timetaken <= timeoutMs+SLOW_GRACE, "timetaken="+timetaken);
      verify(node);
   }
   
   @Test
   public void testFindReachableSocketOnNodeReturnsAvailable() throws Exception {
      SocketOpen socketTester = createMock(SocketOpen.class);
      expect(socketTester.apply(HostAndPort.fromParts("1.2.3.4", 22))).andReturn(false);
      expect(socketTester.apply(HostAndPort.fromParts("1.2.3.5", 22))).andReturn(true);
      
      NodeMetadata node = createMock(NodeMetadata.class);
      expect(node.getPublicAddresses()).andReturn(ImmutableSet.of("1.2.3.4")).atLeastOnce();
      expect(node.getPrivateAddresses()).andReturn(ImmutableSet.of("1.2.3.5")).atLeastOnce();
      
      replay(socketTester);
      replay(node);
      
      HostAndPort result = ComputeServiceUtils.findReachableSocketOnNode(socketTester, node, 22, 2000, TimeUnit.MILLISECONDS, Logger.CONSOLE);
      assertEquals(result, HostAndPort.fromParts("1.2.3.5", 22));

      verify(socketTester);
      verify(node);
   }
   
   @Test
   public void testFindReachableSocketOnNodeAbortsWhenNodeNotRunning() throws Exception {
      final long SLOW_GRACE = 500;
      
      SocketOpen socketTester = SocketOpenPredicates.alwaysFail;
      
      NodeMetadata node = createMock(NodeMetadata.class);
      expect(node.getPublicAddresses()).andReturn(ImmutableSet.of("1.2.3.4")).atLeastOnce();
      expect(node.getPrivateAddresses()).andReturn(ImmutableSet.of("1.2.3.5")).atLeastOnce();
      expect(node.getId()).andReturn("myid").atLeastOnce();
      
      Predicate<AtomicReference<NodeMetadata>> nodeRunning = createMock(Predicate.class);
      expect(nodeRunning.apply(EasyMock.<AtomicReference<NodeMetadata>>anyObject())).andReturn(false);
      
      replay(node);
      replay(nodeRunning);
      
      Stopwatch stopwatch = new Stopwatch();
      stopwatch.start();
      try {
         ComputeServiceUtils.findReachableSocketOnNode(socketTester, nodeRunning, 
                  node, 22, 2000000, TimeUnit.MILLISECONDS, Logger.CONSOLE);
         fail();
      } catch (RuntimeException e) {
         if (!e.getMessage().contains("no longer running")) {
            throw e;
         }
      }
      long timetaken = stopwatch.elapsedMillis();
      
      assertTrue(timetaken <= SLOW_GRACE, "timetaken="+timetaken);

      verify(node);
      verify(nodeRunning);
   }
}
