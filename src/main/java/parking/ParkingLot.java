package parking;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ParkingLot {

	private String name;
	private int maxCapacity;
	private final List<Car> parkedCars = new ArrayList<Car>();

	public ParkingLot(String name, int maxCapacity) {
		this.name = name;
		this.maxCapacity = maxCapacity;
	}

	public static int getBasicHourlyPrice(){
		if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)  == Calendar.SUNDAY){
			return 25;
		}
		return 20;
	}
	
	public int getMaxCapacity() {
		return this.maxCapacity;
	}
	
	public List<Car> getParkedCars(){
		return this.parkedCars;
	}
	
	public boolean isFull() {
		return (this.getParkedCars().size() >= maxCapacity);
	}

	public String getName() {
		return name;
	}
}
