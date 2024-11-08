package fr.pantheonsorbonne.ufr27.miage.camel;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.jms.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class PriceProducer implements Runnable {

    @Inject
    ConnectionFactory connectionFactory;

    private static final Random random = new Random();

    private final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);

    void onStart(@Observes StartupEvent ev) {
        scheduler.scheduleAtFixedRate(this, 0L, 5L, TimeUnit.SECONDS);
    }

    void onStop(@Observes ShutdownEvent ev) {
        scheduler.shutdown();
    }

    @ConfigProperty(name = "quarkus.artemis.username")
    String userName;

    @Override
    public void run() {
        try (JMSContext context = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE)) {
            //on envoie un message de type Text, avec la même payload qu'avant
            Message msg = context.createTextMessage(Integer.toString(random.nextInt(100)));
            try {
                //on rajoute simplement un header indiquant le type du product
                String productType = ProductType.values()[random.nextInt(2) % (ProductType.values().length)].name();
                msg.setStringProperty("productType", productType);
                //et on envoie le prix sur la queue price
                context.createProducer().send(context.createQueue("M1.prices-" + userName), msg);
                // System.out.println("message sent of type " + productType);
            } catch (JMSException e) {
                e.printStackTrace();
            }
            ;
        }
    }
}