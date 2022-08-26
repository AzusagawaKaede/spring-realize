package org.rlk;

import org.rlk.bean.Order;
import org.rlk.bean.User;
import org.rlk.config.TestConfig;
import org.rlk.context.ApplicationContext;

/**
 * @author: rlk
 * @date: 2022/8/26
 * Description: main方法启动
 */
public class TestApplication {

    public static void main(String[] args) {
        //初始化容器
        ApplicationContext context = new ApplicationContext(TestConfig.class);
        //获取Bean
        User user = (User) context.getBean("user");
        System.out.println("user = " + user);
        User user2 = (User) context.getBean("user");
        System.out.println("user2 = " + user2);

        Order order = (Order) context.getBean("order");
        System.out.println("order = " + order);
        Order order2 = (Order) context.getBean("order");
        System.out.println("order2 = " + order2);
    }

}
