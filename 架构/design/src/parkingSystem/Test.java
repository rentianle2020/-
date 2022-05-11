package parkingSystem;

public class Test {

    public static void main(String[] args) {
        ParkingLot parkingLot = ParkingLot.getInstance();
        parkingLot.park("京AX1423", "Car");
        parkingLot.exit("京AX1423");
    }
}
