package parkingSystem;

public class UnavailableVehicleTypeException extends RuntimeException{

    public UnavailableVehicleTypeException() {
        super();
    }

    public UnavailableVehicleTypeException(String message) {
        super(message);
    }
}
