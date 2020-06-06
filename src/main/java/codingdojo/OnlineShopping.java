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
        if (storeToSwitchIsNull(storeToSwitchTo)) {
            whenStoreToSwitchIsEmpty(cart, deliveryInformation);
            session.put("STORE", storeToSwitchTo);
            session.saveAll();
            return;
        }
        if (Cart.exists(cart)) {
            ArrayList<Item> newItems = new ArrayList<>();
            long weight = 0;
            weight = switchItemsToNewStore(storeToSwitchTo, cart, newItems, weight);
            for (Item item: cart.getUnavailableItems()) {
                weight -= item.getWeight();
            }

            Store currentStore = (Store) session.get("STORE");
            if (deliveryInformation != null
                    && deliveryInformation.getType() != null
                    && "HOME_DELIVERY".equals(deliveryInformation.getType())
                    && deliveryInformation.getDeliveryAddress() != null) {
                if (!((LocationService) session.get("LOCATION_SERVICE")).isWithinDeliveryRange(storeToSwitchTo, deliveryInformation.getDeliveryAddress())) {
                    deliveryInformation.setType("PICKUP");
                    deliveryInformation.setPickupLocation(currentStore);
                } else {
                    deliveryInformation.setTotalWeight(weight);
                    deliveryInformation.setPickupLocation(storeToSwitchTo);
                }
            } else {
                if (deliveryInformation != null
                        && deliveryInformation.getDeliveryAddress() != null) {
                    if (((LocationService) session.get("LOCATION_SERVICE")).isWithinDeliveryRange(storeToSwitchTo, deliveryInformation.getDeliveryAddress())) {
                        deliveryInformation.setType("HOME_DELIVERY");
                        deliveryInformation.setTotalWeight(weight);
                        deliveryInformation.setPickupLocation(storeToSwitchTo);

                    }
                }
            }
            for (Item item : newItems) {
                cart.addItem(item);
            }
        }

        session.put("STORE", storeToSwitchTo);
        session.saveAll();
    }

    private long switchItemsToNewStore(Store storeToSwitchTo, Cart cart, ArrayList<Item> newItems,
        long weight) {
        for (Item item : cart.getItems()) {

            if ("EVENT".equals(item.getType()) && storeToSwitchTo.hasItem(item)) {
                cart.markAsUnavailable(item);
                newItems.add(storeToSwitchTo.getItem(item.getName()));
            }
            if (!storeToSwitchTo.hasItem(item)) {
                cart.markAsUnavailable(item);
            }
            weight += item.getWeight();
        }
        return weight;
    }

    private void whenStoreToSwitchIsEmpty(Cart cart, DeliveryInformation deliveryInformation) {
        if (Cart.exists(cart)) {
            cart.markEventsAsUnavailable();
        }
        if (deliveryInformation != null) {
            deliveryInformation.setType("SHIPPING");
            deliveryInformation.setPickupLocation(null);
        }
    }

    private boolean storeToSwitchIsNull(Store storeToSwitchTo) {
        return storeToSwitchTo == null;
    }

    @Override
    public String toString() {
        return "OnlineShopping{\n"
                + "session=" + session + "\n}";
    }
}
