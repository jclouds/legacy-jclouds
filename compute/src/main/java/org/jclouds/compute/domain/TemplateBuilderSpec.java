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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.domain.LoginCredentials.Builder;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * A specification of a {@link TemplateBuilder} configuration.
 * 
 * <p>
 * {@code TemplateBuilderSpec} supports parsing configuration off of a string,
 * which makes it especially useful for command-line configuration of a
 * {@code TemplateBuilder}.
 * 
 * <p>
 * The string syntax is a series of comma-separated keys or key-value pairs,
 * each corresponding to a {@code TemplateBuilder} method.
 * <ul>
 * <li>{@code hardwareId=[String]}: sets {@link TemplateBuilder#hardwareId}.
 * <li>{@code minCores=[double]}: sets {@link TemplateBuilder#minCores}.
 * <li>{@code minRam=[integer]}: sets {@link TemplateBuilder#minRam}.
 * <li>{@code hypervisorMatches=[String]}: sets
 * {@link TemplateBuilder#hypervisorMatches}.
 * <li>{@code imageId=[String]}: sets {@link TemplateBuilder#imageId}.
 * <li>{@code imageNameMatches=[String]}: sets
 * {@link TemplateBuilder#imageNameMatches}.
 * <li>{@code osFamily=[OsFamily]}: sets {@link TemplateBuilder#osFamily}.
 * <li>{@code osVersionMatches=[String]}: sets
 * {@link TemplateBuilder#osVersionMatches}.
 * <li>{@code os64Bit=[boolean]}: sets {@link TemplateBuilder#os64Bit}.
 * <li>{@code osArchMatches=[String]}: sets
 * {@link TemplateBuilder#osArchMatches}.
 * <li>{@code osDescriptionMatches=[String]}: sets
 * {@link TemplateBuilder#osDescriptionMatches}.
 * <li>{@code loginUser=[String]}: sets
 * {@link TemplateOptions#overrideLoginCredentials} parsing password, if colon
 * delimited.
 * <li>{@code authenticateSudo=[Boolean]}: sets
 * {@link TemplateOptions#overrideLoginCredentials}, but only if
 * {@code loginUser} is set.
 * <li>{@code locationId=[String]}: sets {@link TemplateBuilder#locationId}.
 * </ul>
 * 
 * The set of supported keys will grow as {@code TemplateBuilder} evolves, but
 * existing keys will never be removed.
 * 
 * <p>
 * Whitespace before and after commas and equal signs is ignored. Keys may not
 * be repeated.
 * 
 * <p>
 * It is also illegal to use the following combination of keys
 * <ul>
 * <li>{@code hardwareId} and any of
 * <ul>
 * <li>{@code minCores}
 * <li>{@code minRam}
 * <li>{@code hypervisorMatches}
 * </ul>
 * <li>{@code imageId} and any of
 * <ul>
 * <li>{@code imageNameMatches}
 * <li>{@code osFamily}
 * <li>{@code osVersionMatches}
 * <li>{@code os64Bit}
 * <li>{@code osArchMatches}
 * <li>{@code osDescriptionMatches}
 * </ul>
 * </ul>
 * 
 * <p>
 * {@code TemplateBuilderSpec} does not support configuring
 * {@code TemplateBuilder} methods with non-value parameters. These must be
 * configured in code.
 * 
 * <p>
 * A new {@code TemplateBuilder} can be instantiated from a
 * {@code TemplateBuilderSpec} using
 * {@link TemplateBuilder#from(TemplateBuilderSpec)} or
 * {@link TemplateBuilder#from(String)}.
 * 
 * <p>
 * Design inspired by {@link CacheBuilderSpec}
 * 
 * @author Adrian Cole
 * @since 1.5
 */
@Beta
public class TemplateBuilderSpec {

   /** Parses a single value. */
   protected static interface ValueParser {
      void parse(TemplateBuilderSpec spec, String key, @Nullable String value);
   }

   /** Splits each key-value pair. */
   protected static final Splitter KEYS_SPLITTER = Splitter.on(',').trimResults();

   /** Splits the key from the value. */
   protected static final Splitter KEY_VALUE_SPLITTER = Splitter.on('=').trimResults();

   /** Map of names to ValueParser. */
   protected static final ImmutableMap<String, ValueParser> VALUE_PARSERS = ImmutableMap.<String, ValueParser> builder()
         .put("hardwareId", new HardwareIdParser())
         .put("minCores", new MinCoresParser())
         .put("minRam", new MinRamParser())
         .put("minDisk", new MinDiskParser())
         .put("hypervisorMatches", new HypervisorMatchesMatchesParser())
         .put("imageId", new ImageIdParser())
         .put("imageNameMatches", new ImageNameMatchesParser())
         .put("osFamily", new OsFamilyParser())
         .put("osVersionMatches", new OsVersionMatchesParser())
         .put("os64Bit", new Os64BitParser())
         .put("osArchMatches", new OsArchMatchesParser())
         .put("osDescriptionMatches", new OsDescriptionMatchesParser())
         .put("loginUser", new LoginUserParser())
         .put("authenticateSudo", new AuthenticateSudoParser())
         .put("locationId", new LocationIdParser())
         .build();

   @VisibleForTesting
   String hardwareId;
   @VisibleForTesting
   Double minCores;
   @VisibleForTesting
   Integer minRam;
   @VisibleForTesting
   Double minDisk;
   @VisibleForTesting
   String hypervisorMatches;
   @VisibleForTesting
   String imageId;
   @VisibleForTesting
   String imageNameMatches;
   @VisibleForTesting
   OsFamily osFamily;
   @VisibleForTesting
   String osVersionMatches;
   @VisibleForTesting
   Boolean os64Bit;
   @VisibleForTesting
   String osArchMatches;
   @VisibleForTesting
   String osDescriptionMatches;
   @VisibleForTesting
   String loginUser;
   @VisibleForTesting
   Boolean authenticateSudo;
   @VisibleForTesting
   String locationId;
   
   /** Specification; used for toParseableString(). */
   // transient in case people using serializers don't want this to show up
   protected transient String specification;
   
   protected TemplateBuilderSpec() {
      // we want serializers like Gson to work w/o using sun.misc.Unsafe,
      // prohibited in GAE. This also implies fields are not final.
      // see http://code.google.com/p/jclouds/issues/detail?spec=925
   }

   protected TemplateBuilderSpec(String specification) {
      this.specification = specification;
   }

   /**
    * Creates a TemplateBuilderSpec from a string.
    * 
    * @param templateBuilderSpecification
    *           the string form
    */
   public static TemplateBuilderSpec parse(String templateBuilderSpecification) {
      TemplateBuilderSpec spec = new TemplateBuilderSpec(templateBuilderSpecification);
      if (!templateBuilderSpecification.isEmpty()) {
         for (String keyValuePair : KEYS_SPLITTER.split(templateBuilderSpecification)) {
            List<String> keyAndValue = ImmutableList.copyOf(KEY_VALUE_SPLITTER.split(keyValuePair));
            checkArgument(!keyAndValue.isEmpty(), "blank key-value pair");
            checkArgument(keyAndValue.size() <= 2, "key-value pair %s with more than one equals sign", keyValuePair);

            // Find the ValueParser for the current key.
            String key = keyAndValue.get(0);
            ValueParser valueParser = VALUE_PARSERS.get(key);
            checkArgument(valueParser != null, "unknown key %s", key);

            String value = keyAndValue.size() == 1 ? null : keyAndValue.get(1);
            valueParser.parse(spec, key, value);
         }
      }

      return spec;
   }

   /**
    * Returns a TemplateBuilder configured according to this instance's
    * specification.
    * @param templateOptions 
    */
   public TemplateBuilder copyTo(TemplateBuilder builder, TemplateOptions templateOptions) {
      if (hardwareId != null) {
         builder.hardwareId(hardwareId);
      }
      if (minCores != null) {
         builder.minCores(minCores);
      }
      if (minRam != null) {
         builder.minRam(minRam);
      }
      if (minDisk != null) {
         builder.minDisk(minDisk);
      }
      if (hypervisorMatches != null) {
         builder.hypervisorMatches(hypervisorMatches);
      }
      if (imageId != null) {
         builder.imageId(imageId);
      }
      if (imageNameMatches != null) {
         builder.imageNameMatches(imageNameMatches);
      }
      if (osFamily != null) {
         builder.osFamily(osFamily);
      }
      if (osVersionMatches != null) {
         builder.osVersionMatches(osVersionMatches);
      }
      if (os64Bit != null) {
         builder.os64Bit(os64Bit);
      }
      if (osArchMatches != null) {
         builder.osArchMatches(osArchMatches);
      }
      if (osDescriptionMatches != null) {
         builder.osDescriptionMatches(osDescriptionMatches);
      }
      if (loginUser != null) {
         Builder loginBuilder = LoginCredentials.builder();

         int pos = loginUser.indexOf(':');
         if (pos != -1) {
            loginBuilder.user(loginUser.substring(0, pos)).password(loginUser.substring(pos + 1));
         } else
            loginBuilder.user(loginUser);

         if (authenticateSudo != null) {
            loginBuilder.authenticateSudo(authenticateSudo);
         }
         LoginCredentials creds = loginBuilder.build();
         templateOptions.overrideLoginCredentials(creds);
      }
      if (locationId != null) {
         builder.locationId(locationId);
      }
      return builder;
   }

   /**
    * Returns a string that can be used to parse an equivalent
    * {@code TemplateBuilderSpec}. The order and form of this representation is
    * not guaranteed, except that reparsing its output will produce a
    * {@code TemplateBuilderSpec} equal to this instance.
    */
   public String toParsableString() {
      return specification;
   }

   /**
    * Returns a string representation for this TemplateBuilderSpec instance. The
    * form of this representation is not guaranteed.
    */
   @Override
   public String toString() {
      return toStringHelper(this).addValue(toParsableString()).toString();
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(hardwareId, minCores, minRam, hypervisorMatches, imageId, imageNameMatches, osFamily,
            osVersionMatches, os64Bit, osArchMatches, osDescriptionMatches, loginUser, authenticateSudo, locationId);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (!(obj instanceof TemplateBuilderSpec)) {
         return false;
      }
      TemplateBuilderSpec that = (TemplateBuilderSpec) obj;
      return equal(hardwareId, that.hardwareId) && equal(minCores, that.minCores) && equal(minRam, that.minRam)
            && equal(hypervisorMatches, that.hypervisorMatches) && equal(imageId, that.imageId)
            && equal(imageNameMatches, that.imageNameMatches) && equal(osFamily, that.osFamily)
            && equal(osVersionMatches, that.osVersionMatches) && equal(os64Bit, that.os64Bit)
            && equal(osArchMatches, that.osArchMatches) && equal(osDescriptionMatches, that.osDescriptionMatches)
            && equal(loginUser, that.loginUser) && equal(authenticateSudo, that.authenticateSudo)
            && equal(locationId, that.locationId);
   }
   
   /** Base class for parsing doubles. */
   abstract static class DoubleParser implements ValueParser {
      protected abstract void parseDouble(TemplateBuilderSpec spec, double value);

      @Override
      public void parse(TemplateBuilderSpec spec, String key, String value) {
         checkArgument(value != null && !value.isEmpty(), "value of key %s omitted", key);
         try {
            parseDouble(spec, Double.parseDouble(value));
         } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("key %s value set to %s, must be double", key, value), e);
         }
      }
   }

   /** Base class for parsing ints. */
   abstract static class IntegerParser implements ValueParser {
      protected abstract void parseInteger(TemplateBuilderSpec spec, int value);

      @Override
      public void parse(TemplateBuilderSpec spec, String key, String value) {
         checkArgument(value != null && !value.isEmpty(), "value of key %s omitted", key);
         try {
            parseInteger(spec, Integer.parseInt(value));
         } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("key %s value set to %s, must be integer", key, value), e);
         }
      }
   }

   /** Base class for parsing enums. */
   abstract static class EnumParser<E extends Enum<E>> implements ValueParser {
      private final Class<E> type;

      protected EnumParser(Class<E> type) {
         this.type = type;
      }

      protected abstract void parseEnum(TemplateBuilderSpec spec, E value);

      @Override
      public void parse(TemplateBuilderSpec spec, String key, String value) {
         checkArgument(value != null && !value.isEmpty(), "value of key %s omitted", key);
         try {
            parseEnum(spec, Enum.valueOf(type, value));
         } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("key %s value set to %s, must be a name in enum %s", key,
                  value, type), e);
         }
      }
   }

   /** Base class for parsing strings. */
   abstract static class StringParser implements ValueParser {
      protected abstract void set(TemplateBuilderSpec spec, String value);

      @Override
      public void parse(TemplateBuilderSpec spec, String key, String value) {
         checkArgument(value != null && !value.isEmpty(), "value of key %s omitted", key);
         set(spec, value);
      }
   }
   
   /** Base class for parsing booleans. */
   abstract static class BooleanParser implements ValueParser {
      protected abstract void parseBoolean(TemplateBuilderSpec spec, boolean value);

      @Override
      public void parse(TemplateBuilderSpec spec, String key, String value) {
         checkArgument(value != null && !value.isEmpty(), "value of key %s omitted", key);
         try {
            parseBoolean(spec, Boolean.parseBoolean(value));
         } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("key %s value set to %s, must be booleans", key, value), e);
         }
      }
   }

   /** Parse hardwareId */
   static class HardwareIdParser extends StringParser {
      @Override
      protected void set(TemplateBuilderSpec spec, String value) {
         checkArgument(spec.hardwareId == null, "hardware id was already set to ", spec.hardwareId);
         checkArgument(spec.minCores == null, "min cores was already set to ", spec.minCores);
         checkArgument(spec.minRam == null, "min ram was already set to ", spec.minRam);
         checkArgument(spec.hypervisorMatches == null, "hypervisor matches was already set to ", spec.hypervisorMatches);
         spec.hardwareId = value;
      }
   }
   
   /** Parse minCores */
   static class MinCoresParser extends DoubleParser {
      @Override
      protected void parseDouble(TemplateBuilderSpec spec, double value) {
         checkArgument(spec.minCores == null, "min cores was already set to ", spec.minCores);
         checkArgument(spec.hardwareId == null, "hardware id was already set to ", spec.hardwareId);
         spec.minCores = value;
      }
   }

   /** Parse minRam */
   static class MinRamParser extends IntegerParser {
      @Override
      protected void parseInteger(TemplateBuilderSpec spec, int value) {
         checkArgument(spec.minRam == null, "min ram was already set to ", spec.minRam);
         checkArgument(spec.hardwareId == null, "hardware id was already set to ", spec.hardwareId);
         spec.minRam = value;
      }
   }

   /** Parse minDisk */
   static class MinDiskParser extends DoubleParser {
      @Override
      protected void parseDouble(TemplateBuilderSpec spec, double value) {
         checkArgument(spec.minDisk == null, "min disk was already set to ", spec.minDisk);
         checkArgument(spec.hardwareId == null, "hardware id was already set to ", spec.hardwareId);
         spec.minDisk = value;
      }
   }
   
   /** Parse hypervisorMatches */
   static class HypervisorMatchesMatchesParser extends StringParser {
      @Override
      protected void set(TemplateBuilderSpec spec, String value) {
         checkArgument(spec.hypervisorMatches == null, "hypervisor matches was already set to ", spec.hypervisorMatches);
         checkArgument(spec.hardwareId == null, "hardware id was already set to ", spec.hardwareId);
         spec.hypervisorMatches = value;
      }
   }

   /** Parse imageId */
   static class ImageIdParser extends StringParser {
      @Override
      protected void set(TemplateBuilderSpec spec, String value) {
         checkArgument(spec.imageId == null, "image id was already set to ", spec.imageId);
         checkArgument(spec.imageNameMatches == null, "image name matches was already set to ", spec.imageNameMatches);
         checkArgument(spec.osFamily == null, "operating system family was already set to ", spec.osFamily);
         checkArgument(spec.osVersionMatches == null, "os version matches was already set to ", spec.osVersionMatches);
         checkArgument(spec.os64Bit == null, "os 64 bit was already set to ", spec.os64Bit);
         checkArgument(spec.osArchMatches == null, "os arch matches was already set to ", spec.osArchMatches);
         checkArgument(spec.osDescriptionMatches == null, "os description matches was already set to ", spec.osDescriptionMatches);
         spec.imageId = value;
      }
   }

   /** Parse imageNameMatches */
   static class ImageNameMatchesParser extends StringParser {
      @Override
      protected void set(TemplateBuilderSpec spec, String value) {
         checkArgument(spec.imageNameMatches == null, "image name matches was already set to ", spec.imageNameMatches);
         checkArgument(spec.imageId == null, "image id was already set to ", spec.imageId);
         spec.imageNameMatches = value;
      }
   }
   
   /** Parse osFamily */
   static class OsFamilyParser extends EnumParser<OsFamily> {

      protected OsFamilyParser() {
         super(OsFamily.class);
      }

      @Override
      protected void parseEnum(TemplateBuilderSpec spec, OsFamily value) {
         checkArgument(spec.osFamily == null, "operating system family was already set to ", spec.osFamily);
         checkArgument(spec.imageId == null, "image id was already set to ", spec.imageId);
         spec.osFamily = value;
      }
   }

   /** Parse osVersionMatches */
   static class OsVersionMatchesParser extends StringParser {
      @Override
      protected void set(TemplateBuilderSpec spec, String value) {
         checkArgument(spec.osVersionMatches == null, "os version matches was already set to ", spec.osVersionMatches);
         checkArgument(spec.imageId == null, "image id was already set to ", spec.imageId);
         spec.osVersionMatches = value;
      }
   }
   
   /** Parse os64Bit */
   static class Os64BitParser extends BooleanParser {
      @Override
      protected void parseBoolean(TemplateBuilderSpec spec, boolean value) {
         checkArgument(spec.os64Bit == null, "os 64 bit was already set to ", spec.os64Bit);
         checkArgument(spec.imageId == null, "image id was already set to ", spec.imageId);
         spec.os64Bit = value;
      }
   }

   /** Parse osArchMatches */
   static class OsArchMatchesParser extends StringParser {
      @Override
      protected void set(TemplateBuilderSpec spec, String value) {
         checkArgument(spec.osArchMatches == null, "os arch matches was already set to ", spec.osArchMatches);
         checkArgument(spec.imageId == null, "image id was already set to ", spec.imageId);
         spec.osArchMatches = value;
      }
   }

   /** Parse osDescriptionMatches */
   static class OsDescriptionMatchesParser extends StringParser {
      @Override
      protected void set(TemplateBuilderSpec spec, String value) {
         checkArgument(spec.osDescriptionMatches == null, "os description matches was already set to ", spec.osDescriptionMatches);
         checkArgument(spec.imageId == null, "image id was already set to ", spec.imageId);
         spec.osDescriptionMatches = value;
      }
   }
   
   /** Parse loginUser */
   static class LoginUserParser extends StringParser {
      @Override
      protected void set(TemplateBuilderSpec spec, String value) {
         checkArgument(spec.loginUser == null, "login user was already set to ", spec.loginUser);
         spec.loginUser = value;
      }
   }
   
   /** Parse authenticateSudo */
   static class AuthenticateSudoParser extends BooleanParser {
      @Override
      protected void parseBoolean(TemplateBuilderSpec spec, boolean value) {
         checkArgument(spec.loginUser != null, "login user must be set to use authenticateSudo");
         checkArgument(spec.authenticateSudo == null, "authenticate sudo was already set to ", spec.authenticateSudo);
         spec.authenticateSudo = value;
      }
   }

   /** Parse locationId */
   static class LocationIdParser extends StringParser {
      @Override
      protected void set(TemplateBuilderSpec spec, String value) {
         checkArgument(spec.locationId == null, "location id was already set to ", spec.locationId);
         spec.locationId = value;
      }
   }
   
   public String getHardwareId() {
      return hardwareId;
   }

   public Double getMinCores() {
      return minCores;
   }

   public Integer getMinRam() {
      return minRam;
   }

   public String getHypervisorMatches() {
      return hypervisorMatches;
   }

   public String getImageId() {
      return imageId;
   }

   public String getImageNameMatches() {
      return imageNameMatches;
   }

   public OsFamily getOsFamily() {
      return osFamily;
   }

   public String getOsVersionMatches() {
      return osVersionMatches;
   }

   public Boolean getOs64Bit() {
      return os64Bit;
   }

   public String getOsArchMatches() {
      return osArchMatches;
   }

   public String getOsDescriptionMatches() {
      return osDescriptionMatches;
   }

   public String getLoginUser() {
      return loginUser;
   }

   public Boolean getAuthenticateSudo() {
      return authenticateSudo;
   }

   public String getLocationId() {
      return locationId;
   }

   public String getSpecification() {
      return specification;
   }
}
