package fr.pantheonsorbonne.ufr27.miage.camel;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.jms.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class PriceConsumerLuxury implements Runnable {

    @Inject
    ConnectionFactory connectionFactory;

    //indique si la classe est configurée pour recevoir les messages en boucle
    boolean running;

    //cette méthode démarre un nouveau thread exécutant l'instance en cours, jusqu'à ce que la variable running soit false.
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
                //reçoit un message à partir de la queue queue/prices
                Message mess = context.createConsumer(context.createQueue("M1.product-LUXURY-"+userName)).receive();
                //converti ce message en int
                double price = Double.parseDouble(mess.getBody(String.class));
                //affiche le résultat dans la console
                System.out.println("from the consumer (luxury): " + price);

            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
