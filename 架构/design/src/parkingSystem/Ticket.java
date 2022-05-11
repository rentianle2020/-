package parkingSystem;

import java.time.Duration;
import java.time.LocalDateTime;

public class Ticket {

    public ParkingSpot parkingSpot;
    public String plateNumber;
    public LocalDateTime in;

    public Ticket(ParkingSpot parkingSpot, String plateNumber, LocalDateTime in) {
        this.parkingSpot = parkingSpot;
        this.plateNumber = plateNumber;
        this.in = in;
    }

    public float calculateFee() {
        Duration duration = Duration.between(in,LocalDateTime.now().plusHours(5));
        return duration.toHours() * parkingSpot.hourlyFee;
    }
}
