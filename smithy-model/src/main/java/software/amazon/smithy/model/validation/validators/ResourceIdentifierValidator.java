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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.ResourceShape;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.validation.AbstractValidator;
import software.amazon.smithy.model.validation.ValidationEvent;
import software.amazon.smithy.utils.OptionalUtils;

/**
 * Validates that the resource identifiers of children of a resource contain
 * all of the identifiers as their parents.
 */
public final class ResourceIdentifierValidator extends AbstractValidator {

    @Override
    public List<ValidationEvent> validate(Model model) {
        List<ValidationEvent> events = model.shapes(ResourceShape.class)
                .flatMap(resource -> validateAgainstChildren(resource, model))
                .collect(Collectors.toList());
        for (ResourceShape resource : model.getResourceShapes()) {
            events.addAll(validatePropertyRedefine(resource, model));
        }
        return events;
    }

    private List<ValidationEvent> validatePropertyRedefine(ResourceShape resource, Model model) {
        List<ValidationEvent> events = new LinkedList<>();
        if (resource.hasProperties()) {
            Map<String, String> propertyLowerCaseToActual = new HashMap<>();
            for (String propertyName : resource.getProperties().keySet()) {
                propertyLowerCaseToActual.put(propertyName.toLowerCase(), propertyName);
            }

            for (String identifier : resource.getIdentifiers().keySet()) {
                if (propertyLowerCaseToActual.containsKey(identifier.toLowerCase())) {
                   events.add(error(resource, String.format("Resource identifier `%s` cannot also be a"
                           + " resource property", identifier)));
                }
            }
        }
        return events;
    }

    private Stream<ValidationEvent> validateAgainstChildren(ResourceShape resource, Model model) {
        return resource.getResources().stream()
                .flatMap(shape -> OptionalUtils.stream(model.getShape(shape).flatMap(Shape::asResourceShape)))
                .flatMap(child -> Stream.concat(
                        OptionalUtils.stream(checkForMissing(child, resource)),
                        OptionalUtils.stream(checkForMismatches(child, resource))));
    }

    private Optional<ValidationEvent> checkForMissing(ResourceShape resource, ResourceShape parent) {
        // Look for identifiers on the parent that are flat-out missing on the child.
        String missingKeys = parent.getIdentifiers().entrySet().stream()
                .filter(entry -> resource.getIdentifiers().get(entry.getKey()) == null)
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.joining(", "));

        if (missingKeys.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(error(resource, String.format(
                "This resource is bound as a child of `%s`, but it is invalid because its `identifiers` property "
                + "is missing the following identifiers that are defined in `%s`: [%s]",
                parent.getId(), parent.getId(), missingKeys)));
    }

    private Optional<ValidationEvent> checkForMismatches(ResourceShape resource, ResourceShape parent) {
        // Look for identifiers on the child that have the same key but target different shapes.
        String mismatchedTargets = parent.getIdentifiers().entrySet().stream()
                .filter(entry -> resource.getIdentifiers().get(entry.getKey()) != null)
                .filter(entry -> !resource.getIdentifiers().get(entry.getKey()).equals(entry.getValue()))
                .map(entry -> String.format(
                        "expected the `%s` member to target `%s`, but found a target of `%s`",
                        entry.getKey(), entry.getValue(), resource.getIdentifiers().get(entry.getKey())))
                .sorted()
                .collect(Collectors.joining("; "));

        if (mismatchedTargets.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(error(resource, String.format(
                "The `identifiers` property of this resource is incompatible with its binding to `%s`: %s",
                parent.getId(), mismatchedTargets)));
    }
}
