/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.compute.util;

import java.util.Formatter;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.jclouds.compute.ComputeServiceContextBuilder;
import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.domain.Credentials;
import org.jclouds.logging.Logger;
import org.jclouds.ssh.SshClient;
import org.jclouds.util.Utils;

import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
public class ComputeServiceUtils {

   public static final Map<org.jclouds.compute.domain.OsFamily, Map<String, String>> NAME_VERSION_MAP = ImmutableMap
            .<org.jclouds.compute.domain.OsFamily, Map<String, String>> of(
                     org.jclouds.compute.domain.OsFamily.CENTOS, ImmutableMap
                              .<String, String> builder().put("5.3", "5.3").put("5.4", "5.4").put(
                                       "5.5", "5.5").build(),
                     org.jclouds.compute.domain.OsFamily.RHEL, ImmutableMap
                              .<String, String> builder().put("5.3", "5.3").put("5.4", "5.4").put(
                                       "5.5", "5.5").build(),
                     org.jclouds.compute.domain.OsFamily.UBUNTU, ImmutableMap
                              .<String, String> builder().put("hardy", "8.04").put("intrepid",
                                       "8.10").put("jaunty", "9.04").put("karmic", "9.10").put(
                                       "lucid", "10.04").put("maverick", "10.10").build());

   public static String parseVersionOrReturnEmptyString(org.jclouds.compute.domain.OsFamily family,
            final String in) {
      if (NAME_VERSION_MAP.containsKey(family)) {
         CONTAINS_SUBSTRING contains = new CONTAINS_SUBSTRING(in.replace('-', '.'));
         try {
            String key = Iterables.find(NAME_VERSION_MAP.get(family).keySet(), contains);
            return NAME_VERSION_MAP.get(family).get(key);
         } catch (NoSuchElementException e) {
            try {
               return Iterables.find(NAME_VERSION_MAP.get(family).values(), contains);
            } catch (NoSuchElementException e1) {
            }
         }
      }
      return "";
   }

   public static org.jclouds.compute.domain.OsFamily parseOsFamilyOrNull(String in) {
      org.jclouds.compute.domain.OsFamily myOs = null;
      for (org.jclouds.compute.domain.OsFamily os : org.jclouds.compute.domain.OsFamily.values()) {
         if (in.toLowerCase().replaceAll("\\s", "").indexOf(os.toString()) != -1) {
            myOs = os;
         }
      }
      return myOs;
   }

   public static Architecture parseArchitectureOrNull(String in) {
      return in.indexOf("64") == -1 ? Architecture.X86_32 : Architecture.X86_64;
   }

   public static String createExecutionErrorMessage(Map<?, Exception> executionExceptions) {
      Formatter fmt = new Formatter().format("Execution failures:%n%n");
      int index = 1;
      for (Entry<?, Exception> errorMessage : executionExceptions.entrySet()) {
         fmt.format("%s) %s on %s:%n%s%n%n", index++, errorMessage.getValue().getClass()
                  .getSimpleName(), errorMessage.getKey(), Throwables
                  .getStackTraceAsString(errorMessage.getValue()));
      }
      return fmt.format("%s error[s]", executionExceptions.size()).toString();
   }

   public static String createNodeErrorMessage(
            Map<? extends NodeMetadata, ? extends Throwable> failedNodes) {
      Formatter fmt = new Formatter().format("Node failures:%n%n");
      int index = 1;
      for (Entry<? extends NodeMetadata, ? extends Throwable> errorMessage : failedNodes.entrySet()) {
         fmt.format("%s) %s on node %s:%n%s%n%n", index++, errorMessage.getValue().getClass()
                  .getSimpleName(), errorMessage.getKey().getId(), Throwables
                  .getStackTraceAsString(errorMessage.getValue()));
      }
      return fmt.format("%s error[s]", failedNodes.size()).toString();
   }

   public static Iterable<? extends ComputeMetadata> filterByName(
            Iterable<? extends ComputeMetadata> nodes, final String name) {
      return Iterables.filter(nodes, new Predicate<ComputeMetadata>() {
         @Override
         public boolean apply(ComputeMetadata input) {
            return input.getName().equalsIgnoreCase(name);
         }
      });
   }

   private static final class CONTAINS_SUBSTRING implements Predicate<String> {
      private final String in;

      private CONTAINS_SUBSTRING(String in) {
         this.in = in;
      }

      @Override
      public boolean apply(String input) {
         return in.indexOf(input) != -1;
      }
   }

   public static interface SshCallable<T> extends Callable<T> {
      NodeMetadata getNode();

      void setConnection(SshClient ssh, Logger logger);
   }

   public static boolean isKeyAuth(NodeMetadata createdNode) {
      return createdNode.getCredentials().credential != null
               && createdNode.getCredentials().credential
                        .startsWith("-----BEGIN RSA PRIVATE KEY-----");
   }

   /**
    * Given the instances of {@link NodeMetadata} (immutable) and {@link Credentials} (immutable),
    * returns a new instance of {@link NodeMetadata} that has new credentials
    */
   public static NodeMetadata installNewCredentials(NodeMetadata node, Credentials newCredentials) {
      return new NodeMetadataImpl(node.getProviderId(), node.getName(), node.getId(), node
               .getLocation(), node.getUri(), node.getUserMetadata(), node.getTag(), node
               .getImage(), node.getState(), node.getPublicAddresses(), node.getPrivateAddresses(),
               node.getExtra(), newCredentials);
   }

   public static Iterable<String> getSupportedProviders() {
      return Utils.getSupportedProvidersOfType(ComputeServiceContextBuilder.class);
   }

}
