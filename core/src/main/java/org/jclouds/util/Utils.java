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

package org.jclouds.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.jclouds.PropertiesBuilder;
import org.jclouds.domain.Credentials;
import org.jclouds.rest.Providers;
import org.jclouds.rest.RestContextBuilder;

import com.google.common.base.Supplier;
import com.google.common.io.OutputSupplier;
import com.google.inject.Module;
import com.google.inject.ProvisionException;
import com.google.inject.util.Modules;

/**
 * General utilities used in jclouds code.
 * 
 * <h4>note</h4> Please look for locations of new utility methods
 * 
 * @author Adrian Cole
 */
@Deprecated
public class Utils {

   /**
    * 
    * @see Throwables2.propagateAuthorizationOrOriginalException
    */
   @Deprecated
   public static <T> T propagateAuthorizationOrOriginalException(Exception e) {
      return Throwables2.<T> propagateAuthorizationOrOriginalException(e);
   }

   /**
    * 
    * @see Lists2#multiMax
    */
   @Deprecated
   @SuppressWarnings("unchecked")
   public static <T, E extends T> List<E> multiMax(Comparator<T> ordering, Iterable<E> iterable) {
      return Lists2.<T, E> multiMax(ordering, iterable);
   }

   /**
    * 
    * @see Throwables2#propagateOrNull
    */
   @Deprecated
   public static <T> T propagateOrNull(Exception from) {
      return Throwables2.<T> propagateOrNull(from);
   }

   /**
    * 
    * @see Throwables2#getFirstThrowableOfType(Throwable, Class)
    */
   @Deprecated
   @SuppressWarnings("unchecked")
   public static <T extends Throwable> T getFirstThrowableOfType(Throwable from, Class<T> clazz) {
      return Throwables2.<T> getFirstThrowableOfType(from, clazz);
   }

   /**
    * 
    * @see Throwables2#getFirstThrowableOfType(ProvisionException, Class)
    */
   @Deprecated
   public static <T extends Throwable> T getFirstThrowableOfType(ProvisionException e, Class<T> clazz) {
      return Throwables2.<T> getFirstThrowableOfType(e, clazz);
   }

   /**
    * 
    * @see Strings2#replaceTokens
    */
   @Deprecated
   public static String replaceTokens(String value, Iterable<Entry<String, String>> tokenValues) {
      return Strings2.replaceTokens(value, tokenValues);
   }

   /**
    * 
    * @see Strings2#replaceAll(String,Pattern, String)
    */
   @Deprecated
   public static String replaceAll(String returnVal, Pattern pattern, String replace) {
      return Strings2.replaceAll(returnVal, pattern, replace);
   }

   /**
    * 
    * @see Strings2#replaceAll(String,char,Pattern, String)
    */
   @Deprecated
   public static String replaceAll(String input, char ifMatch, Pattern pattern, String replacement) {
      return Strings2.replaceAll(input, ifMatch, pattern, replacement);
   }

   /**
    * 
    * @see Strings2#replaceAll(String,char, String)
    */
   @Deprecated
   public static String replaceAll(String input, char match, String replacement) {
      return Strings2.replaceAll(input, match, replacement);
   }

   /**
    * 
    * @see Suppliers2#newOutputStreamSupplier
    */
   @Deprecated
   public static OutputSupplier<OutputStream> newOutputStreamSupplier(final OutputStream output) {
      return Suppliers2.newOutputStreamSupplier(output);
   }

   /**
    * 
    * @see Assertions#eventuallyTrue
    */
   @Deprecated
   public static boolean eventuallyTrue(Supplier<Boolean> assertion, long inconsistencyMillis)
            throws InterruptedException {
      return Assertions.eventuallyTrue(assertion, inconsistencyMillis);
   }

   /**
    * 
    * @see Strings2#toStringAndClose
    */
   @Deprecated
   public static String toStringAndClose(InputStream input) throws IOException {
      return Strings2.toStringAndClose(input);
   }

   /**
    * 
    * @see Strings2#toInputStream
    */
   @Deprecated
   public static InputStream toInputStream(String in) {
      return Strings2.toInputStream(in);
   }

   /**
    * 
    * @see Strings2#encodeString(String, String)
    */
   @Deprecated
   public static byte[] encodeString(String str, String charsetName) {
      return Strings2.encodeString(str, charsetName);
   }

