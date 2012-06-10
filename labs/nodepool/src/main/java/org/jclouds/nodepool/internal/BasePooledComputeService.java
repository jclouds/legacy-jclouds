package org.jclouds.nodepool.internal;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.transform;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.RunScriptOnNodesException;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;
import org.jclouds.nodepool.PooledComputeService;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * A base class for {@link PooledComputeService}, takes care of keeping (not changing assignments)
 * and of everything that does not change the pool.
 * 
 * @author David Alves
 * 
 */
public abstract class BasePooledComputeService implements PooledComputeService {

   protected final ComputeService backingComputeService;
   protected final String poolGroupName;

   // assignments of nodes to group names
   protected final Multimap<String, NodeMetadata> assignments = HashMultimap.create();

   protected final AtomicBoolean started = new AtomicBoolean(false);

   public BasePooledComputeService(ComputeService backingComputeService, String poolGroupNamePrefix) {
      this.backingComputeService = backingComputeService;
      this.poolGroupName = poolGroupNamePrefix;
   }

   /**
    * Checks which nodes match the users predicate and builds a predicate that returns true to their
    * specific ids.
    * 
    * @param filter
    * @return
    */
   private Predicate<NodeMetadata> transformUserPredicateSpecificIdPredicate(Predicate<NodeMetadata> filter) {
      Iterable<Map.Entry<String, NodeMetadata>> relevantAssginemnts = filterAssignmentsBasedOnUserPredicate(filter);
      final Set<String> ids = Sets.newHashSet();
      for (Map.Entry<String, NodeMetadata> assignment : relevantAssginemnts) {
         ids.add(assignment.getValue().getId());
      }
      return new Predicate<NodeMetadata>() {
         @Override
         public boolean apply(NodeMetadata input) {
            return ids.contains(input.getId());
         }

      };
   }

   @Override
   public boolean isStarted() {
      return started.get();
   }

   // TODO this is n^2 expensive. s
   private Map<? extends NodeMetadata, ExecResponse> transformBackendExecutionMapIntoFrontend(
            Map<? extends NodeMetadata, ExecResponse> backendMap) {
      Map<NodeMetadata, ExecResponse> frontendMap = Maps.newHashMapWithExpectedSize(backendMap.size());
      for (Map.Entry<? extends NodeMetadata, ExecResponse> entry : backendMap.entrySet()) {
         Map.Entry<String, NodeMetadata> assignmentEntry = findAssigmentEntry(entry.getKey().getId());
         frontendMap.put(new PoolNodeMetadata(assignmentEntry.getValue(), assignmentEntry.getKey()), entry.getValue());
      }
      return frontendMap;
   }

   protected Map.Entry<String, NodeMetadata> findAssigmentEntry(final String id) {
      // TODO reverse lookup data structure would be faster but will pools be that big ?
      return find(assignments.entries(), new Predicate<Map.Entry<String, NodeMetadata>>() {
         @Override
         public boolean apply(Entry<String, NodeMetadata> entry) {
            return entry.getValue().getId().equals(id);
         }
      });
   }

   /**
    * Because a lot of predicates are based on group info we need that to check wether the predicate
    * matches.
    */
   protected Iterable<Map.Entry<String, NodeMetadata>> filterAssignmentsBasedOnUserPredicate(
            final Predicate<NodeMetadata> userFilter) {
      return filter(assignments.entries(), new Predicate<Map.Entry<String, NodeMetadata>>() {
         @Override
         public boolean apply(Entry<String, NodeMetadata> input) {
            return userFilter.apply(new PoolNodeMetadata(input.getValue(), input.getKey()));
         }
      });
   }

   @Override
   public NodeMetadata getNodeMetadata(String id) {
      checkState(started.get(), "pool is not started");
      Map.Entry<String, NodeMetadata> assigmentEntry = findAssigmentEntry(id);
      return new PoolNodeMetadata(assigmentEntry.getValue(), assigmentEntry.getKey());
   }

   @Override
   public Map<? extends NodeMetadata, ExecResponse> runScriptOnNodesMatching(Predicate<NodeMetadata> filter,
            String runScript) throws RunScriptOnNodesException {
      checkState(started.get(), "pool is not started");
      return transformBackendExecutionMapIntoFrontend(backingComputeService.runScriptOnNodesMatching(
               transformUserPredicateSpecificIdPredicate(filter), runScript));
   }

   @Override
   public Map<? extends NodeMetadata, ExecResponse> runScriptOnNodesMatching(Predicate<NodeMetadata> filter,
            Statement runScript) throws RunScriptOnNodesException {
      checkState(started.get(), "pool is not started");
      return transformBackendExecutionMapIntoFrontend(backingComputeService.runScriptOnNodesMatching(
               transformUserPredicateSpecificIdPredicate(filter), runScript));
   }

