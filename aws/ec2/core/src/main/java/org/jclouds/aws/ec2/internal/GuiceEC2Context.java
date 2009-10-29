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
package org.jclouds.aws.ec2.internal;

import java.io.IOException;

import javax.annotation.Resource;

import org.jclouds.aws.ec2.EC2Connection;
import org.jclouds.aws.ec2.EC2Context;
import org.jclouds.lifecycle.Closer;
import org.jclouds.logging.Logger;

import javax.inject.Inject;
import com.google.inject.Injector;

/**
 * Uses a Guice Injector to configure the objects served by EC2Context methods.
 * 
 * @author Adrian Cole
 * @see Injector
 */
public class GuiceEC2Context implements EC2Context {
	
   @Resource
   private Logger logger = Logger.NULL;
   private final Injector injector;
   private final Closer closer;

   @Inject
   private GuiceEC2Context(Injector injector, Closer closer) {
      this.injector = injector;
      this.closer = closer;
   }

   /**
    * {@inheritDoc}
    */
   public EC2Connection getConnection() {
      return injector.getInstance(EC2Connection.class);
   }

   /**
    * {@inheritDoc}
    * 
    * @see Closer
    */
   public void close() {
      try {
         closer.close();
      } catch (IOException e) {
         logger.error(e, "error closing content");
      }
   }

}