   /**
    * 
    * @see Strings2#encodeString(String)
    */
   @Deprecated
   public static byte[] encodeString(String str) {
      return Strings2.encodeString(str);
   }

   /**
    * 
    * @see Strings2#replaceTokens(String,Map)
    */
   @Deprecated
   public static String replaceTokens(String input, Map<String, String> replacements) {
      return Strings2.replaceTokens(input, replacements);
   }

   /**
    * 
    * @see Preconditions2#checkNotEmpty(String)
    */
   @Deprecated
   public static void checkNotEmpty(String nullableString) {
      Preconditions2.checkNotEmpty(nullableString);
   }

   /**
    * 
    * @see Preconditions2#checkNotEmpty(String,String)
    */
   @Deprecated
   public static void checkNotEmpty(String nullableString, String message) {
      Preconditions2.checkNotEmpty(nullableString, message);
   }

   /**
    * 
    * @see Providers#getSupportedProviders
    */
   @Deprecated
   public static Iterable<String> getSupportedProviders() {
      return Providers.getSupportedProviders();
   }

   /**
    * 
    * @see Providers#getSupportedProvidersOfType
    */
   @SuppressWarnings("unchecked")
   @Deprecated
   public static Iterable<String> getSupportedProvidersOfType(Class<? extends RestContextBuilder> type) {
      return Providers.getSupportedProvidersOfType(type);
   }

   /**
    * 
    * @see Providers#getSupportedProvidersOfTypeInProperties
    */
   @SuppressWarnings("unchecked")
   @Deprecated
   public static Iterable<String> getSupportedProvidersOfTypeInProperties(
            final Class<? extends RestContextBuilder<?,?>> type, final Properties properties) {
      return Providers.getSupportedProvidersOfTypeInProperties(type, properties);
   }

   /**
    * 
    * @see Providers#resolveContextBuilderClass
    */
   @Deprecated
   @SuppressWarnings("unchecked")
   public static <S, A> Class<RestContextBuilder<S, A>> resolveContextBuilderClass(String provider,
            Properties properties) throws ClassNotFoundException, IllegalArgumentException, SecurityException,
            InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
      return Providers.<S, A> resolveContextBuilderClass(provider, properties);
   }

   /**
    * 
    * @see Providers#initContextBuilder
    */
   @Deprecated
   public static <S, A> RestContextBuilder<S, A> initContextBuilder(
            Class<RestContextBuilder<S, A>> contextBuilderClass, @Nullable Class<S> sync, @Nullable Class<A> async,
            Properties properties) throws ClassNotFoundException, IllegalArgumentException, SecurityException,
            InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
      return Providers.<S, A> initContextBuilder(contextBuilderClass, sync, async, properties);
   }

   /**
    * 
    * @see Providers#resolvePropertiesBuilderClass
    */
   @Deprecated
   @SuppressWarnings("unchecked")
   public static Class<PropertiesBuilder> resolvePropertiesBuilderClass(String providerName, Properties props)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
      return Providers.resolvePropertiesBuilderClass(providerName, props);
   }

   /**
    * 
    * @see Modules#modulesForProviderInProperties
    */
   @Deprecated
   public static Iterable<Module> modulesForProviderInProperties(String providerName, Properties props) {
      return Modules2.modulesForProviderInProperties(providerName, props);
   }

   /**
    * 
    * @see Modules#modulesFromProperty
    */
   @Deprecated
   public static Iterable<Module> modulesFromProperty(Properties props, String property) {
      return Modules2.modulesFromProperty(props, property);
   }

   /**
    * 
    * @see Modules#modulesFromCommaDelimitedString
    */
   @Deprecated
   public static Iterable<Module> modulesFromCommaDelimitedString(String moduleClasses) {
      return Modules2.modulesFromCommaDelimitedString(moduleClasses);
   }

   /**
    * 
    * @see CredentialUtils#isPrivateKeyCredential
    */
   @Deprecated
   public static boolean isPrivateKeyCredential(Credentials credentials) {
      return CredentialUtils.isPrivateKeyCredential(credentials);
   }

   /**
    * 
    * @see CredentialUtils#overrideCredentialsIfSupplied
    */
   @Deprecated
   public static Credentials overrideCredentialsIfSupplied(Credentials defaultCredentials,
            @Nullable Credentials overridingCredentials) {
      return CredentialUtils.overrideCredentialsIfSupplied(defaultCredentials, overridingCredentials);
   }

}
