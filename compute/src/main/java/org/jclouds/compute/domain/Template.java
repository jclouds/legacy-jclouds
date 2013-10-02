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
package org.jclouds.compute.domain;

import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;

/**
 * Configured operating system used to start nodes.
 * 
 * @author Adrian Cole
 */
public interface Template extends Cloneable {
   /**
    * Image that suits the requirements.
    * 
    */
   Image getImage();

   /**
    * Size that suits the requirements.
    */
   Hardware getHardware();

   /**
    * Location of the nodes.
    */
   Location getLocation();

   /**
    * options for launching this template, like run scripts or inbound ports
    */
   TemplateOptions getOptions();
   
   /**
    * clone this template
    * 
    * @see Object#clone
    */
   Template clone();
}
