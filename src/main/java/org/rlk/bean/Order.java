package org.rlk.bean;

import org.rlk.annotation.Component;
import org.rlk.annotation.Scope;

import java.util.Objects;

/**
 * @author: rlk
 * @date: 2022/8/26
 * Description: Order类
 */
@Component
@Scope("prototype")
public class Order {

    private Integer id;
    private String desc;

    public Order() {
    }

    public Order(Integer id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Order order = (Order) o;
        return Objects.equals(id, order.id) &&
                Objects.equals(desc, order.desc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, desc);
    }

//    @Override
//    public String toString() {
//        return "Order{" +
//                "id=" + id +
//                ", desc='" + desc + '\'' +
//                '}';
//    }
}
