(ns org.jclouds.aws.ec2-test
  (:use
    org.jclouds.aws.ec2
    clojure.test))

(deftest translate-enum-value-test
  (is (= org.jclouds.aws.ec2.domain.SpotInstanceRequest$Type/ONE_TIME
         (org.jclouds.aws.ec2/translate-enum-value :type :one-time))))

(deftest spot-options-est
  (is (spot-options {:type :one-time
                     :valid-from (java.util.Date.)
                     :valid-until (java.util.Date.)
                     :launch-group "lg"
                     :availability-zone-group "ag"})))
