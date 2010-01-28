package org.jclouds.compute.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.NoSuchElementException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.domain.internal.TemplateImpl;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;

/**
 * 
 * @author Adrian Cole
 */
@SuppressWarnings("unchecked")
public class TemplateBuilderImpl implements TemplateBuilder {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Map<String, ? extends Image> images;
   private final Map<String, ? extends Size> sizes;
   private final Map<String, ? extends Location> locations;
   private OsFamily os;
   private Architecture arch;
   private String locationId;
   private String imageId;
   private String sizeId;
   private String osDescription;
   private String imageVersion;
   private String imageName;

   private int minCores;
   private int minRam;

   private boolean biggest;
   private boolean fastest;

   private TemplateOptions options = TemplateOptions.NONE;

   @Inject
   public TemplateBuilderImpl(Map<String, ? extends Location> locations,
            Map<String, ? extends Image> images, Map<String, ? extends Size> sizes,
            Location defaultLocation) {
      this.locations = locations;
      this.images = images;
      this.sizes = sizes;
      this.locationId = defaultLocation.getId();
   }

   private final Predicate<ComputeMetadata> locationPredicate = new Predicate<ComputeMetadata>() {
      @Override
      public boolean apply(ComputeMetadata input) {
         boolean returnVal = true;
         if (locationId != null && input.getLocationId() != null)
            returnVal = locationId.equals(input.getLocationId());
         return returnVal;
      }
   };

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
   };

   private final Predicate<Image> osPredicate = new Predicate<Image>() {

      @Override
      public boolean apply(Image input) {
         boolean returnVal = true;
         if (os != null)
            returnVal = os.equals(input.getOsFamily());
         return returnVal;
      }

   };
   private final Predicate<Image> imageArchPredicate = new Predicate<Image>() {

      @Override
      public boolean apply(Image input) {
         boolean returnVal = true;
         if (arch != null)
            returnVal = arch.equals(input.getArchitecture());
         return returnVal;
      }

   };
   private final Predicate<Image> osDescriptionPredicate = new Predicate<Image>() {
      @Override
      public boolean apply(Image input) {
         boolean returnVal = true;
         if (osDescription != null) {
            if (input.getOsDescription() == null)
               returnVal = false;
            else
               returnVal = input.getOsDescription().matches(osDescription);
         }
         return returnVal;
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
               returnVal = input.getVersion().matches(imageVersion);
         }
         return returnVal;
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
               returnVal = input.getName().matches(imageName);
         }
         return returnVal;
      }
   };

   private final Predicate<Size> sizeIdPredicate = new Predicate<Size>() {
      @Override
      public boolean apply(Size input) {
         boolean returnVal = true;
         if (sizeId != null) {
            returnVal = sizeId.equals(input.getId());
            // match our input params so that the later predicates pass.
            if (returnVal) {
               fromSize(input);
            }
         }
         return returnVal;
      }
   };

   private final Predicate<Image> imagePredicate = Predicates.and(idPredicate, locationPredicate,
            osPredicate, imageArchPredicate, osDescriptionPredicate, imageVersionPredicate,
            imageNamePredicate);

   private final Predicate<Size> sizeArchPredicate = new Predicate<Size>() {
      @Override
      public boolean apply(Size input) {
         boolean returnVal = false;
         if (arch != null)
            returnVal = input.getSupportedArchitectures().contains(arch);
         return returnVal;
      }
   };

   private final Predicate<Size> sizeCoresPredicate = new Predicate<Size>() {
      @Override
      public boolean apply(Size input) {
         return input.getCores() >= TemplateBuilderImpl.this.minCores;
      }
   };

   private final Predicate<Size> sizeRamPredicate = new Predicate<Size>() {
      @Override
      public boolean apply(Size input) {
         return input.getRam() >= TemplateBuilderImpl.this.minRam;
      }
   };
   private final Predicate<Size> sizePredicate = Predicates.and(sizeIdPredicate, locationPredicate,
            sizeArchPredicate, sizeCoresPredicate, sizeRamPredicate);

   static final Ordering<Size> DEFAULT_SIZE_ORDERING = new Ordering<Size>() {
      public int compare(Size left, Size right) {
         return ComparisonChain.start().compare(left.getCores(), right.getCores()).compare(
                  left.getRam(), right.getRam()).compare(left.getDisk(), right.getDisk()).result();
      }
   };
   static final Ordering<Size> BY_CORES_ORDERING = new Ordering<Size>() {
      public int compare(Size left, Size right) {
         return Ints.compare(left.getCores(), right.getCores());
      }
   };
   static final Ordering<Image> DEFAULT_IMAGE_ORDERING = new Ordering<Image>() {
      public int compare(Image left, Image right) {
         return ComparisonChain.start().compare(left.getOsDescription(), right.getOsDescription())
                  .compare(left.getVersion(), right.getVersion()).result();
      }
   };

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder fromTemplate(Template template) {
      fromSize(template.getSize());
      fromImage(template.getImage());
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder fromSize(Size size) {
      if (size.getLocationId() != null)
         this.locationId = size.getLocationId();
      this.minCores = size.getCores();
      this.minRam = size.getRam();
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder fromImage(Image image) {
      if (image.getLocationId() != null)
         this.locationId = image.getLocationId();
      if (image.getOsFamily() != null)
         this.os = image.getOsFamily();
      if (image.getOsDescription() != null)
         this.osDescription = image.getOsDescription();
      if (image.getVersion() != null)
         this.imageVersion = image.getVersion();
      if (image.getArchitecture() != null)
         this.arch = image.getArchitecture();
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
      checkArgument(locations.get(checkNotNull(locationId, "locationId")) != null, "locationId "
               + locationId + " not configured in: " + locations.keySet());
      this.locationId = locationId;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder osFamily(OsFamily os) {
      this.os = os;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder architecture(Architecture architecture) {
      this.arch = architecture;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Template build() {
      logger.debug(">> searching params(%s)", this);
      Image image;
      try {
         image = DEFAULT_IMAGE_ORDERING.max(Iterables.filter(images.values(), imagePredicate));
      } catch (NoSuchElementException exception) {
         throw new NoSuchElementException("image didn't match: " + toString() + "\n" + images);
      }
      logger.debug("<<   matched image(%s)", image);
      // ensure we have an architecture matching
      this.arch = image.getArchitecture();

      Ordering<Size> sizeOrdering = DEFAULT_SIZE_ORDERING;
      if (!biggest)
         sizeOrdering = sizeOrdering.reverse();
      if (fastest)
         sizeOrdering = Ordering.compound(ImmutableList.of(BY_CORES_ORDERING, sizeOrdering));
      Size size;
      try {
         size = sizeOrdering.max(Iterables.filter(sizes.values(), sizePredicate));
      } catch (NoSuchElementException exception) {
         throw new NoSuchElementException("size didn't match: " + toString() + "\n" + sizes);
      }
      logger.debug("<<   matched size(%s)", size);
      Location location = locations.get(locationId);
      logger.debug("<<   matched location(%s)", location);
      return new TemplateImpl(image, size, location, options);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder imageId(String imageId) {
      this.imageId = imageId;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder imageDescriptionMatches(String descriptionRegex) {
      this.imageName = descriptionRegex;
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
   public TemplateBuilder minCores(int minCores) {
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
   public TemplateBuilder osDescriptionMatches(String osDescriptionRegex) {
      this.osDescription = osDescriptionRegex;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder sizeId(String sizeId) {
      this.sizeId = sizeId;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder options(TemplateOptions options) {
      this.options = checkNotNull(options, "options");
      return this;
   }

   @Override
   public String toString() {
      return "[arch=" + arch + ", biggest=" + biggest + ", fastest=" + fastest
               + ", imageDescription=" + imageName + ", imageId=" + imageId + ", imageVersion="
               + imageVersion + ", location=" + locationId + ", minCores=" + minCores + ", minRam="
               + minRam + ", os=" + os + ", osDescription=" + osDescription + ", sizeId=" + sizeId
               + "]";
   }

}
