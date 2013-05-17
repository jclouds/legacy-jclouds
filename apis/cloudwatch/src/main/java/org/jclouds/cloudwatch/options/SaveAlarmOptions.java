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
package org.jclouds.cloudwatch.options;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.google.common.annotations.Beta;
import org.jclouds.cloudwatch.domain.ComparisonOperator;
import org.jclouds.cloudwatch.domain.Dimension;
import org.jclouds.cloudwatch.domain.Statistics;
import org.jclouds.cloudwatch.domain.Unit;
import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Options used to create/update an alarm.
 *
 * @see <a href="http://docs.aws.amazon.com/AmazonCloudWatch/latest/APIReference/API_PutMetricAlarm.html" />
 *
 * @author Jeremy Whitlock
 */
@Beta
public class SaveAlarmOptions extends BaseHttpRequestOptions {

   int alarmActionIndex = 1;
   int dimensionIndex = 1;
   int insufficientDataActionsIndex = 1;
   int okActionsIndex = 1;

   /**
    * Indicates whether or not actions should be executed during any changes to the alarm's state.
    *
    * @param actionsEnabled indicates whether or not actions should be executed during any changes to the alarm's state
    *
    * @return this {@code SaveAlarmOptions} object
    */
   public SaveAlarmOptions actionsEnabled(boolean actionsEnabled) {
      formParameters.put("ActionsEnabled", checkNotNull(actionsEnabled, "actionsEnabled").toString());
      return this;
   }

   /**
    * The list of actions to execute when this alarm transitions into an ALARM state from any other state.
    *
    * @param alarmActions the list of actions to execute when this alarm transitions into an ALARM state from any other
    *                     state
    *
    * @return this {@code SaveAlarmOptions} object
    */
   public SaveAlarmOptions alarmActions(Set<String> alarmActions) {
      for (String alarmAction : checkNotNull(alarmActions, "alarmActions")) {
         alarmAction(alarmAction);
      }
      return this;
   }

   /**
    * The action to execute when this alarm transitions into an ALARM state from any other state.
    *
    * @param alarmAction the actions to execute when this alarm transitions into an ALARM state from any other state
    *
    * @return this {@code SaveAlarmOptions} object
    */
   public SaveAlarmOptions alarmAction(String alarmAction) {
      checkArgument(alarmActionIndex <= 5, "maximum number of alarm actions is 5");
      formParameters.put("AlarmActions.member." + alarmActionIndex, checkNotNull(alarmAction, "alarmAction"));
      alarmActionIndex++;
      return this;
   }

   /**
    * The description of the alarm.
    *
    * @param alarmDescription the description of the alarm
    *
    * @return this {@code SaveAlarmOptions} object
    */
   public SaveAlarmOptions alarmDescription(String alarmDescription) {
      formParameters.put("AlarmDescription", checkNotNull(alarmDescription, "alarmDescription"));
      return this;
   }

   /**
    * The name of the alarm.
    *
    * @param alarmName the name of the alarm
    *
    * @return this {@code SaveAlarmOptions} object
    */
   public SaveAlarmOptions alarmName(String alarmName) {
      formParameters.put("AlarmName", checkNotNull(alarmName, "alarmName"));
      return this;
   }

   /**
    * The arithmetic operation to use when comparing the specified statistic and threshold.
    *
    * @param comparisonOperator the arithmetic operation to use when comparing the specified statistic and threshold
    *
    * @return this {@code SaveAlarmOptions} object
    */
   public SaveAlarmOptions comparisonOperator(ComparisonOperator comparisonOperator) {
      checkNotNull(comparisonOperator, "comparisonOperator");
      checkArgument(comparisonOperator != ComparisonOperator.UNRECOGNIZED, "comparisonOperator unrecognized");
      formParameters.put("ComparisonOperator", comparisonOperator.toString());
      return this;
   }

   /**
    * The dimensions for the alarm's associated metric.
    *
    * @param dimensions the dimensions for the alarm's associated metric
    *
    * @return this {@code SaveAlarmOptions} object
    */
   public SaveAlarmOptions dimensions(Set<Dimension> dimensions) {
      for (Dimension dimension : checkNotNull(dimensions, "dimensions")) {
         dimension(dimension);
      }
      return this;
   }

   /**
    * The dimension for the alarm's associated metric.
    *
    * @param dimension the dimension for the alarm's associated metric
    *
    * @return this {@code SaveAlarmOptions} object
    */
   public SaveAlarmOptions dimension(Dimension dimension) {
      checkNotNull(dimension, "dimension");
      checkArgument(dimensionIndex <= 10, "maximum number of dimensions is 10");
      formParameters.put("Dimensions.member." + dimensionIndex + ".Name", dimension.getName());
      formParameters.put("Dimensions.member." + dimensionIndex + ".Value", dimension.getValue());
      dimensionIndex++;
      return this;
   }

