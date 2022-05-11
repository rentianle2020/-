package parkingSystem;

public class SpotFactory {

    public static ParkingSpot getParkingSpot(String vehicleType){
        if("Motorcycle".equals(vehicleType)) return new MotorcycleParkingSpot();
        else if("Car".equals(vehicleType)) return new CarParkingSpot();
        else if("Bus".equals(vehicleType)) return new BusParkingSpot();

        throw new UnavailableVehicleTypeException(String.format("%s is not a valid vehicle type",vehicleType));
    }
}
