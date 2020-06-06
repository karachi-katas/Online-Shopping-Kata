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

}
