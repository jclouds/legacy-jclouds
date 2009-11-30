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

import java.util.Map;

/**
 * Statements used in shell scripts.
 * 
 * @author Adrian Cole
 */
public class Statements {

   public static Statement switchOn(String variable, Map<String, Statement> valueToActions) {
      return new Switch(variable, valueToActions);
   }

   public static Statement call(String function, String... args) {
      return new Call(function, args);
   }

   /**
    * Stores the pid into the variable foundPid if successful.
    * 
    * @param args
    *           - what to search for in the process tree.
    */
   public static Statement findPid(String args) {
      return new Call("findPid", args);
   }

   public static Statement interpret(String portableStatement) {
      return new InterpretableStatement(portableStatement);
   }

}