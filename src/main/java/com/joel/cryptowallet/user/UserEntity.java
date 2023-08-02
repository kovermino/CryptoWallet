package com.joel.cryptowallet.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    private String walletId;

    private String password;

    private String walletAddress;

    private String privateKey;

    @Enumerated(EnumType.STRING)
    private UserStatus status;
}
