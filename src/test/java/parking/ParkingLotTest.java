package parking;

import org.junit.Assert;
import org.junit.Test;


public class ParkingLotTest {
	
	private static final String PARKING_LOT_NAME_A = "A";
	private static final String PARKING_LOT_NAME_B = "B";
	
	private static final String CAR_NAME_OL1234 = "OL1234";
	private static final String CAR_NAME_EMPTY = "";
	private static final String CAR_NAME_DUMMY = "DUMMY";

	/*
	 * Given the parking lot managed by parking boy has space
	 * When parking boy park a new car
	 * Then parking boy will provide a receipt with car name */
	@Test
	public void testParkingBoyParkCar_givenManagedParkingLotHasSpace_thenProvideReceiptWithCarName() {
		
		ParkingLot parkingLot = new ParkingLot(PARKING_LOT_NAME_A, 3);
		
		ParkingBoy parkingBoy = new ParkingBoy(new InOrderParkingStrategy());
		parkingBoy.manage(parkingLot);
		
		parkingBoy.parkCar(this.createDummyCar());
		parkingBoy.parkCar(this.createDummyCar());
		
		Car car = new Car(CAR_NAME_OL1234);
		Receipt receipt = parkingBoy.parkCar(car);
		
		Assert.assertEquals(CAR_NAME_OL1234, receipt.getCarName());
		
	}


	/*
	 * Given the parking lot managed by parking boy has no space
	 * When parking boy park a new car
	 * Then parking boy will provide a receipt with empty car name */
	@Test
	public void testParkingBoyParkCar_givenManagedParkingLotHasNoSpace_thenProvideReceiptWithEmptyCarName() {
		
		ParkingLot parkingLot = new ParkingLot(PARKING_LOT_NAME_A, 1);
		
		ParkingBoy parkingBoy = new ParkingBoy(new InOrderParkingStrategy());
		parkingBoy.manage(parkingLot);
		
		parkingBoy.parkCar(this.createDummyCar());
		
		Car car = new Car(CAR_NAME_OL1234);
		Receipt receipt = parkingBoy.parkCar(car);
		
		Assert.assertEquals(ParkingStrategy.NO_PARKING_LOT, receipt.getParkingLotName());
		
	}
	
	@Test
	public void testIsFull_givenParkedCarNumberIsLessThanMaxCapacity_thenAnswerNotFull() {
		
		ParkingLot parkingLot = new ParkingLot(PARKING_LOT_NAME_A, 10);
		
		Assert.assertFalse(parkingLot.isFull());
	}
	
	private Car createDummyCar() {
		return new Car(CAR_NAME_DUMMY);
	}
}
