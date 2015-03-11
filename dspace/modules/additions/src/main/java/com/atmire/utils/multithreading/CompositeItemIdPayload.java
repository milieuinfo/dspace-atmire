package com.atmire.utils.multithreading;

import org.dspace.content.Item;
import org.dspace.core.Context;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Roeland Dillen (roeland at atmire dot com)
 * Date: 31/05/13
 * Time: 15:03
 */
public class CompositeItemIdPayload extends ItemIdPayload {

    private List<ItemIdPayload> payloads;

    public CompositeItemIdPayload(List<ItemIdPayload> payloads) {
        this.payloads = payloads;
    }

    public CompositeItemIdPayload(Integer id, Context context, List<ItemIdPayload> payloads) {
        super(id, context);
        this.payloads = payloads;
    }

    @Override
    protected void finish() {
        for(ItemIdPayload payload:payloads){
            payload.finish();
        }
    }

    @Override
    protected void doRun(Item item) {
        for(ItemIdPayload payload:payloads){
            payload.doRun(item);
        }
    }

    @Override
    public ItemIdPayload create(Integer id, Context context) {
        LinkedList<ItemIdPayload> pl=new LinkedList<ItemIdPayload>();
        for(ItemIdPayload payload:payloads){
            pl.add(payload.create(id,context));
        }
        return new CompositeItemIdPayload(id,context,pl);
    }
}
