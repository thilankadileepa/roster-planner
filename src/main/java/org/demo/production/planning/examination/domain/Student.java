/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.demo.production.planning.examination.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.demo.rostering.domain.AbstractPersistable;

/**
 * Not used during score calculation, so not inserted into the working memory.
 */
@XStreamAlias("Student")
public class Student extends AbstractPersistable {
    public Student() {
    }

    public Student(long id) {
        super(id);
    }
}
