package org.supercoding.supertime.web.entity.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.supercoding.supertime.web.entity.TimeEntity;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "refresh_token_table")
public class RefreshToken extends TimeEntity {

    @Id
    @Column(name = "refresh_token_key" )
    private String key;

    @Column(name = "refresh_token_value")
    private String value;           // Refresh Token String

    public RefreshToken updateValue(String token) {
        this.value = token;
        return this;
    }
}