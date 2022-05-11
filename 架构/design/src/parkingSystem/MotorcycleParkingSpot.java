package parkingSystem;

public class MotorcycleParkingSpot extends ParkingSpot{

    public MotorcycleParkingSpot() {
        this.hourlyFee = 3;
    }

    @Override
    void take(ParkingLot parkingLot) {
        int motorcycleParkingSpotCount = parkingLot.getMotorcycleParkingSpotCount();
        if(motorcycleParkingSpotCount == 0) throw new ParkingLotFullException("parking lot is full");

        parkingLot.setMotorcycleParkingSpotCount(motorcycleParkingSpotCount - 1);
    }

    void free(ParkingLot parkingLot){
        parkingLot.setMotorcycleParkingSpotCount(parkingLot.getMotorcycleParkingSpotCount() + 1);
    }
}
