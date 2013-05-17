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

import java.net.URI;

/**
 * Defines the environment of a java process that can be started in the background on an operating
 * system.
 * 
 * @see InitMetadata
 * @author Adrian Cole
 */
public class JavaInitMetadata extends InitMetadata {
   private final String javaHome;
   private final String[] classpath;
   private final String mainClass;
   private final String[] opts;
   private final String[] args;

   public JavaInitMetadata(String name, String platformHome, URI endPoint, String startDir,
            String stopDir, String configDir, String dataDir, String logDir, String goldDir,
            String javaHome, String[] classpath, String mainClass, String[] opts, String[] args) {
      super(name, platformHome, endPoint, startDir, stopDir, configDir, dataDir, logDir, goldDir);
      this.javaHome = checkNotNull(javaHome, "JAVA_HOME");
      this.classpath = checkNotNull(classpath, "classpath");
      this.mainClass = checkNotNull(mainClass, "mainClass");
      this.opts = checkNotNull(opts, "opts");
      this.args = checkNotNull(args, "args");
   }

   public String getJavaHome() {
      return javaHome;
   }

   public String[] getClasspath() {
      return classpath;
   }

   public String getMainClass() {
      return mainClass;
   }

   public String[] getOpts() {
      return opts;
   }

   public String[] getArgs() {
      return args;
   }

}
