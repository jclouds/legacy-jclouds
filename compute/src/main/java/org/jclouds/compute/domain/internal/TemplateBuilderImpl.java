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
package org.jclouds.compute.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Predicates.and;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.size;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Iterables.tryFind;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.jclouds.compute.util.ComputeServiceUtils.getCoresAndSpeed;
import static org.jclouds.compute.util.ComputeServiceUtils.getSpace;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.domain.TemplateBuilderSpec;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Doubles;

/**
 * 
 * @author Adrian Cole
 */
public class TemplateBuilderImpl implements TemplateBuilder {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final Supplier<Set<? extends Image>> images;
   protected final Supplier<Set<? extends Hardware>> hardwares;
   protected final Supplier<Set<? extends Location>> locations;
   protected final Supplier<Location> defaultLocation;
   protected final Provider<TemplateOptions> optionsProvider;
   protected final Provider<TemplateBuilder> defaultTemplateProvider;

   @VisibleForTesting
   protected Location location;
   @VisibleForTesting
   protected String imageId;
   @VisibleForTesting
   protected String hardwareId;
   @VisibleForTesting
   protected String hypervisor;
   @VisibleForTesting
   protected String imageVersion;
   @VisibleForTesting
   protected OsFamily osFamily;
   @VisibleForTesting
   protected String osVersion;
   @VisibleForTesting
   protected Boolean os64Bit;
   @VisibleForTesting
   protected String osName;
   @VisibleForTesting
   protected String osDescription;
   @VisibleForTesting
   protected String osArch;
   @VisibleForTesting
   protected String imageName;
   @VisibleForTesting
   protected String imageDescription;
   @VisibleForTesting
   protected Predicate<Image> imagePredicate;
   @VisibleForTesting
   protected double minCores;
   @VisibleForTesting
   protected int minRam;
   @VisibleForTesting
   protected double minDisk;
   @VisibleForTesting
   protected boolean biggest;
   @VisibleForTesting
   protected boolean fastest;
   @VisibleForTesting
   protected TemplateOptions options;

   @Inject
   protected TemplateBuilderImpl(@Memoized Supplier<Set<? extends Location>> locations,
         @Memoized Supplier<Set<? extends Image>> images, @Memoized Supplier<Set<? extends Hardware>> hardwares,
         Supplier<Location> defaultLocation2, @Named("DEFAULT") Provider<TemplateOptions> optionsProvider,
         @Named("DEFAULT") Provider<TemplateBuilder> defaultTemplateProvider) {
      this.locations = locations;
      this.images = images;
      this.hardwares = hardwares;
      this.defaultLocation = defaultLocation2;
      this.optionsProvider = optionsProvider;
      this.defaultTemplateProvider = defaultTemplateProvider;
   }

   static Predicate<Hardware> supportsImagesPredicate(final Iterable<? extends Image> images) {
      return new Predicate<Hardware>() {
         @Override
         public boolean apply(final Hardware hardware) {
            return Iterables.any(images, new Predicate<Image>() {

               @Override
               public boolean apply(Image input) {
                  return hardware.supportsImage().apply(input);
               }

               @Override
               public String toString() {
                  return "hardware(" + hardware + ").supportsImage()";
               }

            });

         }

      };
   } 

   final Predicate<ComputeMetadata> locationPredicate = new NullEqualToIsParentOrIsGrandparentOfCurrentLocation(new Supplier<Location>(){

      @Override
      public Location get() {
         return location;
      }
      
   });

   private final Predicate<Image> idPredicate = new Predicate<Image>() {
      @Override
      public boolean apply(Image input) {
         boolean returnVal = true;
         if (imageId != null) {
            returnVal = imageId.equals(input.getId());
            // match our input params so that the later predicates pass.
            if (returnVal) {
               fromImage(input);
            }
         }
         return returnVal;
      }

      @Override
      public String toString() {
         return "imageId(" + imageId + ")";
      }
   };

   private final Predicate<OperatingSystem> osFamilyPredicate = new Predicate<OperatingSystem>() {

      @Override
      public boolean apply(OperatingSystem input) {
         boolean returnVal = true;
         if (osFamily != null)
            returnVal = osFamily.equals(input.getFamily());
         return returnVal;
      }

      @Override
      public String toString() {
         return "osFamily(" + osFamily + ")";
      }
   };

