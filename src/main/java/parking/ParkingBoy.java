package parking;

import java.util.Arrays;

public class ParkingBoy {

	private ParkingLot managingParkingLot = null;
	private ParkingStrategy parkingStrategy = null;
	
	public ParkingBoy(ParkingStrategy parkingStrategy) {
		this.parkingStrategy =parkingStrategy;
	}
	
	public ParkingLot getManagingParkingLot() {
		return this.managingParkingLot;
	}
	
	public void manage(ParkingLot parkingLot) {
		this.managingParkingLot = parkingLot;
	}

	public Receipt parkCar(Car car) {
		
		return parkingStrategy.park(Arrays.asList(getManagingParkingLot()), car);
	}
	
	private Receipt createNoParkingSpaceReceipt() {
		Receipt receipt = new Receipt();
		receipt.setParkingLotName("");
		return receipt;
	}

	private ParkingLot selectParkingLot() {
		
		if (this.managingParkingLot.isFull()) {
			return null;
		}
		return this.managingParkingLot;
	}
}
