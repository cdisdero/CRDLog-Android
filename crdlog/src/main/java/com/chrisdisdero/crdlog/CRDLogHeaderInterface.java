package com.chrisdisdero.crdlog;

/**
 * Interface that provides the log header when needed by the app instance of {@link CRDLog}.
 *
 * @author cdisdero
 *
 *
Copyright © 2017 Christopher Disdero.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
public interface CRDLogHeaderInterface {

    /**
     * Provides a log file header as a {@link String}
     *
     * @return A {@link String} representing the log file header.
     */
    String onProvideHeader();
}
