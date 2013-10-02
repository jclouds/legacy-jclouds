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
package org.jclouds.cloudservers.domain;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

/**
 * Tests behavior of {@code CreateImageBinder}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ServerTest {
   public void testStatusDoesntAffectEquals() {
      Server server1 = Server.builder().id(1).name("hello").status(ServerStatus.ACTIVE).build();
      Server server2 = Server.builder().id(1).name("hello").status(ServerStatus.BUILD).build();
      assertEquals(server1, server2);
   }

   public void testProgressDoesntAffectEquals() {
      Server server1 = Server.builder().id(1).name("hello").progress(1).build();
      Server server2 = Server.builder().id(1).name("hello").progress(2).build();
      assertEquals(server1, server2);
   }

}
