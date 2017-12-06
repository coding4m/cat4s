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

package cat4s.japi;

import cat4s.Cat$;
import cat4s.trace.TraceSet;
import cat4s.metric.MetricSet;

/**
 * @author siuming
 */
public final class Cat {

    public static void start() {
        Cat$.MODULE$.start();
    }

    public static TraceSet tracer() {
        return Cat$.MODULE$.tracer();
    }

    public static MetricSet metrics() {
        return Cat$.MODULE$.metrics();
    }

    public static void stop() {
        Cat$.MODULE$.stop();
    }

    private Cat(){}
}