   /**
    * The number of periods over which data is compared to the specified threshold.
    *
    * @param evaluationPeriods the number of periods over which data is compared to the specified threshold
    *
    * @return this {@code SaveAlarmOptions} object
    */
   public SaveAlarmOptions evaluationPeriods(int evaluationPeriods) {
      formParameters.put("EvaluationPeriods", checkNotNull(evaluationPeriods, "evaluationPeriods").toString());
      return this;
   }

   /**
    * The list of actions to execute when this alarm transitions into an INSUFFICIENT_DATA state from any other state.
    *
    * @param insufficientDataActions the list of actions to execute when this alarm transitions into an
    *                                INSUFFICIENT_DATA state from any other state
    *
    * @return this {@code SaveAlarmOptions} object
    */
   public SaveAlarmOptions insufficientDataActions(Set<String> insufficientDataActions) {
      for (String insufficientDataAction : checkNotNull(insufficientDataActions)) {
         insufficientDataAction(insufficientDataAction);
      }
      return this;
   }

   /**
    * The actions to execute when this alarm transitions into an INSUFFICIENT_DATA state from any other state.
    *
    * @param insufficientDataAction the action to execute when this alarm transitions into an INSUFFICIENT_DATA state
    *                               from any other state
    *
    * @return this {@code SaveAlarmOptions} object
    */
   public SaveAlarmOptions insufficientDataAction(String insufficientDataAction) {
      checkNotNull(insufficientDataAction, "insufficientDataAction");
      checkArgument(insufficientDataActionsIndex <= 5, "maximum number of insufficient data actions is 5");
      formParameters.put("InsufficientDataActions.member." + insufficientDataActionsIndex, insufficientDataAction);
      insufficientDataActionsIndex++;
      return this;
   }

   /**
    * The name for the alarm's associated metric.
    *
    * @param metricName the name for the alarm's associated metric
    *
    * @return this {@code SaveAlarmOptions} object
    */
   public SaveAlarmOptions metricName(String metricName) {
      formParameters.put("MetricName", checkNotNull(metricName, "metricName"));
      return this;
   }

   /**
    * The namespace for the alarm's associated metric.
    *
    * @param namespace the namespace for the alarm's associated metric
    *
    * @return this {@code SaveAlarmOptions} object
    */
   public SaveAlarmOptions namespace(String namespace) {
      formParameters.put("Namespace", checkNotNull(namespace));
      return this;
   }

   /**
    * The list of actions to execute when this alarm transitions into an OK state from any other state.
    *
    * @param okActions the list of actions to execute when this alarm transitions into an OK state from any other state
    *
    * @return this {@code SaveAlarmOptions} object
    */
   public SaveAlarmOptions okActions(Set<String> okActions) {
      for (String okAction : checkNotNull(okActions, "okActions")) {
         okAction(okAction);
      }
      return this;
   }

   /**
    * The action to execute when this alarm transitions into an OK state from any other state.
    *
    * @param okAction the action to execute when this alarm transitions into an OK state from any other state
    *
    * @return this {@code SaveAlarmOptions} object
    */
   public SaveAlarmOptions okAction(String okAction) {
      checkNotNull(okAction, "okAction");
      checkArgument(okActionsIndex <= 5, "maximum number of ok actions is 5");
      formParameters.put("OKActions.member." + okActionsIndex, okAction);
      okActionsIndex++;
      return this;
   }

   /**
    * The period in seconds over which the specified statistic is applied.
    *
    * @param period the period in seconds over which the specified statistic is applied
    *
    * @return this {@code SaveAlarmOptions} object
    */
   public SaveAlarmOptions period(int period) {
      formParameters.put("Period", checkNotNull(period, "period").toString());
      return this;
   }

   /**
    * The statistic to apply to the alarm's associated metric.
    *
    * @param statistic the statistic to apply to the alarm's associated metric
    *
    * @return this {@code SaveAlarmOptions} object
    */
   public SaveAlarmOptions statistic(Statistics statistic) {
      checkNotNull(statistic, "statistic");
      checkArgument(statistic != Statistics.UNRECOGNIZED, "statistic unrecognized");
      formParameters.put("Statistic", statistic.toString());
      return this;
   }

   /**
    * The value against which the specified statistic is compared.
    *
    * @param threshold the value against which the specified statistic is compared
    *
    * @return this {@code SaveAlarmOptions} object
    */
   public SaveAlarmOptions threshold(double threshold) {
      formParameters.put("Threshold", checkNotNull(threshold).toString());
      return this;
   }

   /**
    * The unit for the alarm's associated metric.
    *
    * @param unit the unit for the alarm's associated metric
    *
    * @return this {@code SaveAlarmOptions} object
    */
   public SaveAlarmOptions unit(Unit unit) {
      checkNotNull(unit, "unit");
      checkArgument(unit != Unit.UNRECOGNIZED, "unit unrecognized");
      formParameters.put("Unit", unit.toString());
      return this;
   }

}
