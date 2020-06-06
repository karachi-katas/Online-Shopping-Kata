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
    private Cart cart;
    private DeliveryInformation deliveryInformation;
    private Store currentStore;
    private LocationService locationService;

    public OnlineShopping(Session session) {
        this.session = session;
        this.cart = (Cart) session.get("CART");
        this.deliveryInformation = (DeliveryInformation) session.get("DELIVERY_INFO");
        this.currentStore = (Store) session.get("STORE");
        this.locationService = ((LocationService) session.get("LOCATION_SERVICE"));
    }

    /**
     * This method is called when the user changes the
     * store they are shopping at in the online shopping
     * website.
     *
     */
    public void switchStore(Store storeToSwitchTo) {
        if (isCentralWarehouse(storeToSwitchTo)) {
            handleStoreEventsWhenCentralWarehouse();
        } else {
            if (cart != null) {
                ArrayList<Item> newItems = new ArrayList<>();
                long weight = 0;
                for (Item item : cart.getItems()) {

                    if (item.isEvent()) {
                        handleEventWhenStoreSwitched(storeToSwitchTo, newItems, item);
                    }

                    weight += item.getWeight();
                }
                for (Item item: cart.getUnavailableItems()) {
                    weight -= item.getWeight();
                }
                if (deliveryInformation != null) {
                    deliveryInformation.updateDeliveryInformation(storeToSwitchTo, currentStore, weight, locationService);
                }
                for (Item item : newItems) {
                    cart.addItem(item);
                }
            }
        }

        session.put("DELIVERY_INFO", deliveryInformation);
        session.put("STORE", storeToSwitchTo);
        session.saveAll();
    }

    private boolean isCentralWarehouse(Store storeToSwitchTo) {
        return storeToSwitchTo == null;
    }

    private void handleEventWhenStoreSwitched(Store storeToSwitchTo, ArrayList<Item> newItems, Item item) {
        if (storeToSwitchTo.hasItem(item)) {
            newItems.add(storeToSwitchTo.getItem(item.getName()));
        } else {
            cart.markAsUnavailable(item);
        }
    }

    private void handleStoreEventsWhenCentralWarehouse() {
        if (cart != null) {
            markStoreEventsUnavailable();
        }
        setOrderToShippingIfNoDeliveryInformation();
    }

    private void setOrderToShippingIfNoDeliveryInformation() {
        if (deliveryInformation != null) {
            deliveryInformation = new DeliveryInformation("SHIPPING", null, deliveryInformation.getWeight());
        }
    }

    private void markStoreEventsUnavailable() {
        for (Item item : cart.getItems()) {
            if (item.isEvent()) {
                cart.markAsUnavailable(item);
            }
        }
    }

    @Override
    public String toString() {
        return "OnlineShopping{\n"
                + "session=" + session + "\n}";
    }
}
