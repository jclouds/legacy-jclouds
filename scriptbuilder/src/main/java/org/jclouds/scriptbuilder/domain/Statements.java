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

import java.net.URI;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
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
         public Iterable<String> functionDependencies(OsFamily family) {
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

   public static Statement appendFile(String path, String line, String delimiter) {
      return AppendFile.builder().path(path).lines(ImmutableSet.of(line)).delimiter(delimiter).build();
   }

   public static Statement appendFile(String path, Iterable<String> lines) {
      return AppendFile.builder().path(path).lines(lines).build();
   }

   public static Statement appendFile(String path, Iterable<String> lines, String delimiter) {
      return AppendFile.builder().path(path).lines(lines).delimiter(delimiter).build();
   }

   public static Statement createOrOverwriteFile(String path, Iterable<String> lines) {
      return CreateOrOverwriteFile.builder().path(path).lines(lines).build();
   }

   public static Statement createOrOverwriteFile(String path, Iterable<String> lines, String delimiter) {
      return CreateOrOverwriteFile.builder().path(path).lines(lines).delimiter(delimiter).build();
   }

   /**
    * @param exports
    *            variable names to export in UPPER_UNDERSCORE case format
    */
   public static CreateRunScript createRunScript(String instanceName, Iterable<String> exports, String pwd,
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
    * Runs the script in a way that it can be matched later with
    * {@link #findPid}
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
    * Kills the pid and subprocesses related to the variable {@code FOUND_PID}
    * if set.
    * 
    * @see #findPid
    */
   public static Statement kill() {
      return KILL;
   }

   /**
    * statement can have multiple newlines, note you should use {@code lf} to be
    * portable
    * 
    * @see ShellToken
    */
   public static Statement interpret(String... portableStatements) {
      return new InterpretableStatement(portableStatements);
   }

   /**
    * sends statement only appending a newline
    */
   public static Statement literal(String literalStatement) {
      return new LiteralStatement(literalStatement);
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
    * like {@link #extractTargzIntoDirectory(URI, String)} except that it
    * flattens the first directory in the archive
    * 
    * For example, {@code apache-maven-3.0.4-bin.tar.gz} normally extracts
    * directories like {@code ./apache-maven-3.0.4/bin}. This command eliminates
    * the intermediate directory, in the example {@code ./apache-maven-3.0.4/}
    * 
    * @param tgz remote ref to download
    * @param dest path where the files in the intermediate directory will end
    */
   public static Statement extractTargzAndFlattenIntoDirectory(URI tgz, String dest) {
      return new StatementList(ImmutableSet.<Statement> builder()
            .add(exec("mkdir /tmp/$$"))
            .add(extractTargzIntoDirectory(tgz, "/tmp/$$"))
            .add(exec("mkdir -p " + dest))
            .add(exec("mv /tmp/$$/*/* " + dest))
            .add(exec("rm -rf /tmp/$$")).build());
   }
   
   public static Statement extractTargzIntoDirectory(URI targz, String directory) {
      return extractTargzIntoDirectory("GET", targz, ImmutableMultimap.<String, String> of(), directory);
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

   public static Statement saveHttpResponseTo(URI source, String dir, String file) {
      return new SaveHttpResponseTo(dir, file, "GET", source, ImmutableMultimap.<String, String> of());
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
