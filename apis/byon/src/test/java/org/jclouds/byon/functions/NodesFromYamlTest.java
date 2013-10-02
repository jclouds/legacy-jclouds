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
package org.jclouds.byon.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.byon.Node;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Adrian Cole
 */
public class NodesFromYamlTest {
   public static final String key = new StringBuilder().append("-----BEGIN RSA PRIVATE KEY-----\n")
         .append("MIIEowIBAAKCAQEAuzaE6azgUxwESX1rCGdJ5xpdrc1XC311bOGZBCE8NA+CpFh2\n")
         .append("u01Vfv68NC4u6LFgdXSY1vQt6hiA5TNqQk0TyVfFAunbXgTekF6XqDPQUf1nq9aZ\n")
         .append("lMvo4vlaLDKBkhG5HJE/pIa0iB+RMZLS0GhxsIWerEDmYdHKM25o\n").append("-----END RSA PRIVATE KEY-----\n")
         .toString();

   public static final Node TEST1 = new Node("cluster-1", "cluster-1", "accounting analytics cluster",
         "cluster-1.mydomain.com", null, "x86", "rhel", "redhat", "5.3", false, 22, "hadoop",
         ImmutableList.of("vanilla"), ImmutableMap.of("Name", "foo"), "myUser", key, null, "happy bear");

   public static final Node TEST2 = new Node("cluster-1", "cluster-1", "accounting analytics cluster",
         "cluster-1.mydomain.com", "virginia", "x86", "rhel", "redhat", "5.3", false, 22, "hadoop",
         ImmutableList.of("vanilla"), ImmutableMap.of("Name", "foo"), "myUser", key, null, "happy bear");

   public static final Node TEST3 = new Node("cluster-2", "cluster-2", "accounting analytics cluster",
         "cluster-2.mydomain.com", "maryland", "x86", "rhel", "redhat", "5.3", false, 2022, "hadoop",
         ImmutableList.of("vanilla"), ImmutableMap.of("Name", "foo"), "myUser", key, null, "happy bear");

   @Test
   public void testNodesParse() throws Exception {

      InputStream is = getClass().getResourceAsStream("/test1.yaml");
      NodesFromYamlStream parser = new NodesFromYamlStream();

      assertEquals(parser.apply(is).asMap(), ImmutableMap.of(TEST1.getId(), TEST1));
   }

   @Test
   public void testNodesParseLocation() throws Exception {

      InputStream is = getClass().getResourceAsStream("/test_location.yaml");
      NodesFromYamlStream parser = new NodesFromYamlStream();

      assertEquals(parser.apply(is).asMap(), ImmutableMap.of(TEST2.getId(), TEST2, TEST3.getId(), TEST3));
   }

   @Test
   public void testNodesParseWhenCredentialInUrl() throws Exception {

      InputStream is = getClass().getResourceAsStream("/test_with_url.yaml");
      NodesFromYamlStream parser = new NodesFromYamlStream();

      assertEquals(parser.apply(is).asMap(), ImmutableMap.of(TEST1.getId(), TEST1));
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testMustParseSomething() throws Exception {
      new NodesFromYamlStream().apply(Strings2.toInputStream(""));
   }

}
