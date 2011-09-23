/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.virtualbox.experiment;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.jclouds.byon.Node;
import org.jclouds.byon.config.CacheNodeStoreModule;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.encryption.bouncycastle.config.BouncyCastleCryptoModule;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.sshj.config.SshjSshClientModule;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

public class TestUtils {
   public static ComputeServiceContext computeServiceForLocalhost() throws IOException {

      Node host = Node.builder().id("host")
              .name("host installing virtualbox")
              .hostname("localhost")
              .osFamily(OsFamily.LINUX.toString())
              .osDescription(System.getProperty("os.name"))
              .osVersion(System.getProperty("os.version"))
              .group("ssh")
              .username(System.getProperty("user.name"))
              .credentialUrl(privateKeyFile())
              .build();
      Node guest = Node.builder().id("guest")
              .name("new guest")
              .hostname("localhost")
              .loginPort(2222)
              .osFamily(OsFamily.UBUNTU.toString())
              .osDescription("ubuntu/11.04")
              .osVersion(System.getProperty("11.04"))
              .group("guest")
              .username("toor")
              .sudoPassword("password")
              .credential("password")
              .build();

      final Map<String, Node> nodeMap = ImmutableMap.<String, Node>builder().put("host", host).put("guest", guest).build();
      return new ComputeServiceContextFactory().createContext("byon", "foo", "bar", ImmutableSet.<Module>of(
              new SshjSshClientModule(), new SLF4JLoggingModule(), new BouncyCastleCryptoModule(), new CacheNodeStoreModule(nodeMap)));
   }

   public static ComputeServiceContext computeServiceForVBox() throws IOException {

	      Node host = Node.builder().id("host")
	              .name("host installing virtualbox")
	              .hostname("localhost")
	              .osFamily(OsFamily.LINUX.toString())
	              .osDescription(System.getProperty("os.name"))
	              .osVersion(System.getProperty("os.version"))
	              .group("ssh")
	              .username(System.getProperty("user.name"))
	              .credentialUrl(privateKeyFile())
	              .build();
	      Node guest = Node.builder().id("guest")
	              .name("new guest")
	              .hostname("localhost")
	              .loginPort(2222)
	              .osFamily(OsFamily.UBUNTU.toString())
	              .osDescription("ubuntu/11.04")
	              .osVersion(System.getProperty("11.04"))
	              .group("guest")
	              .username("toor")
	              .sudoPassword("password")
	              .credential("password")
	              .build();

	      
	      final CacheLoader<String, Node> cacheLoader = new CacheLoader<String, Node>() {
			/*
			 * (non-Javadoc)
			 * @see com.google.common.cache.CacheLoader#load(java.lang.Object)
			 * this will take the machine from vbox and convert to a node by id
			 * so you would use vbox/arp commands whatever and then return Node.builder()..... 
			 * inside the load method
			 */
			@Override
			public Node load(String instanceName) throws Exception {
				// TODO Auto-generated method stub
				return null;
			}
		}; 
		
	      return new ComputeServiceContextFactory().createContext("byon", "foo", "bar", ImmutableSet.<Module>of(
	              new SshjSshClientModule(), new SLF4JLoggingModule(), new BouncyCastleCryptoModule(), new CacheNodeStoreModule(cacheLoader)));
	   }
   
   private static URI privateKeyFile() {
      try {
         return new URI("file://" + System.getProperty("user.home") + "/.ssh/id_rsa");
      } catch (URISyntaxException e) {
         e.printStackTrace();
      }
      return null;
   }
}
