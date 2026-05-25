package com.example.CardGame.specifications.fetch;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;

public final class FetchUtil {
    public static void fetch(Root<?> root, FetchList fetchList) {
        for (var chain : fetchList.getChains()) {
            var attributes = chain.getAttributes();
            var initialFetch = root.fetch(attributes.get(0).getName(), JoinType.LEFT);
            for (int i = 1; i < attributes.size(); i ++) {
                initialFetch = initialFetch.fetch(attributes.get(i).getName(), JoinType.LEFT);
            }
        }
    }
}
