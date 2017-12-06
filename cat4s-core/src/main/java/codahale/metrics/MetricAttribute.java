/*
 * Copyright 2015 - 2016 Forever High Tech <http://www.foreverht.com> - all rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package codahale.metrics;

/**
 * Represents attributes of metrics which can be reported.
 */
public enum MetricAttribute {

    MAX("max"),
    MEAN("mean"),
    MIN("min"),
    STDDEV("stddev"),
    P50("p50"),
    P75("p75"),
    P95("p95"),
    P98("p98"),
    P99("p99"),
    P999("p999"),
    COUNT("count"),
    M1_RATE("m1_rate"),
    M5_RATE("m5_rate"),
    M15_RATE("m15_rate"),
    MEAN_RATE("mean_rate");

    private final String code;

    MetricAttribute(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
