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
package org.jclouds.compute.util;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Throwables.getStackTraceAsString;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.size;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.scriptbuilder.domain.Statements.pipeHttpResponseToBash;

import java.net.URI;
import java.util.Formatter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.http.HttpRequest;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.Statements;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.net.HostAndPort;
import com.google.common.reflect.TypeToken;

/**
 * 
 * @author Adrian Cole
 */
public class ComputeServiceUtils {
   public static final Pattern DELIMETED_BY_HYPHEN_ENDING_IN_HYPHEN_HEX = Pattern.compile("(.+)-[0-9a-f]+");

   /**
    * build a shell script that invokes the contents of the http request in bash.
    * 
    * @return a shell script that will invoke the http request
    */
   public static Statement execHttpResponse(HttpRequest request) {
      return pipeHttpResponseToBash(request.getMethod(), request.getEndpoint(), request.getHeaders());
   }

   public static Statement execHttpResponse(URI location) {
      return execHttpResponse(new HttpRequest("GET", location));
   }

   /**
    * build a shell script that invokes the contents of the http request in bash.
    * 
    * @return a shell script that will invoke the http request
    */
   public static Statement extractTargzIntoDirectory(HttpRequest targz, String directory) {
      return Statements
               .extractTargzIntoDirectory(targz.getMethod(), targz.getEndpoint(), targz.getHeaders(), directory);
   }

   public static Statement extractTargzIntoDirectory(URI targz, String directory) {
      return extractTargzIntoDirectory(new HttpRequest("GET", targz), directory);
   }

   /**
    * build a shell script that invokes the contents of the http request in bash.
    * 
    * @return a shell script that will invoke the http request
    */
   public static Statement extractZipIntoDirectory(HttpRequest zip, String directory) {
      return Statements.extractZipIntoDirectory(zip.getMethod(), zip.getEndpoint(), zip.getHeaders(), directory);
   }

   public static Statement extractZipIntoDirectory(URI zip, String directory) {
      return extractZipIntoDirectory(new HttpRequest("GET", zip), directory);
   }

   public static double getCores(Hardware input) {
      double cores = 0;
      for (Processor processor : input.getProcessors())
         cores += processor.getCores();
      return cores;
   }

   public static double getCoresAndSpeed(Hardware input) {
      double total = 0;
      for (Processor processor : input.getProcessors())
         total += (processor.getCores() * processor.getSpeed());
      return total;
   }

   public static double getSpace(Hardware input) {
      double total = 0;
      for (Volume volume : input.getVolumes())
         total += volume.getSize() != null ? volume.getSize() : 0;
      return total;
   }

   public static org.jclouds.compute.domain.OsFamily parseOsFamilyOrUnrecognized(String in) {
      org.jclouds.compute.domain.OsFamily myOs = null;
      for (org.jclouds.compute.domain.OsFamily os : org.jclouds.compute.domain.OsFamily.values()) {
         if (in.toLowerCase().replaceAll("\\s", "").indexOf(os.toString()) != -1) {
            myOs = os;
         }
      }
      return myOs != null ? myOs : OsFamily.UNRECOGNIZED;
   }

   public static String createExecutionErrorMessage(Map<?, Exception> executionExceptions) {
      Formatter fmt = new Formatter().format("Execution failures:%n%n");
      int index = 1;
      for (Entry<?, Exception> errorMessage : executionExceptions.entrySet()) {
         fmt.format("%s) %s on %s:%n%s%n%n", index++, errorMessage.getValue().getClass().getSimpleName(), errorMessage
                  .getKey(), getStackTraceAsString(errorMessage.getValue()));
      }
      return fmt.format("%s error[s]", executionExceptions.size()).toString();
   }

   public static String createNodeErrorMessage(Map<? extends NodeMetadata, ? extends Throwable> failedNodes) {
      Formatter fmt = new Formatter().format("Node failures:%n%n");
      int index = 1;
      for (Entry<? extends NodeMetadata, ? extends Throwable> errorMessage : failedNodes.entrySet()) {
         fmt.format("%s) %s on node %s:%n%s%n%n", index++, errorMessage.getValue().getClass().getSimpleName(),
                  errorMessage.getKey().getId(), getStackTraceAsString(errorMessage.getValue()));
      }
      return fmt.format("%s error[s]", failedNodes.size()).toString();
   }

