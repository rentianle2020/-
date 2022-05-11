package parkingSystem;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class ParkingLot{

    private int motorcycleParkingSpotCount = 100;
    private int carParkingSpotCount = 100;
    private int busParkingSpotCount = 100;

    private ParkingLot(){}

    public static ParkingLot getInstance(){
        return ParkingLotHolder.parkingLot;
    }

    private static class ParkingLotHolder{
        private static final ParkingLot parkingLot = new ParkingLot();
    }

    private HashMap<String,Ticket> tickets = new HashMap<>();
    
    public void park(String plateNumber, String vehicle){
        ParkingSpot parkingSpot = SpotFactory.getParkingSpot(vehicle);
        parkingSpot.take(this);

        Ticket ticket = new Ticket(parkingSpot,plateNumber, LocalDateTime.now());
        tickets.put(plateNumber,ticket);

        System.out.printf("vehicle %s in at %s\n",plateNumber,ticket.in.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    public void exit(String plateNumber){
        Ticket ticket = tickets.get(plateNumber);
        tickets.remove(plateNumber);

        ticket.parkingSpot.free(this);
        float fee = ticket.calculateFee();
        System.out.printf("vehicle %s leave, total fee : %.2f\n",ticket.plateNumber,fee);
    }

    public int getMotorcycleParkingSpotCount() {
        return motorcycleParkingSpotCount;
    }

    public int getCarParkingSpotCount() {
        return carParkingSpotCount;
    }

    public int getBusParkingSpotCount() {
        return busParkingSpotCount;
    }

    public void setMotorcycleParkingSpotCount(int motorcycleParkingSpotCount) {
        this.motorcycleParkingSpotCount = motorcycleParkingSpotCount;
    }

    public void setCarParkingSpotCount(int carParkingSpotCount) {
        this.carParkingSpotCount = carParkingSpotCount;
    }

    public void setBusParkingSpotCount(int busParkingSpotCount) {
        this.busParkingSpotCount = busParkingSpotCount;
    }
}