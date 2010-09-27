/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.scriptbuilder.domain;

import java.net.URI;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

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

   public static Statement rm(final String path) {
      return new Statement() {

         @Override
         public Iterable<String> functionDependecies(OsFamily family) {
            return ImmutableList.of();
         }

         @Override
         public String render(OsFamily family) {
            if (family == OsFamily.WINDOWS)
               return exec(String.format("{rm} %s 2{closeFd}", path)).render(family);
            else
               return exec(String.format("{rm} %s", path)).render(family);
         }

      };
   }

   public static Statement call(String function, String... args) {
      return new Call(function, args);
   }

   public static Statement appendFile(String path, Iterable<String> lines) {
      return new AppendFile(path, lines);
   }

   public static Statement createRunScript(String instanceName, Iterable<String> exports, String pwd,
            Iterable<Statement> statements) {// TODO: convert so
      // that
      // createRunScript
      // can take from a
      // variable
      return new CreateRunScript(instanceName, exports, pwd, statements);
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
    * Runs the script in a way that it can be matched later with {@link #findPid}
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
    * statement can have multiple newlines, note you should use {@code lf} to be portable
    * 
    * @see ShellToken
    */
   public static Statement interpret(String... portableStatements) {
      return new InterpretableStatement(portableStatements);
   }

   /**
    * interprets and adds a newline to the statement
    */
   public static Statement exec(String portableStatement) {
      return interpret(portableStatement + "{lf}");
   }

   /**
    * untar, ungzip the data received from the request parameters.
    * 
    * @param method
    *           http method: ex GET
    * @param endpoint
    *           uri corresponding to the request
    * @param headers
    *           request headers to send
    * @param directory
    */
   public static Statement extractTargzIntoDirectory(String method, URI endpoint, Multimap<String, String> headers,
            String directory) {
      return new PipeHttpResponseToTarxpzfIntoDirectory(method, endpoint, headers, directory);
   }

   /**
    * unzip the data received from the request parameters.
    * 
    * @param method
    *           http method: ex GET
    * @param endpoint
    *           uri corresponding to the request
    * @param headers
    *           request headers to send
    * @param directory
    */
   public static Statement extractZipIntoDirectory(String method, URI endpoint, Multimap<String, String> headers,
            String directory) {
      return new UnzipHttpResponseIntoDirectory(method, endpoint, headers, directory);
   }

   /**
    * exec the data received from the request parameters.
    * 
    * @param method
    *           http method: ex GET
    * @param endpoint
    *           uri corresponding to the request
    * @param headers
    *           request headers to send
    */
   public static Statement pipeHttpResponseToBash(String method, URI endpoint, Multimap<String, String> headers) {
      return new PipeHttpResponseToBash(method, endpoint, headers);
   }
}