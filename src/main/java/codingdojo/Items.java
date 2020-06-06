package codingdojo;

import java.util.ArrayList;
import java.util.Collection;

public class Items {

    private ArrayList<Item> values;

    public ArrayList<Item> getValues() {
        return values;
    }

    public void setValues(ArrayList<Item> values) {
        this.values = values;
    }

    public Items(ArrayList<Item> values) {
        this.values = values;
    }

    public Items() {
        this.values = new ArrayList<>();
    }

    public void addItem(Item item){
        values.add(item);
    }

    public void addItems(Collection<Item> items){
        values.addAll(items);
    }

    public Collection<Item> filterType(String type){
        Collection<Item> items = new ArrayList<>();

        for (Item item : values){
            if(item.isType(type)) {
                items.add(item);
            }
        }

        return items;
    }

    public Collection<Item> filterNotType(String type){
        Collection<Item> items = new ArrayList<>();

        for (Item item : values){
            if(!item.isType(type)) {
                items.add(item);
            }
        }

        return items;
    }
}
