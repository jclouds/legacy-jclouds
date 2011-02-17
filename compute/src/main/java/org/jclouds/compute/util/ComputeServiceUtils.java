/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Throwables.getStackTraceAsString;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.size;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.scriptbuilder.domain.Statements.pipeHttpResponseToBash;

import java.net.URI;
import java.util.Formatter;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jclouds.compute.ComputeServiceContextBuilder;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.predicates.RetryIfSocketNotYetOpen;
import org.jclouds.http.HttpRequest;
import org.jclouds.net.IPSocket;
import org.jclouds.rest.Providers;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.Statements;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

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

   /**
    * 
    * @return null if group cannot be parsed
    */
   public static String parseGroupFromName(String from) {
      Matcher matcher = DELIMETED_BY_HYPHEN_ENDING_IN_HYPHEN_HEX.matcher(from);
      return matcher.find() ? matcher.group(1) : null;
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

   public static Iterable<String> getSupportedProviders() {
      return Providers.getSupportedProvidersOfType(ComputeServiceContextBuilder.class);
   }

   public static IPSocket findReachableSocketOnNode(RetryIfSocketNotYetOpen socketTester, final NodeMetadata node,
            final int port) {
      checkNodeHasIps(node);
      IPSocket socket = null;
      try {
         socket = find(transform(concat(node.getPublicAddresses(), node.getPrivateAddresses()),
                  new Function<String, IPSocket>() {

                     @Override
                     public IPSocket apply(String from) {
                        return new IPSocket(from, port);
                     }
                  }), socketTester);
      } catch (NoSuchElementException e) {
         throw new NoSuchElementException(String.format("could not connect to any ip address port %d on node %s", port,
                  node));
      }
      return socket;
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
         return in.indexOf(input) != -1;
      }
   }

}
