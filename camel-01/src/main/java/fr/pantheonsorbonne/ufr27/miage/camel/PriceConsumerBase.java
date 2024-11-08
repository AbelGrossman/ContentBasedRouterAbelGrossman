package fr.pantheonsorbonne.ufr27.miage.camel;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.jms.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class PriceConsumerBase implements Runnable {
    @Inject
    ConnectionFactory connectionFactory;
    boolean running;

    void onStart(@Observes StartupEvent ev) {
        running = true;
        new Thread(this).start();
    }

    void onStop(@Observes ShutdownEvent ev) {
        running = false;
    }
    @ConfigProperty(name="quarkus.artemis.username")
    String userName;
    @Override
    public void run() {
        while (running) {
            try (JMSContext context = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE)) {
                Message mess = context.createConsumer(context.createQueue("M1.product-BASE-"+userName)).receive();
                double price = Double.parseDouble(mess.getBody(String.class));
                System.out.println("from the consume (base): " + price);

            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}