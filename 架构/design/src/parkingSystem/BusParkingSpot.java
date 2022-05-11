package parkingSystem;

public class BusParkingSpot extends ParkingSpot{

    public BusParkingSpot() {
        this.hourlyFee = 10;
    }

    @Override
    void take(ParkingLot parkingLot) {
        int busParkingSpotCount = parkingLot.getBusParkingSpotCount();
        if(busParkingSpotCount == 0) throw new ParkingLotFullException("parking lot is full");

        parkingLot.setBusParkingSpotCount(busParkingSpotCount - 1);
    }

    void free(ParkingLot parkingLot){
        parkingLot.setBusParkingSpotCount(parkingLot.getBusParkingSpotCount() + 1);
    }
}
