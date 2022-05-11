package parkingSystem;

public class CarParkingSpot extends ParkingSpot{

    public CarParkingSpot() {
        this.hourlyFee = 5;
    }

    @Override
    void take(ParkingLot parkingLot) {
        int carParkingSpotCount = parkingLot.getCarParkingSpotCount();
        if(carParkingSpotCount == 0) throw new ParkingLotFullException("parking lot is full");

        parkingLot.setCarParkingSpotCount(carParkingSpotCount - 1);
    }

    void free(ParkingLot parkingLot){
        parkingLot.setCarParkingSpotCount(parkingLot.getCarParkingSpotCount() + 1);
    }
}
