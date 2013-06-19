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
package org.jclouds.compute.util;

import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Throwables.getStackTraceAsString;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Maps.filterKeys;
import static com.google.common.collect.Maps.filterValues;
import static org.jclouds.scriptbuilder.domain.Statements.pipeHttpResponseToBash;

import java.net.URI;
import java.util.Formatter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Pattern;

import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ComputeMetadataIncludingStatus;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.Statements;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import com.google.common.primitives.Ints;

/**
 * 
 * @author Adrian Cole
 */
public class ComputeServiceUtils {
   
   /**
    * status as a string which optionally includes the backend status
    */
   public static String formatStatus(ComputeMetadataIncludingStatus<?> resource) {
      if (resource.getBackendStatus() == null)
         return resource.getStatus().toString();
      return String.format("%s[%s]", resource.getStatus(), resource.getBackendStatus());
   }
   
   public static final Pattern DELIMITED_BY_HYPHEN_ENDING_IN_HYPHEN_HEX = Pattern.compile("(.+)-[0-9a-f]+");

   /**
    * build a shell script that invokes the contents of the http request in bash.
    * 
    * @return a shell script that will invoke the http request
    */
   public static Statement execHttpResponse(HttpRequest request) {
      return pipeHttpResponseToBash(request.getMethod(), request.getEndpoint(), request.getHeaders());
   }

   public static Statement execHttpResponse(URI location) {
      return execHttpResponse(HttpRequest.builder().method("GET").endpoint(location).build());
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
      return extractTargzIntoDirectory(HttpRequest.builder().method("GET").endpoint(targz).build(), directory);
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
      return extractZipIntoDirectory(HttpRequest.builder().method("GET").endpoint(zip).build(), directory);
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
         total += processor.getCores() * processor.getSpeed();
      return total;
   }

   public static double getSpace(Hardware input) {
      double total = 0;
      for (Volume volume : input.getVolumes()) {
         Float size = volume.getSize();
         if (size != null) {
            total += size;
         }
      }
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
   
   /**
    * For cloud apis that have a pattern of using empty strings as tags, return a map that contains
    * that.
    */
   public static Map<String, String> metadataAndTagsAsValuesOfEmptyString(TemplateOptions options) {
      Builder<String, String> builder = ImmutableMap.<String, String> builder();
      builder.putAll(options.getUserMetadata());
      for (String tag : options.getTags())
         builder.put(tag, "");
      return builder.build();
   }

   /**
    * @see #metadataAndTagsAsValuesOfEmptyString
    */
   public static NodeMetadataBuilder addMetadataAndParseTagsFromValuesOfEmptyString(NodeMetadataBuilder builder,
            Map<String, String> map) {
      return builder.tags(filterValues(map, equalTo("")).keySet()).userMetadata(filterValues(map, not(equalTo(""))));
   }

   /**
    * For cloud apis that need to namespace tags as the value of the key {@code jclouds.tags}
    */
   public static Map<String, String> metadataAndTagsAsCommaDelimitedValue(TemplateOptions options) {
      Builder<String, String> builder = ImmutableMap.<String, String> builder();
      builder.putAll(options.getUserMetadata());
      if (options.getTags().size() > 0)
         builder.put("jclouds_tags", Joiner.on(',').join(options.getTags()));
      return builder.build();
   }

   /**
    * @see #metadataAndTagsAsCommaDelimitedValue
    */
   public static NodeMetadataBuilder addMetadataAndParseTagsFromCommaDelimitedValue(NodeMetadataBuilder builder,
            Map<String, String> map) {
      String tagString = map.get("jclouds_tags");
      if (tagString != null)
         builder.tags(Splitter.on(',').split(tagString));
      builder.userMetadata(filterKeys(map, not(equalTo("jclouds_tags"))));
      return builder;
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

   public static Map<Integer, Integer> getPortRangesFromList(int... ports) {
      Set<Integer> sortedPorts = ImmutableSortedSet.copyOf(Ints.asList(ports));

      RangeSet<Integer> ranges = TreeRangeSet.create();
      
      for (Integer port : sortedPorts) {
         ranges.add(Range.closedOpen(port, port + 1));
      }
      
      Map<Integer, Integer> portRanges = Maps.newHashMap();

      for (Range<Integer> r : ranges.asRanges()) {
         portRanges.put(r.lowerEndpoint(), r.upperEndpoint() - 1);
      }

      return portRanges;
   }
}
