/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.scriptbuilder.domain;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;
import static org.jclouds.scriptbuilder.domain.Statements.*;
import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "scriptbuilder.SwitchTest")
public class SwitchTest {

   public void testSwitchUNIX() {
      assertEquals(new Switch("i", ImmutableMap.of("0", interpret("echo hello zero{lf}"), "1",
               interpret("echo hello one{lf}"))).render(OsFamily.UNIX),
               "case $I in\n0)\n   echo hello zero\n   ;;\n1)\n   echo hello one\n   ;;\nesac\n");
   }

   public void testSwitchWindows() {
      assertEquals(
               new Switch("i", ImmutableMap.of("0", interpret("echo hello zero{lf}"), "1",
                        interpret("echo hello one{lf}"))).render(OsFamily.WINDOWS),
               "goto CASE%I\r\n:CASE_0\r\n   echo hello zero\r\n   GOTO END_SWITCH\r\n:CASE_1\r\n   echo hello one\r\n   GOTO END_SWITCH\r\n:END_SWITCH\r\n");
   }
}
