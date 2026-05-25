package com.example.CardGame.specifications.fetch;

import java.util.Arrays;
import java.util.List;

public class FetchList {
    private final List<Chain> chains;

    private FetchList(Chain... chains) {
        this.chains = Arrays.stream(chains).toList();
    }

    public static FetchList of(Chain... chains) {
        return new FetchList(chains);
    }

    public List<Chain> getChains() {
        return chains;
    }
}
