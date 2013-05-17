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
package org.jclouds.logging;

import java.util.logging.Level;

import org.jclouds.logging.BufferLogger.Record;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class BufferLoggerTest {

   public void testLogCapturesRightMessages() {
      BufferLogger b = new BufferLogger("foo");
      b.setLevel(Level.INFO);
      b.info("hi 1");
      b.error(new Throwable("check"), "hi 2");
      b.debug("hi 3 nope");
      
      Record r;
      r = b.assertLogContains("hi 1");
      Assert.assertEquals(Level.INFO, r.getLevel());
      Assert.assertNull(r.getTrace());
      
      r = b.assertLogContains("hi 2");
      Assert.assertEquals(Level.SEVERE, r.getLevel());
      Assert.assertEquals(r.getTrace().getMessage(), "check");
      
      b.assertLogDoesntContain("hi 3");
   }
   
}
