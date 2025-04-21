package com.example.CardGame.specifications;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FetchService<T> {
    public enum Field {
        IMPLEMENTER("implementer"), ROLE("role"), COMMENTS("comments"),
        CREATOR("creator"), USER("user"), USER_GAME_SESSIONS("user_gameSessions"),
        USERS("users"), USER_GAME_SESSION_CK("userGameSessionCK"), GAME_SESSION_USERS("gameSession_users"),
        CREATED_BY("createdBy"), GAME_SESSION("gameSession"), CARD("card"),
        GAME_SESSION_STARTED_USERS("gameSessionStarted_Users"), GAME_SESSION_STARTED_CARDS("gameSessionStarted_Cards"),
        TURNS("turns");

        private final String name;

        Field(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private final List<List<Field>> fetchChainList;

    @SafeVarargs
    public FetchService(List<Field>... fetchChainList) {
        this.fetchChainList = Arrays.stream(fetchChainList).toList();
    }

    public FetchService(Field... entityFields) {
        this.fetchChainList = List.of(Arrays.stream(entityFields).toList());
    }

    public void fetch(Root<T> root) {
        for (var fetchChain : fetchChainList) {
            if (fetchChain.isEmpty()) continue;

            var initialFetch = root.fetch(fetchChain.get(0).getName(), JoinType.LEFT);
            for (int i = 1; i < fetchChain.size(); i ++) {
                initialFetch = initialFetch.fetch(fetchChain.get(i).getName(), JoinType.LEFT);
            }
        }
    }
}
