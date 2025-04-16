package com.dwmyhouse;

import com.dwmyhouse.ui.MainController;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        MainController controller = context.getBean(MainController.class);
        controller.run();
    }
}
