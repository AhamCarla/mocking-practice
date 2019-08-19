package parking;

public class Receipt {

	private String carName = null;
	private String parkingLotName = null;
	
	public Receipt() {
		
	}
	
	public void setCarName(String carName) {
		this.carName = carName;
	}
	
	public String getCarName() {
		return this.carName;
	}

	public String getParkingLotName() {
		return parkingLotName;
	}

	public void setParkingLotName(String parkingLotName) {
		this.parkingLotName = parkingLotName;
	}
}
