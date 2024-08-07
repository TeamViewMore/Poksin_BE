package com.viewmore.poksin.entity;

import org.springframework.data.annotation.Id;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "refresh", timeToLive = 86400) 
public class RefreshEntity {

    @Id
    private String refresh;

    private String username;

    public RefreshEntity(String refresh, String username) {
        this.username = username;
        this.refresh = refresh;
    }

}
