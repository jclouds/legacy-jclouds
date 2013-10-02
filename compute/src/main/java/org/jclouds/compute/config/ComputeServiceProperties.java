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
package org.jclouds.compute.config;

/**
 * 
 * @author Adrian Cole
 */
public interface ComputeServiceProperties {
   public static final String RESOURCENAME_PREFIX = "jclouds.compute.resourcename-prefix";
   public static final String RESOURCENAME_DELIMITER = "jclouds.compute.resourcename-delimiter";

   public static final String TIMEOUT_NODE_RUNNING = "jclouds.compute.timeout.node-running";
   public static final String TIMEOUT_NODE_SUSPENDED = "jclouds.compute.timeout.node-suspended";
   public static final String TIMEOUT_NODE_TERMINATED = "jclouds.compute.timeout.node-terminated";

   public static final String TIMEOUT_SCRIPT_COMPLETE = "jclouds.compute.timeout.script-complete";
   public static final String TIMEOUT_PORT_OPEN = "jclouds.compute.timeout.port-open";

   public static final String INIT_STATUS_INITIAL_PERIOD = "jclouds.compute.init-status.initial-period";
   public static final String INIT_STATUS_MAX_PERIOD = "jclouds.compute.init-status.max-period";

   /**
    * Initial period between the ComputeService's node polls. Subsequent periods increase exponentially
    * (based on the backoff factor) and become constant when the maximum period is reached.
    * The unit is milliseconds.
    */
   public static final String POLL_INITIAL_PERIOD = "jclouds.compute.poll-status.initial-period";

   /**
    * Once the exponentially increasing period between ComputeService's node
    * polls has reached this maximum period, it remains at this value.
    * The unit is milliseconds.
    */
   public static final String POLL_MAX_PERIOD = "jclouds.compute.poll-status.max-period";

   /**
    * time in milliseconds to wait for an image to finish creating.
    * 
    * Override {@link Timeouts#imageAvailable default} by setting this property using
    * {@link ContextBuilder#overrides}
    */
   public static final String TIMEOUT_IMAGE_AVAILABLE = "jclouds.compute.timeout.image-available";
   
   /**
    * time in milliseconds to wait for an image to delete.
    * 
    * Override {@link Timeouts#imageDeleted default} by setting this property using
    * {@link ContextBuilder#overrides}
    */
   public static final String TIMEOUT_IMAGE_DELETED = "jclouds.compute.timeout.image-deleted";

   /**
    * overrides the default specified in the subclass of
    * {@link BaseComputeServiceContextModule#provideTemplate}
    * 
    * @see TemplateBuilderSpec
    */
   public static final String TEMPLATE = "jclouds.template";

   /**
    * overrides the image specified in the subclass of
    * {@link BaseComputeServiceContextModule#provideTemplate}
    */
   public static final String IMAGE_ID = "jclouds.image-id";

   /**
    * username and, if colon delimited, password of the default user on the image that is or can
    * become root
    * <p/>
    * ex. {@code ubuntu} ex. {@code toor:password}
    */
   public static final String IMAGE_LOGIN_USER = "jclouds.image.login-user";

   /**
    * true if gaining a sudo shell requires a password
    */
   public static final String IMAGE_AUTHENTICATE_SUDO = "jclouds.image.authenticate-sudo";

   /**
    * comma-separated nodes that we shouldn't attempt to list as they are dead in the provider for
    * some reason.
    */
   public static final String BLACKLIST_NODES = "jclouds.compute.blacklist-nodes";

   /**
    * os to map of version input string to output string
    * <p/>
    * ex.
    * 
    * <pre>
    * {"centos":{"5.4":"5.4","5.5":"5.5"},"rhel":{"5.4":"5.4","5.5":"5.5"},"ubuntu":{"karmic":"9.10","lucid":"10.04","maverick":"10.10","natty":"11.04"},"windows":{"2008":"2008","Server 2008":"2008","2008 R2":"2008 R2","Server 2008 R2":"2008 R2","2008 SP2":"2008 SP2","Server 2008 SP2":"2008 SP2"}}
    * </pre>
    */
   public static final String OS_VERSION_MAP_JSON = "jclouds.compute.os-version-map-json";

}
