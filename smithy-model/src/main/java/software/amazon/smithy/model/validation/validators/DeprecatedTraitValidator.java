/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

package software.amazon.smithy.model.validation.validators;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.traits.DeprecatedTrait;
import software.amazon.smithy.model.traits.TraitDefinition;
import software.amazon.smithy.model.validation.AbstractValidator;
import software.amazon.smithy.model.validation.ValidationEvent;

/**
 * Emits a validation event if a model contains shapes that are bound to deprecated traits.
 */
public final class DeprecatedTraitValidator extends AbstractValidator {
    @Override
    public List<ValidationEvent> validate(Model model) {
        List<ValidationEvent> events = new ArrayList<>();

        for (Shape trait : model.getShapesWithTrait(TraitDefinition.class)) {
            if (trait.hasTrait(DeprecatedTrait.class)) {
                Set<Shape> shapesWithTrait = model.getShapesWithTrait(trait);
                if (!shapesWithTrait.isEmpty()) {
                    DeprecatedTrait deprecatedTrait = trait.expectTrait(DeprecatedTrait.class);
                    String traitMessage = trait.toShapeId().toString();
                    if (deprecatedTrait.getMessage().isPresent()) {
                        traitMessage = traitMessage + ", " + deprecatedTrait.getMessage().get();
                    }
                    for (Shape shape : shapesWithTrait) {
                        events.add(warning(shape, trait, format(
                                "This shape applies a trait that is deprecated: %s", traitMessage)));
                    }
                }
            }
        }

        return events;
    }
}
