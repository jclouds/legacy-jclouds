package org.jclouds.aws.config;

import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.location.Provider;
import org.jclouds.location.Region;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

@Singleton
public class ProvideRegionsViaProperties implements javax.inject.Provider<Map<String, URI>> {

   private final Injector injector;

   @Inject
   ProvideRegionsViaProperties(Injector injector) {
      this.injector = injector;
   }

   @Singleton
   @Region
   @Override
   public Map<String, URI> get() {
      try {
         String regionString = injector.getInstance(Key.get(String.class, Names.named(PROPERTY_REGIONS)));
         Builder<String, URI> regions = ImmutableMap.<String, URI> builder();
         for (String region : Splitter.on(',').split(regionString)) {
            regions.put(
                  region,
                  URI.create(injector.getInstance(Key.get(String.class,
                        Names.named(Constants.PROPERTY_ENDPOINT + "." + region)))));
         }
         return regions.build();
      } catch (ConfigurationException e) {
         // this happens if regions property isn't set
         // services not run by AWS may not have regions, so this is ok.
         return ImmutableMap.of(injector.getInstance(Key.get(String.class, Provider.class)),
               injector.getInstance(Key.get(URI.class, Provider.class)));
      }
   }

}