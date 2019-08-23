/*
 * DISCLAIMER
 * Copyright 2019 ArangoDB GmbH, Cologne, Germany
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
 *
 * Copyright holder is ArangoDB GmbH, Cologne, Germany
 *
 */

package com.arangodb.graphql.query.result.resolver;

import com.arangodb.entity.BaseDocument;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class PropertyMergerTest {


    final String leftKey = "mykey";
    final String leftId = "myid";
    final String leftRevision = "leftRevision";
    final String leftOnly = "leftOnly";

    final String rightKey = "Rmykey";
    final String rightId = "Rmyid";
    final String rightRevision = "Rrevision";

    final String rightOnly = "rightOnly";

    final String bothLeft = "bothLeft";
    final String bothRight = "bothRight";

    BaseDocument left;
    BaseDocument right;

    PropertyMerger propertyMerger;

    @Before
    public void setUp(){

        left = new BaseDocument();
        left.setId(leftId);
        left.setKey(leftKey);
        left.setRevision(leftRevision);
        left.getProperties().put("leftOnly", leftOnly);
        left.getProperties().put("both", bothLeft);

        right = new BaseDocument();
        right.setId(rightId);
        right.setKey(rightKey);
        right.setRevision(rightRevision);
        right.getProperties().put("rightOnly", rightOnly);
        right.getProperties().put("both", bothRight);


        propertyMerger = new PropertyMerger();

    }

    @Test
    public void key(){
        Object key = propertyMerger.merge(left, right, "_key");
        assertThat(key, equalTo(this.leftKey));
    }

    @Test
    public void id(){
        Object id = propertyMerger.merge(left, right, "_id");
        assertThat(id, equalTo(this.leftId));
    }

    @Test
    public void revision(){
        Object revision = propertyMerger.merge(left, right, "_revision");
        assertThat(revision, equalTo(this.leftRevision));
    }


    @Test
    public void leftOnly(){
        Object leftOnly = propertyMerger.merge(left, right, "leftOnly");
        assertThat(leftOnly, equalTo(this.leftOnly));
    }

    @Test
    public void rightOnly(){
        Object rightOnly = propertyMerger.merge(left, right, "rightOnly");
        assertThat(rightOnly, equalTo(this.rightOnly));
    }

    @Test
    public void both(){
        Object both = propertyMerger.merge(left, right, "both");
        assertThat(both, equalTo(this.bothLeft));
    }

}