   private final Predicate<OperatingSystem> osNamePredicate = new Predicate<OperatingSystem>() {
      @Override
      public boolean apply(OperatingSystem input) {
         boolean returnVal = true;
         if (osName != null) {
            if (input.getName() == null)
               returnVal = false;
            else
               returnVal = input.getName().contains(osName) || input.getName().matches(osName);
         }
         return returnVal;
      }

      @Override
      public String toString() {
         return "osName(" + osName + ")";
      }
   };

   private final Predicate<OperatingSystem> osDescriptionPredicate = new Predicate<OperatingSystem>() {
      @Override
      public boolean apply(OperatingSystem input) {
         boolean returnVal = true;
         if (osDescription != null) {
            if (input.getDescription() == null)
               returnVal = false;
            else
               returnVal = input.getDescription().contains(osDescription)
                     || input.getDescription().matches(osDescription);
         }
         return returnVal;
      }

      @Override
      public String toString() {
         return "osDescription(" + osDescription + ")";
      }
   };

   private final Predicate<OperatingSystem> osVersionPredicate = new Predicate<OperatingSystem>() {
      @Override
      public boolean apply(OperatingSystem input) {
         boolean returnVal = true;
         if (osVersion != null) {
            if (input.getVersion() == null)
               returnVal = false;
            else
               returnVal = input.getVersion().contains(osVersion) || input.getVersion().matches(osVersion);
         }
         return returnVal;
      }

      @Override
      public String toString() {
         return "osVersion(" + osVersion + ")";
      }
   };

   private final Predicate<OperatingSystem> os64BitPredicate = new Predicate<OperatingSystem>() {
      @Override
      public boolean apply(OperatingSystem input) {
         boolean returnVal = true;
         if (os64Bit != null) {
            if (os64Bit)
               return input.is64Bit();
            else
               return !input.is64Bit();
         }
         return returnVal;
      }

      @Override
      public String toString() {
         return "os64Bit(" + os64Bit + ")";
      }
   };

   private final Predicate<OperatingSystem> osArchPredicate = new Predicate<OperatingSystem>() {
      @Override
      public boolean apply(OperatingSystem input) {
         boolean returnVal = true;
         if (osArch != null) {
            if (input.getArch() == null)
               returnVal = false;
            else
               returnVal = input.getArch().contains(osArch) || input.getArch().matches(osArch);
         }
         return returnVal;
      }

      @Override
      public String toString() {
         return "osArch(" + osArch + ")";
      }
   };

   private final Predicate<Image> imageVersionPredicate = new Predicate<Image>() {
      @Override
      public boolean apply(Image input) {
         boolean returnVal = true;
         if (imageVersion != null) {
            if (input.getVersion() == null)
               returnVal = false;
            else
               returnVal = input.getVersion().contains(imageVersion) || input.getVersion().matches(imageVersion);
         }
         return returnVal;
      }

      @Override
      public String toString() {
         return "imageVersion(" + imageVersion + ")";
      }
   };

   private final Predicate<Image> imageNamePredicate = new Predicate<Image>() {
      @Override
      public boolean apply(Image input) {
         boolean returnVal = true;
         if (imageName != null) {
            if (input.getName() == null)
               returnVal = false;
            else
               returnVal = input.getName().equals(imageName) || input.getName().contains(imageName)
                        || input.getName().matches(imageName);
         }
         return returnVal;
      }

      @Override
      public String toString() {
         return "imageName(" + imageName + ")";
      }
   };
   
   private final Predicate<Image> imageDescriptionPredicate = new Predicate<Image>() {
      @Override
      public boolean apply(Image input) {
         boolean returnVal = true;
         if (imageDescription != null) {
            if (input.getDescription() == null)
               returnVal = false;
            else
               returnVal = input.getDescription().equals(imageDescription)
                     || input.getDescription().contains(imageDescription)
                     || input.getDescription().matches(imageDescription);
         }
         return returnVal;
      }

      @Override
      public String toString() {
         return "imageDescription(" + imageDescription + ")";
      }
   };
   
