package codingdojo;

import javax.xml.stream.Location;

/**
 * This class contains the information about how the customer would like to
 * have the contents of their shopping cart delivered to them.
 */
public class DeliveryInformation implements ModelObject {
    private String type;
    private String deliveryAddress;
    private Store pickupLocation;
    private long weight;

    public DeliveryInformation(String type, Store pickupLocation,
                               long weight) {
        this.type = type;
        this.pickupLocation = pickupLocation;
        this.weight = weight;
    }

    public DeliveryInformation(String type, Store pickupLocation, long weight, String deliveryAddress) {
        this.type = type;
        this.deliveryAddress = deliveryAddress;
        this.pickupLocation = pickupLocation;
        this.weight = weight;
    }

    public String getType() {
        return type;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public long getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "DeliveryInformation{" + "\n" +
                "type='" + type + '\'' + "\n" +
                "deliveryAddress='" + deliveryAddress + '\'' + "\n" +
                "pickupLocation=" + pickupLocation + "\n" +
                "weight=" + weight + "\n" +
                '}';
    }

    public boolean isHome() {
        return getType() != null
                && "HOME_DELIVERY".equals(getType())
                && getDeliveryAddress() != null;
    }

    @Override
    public void saveToDatabase() {
        throw new UnsupportedOperationException("missing from this exercise - shouldn't be called from a unit test");
    }

    void updateDeliveryInformation(Store storeToSwitchTo, Store currentStore, long weight, LocationService locationService) {
        if (isHome()) {
            if (!locationService.isWithinDeliveryRange(storeToSwitchTo, getDeliveryAddress())) {
                this.type = "PICKUP";
                this.pickupLocation = currentStore;
            } else {
                this.pickupLocation = storeToSwitchTo;
                this.weight = weight;
            }
        } else {
            if (getDeliveryAddress() != null) {
                if (!locationService.isWithinDeliveryRange(storeToSwitchTo, getDeliveryAddress())) {
                    this.type = "HOME_DELIVERY";
                    this.pickupLocation = storeToSwitchTo;
                    this.weight = weight;

                }
            }
        }
    }
}
