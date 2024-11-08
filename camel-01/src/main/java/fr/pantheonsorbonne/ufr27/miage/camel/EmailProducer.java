package fr.pantheonsorbonne.ufr27.miage.camel;

import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.jms.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class EmailProducer implements Runnable {

    @Inject
    ConnectionFactory connectionFactory;
    private final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);
    private static final Random random = new Random();

    @ConfigProperty(name = "quarkus.artemis.username")
    String userName;

    void onStart(@Observes StartupEvent ev) {
        scheduler.scheduleAtFixedRate(this, 0L, 5L, TimeUnit.SECONDS);
    }

    void onStop(@Observes ShutdownEvent ev) {
        scheduler.shutdown();
    }

    @Override
    public void run() {
        try (JMSContext context = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE)) {
            Message msg = context.createTextMessage("New email content");
            String priority = EmailPriority.values()[random.nextInt(2)].name();
            msg.setStringProperty("priority", priority);
            context.createProducer().send(context.createQueue("M1.emails-" + userName), msg);
            System.out.println("Email sent with priority: " + priority);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
