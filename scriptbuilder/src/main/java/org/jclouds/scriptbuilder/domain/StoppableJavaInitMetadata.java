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
 * A java process that has a graceful shutdown mechanism.
 * 
 * @see JavaInitMetadata
 * @author Adrian Cole
 */
public class StoppableJavaInitMetadata extends JavaInitMetadata {
   private final String[] stopClasspath;
   private final String stopClass;
   private final String[] stopOpts;
   private final String[] stopArgs;

   public StoppableJavaInitMetadata(String name, String platformHome, URI endPoint,
            String startDir, String stopDir, String configDir, String dataDir, String logDir,
            String goldDir, String javaHome, String[] classpath, String mainClass, String[] opts,
            String[] args, String[] stopClasspath, String stopClass, String[] stopOpts,
            String[] stopArgs) {
      super(name, platformHome, endPoint, startDir, stopDir, configDir, dataDir, logDir, goldDir,
               javaHome, classpath, mainClass, opts, args);
      this.stopClasspath = checkNotNull(stopClasspath, "stopClasspath");
      this.stopClass = checkNotNull(stopClass, "stopClass");
      this.stopOpts = checkNotNull(stopOpts, "stopOpts");
      this.stopArgs = checkNotNull(stopArgs, "stopArgs");
   }

   public String[] getStopClasspath() {
      return stopClasspath;
   }

   public String getStopClass() {
      return stopClass;
   }

   public String[] getStopOpts() {
      return stopOpts;
   }

   public String[] getStopArgs() {
      return stopArgs;
   }

}
