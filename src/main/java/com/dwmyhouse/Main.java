package com.dwmyhouse;

import com.dwmyhouse.ui.MainController;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Application entry point.
 * Initializes Spring Context and launches the main controller.
 */
public class Main {
    public static void main(String[] args) {

        // Load Spring context with beans and property config
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        //Retrieve main controller bean from bean context
        MainController controller = context.getBean(MainController.class);
        controller.run();
    }
}
