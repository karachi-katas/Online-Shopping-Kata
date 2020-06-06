package codingdojo;

/**
 * While shopping online in a Store, the Cart stores the Items you intend to buy
 */
public class Cart implements ModelObject {
    Items items = new Items();
    Items unavailableItems = new Items();
    public Items getItems() {
        return items;
    }
    public void addItem(Item item) {
        this.items.addItem(item);
    }
    public void addItems(Items items) {
        this.items.addItems(items);
    }

    public void markAsUnavailable(Item item) {
        this.unavailableItems.addItem(item);
    }

    @Override
    public String toString() {
        return "Cart{" +
                "items=" + items +
                "unavailable=" + unavailableItems +
                '}';
    }

    @Override
    public void saveToDatabase() {
        throw new UnsupportedOperationException("missing from this exercise - shouldn't be called from a unit test");
    }

    public Items getUnavailableItems() {
        return unavailableItems;
    }

    public void markEventItemsUnavailable() {
        for (Item item : items) {
            makeItemUnavailableIfTypeEvent(item);
        }
    }

    private void makeItemUnavailableIfTypeEvent(Item item) {
        if ("EVENT".equals(item.getType())) {
            markAsUnavailable(item);
        }
    }

    public long weight() {
        return items.totalWeight() - unavailableItems.totalWeight();
    }
}
