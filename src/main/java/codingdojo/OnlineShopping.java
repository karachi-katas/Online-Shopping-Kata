package codingdojo;

import java.util.ArrayList;

/**
 * The online shopping company owns a chain of Stores selling
 * makeup and beauty products.
 * <p>
 * Customers using the online shopping website can choose a Store then
 * can put Items available at that store into their Cart.
 * <p>
 * If no store is selected, then items are shipped from
 * a central warehouse.
 */
public class OnlineShopping {

    private Session session;

    public OnlineShopping(Session session) {
        this.session = session;
    }

    /**
     * This method is called when the user changes the
     * store they are shopping at in the online shopping
     * website.
     *
     */
    public void switchStore(Store storeToSwitchTo) {
        Cart cart = (Cart) session.get("CART");
        DeliveryInformation deliveryInformation = (DeliveryInformation) session.get("DELIVERY_INFO");

        if (cart != null) {
            cart.invalidateTickets();
        }

        shipItemIfDeliverInformationGivenAndStoreIsNull(storeToSwitchTo, deliveryInformation);

        if (storeToSwitchTo != null && cart != null) {
            ArrayList<Item> newItems = new ArrayList<>();
            long weight = 0;

            for (Item item : cart.getItems()) {

                if (!storeToSwitchTo.hasItem(item)) {
                    cart.markAsUnavailable(item);
                }
                else if ("EVENT".equals(item.getType())) {
                    cart.addItem(item);
                }
            }

            weight = cart.getItems().stream().mapToLong(Item::getWeight).sum() - cart.getUnavailableItems().stream().mapToLong(Item::getWeight).sum();

            Store currentStore = (Store) session.get("STORE");
            setDeliveryInformationAccordingToType(storeToSwitchTo, deliveryInformation, weight, currentStore);
        }

        session.put("STORE", storeToSwitchTo);
        session.saveAll();
    }

    private void setDeliveryInformationAccordingToType(Store store, DeliveryInformation deliveryInformation, long weight, Store currentStore) {
        LocationService locationService = (LocationService) session.get("LOCATION_SERVICE");

        if (deliveryInformation == null) return;

        if (deliveryInformation.getType() == null) {
            deliveryInformation.setType("HOME_DELIVERY");
        }

        if (deliveryInformation.isDeliveryAddressProvided()) {
            if (locationService.isWithinDeliveryRange(store, deliveryInformation.getDeliveryAddress())) {
                deliveryInformation.setTotalWeight(weight);
                deliveryInformation.setPickupLocation(store);
                return;
            }

            deliveryInformation.setType("PICKUP");
            deliveryInformation.setPickupLocation(currentStore);
        }
    }

    private void shipItemIfDeliverInformationGivenAndStoreIsNull(Store storeToSwitchTo, DeliveryInformation deliveryInformation) {
        if (storeToSwitchTo == null && deliveryInformation != null) {
            deliveryInformation.setType("SHIPPING");
            deliveryInformation.setPickupLocation(null);
        }
    }

    @Override
    public String toString() {
        return "OnlineShopping{\n"
                + "session=" + session + "\n}";
    }
}
