package parking;

import java.util.Random;

public class CarDaoImpl implements CarDao{
    @Override
    public boolean isVip(String carName) {
//        return (new Random()).nextBoolean();
        return false;
    }
}
