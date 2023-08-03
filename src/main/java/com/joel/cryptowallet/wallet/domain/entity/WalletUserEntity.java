package com.joel.cryptowallet.wallet.domain.entity;

import com.joel.cryptowallet.wallet.domain.enums.WalletUserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletUserEntity {
    @Id
    private String walletId;

    private String password;

    private String walletAddress;

    private String privateKey;

    @Enumerated(EnumType.STRING)
    private WalletUserStatus status;

    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
