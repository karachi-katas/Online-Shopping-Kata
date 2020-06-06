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
        UpdateStoreInfoIfStoreSwitchIsNull(storeToSwitchTo, cart, deliveryInformation);
        UpdateStoreInfoIfStoreSwitchIsNotNull(storeToSwitchTo, cart, deliveryInformation);
        saveStoreSession(storeToSwitchTo);
    }

    private void UpdateStoreInfoIfStoreSwitchIsNull(Store storeToSwitchTo, Cart cart, DeliveryInformation deliveryInformation) {
        if (storeToSwitchTo == null) {
            cart.markAllEventItemsUnavailable();
            updateDeliveryInformation(deliveryInformation);
        }
    }

    private void UpdateStoreInfoIfStoreSwitchIsNotNull(Store storeToSwitchTo, Cart cart, DeliveryInformation deliveryInformation) {
        if (cart == null) {
            return;
        }

        long weight = 0;
        for(Item item: cart.filterItems("EVENT")) {
            cart.markAsUnavailable(item);
            if (storeToSwitchTo.hasItem(item)) {
                cart.addItem(storeToSwitchTo.getItem(item.getName()));
            }
            weight += item.getWeight();
        }

        for(Item item: cart.filterNotItems("EVENT")) {
            if (!storeToSwitchTo.hasItem(item)) {
                cart.markAsUnavailable(item);
            }
            weight += item.getWeight();
        }

        for (Item item: cart.getUnavailableItems()) {
            weight -= item.getWeight();
        }

        Store currentStore = (Store) session.get("STORE");
        if(deliveryInformation == null || deliveryInformation.getDeliveryAddress() == null) {
            return;
        }

        if (isTypeHomeDelivery(deliveryInformation)) {
            deliveryInformation.setTotalWeight(weight);
            deliveryInformation.setPickupLocation(storeToSwitchTo);
            if (!isLocationServiceAvailable(storeToSwitchTo, deliveryInformation)) {
                deliveryInformation.setType("PICKUP");
                deliveryInformation.setPickupLocation(currentStore);
            }
        } else if (isLocationServiceAvailable(storeToSwitchTo, deliveryInformation)) {
            deliveryInformation.setType("HOME_DELIVERY");
            deliveryInformation.setTotalWeight(weight);
            deliveryInformation.setPickupLocation(storeToSwitchTo);
        }
    }

    private boolean isLocationServiceAvailable(Store storeToSwitchTo, DeliveryInformation deliveryInformation) {
        return ((LocationService) session.get("LOCATION_SERVICE")).isWithinDeliveryRange(storeToSwitchTo, deliveryInformation.getDeliveryAddress());
    }

    private boolean isTypeHomeDelivery(DeliveryInformation deliveryInformation) {
        return deliveryInformation.getType() != null
                && "HOME_DELIVERY".equals(deliveryInformation.getType());
    }

    private void saveStoreSession(Store storeToSwitchTo) {
        session.put("STORE", storeToSwitchTo);
        session.saveAll();
    }

    private void updateDeliveryInformation(DeliveryInformation deliveryInformation) {
        if (deliveryInformation != null) {
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
