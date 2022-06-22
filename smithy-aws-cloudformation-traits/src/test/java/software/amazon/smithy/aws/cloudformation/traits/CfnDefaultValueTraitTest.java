/*
 * Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.smithy.aws.cloudformation.traits;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.traits.Trait;
import software.amazon.smithy.model.traits.TraitFactory;

import java.util.Optional;

public final class CfnDefaultValueTraitTest {

    @Test
    public void loadsTraitWithString() {
        Node node = Node.from("Test");
        TraitFactory provider = TraitFactory.createServiceFactory();
        Optional<Trait> trait = provider.createTrait(
                ShapeId.from("aws.cloudformation#cfnDefaultValue"), ShapeId.from("ns.qux#Foo"), node);

        assertTrue(trait.isPresent());
        assertThat(trait.get(), instanceOf(CfnDefaultValueTrait.class));
        CfnDefaultValueTrait cfnDefaultValueTrait = (CfnDefaultValueTrait) trait.get();
        assertThat(cfnDefaultValueTrait.getValue(), equalTo("Test"));
        assertThat(cfnDefaultValueTrait.toNode(), equalTo(node));
    }
}
