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
package org.jclouds.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.base.Throwables.getCausalChain;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.io.ByteStreams.toByteArray;
import static com.google.common.io.Closeables.closeQuietly;
import static org.jclouds.util.Patterns.CHAR_TO_PATTERN;
import static org.jclouds.util.Patterns.TOKEN_TO_PATTERN;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.annotation.Resource;

import org.jclouds.PropertiesBuilder;
import org.jclouds.logging.Logger;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.RestContextBuilder;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.io.OutputSupplier;
import com.google.inject.ProvisionException;
import com.google.inject.spi.Message;

/**
 * General utilities used in jclouds code.
 * 
 * @author Adrian Cole
 */
public class Utils {

   public static <K, V> Supplier<Map<K, V>> composeMapSupplier(Iterable<Supplier<Map<K, V>>> suppliers) {
      return new ListMapSupplier<K, V>(suppliers);
   }

   static class ListMapSupplier<K, V> implements Supplier<Map<K, V>> {

      private final Iterable<Supplier<Map<K, V>>> suppliers;

      ListMapSupplier(Iterable<Supplier<Map<K, V>>> suppliers) {
         this.suppliers = suppliers;
      }

      @Override
      public Map<K, V> get() {
         Map<K, V> toReturn = Maps.newLinkedHashMap();
         for (Supplier<Map<K, V>> supplier : suppliers) {
            toReturn.putAll(supplier.get());
         }
         return toReturn;
      }
   }

   public static <T> T propagateAuthorizationOrOriginalException(Exception e) {
      AuthorizationException aex = getFirstThrowableOfType(e, AuthorizationException.class);
      if (aex != null)
         throw aex;
      propagate(e);
      assert false : "exception should have propogated " + e;
      return null;
   }

   /**
    * Like Ordering, but handle the case where there are multiple valid maximums
    */
   @SuppressWarnings("unchecked")
   public static <T, E extends T> List<E> multiMax(Comparator<T> ordering, Iterable<E> iterable) {
      Iterator<E> iterator = iterable.iterator();
      List<E> maxes = newArrayList(iterator.next());
      E maxSoFar = maxes.get(0);
      while (iterator.hasNext()) {
         E current = iterator.next();
         int comparison = ordering.compare(maxSoFar, current);
         if (comparison == 0) {
            maxes.add(current);
         } else if (comparison < 0) {
            maxes = newArrayList(current);
            maxSoFar = current;
         }
      }
      return maxes;
   }

   public static final String UTF8_ENCODING = "UTF-8";

   public static <T> Set<T> nullSafeSet(T in) {
      if (in == null) {
         return ImmutableSet.<T> of();
      }
      return ImmutableSet.<T> of(in);
   }

   public static Object propagateOrNull(Exception from) {
      propagate(from);
      assert false : "exception should have propogated";
      return null;
   }

   @SuppressWarnings("unchecked")
   public static <T extends Throwable> T getFirstThrowableOfType(Throwable from, Class<T> clazz) {
      if (from instanceof ProvisionException)
         return getFirstThrowableOfType(ProvisionException.class.cast(from), clazz);
      try {
         return (T) find(getCausalChain(from), instanceOf(clazz));
      } catch (NoSuchElementException e) {
         return null;
      }
   }

   public static <T extends Throwable> T getFirstThrowableOfType(ProvisionException e, Class<T> clazz) {
      for (Message message : e.getErrorMessages()) {
         T cause = getFirstThrowableOfType(message.getCause(), clazz);
         if (cause instanceof ProvisionException)
            return getFirstThrowableOfType(ProvisionException.class.cast(cause), clazz);
         return cause;
      }
      return null;
   }

   public static String replaceTokens(String value, Iterable<Entry<String, String>> tokenValues) {
      for (Entry<String, String> tokenValue : tokenValues) {
         value = replaceAll(value, TOKEN_TO_PATTERN.get(tokenValue.getKey()), tokenValue.getValue());
      }
      return value;
   }

   public static String replaceAll(String returnVal, Pattern pattern, String replace) {
      Matcher m = pattern.matcher(returnVal);
      returnVal = m.replaceAll(replace);
      return returnVal;
   }

