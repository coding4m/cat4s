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

import java.util.EventListener;

/**
 * Listeners for events from the registry.  Listeners must be thread-safe.
 */
public interface MetricListener extends EventListener {
    /**
     * A no-op implementation of {@link MetricListener}.
     */
    abstract class Base implements MetricListener {
        @Override
        public void onGaugeAdded(String name, Gauge<?> gauge) {
        }

        @Override
        public void onGaugeRemoved(String name) {
        }

        @Override
        public void onCounterAdded(String name, Counter counter) {
        }

        @Override
        public void onCounterRemoved(String name) {
        }

        @Override
        public void onHistogramAdded(String name, Histogram histogram) {
        }

        @Override
        public void onHistogramRemoved(String name) {
        }

        @Override
        public void onMeterAdded(String name, Meter meter) {
        }

        @Override
        public void onMeterRemoved(String name) {
        }

        @Override
        public void onTimerAdded(String name, Timer timer) {
        }

        @Override
        public void onTimerRemoved(String name) {
        }
    }

    /**
     * Called when a {@link Gauge} is added to the registry.
     *
     * @param name  the gauge's name
     * @param gauge the gauge
     */
    void onGaugeAdded(String name, Gauge<?> gauge);

    /**
     * Called when a {@link Gauge} is removed from the registry.
     *
     * @param name the gauge's name
     */
    void onGaugeRemoved(String name);

    /**
     * Called when a {@link Counter} is added to the registry.
     *
     * @param name    the counter's name
     * @param counter the counter
     */
    void onCounterAdded(String name, Counter counter);

    /**
     * Called when a {@link Counter} is removed from the registry.
     *
     * @param name the counter's name
     */
    void onCounterRemoved(String name);

    /**
     * Called when a {@link Histogram} is added to the registry.
     *
     * @param name      the histogram's name
     * @param histogram the histogram
     */
    void onHistogramAdded(String name, Histogram histogram);

    /**
     * Called when a {@link Histogram} is removed from the registry.
     *
     * @param name the histogram's name
     */
    void onHistogramRemoved(String name);

    /**
     * Called when a {@link Meter} is added to the registry.
     *
     * @param name  the meter's name
     * @param meter the meter
     */
    void onMeterAdded(String name, Meter meter);

    /**
     * Called when a {@link Meter} is removed from the registry.
     *
     * @param name the meter's name
     */
    void onMeterRemoved(String name);

    /**
     * Called when a {@link Timer} is added to the registry.
     *
     * @param name  the timer's name
     * @param timer the timer
     */
    void onTimerAdded(String name, Timer timer);

    /**
     * Called when a {@link Timer} is removed from the registry.
     *
     * @param name the timer's name
     */
    void onTimerRemoved(String name);
}
