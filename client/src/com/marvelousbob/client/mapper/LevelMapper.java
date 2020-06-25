package com.marvelousbob.client.mapper;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.marvelousbob.client.entities.EnemySpawnPoint;
import com.marvelousbob.client.entities.Level;
import com.marvelousbob.client.entities.PlayersBase;
import com.marvelousbob.client.entities.Wall;
import com.marvelousbob.common.network.register.dto.LevelInitializationDto;
import com.marvelousbob.common.network.register.dto.PlayersBaseDto;
import com.marvelousbob.common.network.register.dto.SpawnPointDto;
import com.marvelousbob.common.network.register.dto.WallDto;

import java.util.List;
import java.util.stream.Collectors;

public class LevelMapper {
    public Level toLevel(LevelInitializationDto levelInitializationDto) {
        List<Wall> walls = levelInitializationDto.walls.stream()
                .map(this::toWall).collect(Collectors.toList());
        List<EnemySpawnPoint> spawnPoints = levelInitializationDto.spawnPointDtos.stream()
                .map(this::toSpawnPoint).collect(Collectors.toList());
        List<PlayersBase> bases = levelInitializationDto.bases.stream()
                .map(this::toBase).collect(Collectors.toList());
        return new Level(bases, spawnPoints, walls);
    }

    private PlayersBase toBase(PlayersBaseDto playersBaseDto) {
        return new PlayersBase(playersBaseDto.x, playersBaseDto.y, playersBaseDto.width, playersBaseDto.height);
    }

    private EnemySpawnPoint toSpawnPoint(SpawnPointDto spawnPointDto) {
        // todo other shapes ?     --- OLA
        return EnemySpawnPoint.starShaped(new Vector2(spawnPointDto.x, spawnPointDto.y), spawnPointDto.size, new Color(spawnPointDto.r, spawnPointDto.g, spawnPointDto.b, spawnPointDto.a));
    }

    public Wall toWall(WallDto wallDto) {
        return new Wall(wallDto.getX1(), wallDto.getY1(), wallDto.getWidth(), wallDto.getHeight());
    }
}