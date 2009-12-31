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
   private static final Kill KILL = new Kill();

   public static Statement newStatementList(Statement... statements) {
      return new StatementList(statements);
   }

   public static Statement switchArg(int arg, Map<String, Statement> valueToActions) {
      return new SwitchArg(arg, valueToActions);
   }

   public static Statement call(String function, String... args) {
      return new Call(function, args);
   }

   public static Statement createRunScript(String instanceName, Iterable<String> exports,
            String pwd, String... execLines) {// TODO: convert so
      // that
      // createRunScript
      // can take from a
      // variable
      return new CreateRunScript(instanceName, exports, pwd, execLines);
   }

   /**
    * Stores the pid into the variable {@code FOUND_PID} if successful.
    * 
    * @param args
    *           - what to search for in the process tree.
    */
   public static Statement findPid(String args) {
      return new Call("findPid", args);
   }

   /**
    * 
    * Runs the script in a way that it can be matched later with {@link findPid}
    * 
    * @param instanceName
    *           - what to match the process on
    * @param script
    *           - what to run in the background
    * @param logDir
    *           - where to write the following logs:
    *           <ol>
    *           <li>stdout.log</li>
    *           <li>stderr.log</li>
    *           </ol>
    */
   public static Statement forget(String instanceName, String script, String logDir) {
      return new Call("forget", instanceName, script, logDir);
   }

   /**
    * Kills the pid and subprocesses related to the variable {@code FOUND_PID} if set.
    * 
    * @see #findPid
    */
   public static Statement kill() {
      return KILL;
   }
   
   /**
    * statement can have multiple newlines, note you should use {@code {lf} } to be portable
    * 
    * @see ShellToken
    */
   public static Statement interpret(String portableStatement) {
      return new InterpretableStatement(portableStatement);
   }

   /**
    * interprets and adds a newline to the statement
    */
   public static Statement exec(String portableStatement) {
      return interpret(portableStatement+"{lf}");
   }
}