package org.jclouds.glesys.reference;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jclouds.compute.domain.ComputeMetadata;

/**
 * Configuration properties and constants in GleSYS connections.
 * 
 * @author Adam Lowe
 */
public class GleSYSConstants {

   public static final Pattern JCLOUDS_ID_TO_PLATFORM = Pattern.compile("([a-zA-Z]+) .*");
   
   public static String getPlatform(ComputeMetadata jcloudsObject) {
      checkNotNull(jcloudsObject, "jcloudsObject");
      Matcher matcher = JCLOUDS_ID_TO_PLATFORM.matcher(jcloudsObject.getId());
      if (!matcher.matches()) {
         throw new IllegalArgumentException(jcloudsObject.getId() + " not a GleSYS platform-based id!");
      }
      return matcher.group(1);
   }
}
