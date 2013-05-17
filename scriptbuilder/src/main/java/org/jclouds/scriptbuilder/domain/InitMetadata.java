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
 * Defines the environment of a process that can be started in the background on an operating
 * system.
 * 
 * @author Adrian Cole
 */
public class InitMetadata {

   private final String name;
   private final String platformHome;
   private final URI endPoint;
   private final String startDir;
   private final String stopDir;
   private final String configDir;
   private final String dataDir;
   private final String logDir;
   private final String goldDir;

   public InitMetadata(String name, String platformHome, URI endPoint, String startDir,
            String stopDir, String configDir, String dataDir, String logDir, String goldDir) {
      this.name = checkNotNull(name, "name");
      this.platformHome = checkNotNull(platformHome, "platformHome");
      this.endPoint = endPoint;
      this.startDir = checkNotNull(startDir, "startDir");
      this.stopDir = checkNotNull(stopDir, "stopDir");
      this.configDir = checkNotNull(configDir, "configDir");
      this.dataDir = checkNotNull(dataDir, "dataDir");
      this.logDir = checkNotNull(logDir, "LOG_DIR");
      this.goldDir = checkNotNull(goldDir, "goldDir");
   }

   /**
    * working directory when starting the server.
    */
   public String getStartDir() {
      return startDir;
   }

   /**
    * working directory when stopping the server.
    */
   public String getStopDir() {
      return stopDir;
   }

   /**
    * Where the platform that this process is an instance of is located. This is analogous to the
    * CATALINA_HOME on the tomcat platform.
    */
   public String getPlatformHome() {
      return platformHome;
   }

   /**
    * what uniquely identifies your process in a listing. Note that this will become a part of the
    * process args.
    */
   public String getName() {
      return name;
   }

   /**
    * holds configuration files of the process. These are generated or copied from data in the
    * {@link #getGoldDir gold copy directory}.
    */
   public String getConfigDir() {
      return configDir;
   }

   /**
    * holds files that are generated at runtime, but are not temporary. Ex. customer data, state,
    * etc. These files survive recreation of the instance.
    */
   public String getDataDir() {
      return dataDir;
   }

   /**
    * where all logs are written. The following files are created here:
    * <ul>
    * <li>stdout.log - where stdout is piped to upon start.</li>
    * <li>stderr.log - where stderr is piped to upon start.</li>
    * <li>pid.log - holds the process id, if the process is running.</li>
    * </ul>
    */
   public String getLogDir() {
      return logDir;
   }

   /**
    * on-disk, read-only location of the artifacts needed to recreate this process.
    */
   public String getGoldDir() {
      return goldDir;
   }

   /**
    * the named ip and port that this process will bind server sockets to, as well the protocol used
    * to test it.
    */
   public URI getEndPoint() {
      return endPoint;
   }

}
