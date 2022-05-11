package parkingSystem;

abstract public class ParkingSpot {

    public String parkingLocation;
    public float hourlyFee;

    abstract void take(ParkingLot parkingLot);

    abstract void free(ParkingLot parkingLot);
}
