package codingdojo;

import java.util.ArrayList;

public class Items {
    private ArrayList<Item> items = new ArrayList<>();

    public void addItem(Item item) {
        this.items.add(item);
    }

    public void addItems(Items items) {
        this.items.addAll(items.items);
    }

    @Override
    public String toString() {
        StringBuffer itemDisplay = new StringBuffer("\n");
        for (Item item : items) {
            itemDisplay.append(item.toString());
            itemDisplay.append("\n");
        }
        return itemDisplay.toString();
    }

    public ArrayList<Item> getItems() {
        return items;
    }
}
