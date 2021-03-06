/*
 * Copyright 2013-2014 OW2 Nanoko Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.*;

def artifactNameWithoutExtension = "target/test.just-coffee-1.0.0-SNAPSHOT"

// check the main js artifact
def js = new File (basedir, artifactNameWithoutExtension + ".js");
assert js.exists();
assert js.canRead();

// check the zip distribution
def zip = new File (basedir, artifactNameWithoutExtension + ".zip");
assert zip.exists();
assert zip.canRead();

// check the minified js file
def min = new File (basedir, artifactNameWithoutExtension + "-min.js");
assert min.exists();
assert min.canRead();