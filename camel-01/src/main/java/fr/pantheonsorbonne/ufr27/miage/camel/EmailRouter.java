package fr.pantheonsorbonne.ufr27.miage.camel;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class EmailRouter extends RouteBuilder {

    @ConfigProperty(name="quarkus.artemis.username")
    String userName;

    @Override
    public void configure() {
        from("sjms2:M1.emails-" + userName)
            .process(new Processor() {
                @Override
                    public void process(Exchange exchange) throws Exception {
                        EmailPriority type = EmailPriority.valueOf(exchange.getMessage().getHeader("priority").toString());
                        exchange.getMessage().setBody("" + type);
                    }
                })
            .choice()
            .when(new Predicate() {
                @Override
                public boolean matches(Exchange exchange) {
                    return "IMPORTANT".equals(exchange.getMessage().getHeader("priority"));
                }
            })
            .to("sjms2:M1.emails-important-" + userName)
            .to("file:data/important-emails")
            .when(new Predicate() {
                @Override
                public boolean matches(Exchange exchange) {
                    return "GENERAL".equals(exchange.getMessage().getHeader("priority"));
                }
            })
            .to("sjms2:M1.emails-general-" + userName)
            .to("file:data/general-emails")
            .otherwise()
                .to("file:data/other-emails");
            
    }
}

