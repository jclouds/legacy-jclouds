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
package org.jclouds.cloudwatch.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * @see <a href="http://docs.aws.amazon.com/AmazonCloudWatch/latest/APIReference/API_MetricAlarm.html" />
 *
 * @author Jeremy Whitlock
 */
@Beta
public class Alarm {

   public static enum State {
      ALARM,
      INSUFFICIENT_DATA,
      OK,
      UNRECOGNIZED;

      public static State fromValue(String value) {
         try {
            return State.valueOf(checkNotNull(value, "value"));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   private final boolean areActionsEnabled;
   private final Set<String> alarmActions;
   private final String alarmARN;
   private final Date alarmConfigurationUpdatedTimestamp;
   private final String alarmDescription;
   private final String alarmName;
   private final ComparisonOperator comparisonOperator;
   private final Set<Dimension> dimensions;
   private final int evaluationPeriods;
   private final Set<String> insufficientDataActions;
   private final String metricName;
   private final String namespace;
   private final Set<String> okActions;
   private final int period;
   private final String stateReason;
   private final Optional<String> stateReasonData;
   private final Date stateUpdatedTimestamp;
   private final State state;
   private final Statistics statistic;
   private final double threshold;
   private final Optional<Unit> unit;

   public Alarm(boolean areActionsEnabled, Set<String> alarmActions, String alarmARN,
                Date alarmConfigurationUpdatedTimestamp, String alarmDescription, String alarmName,
                ComparisonOperator comparisonOperator, Set<Dimension> dimensions, int evaluationPeriods,
                Set<String> insufficientDataActions, String metricName, String namespace, Set<String> okActions,
                int period, String stateReason, Optional<String> stateReasonData, Date stateUpdatedTimestamp,
                State state, Statistics statistic, double threshold, Optional<Unit> unit) {
      this.alarmName = checkNotNull(alarmName, "alarmName");
      this.areActionsEnabled = checkNotNull(areActionsEnabled, "actionsEnabled for %s", alarmName);
      this.alarmActions = checkNotNull(alarmActions, "alarmActions for %s", alarmName);
      this.alarmARN = checkNotNull(alarmARN, "alarmArn for %s", alarmName);
      this.alarmConfigurationUpdatedTimestamp = checkNotNull(alarmConfigurationUpdatedTimestamp,
                                                             "alarmConfigurationUpdatedTimestamp for %s", alarmName);
      this.alarmDescription = checkNotNull(alarmDescription, "alarmDescription for %s", alarmName);
      this.comparisonOperator = checkNotNull(comparisonOperator, "comparisonOperator for %s", alarmName);
      checkArgument(comparisonOperator != ComparisonOperator.UNRECOGNIZED, "comparisonOperator unrecognized");
      this.dimensions = checkNotNull(dimensions, "dimensions for %s", alarmName);
      this.evaluationPeriods = checkNotNull(evaluationPeriods, "evaluationPeriods for %s", alarmName);
      this.insufficientDataActions = checkNotNull(insufficientDataActions, "insufficientDataActions for %s", alarmName);
      this.metricName = checkNotNull(metricName, "metricName for %s", alarmName);
      this.namespace = checkNotNull(namespace, "namespace for %s", alarmName);
      this.okActions = checkNotNull(okActions, "okActions for %s", alarmName);
      this.period = checkNotNull(period, "period for %s", alarmName);
      this.stateReason = checkNotNull(stateReason, "stateReason for %s", alarmName);
      this.stateReasonData = checkNotNull(stateReasonData, "stateReasonData for %s", alarmName);
      this.stateUpdatedTimestamp = checkNotNull(stateUpdatedTimestamp, "stateUpdatedTimestamp for %s", alarmName);
      this.state = checkNotNull(state, "state for %s", alarmName);
      checkArgument(state != State.UNRECOGNIZED, "state unrecognized");
      this.statistic = checkNotNull(statistic, "statistic for %s", alarmName);
      checkArgument(statistic != Statistics.UNRECOGNIZED, "statistic unrecognized");
      this.threshold = checkNotNull(threshold, "threshold for %s", alarmName);
      this.unit = checkNotNull(unit, "unit for %s", alarmName);
      if (unit.isPresent()) {
         checkArgument(unit.get() != Unit.UNRECOGNIZED, "unit unrecognized");
      }
   }

   /**
    * return whether actions are enabled if the alarm state changes
    */
   public boolean areActionsEnabled() {
      return areActionsEnabled;
   }

   /**
    * return list of actions to perform when the alarm state changes to {@link org.jclouds.cloudwatch.domain.Alarm.State#ALARM} from any other state
    */
   public Set<String> getAlarmActions() {
      return alarmActions;
   }

   /**
    * return the Amazon Resource Name (ARN) of the alarm
    */
   public String getAlarmARN() {
      return alarmARN;
   }

   /**
    * return the date timestamp of when the alarm was last updated
    */
   public Date getAlarmConfigurationUpdatedTimestamp() {
      return alarmConfigurationUpdatedTimestamp;
   }

   /**
    * return the description of the alarm
    */
   public String getAlarmDescription() {
      return alarmDescription;
   }

   /**
    * return the name of the alarm
    */
   public String getAlarmName() {
      return alarmName;
   }

   /**
    * return the arithmetic operation to use when comparing the specified statistic and threshold
    */
   public ComparisonOperator getComparisonOperator() {
      return comparisonOperator;
   }

   /**
    * return the list of dimensions associated with the alarm's associated metric
    */
   public Set<Dimension> getDimensions() {
      return dimensions;
   }

   /**
    * return the number of periods over which data is compared to the specified threshold
    */
   public int getEvaluationPeriods() {
      return evaluationPeriods;
   }

   /**
    * return the list of actions to execute when this alarm transitions into an {@link org.jclouds.cloudwatch.domain.Alarm.State#INSUFFICIENT_DATA} state
    * from any other state
    */
   public Set<String> getInsufficientDataActions() {
      return insufficientDataActions;
   }

   /**
    * return the name of the alarm's metric
    */
   public String getMetricName() {
      return metricName;
   }

   /**
    * return the namespace of alarm's associated metric
    */
   public String getNamespace() {
      return namespace;
   }

   /**
    * return the list of actions to execute when this alarm transitions into an {@link org.jclouds.cloudwatch.domain.Alarm.State#OK} state from any other
    * state
    */
   public Set<String> getOkActions() {
      return okActions;
   }

   /**
    * return the period in seconds over which the statistic is applied
    */
   public int getPeriod() {
      return period;
   }

   /**
    * return the human-readable explanation for the alarm's state
    */
   public String getStateReason() {
      return stateReason;
   }

   /**
    * return the explanation for the alarm's state in machine-readable JSON format
    */
   public Optional<String> getStateReasonData() {
      return stateReasonData;
   }

   /**
    * return the time stamp of the last update to the alarm's state
    */
   public Date getStateUpdatedTimestamp() {
      return stateUpdatedTimestamp;
   }

   /**
    * return the state value for the alarm
    */
   public State getState() {
      return state;
   }

   /**
    * return the statistic to apply to the alarm's associated metric
    */
   public Statistics getStatistic() {
      return statistic;
   }

   /**
    * return the value against which the specified statistic is compared
    */
   public double getThreshold() {
      return threshold;
   }

   /**
    * return the unit of the alarm's associated metric
    */
   public Optional<Unit> getUnit() {
      return unit;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(alarmActions, alarmARN, alarmConfigurationUpdatedTimestamp, alarmDescription, alarmName,
                              areActionsEnabled, comparisonOperator, dimensions, evaluationPeriods,
                              insufficientDataActions, metricName, namespace, okActions, period, stateReason,
                              stateReasonData, stateUpdatedTimestamp, state, statistic, threshold, unit);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Alarm other = (Alarm) obj;
      return equal(this.alarmActions, other.alarmActions) &&
            equal(this.alarmARN, other.alarmARN) &&
            equal(this.alarmConfigurationUpdatedTimestamp, other.alarmConfigurationUpdatedTimestamp) &&
            equal(this.alarmDescription, other.alarmDescription) &&
            equal(this.alarmName, other.alarmName) &&
            equal(this.areActionsEnabled, other.areActionsEnabled) &&
            equal(this.comparisonOperator, other.comparisonOperator) &&
            equal(this.dimensions, other.dimensions) &&
            equal(this.evaluationPeriods, other.evaluationPeriods) &&
            equal(this.insufficientDataActions, other.insufficientDataActions) &&
            equal(this.metricName, other.metricName) &&
            equal(this.namespace, other.namespace) &&
            equal(this.okActions, other.okActions) &&
            equal(this.period, other.period) &&
            equal(this.stateReason, other.stateReason) &&
            equal(this.stateReasonData, other.stateReasonData) &&
            equal(this.stateUpdatedTimestamp, other.stateUpdatedTimestamp) &&
            equal(this.state, other.state) &&
            equal(this.statistic, other.statistic) &&
            equal(this.threshold, other.threshold) &&
            equal(this.unit, other.unit);
   }

   @Override
   public String toString() {
      return toStringHelper(this)
                    .add("alarmActions", alarmActions)
                    .add("alarmARN", alarmARN)
                    .add("alarmConfigurationUpdateTimestamp", alarmConfigurationUpdatedTimestamp)
                    .add("alarmDescription", alarmDescription)
                    .add("alarmName", alarmName)
                    .add("areActionsEnabled", areActionsEnabled)
                    .add("comparisonOperator", comparisonOperator)
                    .add("dimensions", dimensions)
                    .add("evaluationPeriods", evaluationPeriods)
                    .add("insufficientDataActions", insufficientDataActions)
                    .add("metricName", metricName)
                    .add("namespace", namespace)
                    .add("okActions", okActions)
                    .add("period", period)
                    .add("stateReason", stateReason)
                    .add("stateReasonData", stateReasonData.orNull())
                    .add("stateUpdatedTimestamp", stateUpdatedTimestamp)
                    .add("state", state)
                    .add("statistic", statistic)
                    .add("threshold", threshold)
                    .add("unit", unit.orNull()).toString();
   }

}