   public static String replaceAll(String input, char ifMatch, Pattern pattern, String replacement) {
      if (input.indexOf(ifMatch) != -1) {
         input = pattern.matcher(input).replaceAll(replacement);
      }
      return input;
   }

   public static String replaceAll(String input, char match, String replacement) {
      if (input.indexOf(match) != -1) {
         input = CHAR_TO_PATTERN.get(match).matcher(input).replaceAll(replacement);
      }
      return input;
   }

   /**
    * converts an {@link OutputStream} to an {@link OutputSupplier}
    * 
    */
   public static OutputSupplier<OutputStream> newOutputStreamSupplier(final OutputStream output) {
      checkNotNull(output, "output");
      return new OutputSupplier<OutputStream>() {
         public OutputStream getOutput() throws IOException {
            return output;
         }
      };
   }

   public static boolean eventuallyTrue(Supplier<Boolean> assertion, long inconsistencyMillis)
         throws InterruptedException {

      for (int i = 0; i < 30; i++) {
         if (!assertion.get()) {
            Thread.sleep(inconsistencyMillis / 30);
            continue;
         }
         return true;
      }
      return false;

   }

   @Resource
   protected static Logger logger = Logger.NULL;

   public static String toStringAndClose(InputStream input) throws IOException {
      checkNotNull(input, "input");
      try {
         return new String(toByteArray(input), Charsets.UTF_8);
      } catch (IOException e) {
         logger.warn(e, "Failed to read from stream");
         return null;
      } catch (NullPointerException e) {
         return null;
      } finally {
         closeQuietly(input);
      }
   }

   public static InputStream toInputStream(String in) {
      return new ByteArrayInputStream(in.getBytes(Charsets.UTF_8));
   }

   /**
    * Encode the given string with the given encoding, if possible. If the
    * encoding fails with {@link UnsupportedEncodingException}, log a warning
    * and fall back to the system's default encoding.
    * 
    * @param str
    *           what to encode
    * @param charsetName
    *           the name of a supported {@link java.nio.charset.Charset
    *           </code>charset<code>}
    * @return properly encoded String.
    */
   public static byte[] encodeString(String str, String charsetName) {
      try {
         return str.getBytes(charsetName);
      } catch (UnsupportedEncodingException e) {
         logger.warn(e, "Failed to encode string to bytes with encoding " + charsetName
               + ". Falling back to system's default encoding");
         return str.getBytes();
      }
   }

   /**
    * Encode the given string with the UTF-8 encoding, the sane default. In the
    * very unlikely event the encoding fails with
    * {@link UnsupportedEncodingException}, log a warning and fall back to the
    * system's default encoding.
    * 
    * @param str
    *           what to encode
    * @return properly encoded String.
    */
   public static byte[] encodeString(String str) {
      return encodeString(str, UTF8_ENCODING);
   }

   /**
    * replaces tokens that are expressed as <code>{token}</code>
    * 
    * <p/>
    * ex. if input is "hello {where}"<br/>
    * and replacements is "where" -> "world" <br/>
    * then replaceTokens returns "hello world"
    * 
    * @param input
    *           source to replace
    * @param replacements
    *           token/value pairs
    */
   public static String replaceTokens(String input, Map<String, String> replacements) {
      Matcher matcher = Patterns.TOKEN_PATTERN.matcher(input);
      StringBuilder builder = new StringBuilder();
      int i = 0;
      while (matcher.find()) {
         String replacement = replacements.get(matcher.group(1));
         builder.append(input.substring(i, matcher.start()));
         if (replacement == null)
            builder.append(matcher.group(0));
         else
            builder.append(replacement);
         i = matcher.end();
      }
      builder.append(input.substring(i, input.length()));
      return builder.toString();
   }

   /**
    * Will throw an exception if the argument is null or empty.
    * 
    * @param nullableString
    *           string to verify. Can be null or empty.
    */
   public static void checkNotEmpty(String nullableString) {
      checkNotEmpty(nullableString, "Argument can't be null or empty");
   }

   /**
    * Will throw an exception if the argument is null or empty. Accepts a custom
    * error message.
    * 
    * @param nullableString
    *           string to verify. Can be null or empty.
    * @param message
    *           message to show in case of exception
    */
   public static void checkNotEmpty(String nullableString, String message) {
      checkArgument(nullableString != null && nullableString.length() > 0, message);
   }