   private final Predicate<Hardware> hardwareIdPredicate = new Predicate<Hardware>() {
      @Override
      public boolean apply(Hardware input) {
         boolean returnVal = true;
         if (hardwareId != null) {
            returnVal = hardwareId.equals(input.getId());
            // match our input params so that the later predicates pass.
            if (returnVal) {
               fromHardware(input);
            }
         }
         return returnVal;
      }

      @Override
      public String toString() {
         return "hardwareId(" + hardwareId + ")";
      }
   };
   
   private final Predicate<Hardware> hypervisorPredicate = new Predicate<Hardware>() {
      @Override
      public boolean apply(Hardware input) {
         boolean returnVal = true;
         if (hypervisor != null) {
            if (input.getHypervisor() == null)
               returnVal = false;
            else
               returnVal = input.getHypervisor().contains(hypervisor)
                     || input.getHypervisor().matches(hypervisor);
         }
         return returnVal;
      }

      @Override
      public String toString() {
         return "hypervisorMatches(" + hypervisor + ")";
      }
   };

   private final Predicate<Hardware> hardwareCoresPredicate = new Predicate<Hardware>() {
      @Override
      public boolean apply(Hardware input) {
         double cores = getCores(input);
         return cores >= TemplateBuilderImpl.this.minCores;
      }

      @Override
      public String toString() {
         return "minCores(" + minCores + ")";
      }
   };

   private final Predicate<Hardware> hardwareDiskPredicate = new Predicate<Hardware>() {
      @Override
      public boolean apply(Hardware input) {
         return getSpace(input) >= TemplateBuilderImpl.this.minDisk;
      }

      @Override
      public String toString() {
         return "minDisk(" + minDisk + ")";
      }
   };

   private final Predicate<Hardware> hardwareRamPredicate = new Predicate<Hardware>() {
      @Override
      public boolean apply(Hardware input) {
         return input.getRam() >= TemplateBuilderImpl.this.minRam;
      }

      @Override
      public String toString() {
         return "minRam(" + minRam + ")";
      }
   };

   private Predicate<Hardware> buildHardwarePredicate() {
      List<Predicate<Hardware>> predicates = newArrayList();
      if (location != null)
         predicates.add(new Predicate<Hardware>() {

            @Override
            public boolean apply(Hardware input) {
               return locationPredicate.apply(input);
            }

            @Override
            public String toString() {
               return locationPredicate.toString();
            }
         });
      if (hypervisor != null)
         predicates.add(hypervisorPredicate);
      predicates.add(hardwareCoresPredicate);
      predicates.add(hardwareRamPredicate);
      predicates.add(hardwareDiskPredicate);

      // looks verbose, but explicit <Hardware> type needed for this to compile
      // properly
      Predicate<Hardware> hardwarePredicate = predicates.size() == 1 ? Iterables.<Predicate<Hardware>> get(predicates, 0)
            : Predicates.<Hardware> and(predicates);
      return hardwarePredicate;
   }
   
