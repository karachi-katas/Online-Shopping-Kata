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
     */
    public void switchStore(Store storeToSwitchTo) {
        Cart cart = (Cart) session.get("CART");
        DeliveryInformation deliveryInformation = (DeliveryInformation) session.get("DELIVERY_INFO");
        if (newStoreIsCentralWarehouse(storeToSwitchTo)) {
            markEventItemsUnavailable(cart);
            setDeliveryInformationToShipping(deliveryInformation);
        } else {
            switchStorePt2(storeToSwitchTo, cart, deliveryInformation);
        }
        session.put("STORE", storeToSwitchTo);
        session.saveAll();
    }

    private boolean newStoreIsCentralWarehouse(Store storeToSwitchTo) {
        return storeToSwitchTo == null;
    }

    private void switchStorePt2(Store storeToSwitchTo, Cart cart, DeliveryInformation deliveryInformation) {
        if (cart == null) {
            return;
        }
        Items newItems = new Items();
        for (Item item : cart.getItems()) {
            if ("EVENT".equals(item.getType())) {
                cart.markAsUnavailable(item);
                if (storeToSwitchTo.hasItem(item)) {
                    newItems.addItem(storeToSwitchTo.getItem(item.getName()));
                }
            } else if (!storeToSwitchTo.hasItem(item)) {
                cart.markAsUnavailable(item);
            }
        }

        long weight = cart.weight();

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
        } else if (deliveryInformation != null
                && deliveryInformation.getDeliveryAddress() != null
                && ((LocationService) session.get("LOCATION_SERVICE"))
                        .isWithinDeliveryRange(storeToSwitchTo, deliveryInformation.getDeliveryAddress())) {
                    deliveryInformation.setType("HOME_DELIVERY");
                    deliveryInformation.setTotalWeight(weight);
                    deliveryInformation.setPickupLocation(storeToSwitchTo);
        }
        cart.addItems(newItems);
    }


    private void setDeliveryInformationToShipping(DeliveryInformation deliveryInformation) {
        if (deliveryInformation != null) {
            deliveryInformation.setType("SHIPPING");
            deliveryInformation.setPickupLocation(null);
        }
    }

    private void markEventItemsUnavailable(Cart cart) {
        if (cart != null) {
            cart.markEventItemsUnavailable();
        }
    }

    @Override
    public String toString() {
        return "OnlineShopping{\n"
                + "session=" + session + "\n}";
    }
}
