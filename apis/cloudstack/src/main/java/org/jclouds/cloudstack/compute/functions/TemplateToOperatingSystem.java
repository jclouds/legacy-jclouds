package org.jclouds.cloudstack.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.cloudstack.domain.OSType;
import org.jclouds.cloudstack.domain.Template;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OperatingSystem.Builder;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.util.ComputeServiceUtils;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class TemplateToOperatingSystem implements Function<Template, OperatingSystem> {
   // CentOS 5.2 (32-bit)
   public static final Pattern DEFAULT_PATTERN = Pattern.compile(".* ([0-9.]+) ?\\(.*");

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Supplier<Map<Long, OSType>> osTypes;
   private final Supplier<Map<Long, String>> osCategories;
   private final Map<OsFamily, Map<String, String>> osVersionMap;

   @Inject
   public TemplateToOperatingSystem(@Memoized Supplier<Map<Long, OSType>> osTypes,
         @Memoized Supplier<Map<Long, String>> osCategories, Map<OsFamily, Map<String, String>> osVersionMap) {
      this.osTypes = checkNotNull(osTypes, "osTypes");
      this.osCategories = checkNotNull(osCategories, "osCategories");
      this.osVersionMap = checkNotNull(osVersionMap, "osVersionMap");
   }

   public OperatingSystem apply(Template from) {
      Builder builder = OperatingSystem.builder().description(from.getOSType());

      OSType type = osTypes.get().get(from.getOSTypeId());
      if (type == null) {
         logger.warn("type for template %s not found in %s", from, osTypes.get());
         return builder.build();
      }
      builder.description(type.getDescription());
      builder.is64Bit(type.getDescription().indexOf("64-bit") != -1);
      String osCategory = osCategories.get().get(type.getOSCategoryId());
      if (osCategory == null) {
         logger.warn("category for OSType %s not found in %s", type, osCategories.get());
         return builder.build();
      }
      builder.name(osCategory);
      OsFamily family = OsFamily.fromValue(osCategory.toLowerCase());
      builder.family(family);
      Matcher matcher = DEFAULT_PATTERN.matcher(type.getDescription());
      if (matcher.find()) {
         builder.version(ComputeServiceUtils.parseVersionOrReturnEmptyString(family, matcher.group(1), osVersionMap));
      }
      return builder.build();
   }
}
