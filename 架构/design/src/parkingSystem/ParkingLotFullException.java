package parkingSystem;

public class ParkingLotFullException extends RuntimeException {

    public ParkingLotFullException() {
        super();
    }

    public ParkingLotFullException(String message) {
        super(message);
    }
}
