/*
 * Copyright (c) Joseph Prichard 2024.
 */

package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "Stats", indexes = {@Index(name = "idx_elo", columnList = "elo")})
@AllArgsConstructor
public class StatsEntity {

    @Id
    public Long playerId;
    @Column(nullable = false)
    public Float elo;
    @Column(nullable = false)
    public Integer won;
    @Column(nullable = false)
    public Integer lost;
    @Column(nullable = false)
    public Integer drawn;

    public StatsEntity() {
    }

    public StatsEntity(Long playerId) {
        this(playerId, 0f, 0, 0, 0);
    }
}
