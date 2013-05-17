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
package org.jclouds.scriptbuilder.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Statement used in a shell script
 * 
 * @author Adrian Cole
 */
public class Kill implements Statement {

   public static final Map<OsFamily, String> OS_TO_KILL = ImmutableMap.of(OsFamily.UNIX,
            "[ -n \"$FOUND_PID\" ]  && {\n   echo stopping $FOUND_PID\n   kill -9 $FOUND_PID\n}\n",
            OsFamily.WINDOWS,
            "if defined FOUND_PID (\r\n   TASKKILL /F /T /PID %FOUND_PID% >NUL\r\n)\r\n");

   public Kill() {
   }

   public String render(OsFamily family) {
      return OS_TO_KILL.get(checkNotNull(family, "family"));
   }

   @Override
   public Iterable<String> functionDependencies(OsFamily family) {
      return ImmutableList.of();
   }
}