   @Override
   public Map<? extends NodeMetadata, ExecResponse> runScriptOnNodesMatching(Predicate<NodeMetadata> filter,
            String runScript, RunScriptOptions options) throws RunScriptOnNodesException {
      checkState(started.get(), "pool is not started");
      return transformBackendExecutionMapIntoFrontend(backingComputeService.runScriptOnNodesMatching(
               transformUserPredicateSpecificIdPredicate(filter), runScript, options));
   }

   @Override
   public Map<? extends NodeMetadata, ExecResponse> runScriptOnNodesMatching(Predicate<NodeMetadata> filter,
            Statement runScript, RunScriptOptions options) throws RunScriptOnNodesException {
      checkState(started.get(), "pool is not started");
      return transformBackendExecutionMapIntoFrontend(backingComputeService.runScriptOnNodesMatching(
               transformUserPredicateSpecificIdPredicate(filter), runScript, options));
   }

   @Override
   public Set<? extends ComputeMetadata> listNodes() {
      checkState(started.get(), "pool is not started");
      return Sets.newHashSet(transform(assignments.entries(),
               new Function<Map.Entry<String, NodeMetadata>, PoolNodeMetadata>() {
                  @Override
                  public PoolNodeMetadata apply(Map.Entry<String, NodeMetadata> input) {
                     return new PoolNodeMetadata(input.getValue(), input.getKey());
                  }
               }));
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   @Override
   public Set<? extends NodeMetadata> listNodesDetailsMatching(Predicate filter) {
      checkState(started.get(), "pool is not started");
      return Sets.newHashSet(transform(filterAssignmentsBasedOnUserPredicate(filter),
               new Function<Map.Entry<String, NodeMetadata>, NodeMetadata>() {
                  @Override
                  public NodeMetadata apply(Entry<String, NodeMetadata> input) {
                     return new PoolNodeMetadata(input.getValue(), input.getKey());
                  }
               }));
   }

   @Override
   public void rebootNodesMatching(final Predicate<NodeMetadata> filter) {
      checkState(started.get(), "pool is not started");
      backingComputeService.rebootNodesMatching(transformUserPredicateSpecificIdPredicate(filter));
   }

   @Override
   public void resumeNodesMatching(Predicate<NodeMetadata> filter) {
      checkState(started.get(), "pool is not started");
      backingComputeService.resumeNodesMatching(transformUserPredicateSpecificIdPredicate(filter));
   }

   @Override
   public void suspendNodesMatching(Predicate<NodeMetadata> filter) {
      checkState(started.get(), "pool is not started");
      backingComputeService.suspendNodesMatching(transformUserPredicateSpecificIdPredicate(filter));
   }

   @Override
   public ComputeServiceContext getContext() {
      // not sure this is enough, should we have our own?
      return backingComputeService.getContext();
   }

   // we ignore user provided templates and options

   @Override
   public Set<? extends NodeMetadata> createNodesInGroup(String group, int count, Template template)
            throws RunNodesException {
      return createNodesInGroup(group, count);
   }

   @Override
   public Set<? extends NodeMetadata> createNodesInGroup(String group, int count, TemplateOptions templateOptions)
            throws RunNodesException {
      return createNodesInGroup(group, count);
   }

   // set of direct delegation methods

   @Override
   public TemplateBuilder templateBuilder() {
      return backingComputeService.templateBuilder();
   }

   @Override
   public TemplateOptions templateOptions() {
      return backingComputeService.templateOptions();
   }

   @Override
   public Set<? extends Hardware> listHardwareProfiles() {
      return backingComputeService.listHardwareProfiles();
   }

   @Override
   public Set<? extends Image> listImages() {
      return backingComputeService.listImages();

   }

   @Override
   public Image getImage(String id) {
      return backingComputeService.getImage(id);
   }

   @Override
   public Set<? extends Location> listAssignableLocations() {
      return backingComputeService.listAssignableLocations();
   }

   @Override
   public void suspendNode(String id) {
      backingComputeService.suspendNode(id);
   }

   @Override
   public void resumeNode(String id) {
      backingComputeService.resumeNode(id);
   }

   @Override
   public void rebootNode(String id) {
      backingComputeService.rebootNode(id);
   }

   @Override
   public ExecResponse runScriptOnNode(String id, Statement runScript, RunScriptOptions options) {
      return backingComputeService.runScriptOnNode(id, runScript, options);
   }

   @Override
   public ListenableFuture<ExecResponse> submitScriptOnNode(String id, Statement runScript, RunScriptOptions options) {
      return backingComputeService.submitScriptOnNode(id, runScript, options);
   }

   @Override
   public ExecResponse runScriptOnNode(String id, Statement runScript) {
      return backingComputeService.runScriptOnNode(id, runScript);
   }

   @Override
   public ExecResponse runScriptOnNode(String id, String runScript, RunScriptOptions options) {
      return backingComputeService.runScriptOnNode(id, runScript, options);
   }

   @Override
   public ExecResponse runScriptOnNode(String id, String runScript) {
      return backingComputeService.runScriptOnNode(id, runScript);
   }

   @Override
   public Optional<ImageExtension> getImageExtension() {
      return backingComputeService.getImageExtension();
   }

}
