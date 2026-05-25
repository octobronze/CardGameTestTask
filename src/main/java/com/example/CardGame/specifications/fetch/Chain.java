package com.example.CardGame.specifications.fetch;

import jakarta.persistence.metamodel.Attribute;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Chain {
    private final List<Attribute<?, ?>> attributes = new LinkedList<>();

    private Chain(Attribute<?, ?>... attributes) {
        this.attributes.addAll(Arrays.stream(attributes).toList());
    }

    public static Chain of(Attribute<?, ?>... attributes) {
        return new Chain(attributes);
    }

    public List<Attribute<?, ?>> getAttributes() {
        return attributes;
    }
}
