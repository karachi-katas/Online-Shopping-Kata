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
        Cart cart = session.getCart();
        DeliveryInformation deliveryInformation = session.getDeliveryInfo();

        if (storeToSwitchTo == null) {
            storeToSwitchIsNull(cart, deliveryInformation);
        } else {
            storeToSwitchToIsNotNull(storeToSwitchTo, cart, deliveryInformation);
        }

        session.put("STORE", storeToSwitchTo);
        session.saveAll();
    }

    private void storeToSwitchToIsNotNull(Store storeToSwitchTo, Cart cart, DeliveryInformation deliveryInformation) {
        if (cart == null) {
            return;
        }
        ArrayList<Item> newItems = new ArrayList<>();
        long weight = 0;
        weight = getWeight(storeToSwitchTo, cart, newItems, weight);

        if (deliveryAddressExists(deliveryInformation)) {
            Store currentStore = session.getStore();
            updateDeliveryInformation(storeToSwitchTo, deliveryInformation, weight, currentStore);
        }
        for (Item item : newItems) {
            cart.addItem(item);
        }
    }

    private void updateDeliveryInformation(Store storeToSwitchTo,
        DeliveryInformation deliveryInformation, long weight, Store currentStore) {

        // add drone checks here...

        if ("HOME_DELIVERY".equals(deliveryInformation.getType())) {

            setToHomeDelivery(storeToSwitchTo, deliveryInformation, weight);

            if (!storeIsInRange(storeToSwitchTo, deliveryInformation)) {
                deliveryInformation.setType("PICKUP");
                deliveryInformation.setPickupLocation(currentStore);
            }
        } else {
            setToHomeDelivery(storeToSwitchTo, deliveryInformation, weight);
        }
    }

    private boolean deliveryAddressExists(DeliveryInformation deliveryInformation) {
        return deliveryInformation != null
                && deliveryInformation.getDeliveryAddress() != null;
    }

    private void setToHomeDelivery(Store storeToSwitchTo, DeliveryInformation deliveryInformation,
        long weight) {
        if (storeIsInRange(storeToSwitchTo, deliveryInformation)) {
            deliveryInformation.setType("HOME_DELIVERY");
            deliveryInformation.setTotalWeight(weight);
            deliveryInformation.setPickupLocation(storeToSwitchTo);

        }
    }

    private boolean storeIsInRange(Store storeToSwitchTo, DeliveryInformation deliveryInformation) {
        return session.getLocationService().isWithinDeliveryRange(storeToSwitchTo,
                deliveryInformation.getDeliveryAddress());
    }

    private long getWeight(Store storeToSwitchTo, Cart cart, ArrayList<Item> newItems,
        long weight) {
        for (Item item : cart.getItems()) {
            if ("EVENT".equals(item.getType())) {
                itemTypeIsEvent(storeToSwitchTo, cart, newItems, item);
            } else if (!storeToSwitchTo.hasItem(item)) {
                cart.markAsUnavailable(item);
            }
            weight += item.getWeight();
        }
        for (Item item: cart.getUnavailableItems()) {
            weight -= item.getWeight();
        }
        return weight;
    }

    private void itemTypeIsEvent(Store storeToSwitchTo, Cart cart, ArrayList<Item> newItems,
        Item item) {
        if (storeToSwitchTo.hasItem(item)) {
            cart.markAsUnavailable(item);
            newItems.add(storeToSwitchTo.getItem(item.getName()));
        } else {
            cart.markAsUnavailable(item);
        }
    }

    private void storeToSwitchIsNull(Cart cart, DeliveryInformation deliveryInformation) {
        if (cart != null) {
            for (Item item : cart.getItems()) {
                if ("EVENT".equals(item.getType())) {
                    cart.markAsUnavailable(item);
                }
            }

        }
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