   /**
    * Gets a set of supported providers. Idea stolen from pallets
    * (supported-clouds). Uses rest.properties to populate the set.
    * 
    */
   public static Iterable<String> getSupportedProviders() {
      return getSupportedProvidersOfType(RestContextBuilder.class);
   }

   /**
    * Gets a set of supported providers. Idea stolen from pallets
    * (supported-clouds). Uses rest.properties to populate the set.
    * 
    */
   @SuppressWarnings("unchecked")
   public static Iterable<String> getSupportedProvidersOfType(Class<? extends RestContextBuilder> type) {
      Properties properties = new Properties();
      try {
         properties.load(Utils.class.getResourceAsStream("/rest.properties"));
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
      return getSupportedProvidersOfTypeInProperties(type, properties);
   }

   @SuppressWarnings("unchecked")
   public static Iterable<String> getSupportedProvidersOfTypeInProperties(
         final Class<? extends RestContextBuilder> type, final Properties properties) {
      return filter(transform(filter(properties.entrySet(), new Predicate<Map.Entry<Object, Object>>() {

         @Override
         public boolean apply(Entry<Object, Object> input) {
            String keyString = input.getKey().toString();
            return keyString.endsWith(".contextbuilder") || keyString.endsWith(".sync");
         }

      }), new Function<Map.Entry<Object, Object>, String>() {

         @Override
         public String apply(Entry<Object, Object> from) {
            String keyString = from.getKey().toString();
            try {
               String provider = get(Splitter.on('.').split(keyString), 0);
               Class<RestContextBuilder<Object, Object>> clazz = resolveContextBuilderClass(provider, properties);
               if (type.isAssignableFrom(clazz))
                  return provider;
            } catch (ClassNotFoundException e) {
            } catch (Exception e) {
               propagate(e);
            }
            return null;
         }

      }), notNull());
   }

   @SuppressWarnings("unchecked")
   public static <S, A> Class<RestContextBuilder<S, A>> resolveContextBuilderClass(String provider,
         Properties properties) throws ClassNotFoundException, IllegalArgumentException, SecurityException,
         InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
      String contextBuilderClassName = properties.getProperty(provider + ".contextbuilder");
      String syncClassName = properties.getProperty(provider + ".sync");
      String asyncClassName = properties.getProperty(provider + ".async");
      if (syncClassName != null) {
         checkArgument(asyncClassName != null, "please configure async class for " + syncClassName);
         Class.forName(syncClassName);
         Class.forName(asyncClassName);
         return (Class<RestContextBuilder<S, A>>) (contextBuilderClassName != null ? Class
               .forName(contextBuilderClassName) : RestContextBuilder.class);
      } else {
         checkArgument(contextBuilderClassName != null, "please configure contextbuilder class for " + provider);
         return (Class<RestContextBuilder<S, A>>) Class.forName(contextBuilderClassName);
      }
   }

   public static <S, A> RestContextBuilder<S, A> initContextBuilder(
         Class<RestContextBuilder<S, A>> contextBuilderClass, @Nullable Class<S> sync, @Nullable Class<A> async,
         Properties properties) throws ClassNotFoundException, IllegalArgumentException, SecurityException,
         InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
      checkArgument(properties != null, "please configure properties for " + contextBuilderClass);
      try {
         return (RestContextBuilder<S, A>) contextBuilderClass.getConstructor(Properties.class).newInstance(properties);
      } catch (NoSuchMethodException e) {
         checkArgument(sync != null, "please configure sync class for " + contextBuilderClass);
         checkArgument(async != null, "please configure async class for " + contextBuilderClass);
         return (RestContextBuilder<S, A>) contextBuilderClass.getConstructor(sync.getClass(), async.getClass(),
               Properties.class).newInstance(sync, async, properties);
      }
   }

   @SuppressWarnings("unchecked")
   public static Class<PropertiesBuilder> resolvePropertiesBuilderClass(String providerName, Properties props)
         throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException,
         NoSuchMethodException {
      String propertiesBuilderClassName = props.getProperty(providerName + ".propertiesbuilder", null);
      if (propertiesBuilderClassName != null) {
         return (Class<PropertiesBuilder>) Class.forName(propertiesBuilderClassName);
      } else {
         return PropertiesBuilder.class;
      }
   }
}
