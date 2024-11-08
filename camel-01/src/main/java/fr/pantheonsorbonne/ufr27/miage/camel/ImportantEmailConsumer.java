package fr.pantheonsorbonne.ufr27.miage.camel;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.jms.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class ImportantEmailConsumer implements Runnable {

    @Inject
    ConnectionFactory connectionFactory;
    boolean running;

    @ConfigProperty(name="quarkus.artemis.username")
    String userName;

    void onStart(@Observes StartupEvent ev) {
        running = true;
        new Thread(this).start();
    }

    void onStop(@Observes ShutdownEvent ev) {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            try (JMSContext context = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE)) {
                Message msg = context.createConsumer(context.createQueue("M1.emails-important-" + userName)).receive();
                System.out.println("Received important email: " + msg.getBody(String.class));
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
