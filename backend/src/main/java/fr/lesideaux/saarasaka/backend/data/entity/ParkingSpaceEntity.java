package fr.lesideaux.saarasaka.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "parking_spaces")
public class ParkingSpaceEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String row;
    private String number;

    private boolean isEquippedWithElectricCharging;
    private boolean isReserved;
    private boolean isConfirmed;

    public ParkingSpaceEntity() {}

    public ParkingSpaceEntity(String row, String number, boolean isEquippedWithElectricCharging, boolean isReserved, boolean isConfirmed) {
        this.row = row;
        this.number = number;
        this.isEquippedWithElectricCharging = isEquippedWithElectricCharging;
        this.isReserved = isReserved;
        this.isConfirmed = isConfirmed;
    }

    public ParkingSpaceEntity(Long id, String row, String number, boolean isEquippedWithElectricCharging, boolean isReserved, boolean isConfirmed) {
        this.id = id;
        this.row = row;
        this.number = number;
        this.isEquippedWithElectricCharging = isEquippedWithElectricCharging;
        this.isReserved = isReserved;
        this.isConfirmed = isConfirmed;
    }
}
