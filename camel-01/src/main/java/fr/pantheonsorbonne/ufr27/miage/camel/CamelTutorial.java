// package fr.pantheonsorbonne.ufr27.miage.camel;

// import jakarta.enterprise.context.ApplicationScoped;
// import org.apache.camel.Exchange;
// import org.apache.camel.Predicate;
// import org.apache.camel.Processor;
// import org.apache.camel.builder.RouteBuilder;
// import org.eclipse.microprofile.config.inject.ConfigProperty;

// @ApplicationScoped
// public class CamelTutorial extends RouteBuilder {

//     @ConfigProperty(name="quarkus.artemis.username")
//     String userName;
//     @Override
//     public void configure() {
//         from("sjms2:M1.prices-"+userName).process(new Processor() {
//                     @Override
//                     public void process(Exchange exchange) throws Exception {
//                         ProductType type = ProductType.valueOf(exchange.getMessage().getHeader("productType").toString());
//                         double price = Double.parseDouble(exchange.getMessage().getBody(String.class));
//                         exchange.getMessage().setBody("" + price * type.getVatRate());
//                     }
//                 })
//                 .choice()
//                 .when(new Predicate() {
//                     @Override
//                     public boolean matches(Exchange exchange) {
//                         return ProductType.BASE.equals(exchange.getMessage().getHeader("productType",ProductType.class));
//                     }
//                 })
//                 .to("sjms2:M1.product-BASE-"+userName).to("file:data/basic-prices")
//                 .when(new Predicate() {
//                     @Override
//                     public boolean matches(Exchange exchange) {
//                         return ProductType.LUXURY.equals(exchange.getMessage().getHeader("productType",ProductType.class));
//                     }
//                 })
//                 .to("sjms2:M1.product-LUXURY-"+userName).to("file:data/luxury-prices");
//     }
// }