package com.example.realm.demo.dao;

import com.example.realm.demo.entity.Dog;
import com.example.realm.demo.entity.Person;

import java.util.Arrays;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * 1、值会自动更新，不管是mananged对象还是列表，都是与数据绑定的。
 * 2、重新查询， RealmResults<xx> 对象不一样，但是都会对查询条件监听
 * @author zhangquan
 */
public class PersonDao {
    RealmResults<Dog> memberPuppies;

    private void test() {
        Realm realm = Realm.getDefaultInstance();
        memberPuppies = realm.where(Dog.class).lessThan("age", 2).findAll();
        int size = memberPuppies.size();
        System.out.println("memberPuppies.size=" + size + ",RealmResults<Dog> memberPuppies=" + memberPuppies);

    }

    public void test1() throws  Exception{
        //测试其他变量同步更新
        test();

        Realm realm = Realm.getDefaultInstance();

        Dog dog = new Dog();
        dog.setName("Rex");
        dog.setAge(1);

        RealmResults<Dog> puppies = realm.where(Dog.class).lessThan("age", 2).findAll();
        int size = puppies.size();
        System.out.println("size=" + size + ",RealmResults<Dog> puppies=" + puppies);

        realm.beginTransaction();
        final Dog managedDog = realm.copyToRealm(dog); // Persist unmanaged objects
        Person person = realm.createObject(Person.class, 3); // Create managed objects directly

        person.getDogs().add(managedDog);
        realm.commitTransaction();

        System.out.println("dog=" + dog);
        System.out.println("managedDog=" + managedDog);
        System.out.println("managePerson=" + person);

        // Listeners will be notified when data changes
        puppies.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Dog>>() {
            @Override
            public void onChange(RealmResults<Dog> results, OrderedCollectionChangeSet changeSet) {
                // Query results are updated in real time with fine grained notifications.
                int[] insertions = changeSet.getInsertions();// => [0] is added.
                System.out.println("changeListener..insertions=" + Arrays.toString(insertions));
                System.out.println("changeListener..results.size=" + results.size() + ",results=" + results);
            }
        });

        System.out.println("------------after inserted--------");
        RealmResults<Dog> afterInserted = realm.where(Dog.class).lessThan("age", 2).findAll();
        System.out.println("puppies.size=" + puppies.size() + ",RealmResults<Dog> puppies=" + puppies);
        System.out.println(" RealmResults<Dog> afterInserted=" + afterInserted);
        System.out.println("managedQuery1==managedQuery2: " + (afterInserted == puppies)); //重新查询 对象不一样
        for (int i = 0; i < afterInserted.size(); i++) {
            Dog item = afterInserted.get(i);
            System.out.println("item=" + item);
        }
        System.out.println("memberPuppies.newSize="+memberPuppies.size()+",memberPuppies="+memberPuppies);

        System.out.println("------------update--------");
        /**
         * 会自动更新managed 对象
         */
        //修改 自动更新managed对象
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Dog dog = realm.where(Dog.class).equalTo("age", 1).findFirst();
                if (null != dog) {
                    dog.setAge(3);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // Original queries and Realm objects are automatically updated.
                int newSize = puppies.size();// => 0 because there are no more puppies younger than 2 years old
                int newAge = managedDog.getAge();// => 3 the dogs age is updated
                System.out.println("puppies.newSize=" + newSize + ",managedDog.getAge()=" + newAge);

                System.out.println("memberPuppies.newSize="+memberPuppies.size()+",memberPuppies="+memberPuppies);
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {

            }
        });

    }
}
