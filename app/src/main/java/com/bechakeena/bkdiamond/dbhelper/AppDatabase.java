package com.bechakeena.bkdiamond.dbhelper;

import com.bechakeena.bkdiamond.models.Notification;
import com.bechakeena.bkdiamond.models.Product;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class AppDatabase {

    public AppDatabase() {
    }

    public static void saveNotification(Notification notification) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(notification);
        realm.commitTransaction();
    }

    public static RealmResults<Notification> getNotifications() {
        return Realm.getDefaultInstance().where(Notification.class).sort("timestamp", Sort.DESCENDING).findAll();
    }

    public static void removeNotification(Notification notification) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(Notification.class).equalTo("id", notification.getId()).findFirst().deleteFromRealm();
        realm.commitTransaction();
    }

    public static RealmResults<Product> getProducts() {
        return Realm.getDefaultInstance().where(Product.class).findAll();
    }

    public static void saveProducts(final List<Product> products) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        for (Product product : products) {
            realm.copyToRealmOrUpdate(product);
        }
        realm.commitTransaction();
    }
    /**
     * Adding orderedProduct to cart
     * Will create a new cart entry if there is no cart created yet
     * Will increase the orderedProduct quantity count if the item exists already
     */
    public static void addItemToCart(Product product) {
        initNewCart(product);
    }

    private static void initNewCart(Product product) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        CartItem cartItem = realm.where(CartItem.class).equalTo("product.productId", product.getProductId()).findFirst();
        if (cartItem == null) {
            CartItem ci = new CartItem();
            ci.product = product;
            ci.quantity = 1;
            realm.copyToRealmOrUpdate(ci);
        } else {
            cartItem.quantity += 1;
            realm.copyToRealmOrUpdate(cartItem);
        }
        realm.commitTransaction();
    }

    public static void removeCartItem(Product product) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        CartItem cartItem = realm.where(CartItem.class).equalTo("product.productId", product.getProductId()).findFirst();
        if (cartItem != null) {
            if (cartItem.quantity == 1) {
                cartItem.deleteFromRealm();
            } else {
                cartItem.quantity -= 1;
                realm.copyToRealmOrUpdate(cartItem);
            }
        }
        realm.commitTransaction();
    }

    public static void removeCartItem(CartItem cartItem) {
        Realm realm = Realm.getDefaultInstance();
        CartItem cartItem1 = realm.where(CartItem.class).equalTo("product.productId", cartItem.product.getProductId()).findFirst();
        realm.beginTransaction();
        if (cartItem1 != null) cartItem1.deleteFromRealm();
        realm.commitTransaction();

    }

    public static void clearCart() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(CartItem.class);
        realm.commitTransaction();
    }

    public static void clearData() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
    }
}