   public static Iterable<? extends ComputeMetadata> filterByName(Iterable<? extends ComputeMetadata> nodes,
            final String name) {
      return filter(nodes, new Predicate<ComputeMetadata>() {
         @Override
         public boolean apply(ComputeMetadata input) {
            return input.getName().equalsIgnoreCase(name);
         }
      });
   }

   @Deprecated
   public static Iterable<String> getSupportedProviders() {
      return org.jclouds.rest.Providers.getSupportedProvidersOfType(TypeToken.of(ComputeServiceContext.class));
   }

   public static HostAndPort findReachableSocketOnNode(final SocketOpen socketTester, final NodeMetadata node,
            final int port, long timeoutValue, TimeUnit timeUnits, Logger logger) {
      return findReachableSocketOnNode(socketTester, null, node, port, timeoutValue, timeUnits, logger);
   }
   
   public static HostAndPort findReachableSocketOnNode(final SocketOpen socketTester, 
            @Nullable final Predicate<AtomicReference<NodeMetadata>> nodeRunning, final NodeMetadata node,
            final int port, long timeoutValue, TimeUnit timeUnits, Logger logger) {
      checkNodeHasIps(node);

      Iterable<HostAndPort> sockets = transform(concat(node.getPublicAddresses(), node.getPrivateAddresses()),
               new Function<String, HostAndPort>() {

                  @Override
                  public HostAndPort apply(String from) {
                     return HostAndPort.fromParts(from, port);
                  }
               });

      // Specify a retry period of 1s, expressed in the same time units.
      long period = timeUnits.convert(1, TimeUnit.SECONDS);

      // For storing the result, as predicate will just tell us true/false
      final AtomicReference<HostAndPort> result = new AtomicReference<HostAndPort>();

      Predicate<Iterable<HostAndPort>> multiIpSocketTester = new Predicate<Iterable<HostAndPort>>() {

         @Override
         public boolean apply(Iterable<HostAndPort> sockets) {
            for (HostAndPort socket : sockets) {
               if (socketTester.apply(socket)) {
                  result.set(socket);
                  return true;
               }
            }
            if (nodeRunning != null && !nodeRunning.apply(new AtomicReference<NodeMetadata>(node))) {
               throw new IllegalStateException(String.format("Node %s is no longer running; aborting waiting for ip:port connection", node.getId()));
            }
            return false;
         }
         
      };
      
      RetryablePredicate<Iterable<HostAndPort>> tester = new RetryablePredicate<Iterable<HostAndPort>>(
               multiIpSocketTester, timeoutValue, period, timeUnits);
      
      logger.debug(">> blocking on sockets %s for %d %s", sockets, timeoutValue, timeUnits);

      boolean passed = tester.apply(sockets);
      
      if (passed) {
         logger.debug("<< socket %s opened", result);
         assert result.get() != null;
         return result.get();
      } else {
         logger.warn("<< sockets %s didn't open after %d %s", sockets, timeoutValue, timeUnits);
         throw new NoSuchElementException(String.format("could not connect to any ip address port %d on node %s", 
                  port, node));
      }
   }

   public static void checkNodeHasIps(NodeMetadata node) {
      checkState(size(concat(node.getPublicAddresses(), node.getPrivateAddresses())) > 0,
               "node does not have IP addresses configured: " + node);
   }

   public static String parseVersionOrReturnEmptyString(org.jclouds.compute.domain.OsFamily family, String in,
            Map<OsFamily, Map<String, String>> osVersionMap) {
      if (osVersionMap.containsKey(family)) {
         if (osVersionMap.get(family).containsKey(in))
            return osVersionMap.get(family).get(in);
         if (osVersionMap.get(family).containsValue(in))
            return in;
         CONTAINS_SUBSTRING contains = new CONTAINS_SUBSTRING(in.replace('-', '.'));
         try {
            String key = Iterables.find(osVersionMap.get(family).keySet(), contains);
            return osVersionMap.get(family).get(key);
         } catch (NoSuchElementException e) {
            try {
               return Iterables.find(osVersionMap.get(family).values(), contains);
            } catch (NoSuchElementException e1) {
            }
         }
      }
      return "";
   }

   static final class CONTAINS_SUBSTRING implements Predicate<String> {
      private final String in;

      CONTAINS_SUBSTRING(String in) {
         this.in = in;
      }

      @Override
      public boolean apply(String input) {
         if ("".equals(input))
            return false;
         return in.indexOf(input) != -1;
      }

      @Override
      public String toString() {
         return String.format("containsSubString(%s)", in);
      }
   }

}
