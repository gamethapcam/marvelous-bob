package com.marvelousbob.common.network.register.dto;

import com.marvelousbob.common.model.Identifiable;
import com.marvelousbob.common.network.register.Timestamped;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public final class PlayerDto implements Identifiable, Timestamped, Dto {

    private UUID uuid;
    private long timestamp;
    private float speed = 20;
    private float size = 40;
    private float currX, currY, destX, destY;

    public PlayerDto(UUID uuid) {
        this.uuid = uuid;
        stampNow();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PlayerDto playerDto = (PlayerDto) o;
        return uuid.equals(playerDto.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.getStringId().hashCode();
    }
}
