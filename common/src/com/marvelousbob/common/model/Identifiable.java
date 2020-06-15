package com.marvelousbob.common.model;


import com.marvelousbob.common.network.register.dto.UUID;

public interface Identifiable {
    UUID getId();

    default boolean isEquals(UUID other) {
        return getId().equals(other);
    }
}
