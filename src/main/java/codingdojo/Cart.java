package codingdojo;

import java.util.Collection;
import java.util.List;

/**
 * While shopping online in a Store, the Cart stores the Items you intend to buy
 */
public class Cart implements ModelObject {
    Items items = new Items();
    Items unavailableItems = new Items();
    public List<Item> getItems() {
        return items.getValues();
    }
    public void addItem(Item item) {
        this.items.addItem(item);
    }
    public void addItems(Collection<Item> items) {
        this.items.addItems(items);
    }

    public void markAsUnavailable(Item item) {
        this.unavailableItems.addItem(item);
    }

    @Override
    public String toString() {
        return "Cart{" +
                "items=" + displayItems(items.getValues()) +
                "unavailable=" + displayItems(unavailableItems.getValues()) +
                '}';
    }

    private String displayItems(List<Item> items) {
        StringBuffer itemDisplay = new StringBuffer("\n");
        for (Item item : items) {
            itemDisplay.append(item.toString());
            itemDisplay.append("\n");
        }
        return itemDisplay.toString();
    }

    @Override
    public void saveToDatabase() {
        throw new UnsupportedOperationException("missing from this exercise - shouldn't be called from a unit test");
    }

    public Collection<Item> getUnavailableItems() {
        return unavailableItems.getValues();
    }

    public void markAllEventItemsUnavailable() {
        unavailableItems.addItems(items.filterType("EVENT"));
    }

    public Collection<Item> filterItems(String type) {
        return items.filterType("EVENT");
    }

    public Collection<Item> filterNotItems(String type) {
        return items.filterNotType("EVENT");
    }
}