   static final Ordering<Hardware> DEFAULT_SIZE_ORDERING = new Ordering<Hardware>() {
      public int compare(Hardware left, Hardware right) {
         return ComparisonChain.start().compare(getCores(left), getCores(right)).compare(left.getRam(), right.getRam())
               .compare(getSpace(left), getSpace(right)).result();
      }
   };
   static final Ordering<Hardware> BY_CORES_ORDERING = new Ordering<Hardware>() {
      public int compare(Hardware left, Hardware right) {
         return Doubles.compare(getCoresAndSpeed(left), getCoresAndSpeed(right));
      }
   };
   static final Ordering<Image> DEFAULT_IMAGE_ORDERING = new Ordering<Image>() {
      public int compare(Image left, Image right) {
         return ComparisonChain.start()
               .compare(left.getName(), right.getName(), Ordering.<String> natural().nullsLast())
               .compare(left.getVersion(), right.getVersion(), Ordering.<String> natural().nullsLast())
               .compare(left.getDescription(), right.getDescription(), Ordering.<String> natural().nullsLast())
               .compare(left.getOperatingSystem().getName(), right.getOperatingSystem().getName(),//
                     Ordering.<String> natural().nullsLast())
               .compare(left.getOperatingSystem().getVersion(), right.getOperatingSystem().getVersion(),//
                     Ordering.<String> natural().nullsLast())
               .compare(left.getOperatingSystem().getDescription(), right.getOperatingSystem().getDescription(),//
                     Ordering.<String> natural().nullsLast())
               .compare(left.getOperatingSystem().getArch(), right.getOperatingSystem().getArch(),//
                     Ordering.<String> natural().nullsLast()).result();
      }
   };

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder fromTemplate(Template template) {
      location = template.getLocation();
      fromHardware(template.getHardware());
      fromImage(template.getImage());
      options(template.getOptions());
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder fromHardware(Hardware hardware) {
      if (currentLocationWiderThan(hardware.getLocation()))
         this.location = hardware.getLocation();
      this.minCores = getCores(hardware);
      this.minRam = hardware.getRam();
      this.minDisk = getSpace(hardware);
      this.hypervisor = hardware.getHypervisor();
      return this;
   }

   private boolean currentLocationWiderThan(Location location) {
      return this.location == null || (location != null && this.location.getScope().compareTo(location.getScope()) < 0);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder fromImage(Image image) {
      if (currentLocationWiderThan(image.getLocation()))
         this.location = image.getLocation();
      if (image.getOperatingSystem().getFamily() != null)
         this.osFamily = image.getOperatingSystem().getFamily();
      if (image.getName() != null)
         this.imageName = image.getName();
      if (image.getDescription() != null)
         this.imageDescription = String.format("^%s$", Pattern.quote(image.getDescription()));
      if (image.getOperatingSystem().getName() != null)
         this.osName = image.getOperatingSystem().getName();
      if (image.getOperatingSystem().getDescription() != null)
         this.osDescription = image.getOperatingSystem().getDescription();
      if (image.getVersion() != null)
         this.imageVersion = String.format("^%s$", Pattern.quote(image.getVersion()));
      if (image.getOperatingSystem().getVersion() != null)
         this.osVersion = image.getOperatingSystem().getVersion();
      this.os64Bit = image.getOperatingSystem().is64Bit();
      if (image.getOperatingSystem().getArch() != null)
         this.osArch = image.getOperatingSystem().getArch();
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder smallest() {
      this.biggest = false;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder biggest() {
      this.biggest = true;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder fastest() {
      this.fastest = true;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder locationId(final String locationId) {
      Set<? extends Location> locations = this.locations.get();
      try {
         this.location = find(locations, new Predicate<Location>() {

            @Override
            public boolean apply(Location input) {
               return input.getId().equals(locationId);
            }

            @Override
            public String toString() {
               return "locationId(" + locationId + ")";
            }

         });
      } catch (NoSuchElementException e) {
         throw new NoSuchElementException(format("location id %s not found in: %s", locationId, locations));
      }
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder osFamily(OsFamily os) {
      this.osFamily = os;
      return this;
   }

   private static final Function<Image, String> imageToId = new Function<Image, String>() {

      @Override
      public String apply(Image arg0) {
         return arg0.getId();
      }
      
   };
   

   private static final Function<Hardware, String> hardwareToId = new Function<Hardware, String>() {

      @Override
      public String apply(Hardware arg0) {
         return arg0.getId();
      }
      
   };
   /**
    * {@inheritDoc}
    */
   @Override
   public Template build() {
      if (nothingChangedExceptOptions()) {
         TemplateBuilder defaultTemplate = defaultTemplateProvider.get();
         if (options != null)
            defaultTemplate.options(options);
         return defaultTemplate.build();
      }

      if (options == null)
         options = optionsProvider.get();
      logger.debug(">> searching params(%s)", this);
      Set<? extends Image> images = getImages();
      checkState(images.size() > 0, "no images present!");
      Set<? extends Hardware> hardwaresToSearch = hardwares.get();
      checkState(hardwaresToSearch.size() > 0, "no hardware profiles present!");

      Image image = null;
      if (imageId != null) {
         image = findImageWithId(images);
         if (currentLocationWiderThan(image.getLocation()))
            this.location = image.getLocation();
      }
      
      Hardware hardware = null;
      if (hardwareId != null) {
         hardware = findHardwareWithId(hardwaresToSearch);
         if (currentLocationWiderThan(hardware.getLocation()))
            this.location = hardware.getLocation();
      }
      
      // if the user hasn't specified a location id, or an image or hardware
      // with location, let's search scoped to the implicit one
      if (location == null)
         location = defaultLocation.get();
      
      if (image == null) {
         Iterable<? extends Image> supportedImages = findSupportedImages(images);
         if (hardware == null)
            hardware = resolveHardware(hardwaresToSearch, supportedImages);
         image = resolveImage(hardware, supportedImages);
      } else {
         if (hardware == null)
            hardware = resolveHardware(hardwaresToSearch, ImmutableSet.of(image));
      }

      logger.debug("<<   matched image(%s) hardware(%s) location(%s)", image.getId(), hardware.getId(),
            location.getId());
      return new TemplateImpl(image, hardware, location, options);
   }

   private Iterable<? extends Image> findSupportedImages(Set<? extends Image> images) {
      Predicate<Image> imagePredicate = buildImagePredicate();
      Iterable<? extends Image> supportedImages = filter(images, imagePredicate);
      if (size(supportedImages) == 0) {
         throw throwNoSuchElementExceptionAfterLoggingImageIds(
               format("no image matched predicate: %s", imagePredicate), images);
      }
      return supportedImages;
   }

   private Image findImageWithId(Set<? extends Image> images) {
      Image image;
      // TODO: switch to GetImageStrategy in version 1.5
      image = tryFind(images, idPredicate).orNull();
      if (image == null)
         throwNoSuchElementExceptionAfterLoggingImageIds(format("%s not found", idPredicate), images);
      return image;
   }

   private Hardware findHardwareWithId(Set<? extends Hardware> hardwaresToSearch) {
      Hardware hardware;
      // TODO: switch to GetHardwareStrategy in version 1.5
      hardware = tryFind(hardwaresToSearch, hardwareIdPredicate).orNull();
      if (hardware == null)
         throw throwNoSuchElementExceptionAfterLoggingHardwareIds(format("%s not found", hardwareIdPredicate),
               hardwaresToSearch);
      return hardware;
   }

   protected NoSuchElementException throwNoSuchElementExceptionAfterLoggingImageIds(String message, Iterable<? extends Image> images) {
      NoSuchElementException exception = new NoSuchElementException(message);
      if (logger.isTraceEnabled())
         logger.warn(exception, "image ids that didn't match: %s", transform(images, imageToId));
      throw exception;
   }

   protected NoSuchElementException throwNoSuchElementExceptionAfterLoggingHardwareIds(String message, Iterable<? extends Hardware> hardwares) {
      NoSuchElementException exception = new NoSuchElementException(message);
      if (logger.isTraceEnabled())
         logger.warn(exception, "hardware ids that didn't match: %s", transform(hardwares, hardwareToId));
      throw exception;
   }

   protected Hardware resolveHardware(Set<? extends Hardware> hardwarel, final Iterable<? extends Image> images) {
      Ordering<Hardware> hardwareOrdering = hardwareSorter();
      
      Iterable<Predicate<Image>> supportsImagePredicates = Iterables.transform(hardwarel,
               new Function<Hardware, Predicate<Image>>() {

                  @Override
                  public Predicate<Image> apply(Hardware input) {
                     return input.supportsImage();
                  }

               });
      
      Predicate<Image> supportsImagePredicate = Iterables.size(supportsImagePredicates) == 1 ? Iterables
               .getOnlyElement(supportsImagePredicates) : Predicates.<Image>or(supportsImagePredicates);

      if (!Iterables.any(images, supportsImagePredicate)) {
         String message = format("no hardware profiles support images matching params: %s", supportsImagePredicate);
         throw throwNoSuchElementExceptionAfterLoggingHardwareIds(message, hardwarel);
      }

      Iterable<? extends Hardware> hardwareCompatibleWithOurImages = filter(hardwarel, supportsImagesPredicate(images));
      Predicate<Hardware> hardwarePredicate = buildHardwarePredicate();
      Hardware hardware;
      try {
         hardware = hardwareOrdering.max(filter(hardwareCompatibleWithOurImages, hardwarePredicate));
      } catch (NoSuchElementException exception) {
         String message = format("no hardware profiles match params: %s", hardwarePredicate);
         throw throwNoSuchElementExceptionAfterLoggingHardwareIds(message, hardwareCompatibleWithOurImages);
      }
      logger.trace("<<   matched hardware(%s)", hardware.getId());
      return hardware;
   }

   protected Ordering<Hardware> hardwareSorter() {
      Ordering<Hardware> hardwareOrdering = DEFAULT_SIZE_ORDERING;
      if (!biggest)
         hardwareOrdering = hardwareOrdering.reverse();
      if (fastest)
         hardwareOrdering = Ordering.compound(ImmutableList.of(BY_CORES_ORDERING, hardwareOrdering));
      return hardwareOrdering;
   }

   /**
    * 
    * @param hardware
    * @param supportedImages
    * @throws NoSuchElementException
    *            if there's no image that matches the predicate
    */
   protected Image resolveImage(final Hardware hardware, Iterable<? extends Image> supportedImages) {
      Predicate<Image> imagePredicate = new Predicate<Image>() {

         @Override
         public boolean apply(Image arg0) {
            return hardware.supportsImage().apply(arg0);
         }

         @Override
         public String toString() {
            return "hardware(" + hardware + ").supportsImage()";
         }
      };

      try {
         Iterable<? extends Image> matchingImages = filter(supportedImages, imagePredicate);
         if (logger.isTraceEnabled())
            logger.trace("<<   matched images(%s)", transform(matchingImages, imageToId));
         List<? extends Image> maxImages = multiMax(DEFAULT_IMAGE_ORDERING, matchingImages);
         if (logger.isTraceEnabled())
            logger.trace("<<   best images(%s)", transform(maxImages, imageToId));
         return maxImages.get(maxImages.size() - 1);
      } catch (NoSuchElementException exception) {
         throwNoSuchElementExceptionAfterLoggingImageIds(format("no image matched params: %s", toString()),
                  supportedImages);
         assert false;
         return null;
      }
   }
   
   /**
    * Like Ordering, but handle the case where there are multiple valid maximums
    */
   @SuppressWarnings("unchecked")
   @VisibleForTesting
   static <T, E extends T> List<E> multiMax(Comparator<T> ordering, Iterable<E> iterable) {
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
   protected Set<? extends Image> getImages() {
      return images.get();
   }

   private Predicate<Image> buildImagePredicate() {
      List<Predicate<Image>> predicates = newArrayList();
      if (location != null)
         predicates.add(new Predicate<Image>() {

            @Override
            public boolean apply(Image input) {
               return locationPredicate.apply(input);
            }

            @Override
            public String toString() {
               return locationPredicate.toString();
            }
         });

      final List<Predicate<OperatingSystem>> osPredicates = newArrayList();
      if (osFamily != null)
         osPredicates.add(osFamilyPredicate);
      if (osName != null)
         osPredicates.add(osNamePredicate);
      if (osDescription != null)
         osPredicates.add(osDescriptionPredicate);
      if (osVersion != null)
         osPredicates.add(osVersionPredicate);
      if (os64Bit != null)
         osPredicates.add(os64BitPredicate);
      if (osArch != null)
         osPredicates.add(osArchPredicate);
      if (osPredicates.size() > 0)
         predicates.add(new Predicate<Image>() {

            @Override
            public boolean apply(Image input) {
               return and(osPredicates).apply(input.getOperatingSystem());
            }

            @Override
            public String toString() {
               return and(osPredicates).toString();
            }

         });
      if (imageVersion != null)
         predicates.add(imageVersionPredicate);
      if (imageName != null)
         predicates.add(imageNamePredicate);
      if (imageDescription != null)
         predicates.add(imageDescriptionPredicate);
      if (imagePredicate != null)
         predicates.add(imagePredicate);

      // looks verbose, but explicit <Image> type needed for this to compile
      // properly
      Predicate<Image> imagePredicate = predicates.size() == 1 ? Iterables.<Predicate<Image>> get(predicates, 0)
            : Predicates.<Image> and(predicates);
      return imagePredicate;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder imageId(String imageId) {
      this.imageId = imageId;
      this.imageName = null;
      this.imageDescription = null;
      this.imagePredicate = null;
      this.imageVersion = null;
      this.osFamily = null;
      this.osName = null;
      this.osDescription = null;
      this.osVersion = null;
      this.os64Bit = null;
      this.osArch = null;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder imageNameMatches(String nameRegex) {
      this.imageName = nameRegex;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder imageDescriptionMatches(String descriptionRegex) {
      this.imageDescription = descriptionRegex;
      return this;
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder imageMatches(Predicate<Image> condition) {
      this.imagePredicate = condition;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder imageVersionMatches(String imageVersionRegex) {
      this.imageVersion = imageVersionRegex;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder osVersionMatches(String osVersionRegex) {
      this.osVersion = osVersionRegex;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder osArchMatches(String osArchitectureRegex) {
      this.osArch = osArchitectureRegex;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder minCores(double minCores) {
      this.minCores = minCores;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder minRam(int megabytes) {
      this.minRam = megabytes;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder minDisk(double gigabytes) {
      this.minDisk = gigabytes;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder osNameMatches(String osNameRegex) {
      this.osName = osNameRegex;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder osDescriptionMatches(String osDescriptionRegex) {
      this.osDescription = osDescriptionRegex;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder hardwareId(String hardwareId) {
      this.hardwareId = hardwareId;
      this.hypervisor = null;
      return this;
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder hypervisorMatches(String hypervisor) {
      this.hypervisor = hypervisor;
      return this;
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder options(TemplateOptions options) {
      this.options = optionsProvider.get();
      checkNotNull(options, "options").copyTo(this.options);
      return this;
   }

   @VisibleForTesting
   boolean nothingChangedExceptOptions() {
      return osFamily == null && location == null && imageId == null && hardwareId == null && hypervisor == null
            && osName == null && imagePredicate == null && osDescription == null && imageVersion == null
            && osVersion == null && osArch == null && os64Bit == null && imageName == null && imageDescription == null
            && minCores == 0 && minRam == 0 && minDisk == 0 && !biggest && !fastest;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder any() {
      return defaultTemplateProvider.get();
   }

   @Override
   public String toString() {
      return string().toString();
   }

   /**
    * @since 1.5
    */
   protected ToStringHelper string() {
      ToStringHelper toString = Objects.toStringHelper("").omitNullValues();
      if (biggest)
         toString.add("biggest", biggest);
      if (fastest)
         toString.add("fastest", fastest);
      toString.add("imageName", imageName);
      toString.add("imageDescription", imageDescription);
      toString.add("imageId", imageId);
      toString.add("imagePredicate", imagePredicate);
      toString.add("imageVersion", imageVersion);
      if (location != null)
         toString.add("locationId", location.getId());
      if (minCores >0) //TODO: make non-primitive
         toString.add("minCores", minCores);
      if (minRam >0) //TODO: make non-primitive
         toString.add("minRam", minRam);
      if (minRam >0) //TODO: make non-primitive
         toString.add("minRam", minRam);
      if (minDisk >0) //TODO: make non-primitive
         toString.add("minDisk", minDisk);
      toString.add("osFamily", osFamily);
      toString.add("osName", osName);
      toString.add("osDescription", osDescription);
      toString.add("osVersion", osVersion);
      toString.add("osArch", osArch);
      toString.add("os64Bit", os64Bit);
      toString.add("hardwareId", hardwareId);
      toString.add("hypervisor", hypervisor);
      return toString;
   }

   @Override
   public TemplateBuilder os64Bit(boolean is64Bit) {
      this.os64Bit = is64Bit;
      return this;
   }

   @Override
   public TemplateBuilder from(TemplateBuilderSpec spec) {
      return spec.copyTo(this, options != null ? options : (options = optionsProvider.get()));
   }

   @Override
   public TemplateBuilder from(String spec) {
      return from(TemplateBuilderSpec.parse(spec));
   }

}
