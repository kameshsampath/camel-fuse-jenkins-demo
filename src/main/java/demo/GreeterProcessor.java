package demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import javax.servlet.http.HttpServletRequest;

/**
 * @author kameshs
 */
@Slf4j
public class GreeterProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        HttpServletRequest request = exchange.getIn().getBody(HttpServletRequest.class);

        String user = request.getParameter("user");

        exchange.getOut().setBody(String.format("<h1>Hello %s , Welcome to  JBoss Fuse CI/CD!", user));
    }
}
