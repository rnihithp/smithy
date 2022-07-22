/*
 * Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

package software.amazon.smithy.model.shapes;

import java.util.Map;
import java.util.Optional;
import software.amazon.smithy.utils.ToSmithyBuilder;

/**
 * Structure shape that maps shape names to members.
 */
public final class StructureShape extends Shape implements ToSmithyBuilder<StructureShape> {

    private final Map<String, MemberShape> members;

    private StructureShape(Builder builder) {
        super(builder, false);
        members = NamedMemberUtils.computeMixinMembers(
                builder.getMixins(), builder.members, getId(), getSourceLocation());
        validateMemberShapeIds();
    }

    /**
     * @return Creates a new StructureShape builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Builder toBuilder() {
        return updateBuilder(builder());
    }

    @Override
    public <R> R accept(ShapeVisitor<R> visitor) {
        return visitor.structureShape(this);
    }

    @Override
    public Optional<StructureShape> asStructureShape() {
        return Optional.of(this);
    }

    @Override
    public ShapeType getType() {
        return ShapeType.STRUCTURE;
    }

    @Override
    public Map<String, MemberShape> getAllMembers() {
        return members;
    }

    /**
     * Builder used to create a {@link StructureShape}.
     */
    public static final class Builder extends NamedMembersShapeBuilder<Builder, StructureShape> {
        @Override
        public StructureShape build() {
            return new StructureShape(this);
        }

        @Override
        public ShapeType getShapeType() {
            return ShapeType.STRUCTURE;
        }
    }
}
