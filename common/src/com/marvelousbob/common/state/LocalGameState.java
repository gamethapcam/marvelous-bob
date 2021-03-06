package com.marvelousbob.common.state;

import com.marvelousbob.common.model.MarvelousBobException;
import com.marvelousbob.common.model.entities.Drawable;
import com.marvelousbob.common.model.entities.dynamic.Enemy;
import com.marvelousbob.common.model.entities.dynamic.Player;
import com.marvelousbob.common.network.register.dto.EnemyCollisionDto;
import com.marvelousbob.common.network.register.dto.MoveActionDto;
import com.marvelousbob.common.network.register.dto.NewEnemyDto;
import com.marvelousbob.common.network.register.dto.PlayerUpdateDto;
import com.marvelousbob.common.utils.UUID;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import space.earlygrey.shapedrawer.ShapeDrawer;

@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class LocalGameState implements Drawable {

    private HashMap<UUID, Long> lastMoveTimestampPerPlayer;
    private ConcurrentHashMap<UUID, Player> players;
    private ConcurrentHashMap<UUID, Enemy> enemies;

    @Override
    public void drawMe(ShapeDrawer shapeDrawer) {
        players.values().forEach(p -> p.drawMe(shapeDrawer));
        enemies.values().forEach(e -> e.drawMe(shapeDrawer));
    }

    // ===============================================
    // Updates coming from Broadcasts

    public void updateUsingMoveAction(MoveActionDto moveAction) {
        verifyFirstMoveAction(moveAction);

        long timestamp = moveAction.getTimestamp();
        if (isOlderMove(moveAction, timestamp)) {
            log.warn("MoveAction is older!");
            return;
        } else {
            updateLatestMoveTimestamp(moveAction);
        }

        getPlayer(moveAction.getSourcePlayerUuid()).ifPresent(p -> {
            p.setDestX(moveAction.getDestX());
            p.setDestY(moveAction.getDestY());
        });
    }

    private void updateLatestMoveTimestamp(MoveActionDto moveAction) {
        lastMoveTimestampPerPlayer.put(moveAction.getSourcePlayerUuid(), moveAction.getTimestamp());
    }

    private boolean isOlderMove(MoveActionDto moveAction, long timestamp) {
        return lastMoveTimestampPerPlayer.get(moveAction.getSourcePlayerUuid()) > timestamp;
    }

    private void verifyFirstMoveAction(MoveActionDto moveAction) {
        if (!lastMoveTimestampPerPlayer.containsKey(moveAction.getSourcePlayerUuid())) {
            log.warn("Very first MoveTimestamp from this player");
            updateLatestMoveTimestamp(moveAction);
        }
    }

    // ===============================================
    // Updates coming from Server

    public void updateUsingEnemyCollision(EnemyCollisionDto enemyCollisionDto) {
        // TODO
    }

    public void updateUsingPlayerList(PlayerUpdateDto playerUpdateDto) {
        // TODO
    }

    public void updateNewEnemy(NewEnemyDto newEnemyDto) {
        // TODO
    }

//    public void updateFromDto(GameStateDto otherGameStateDto) {
//        compareAndRemoveDisconnectedPlayers(otherGameStateDto);
//
//        for (var inputEntry : otherGameStateDto.playersDtos.entrySet()) {
//            if (!players.containsKey(inputEntry.getKey())) { // checking if there's new player
//                addPlayer(inputEntry.getValue()); // todo: should this be a deep copy?
//            }
//
//            // actually updating the state
//            PlayerDto updatedPlayerDto = players.get(inputEntry.getKey());
//            updatedPlayerDto.updateFromDto(inputEntry.getValue());
//        }
//    }

    // ===============================================
    // Basic HashMap manipulations

    public void addPlayer(Player player) {
        if (containsPlayerUuid(player)) {
            log.warn("Overwriting a player who already had that UUID assigned in the GS.");
        }
        players.put(player.getUuid(), player);
    }

    public boolean containsPlayerUuid(Player player) {
        return containsPlayerUuid(player.getUuid());
    }

    public boolean containsPlayerUuid(UUID uuid) {
        return players.containsKey(uuid);
    }

//    public void compareAndRemoveDisconnectedPlayers(GameStateDto inputDto) {
//        players.keySet().removeIf(uuid -> !inputDto.containsPlayerUuid(uuid));
//    }

    public void removePlayer(UUID uuid) {
        if (!containsPlayerUuid(uuid)) {
            throw new MarvelousBobException("Trying to remove a player which isn't in the GS.");
        }

        var iterator = players.keySet().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().equals(uuid)) {
                iterator.remove();
                break;
            }
        }
    }

    public Optional<Player> getPlayer(UUID uuid) {
        return Optional.ofNullable(players.get(uuid));
    }

    /**
     * @deprecated This method is slower than another alternative. Consider using
     * `MyGame.controller.getSelfPlayer().getUuid()` instead.
     */
    @Deprecated
    public Optional<Player> findPlayerUsingColorIndex(int colorIndex) {
        return players.values().stream()
                .filter(p -> p.getColorIndex() == colorIndex)
                .findAny();
    }
}
