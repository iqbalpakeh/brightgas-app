package com.pertamina.brightgas.firebase.models;

import com.google.firebase.database.Exclude;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Package {

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_DIPROSES = 1;
    public static final int STATUS_DIKIRIM = 2;
    public static final int STATUS_DITERIMA = 3;

    public String uid;
    public String deliveryDate;
    public String deliveryTime;
    public String addressId;
    public String promotionCode;
    public ArrayList<Item> items;
    public String invoice;
    public String grandTotal;
    public String subTotal;
    public String totalOngkir;
    public String pinCode;
    public String statusId;
    public String agentId;
    public String driverId;
    public String pendingRemarks;

    public Package() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Package(String uid,
                   String deliveryDate,
                   String deliveryTime,
                   String addressId,
                   String promotionCode,
                   ArrayList<Item> items) {

        this.uid = uid;
        this.deliveryDate = deliveryDate;
        this.deliveryTime = deliveryTime;
        this.addressId = addressId;
        this.promotionCode = promotionCode;
        this.items = items;
        this.invoice = calculateInvoice();
        this.subTotal = calculateSubTotal();
        this.totalOngkir = calculateTotalOngkir();
        this.grandTotal = calculateGrandTotal();
        this.pinCode = generatePinCode();
        this.statusId = STATUS_PENDING + "";
        this.agentId = "";
        this.driverId = "";
        this.pendingRemarks = "";
    }

    public String startTime() {
        return this.deliveryTime.split("-")[0].trim();
    }

    public String endTime() {
        return this.deliveryTime.split("-")[1].trim();
    }

    private String calculateInvoice() {
        // only dummy
        return "INV/00001/01012017";
    }

    private String calculateSubTotal() {
        BigDecimal dSubTotal = new BigDecimal("0");
        for (Item item : items) {
            dSubTotal = dSubTotal.add(new BigDecimal(item.subTotal));
        }
        return dSubTotal.toString();
    }

    private String calculateTotalOngkir() {
        BigDecimal dTotalOngkir = new BigDecimal("0");
        for (Item item : items) {
            dTotalOngkir = dTotalOngkir.add(new BigDecimal(item.ongkir));
        }
        return dTotalOngkir.toString();
    }

    private String calculateGrandTotal() {
        BigDecimal dGrandTotal = new BigDecimal(subTotal);
        dGrandTotal = dGrandTotal.add(new BigDecimal(totalOngkir));
        return dGrandTotal.toString();
    }

    private String generatePinCode() {
        // only dummy
        return "1504";
    }

    public Map<String, Object> toMap() {

        ArrayList<Map> itemMapList = new ArrayList<>();

        for (Item item : items) {
            itemMapList.add(item.toMap());
        }

        HashMap<String, Object> ordersMap = new HashMap<>();
        ordersMap.put("uid", uid);
        ordersMap.put("deliveryDate", deliveryDate);
        ordersMap.put("deliveryTime", deliveryTime);
        ordersMap.put("addressId", addressId);
        ordersMap.put("promotionCode", promotionCode);
        ordersMap.put("invoice", invoice);
        ordersMap.put("subTotal", subTotal);
        ordersMap.put("totalOngkir", totalOngkir);
        ordersMap.put("grandTotal", grandTotal);
        ordersMap.put("pinCode", pinCode);
        ordersMap.put("statusId", statusId);
        ordersMap.put("agentId", agentId);
        ordersMap.put("driverId", driverId);
        ordersMap.put("items", itemMapList);

        return ordersMap;
    }

    public static class Item {

        public String itemId;
        public String type;
        public String tradeIn;
        public String quantity;
        public String price;
        public String subTotal;
        public String ongkir;
        public String extraPrice;
        public String orderPrice;

        public Item() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public Item(String itemId,
                    String type,
                    String tradeIn,
                    String quantity,
                    String price,
                    String extraPrice,
                    String orderPrice) {

            this.itemId = itemId;
            this.type = type;
            this.tradeIn = tradeIn;
            this.quantity = quantity;
            this.price = price;
            this.subTotal = calculateSubTotal();
            this.extraPrice = extraPrice;
            this.orderPrice = orderPrice;
            this.ongkir = calculateOngkir();
        }

        private String calculateSubTotal() {
            BigDecimal dSubTotal = new BigDecimal(price);
            dSubTotal = dSubTotal.multiply(new BigDecimal(quantity));
            return dSubTotal.toString();
        }

        private String calculateOngkir() {

            int qty = Integer.parseInt(quantity);
            BigDecimal dOngkir = new BigDecimal("0");
            BigDecimal dExtra = new BigDecimal("0");

            dOngkir = dOngkir.add(new BigDecimal(orderPrice));

            if (qty > 1) {
                qty--;
                dExtra = dExtra.add(new BigDecimal(extraPrice));
                dExtra = dExtra.multiply(new BigDecimal(String.valueOf(qty)));
                dOngkir = dOngkir.add(dExtra);
            }

            return dOngkir.toString();
        }

        @Exclude
        public Map<String, Object> toMap() {
            HashMap<String, Object> itemMap = new HashMap<>();
            itemMap.put("itemId", itemId);
            itemMap.put("type", type);
            itemMap.put("tradeIn", tradeIn);
            itemMap.put("quantity", quantity);
            itemMap.put("price", price);
            itemMap.put("subTotal", subTotal);
            itemMap.put("ongkir", ongkir);
            return itemMap;
        }

    }
}
