package fr.lesideaux.saarasaka.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.util.Date;

@Getter
@Entity
@Table(name = "reservations")
public class ReservationEntity {

    @Id
    @GeneratedValue
    private Long id;

    private Date startDate;
    private Date endDate;

    private Long parkingSpaceId;

    public ReservationEntity() {}

    public ReservationEntity(Date startDate, Date endDate, Long parkingSpaceId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.parkingSpaceId = parkingSpaceId;
    }

    public ReservationEntity(Long id, Date startDate, Date endDate, Long parkingSpaceId) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.parkingSpaceId = parkingSpaceId;
    }
}
